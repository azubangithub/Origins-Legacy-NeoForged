(() => {
  'use strict';

  // Supported NeoForge branches in azubangithub/Origins-Legacy-NeoForged
  const TARGET_VERSIONS = [
    { mc: '1.21.1',  neo: '21.1.234',       mcRange: '[1.21.1]',  renameAttrs: false },
    { mc: '1.21.4',  neo: '21.4.161',       mcRange: '[1.21.4]',  renameAttrs: true },
    { mc: '1.21.5',  neo: '21.5.101',       mcRange: '[1.21.5]',  renameAttrs: true },
    { mc: '1.21.6',  neo: '21.6.20-beta',   mcRange: '[1.21.6]',  renameAttrs: true },
    { mc: '1.21.7',  neo: '21.7.25-beta',   mcRange: '[1.21.7]',  renameAttrs: true },
    { mc: '1.21.8',  neo: '21.8.57',        mcRange: '[1.21.8]',  renameAttrs: true },
    { mc: '1.21.11', neo: '21.11.48',       mcRange: '[1.21.11]', renameAttrs: true },
    { mc: '26.1',    neo: '26.1.0.19-beta', mcRange: '[26.1]',    renameAttrs: true },
    { mc: '26.1.1',  neo: '26.1.1.15-beta', mcRange: '[26.1.1]',  renameAttrs: true },
    { mc: '26.1.2',  neo: '26.1.2.126',     mcRange: '[26.1.2]',  renameAttrs: true },
    { mc: '26.2',    neo: '26.2.0.93',      mcRange: '[26.2]',    renameAttrs: true },
    { mc: '26.3',    neo: '26.3.0.101',     mcRange: '[26.3]',    renameAttrs: true },
  ];

  // 1.20.1 -> 1.21+ data pack folder renames
  const DATA_FOLDER_RENAMES = [
    ['advancements/', 'advancement/'],
    ['recipes/', 'recipe/'],
    ['loot_tables/', 'loot_table/'],
    ['structures/', 'structure/'],
    ['tags/blocks/', 'tags/block/'],
    ['tags/items/', 'tags/item/'],
    ['tags/entity_types/', 'tags/entity_type/'],
    ['tags/fluids/', 'tags/fluid/'],
    ['tags/game_events/', 'tags/game_event/'],
  ];

  // 1.21.2+ renamed generic.* attributes
  const RENAMED_GENERIC_ATTRS = new Set([
    'armor',
    'armor_toughness',
    'attack_damage',
    'attack_knockback',
    'attack_speed',
    'burning_time',
    'explosion_knockback_resistance',
    'fall_damage_multiplier',
    'flying_speed',
    'follow_range',
    'gravity',
    'jump_strength',
    'knockback_resistance',
    'luck',
    'max_absorption',
    'max_health',
    'movement_efficiency',
    'movement_speed',
    'oxygen_bonus',
    'safe_fall_distance',
    'scale',
    'step_height',
    'water_movement_efficiency',
  ]);

  const CODE_MARKERS = [
    'PowerFactory',
    'ConditionFactory',
    'ActionFactory',
    'org/spongepowered/asm/mixin',
    'net/fabricmc/fabric/api',
    'net/minecraft/world/item/Item',
    'net/minecraft/world/level/block/Block',
    'net/minecraft/world/entity/EntityType',
    'BuiltInRegistries',
    'Registry',
    'ServerPlayNetworking',
    'ClientPlayNetworking',
    'KeyMapping',
  ];

  const dropzone = document.getElementById('dropzone');
  const fileInput = document.getElementById('fileInput');
  const dropTitle = document.getElementById('dropTitle');
  const dropHint = document.getElementById('dropHint');
  const targetSelect = document.getElementById('targetVersion');
  const optRenameFolders = document.getElementById('optRenameFolders');
  const optRenameAttributes = document.getElementById('optRenameAttributes');
  const convertBtn = document.getElementById('convertBtn');
  const reportEl = document.getElementById('report');

  let loadedFile = null;
  let loadedZip = null;
  let loadedMeta = null;

  for (const v of TARGET_VERSIONS) {
    const opt = document.createElement('option');
    opt.value = v.mc;
    opt.textContent = `Minecraft ${v.mc} (NeoForge ${v.neo})`;
    if (v.mc === '1.21.11') opt.selected = true;
    targetSelect.appendChild(opt);
  }

  targetSelect.addEventListener('change', () => {
    const v = getSelectedTarget();
    optRenameAttributes.checked = v.renameAttrs;
  });

  function getSelectedTarget() {
    return TARGET_VERSIONS.find((v) => v.mc === targetSelect.value) || TARGET_VERSIONS[0];
  }

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function sanitizeModId(rawId) {
    let id = String(rawId || 'converted_origins_addon')
      .toLowerCase()
      .replace(/[^a-z0-9_]/g, '_');
    if (!/^[a-z]/.test(id)) id = 'addon_' + id;
    if (id.length < 2) id = id + '_addon';
    return id.slice(0, 64);
  }

  function tomlString(value) {
    return '"' + String(value ?? '')
      .replace(/\\/g, '\\\\')
      .replace(/"/g, '\\"')
      .replace(/\r?\n/g, ' ')
      .trim() + '"';
  }

  function tomlMultiline(value) {
    const clean = String(value ?? '').replace(/'''/g, "''' + \"'''\" + '''").trim();
    return "'''\n" + clean + "\n'''";
  }

  function extractAuthors(authorsField) {
    if (!Array.isArray(authorsField)) return '';
    return authorsField
      .map((entry) => (typeof entry === 'string' ? entry : entry && entry.name ? entry.name : ''))
      .filter(Boolean)
      .join(', ');
  }

  function extractLicense(licenseField) {
    if (Array.isArray(licenseField)) return licenseField.filter(Boolean).join(', ') || 'All Rights Reserved';
    if (typeof licenseField === 'string' && licenseField.trim()) return licenseField.trim();
    return 'All Rights Reserved';
  }

  // Extract UTF-8 constant pool strings from a JVM .class binary
  function extractClassStrings(bytes) {
    if (bytes.length < 10) return [];
    const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
    if (view.getUint32(0, false) !== 0xcafebabe) return [];

    const cpCount = view.getUint16(8, false);
    const strings = [];
    const decoder = new TextDecoder('utf-8', { fatal: false });
    let offset = 10;

    for (let i = 1; i < cpCount && offset < bytes.length; i++) {
      const tag = bytes[offset++];
      switch (tag) {
        case 1: {
          if (offset + 2 > bytes.length) return strings;
          const len = view.getUint16(offset, false);
          offset += 2;
          if (offset + len > bytes.length) return strings;
          strings.push(decoder.decode(bytes.subarray(offset, offset + len)));
          offset += len;
          break;
        }
        case 3:
        case 4:
          offset += 4;
          break;
        case 5:
        case 6:
          offset += 8;
          i++; // double-slot entry
          break;
        case 7:
        case 8:
        case 16:
        case 19:
        case 20:
          offset += 2;
          break;
        case 9:
        case 10:
        case 11:
        case 12:
        case 17:
        case 18:
          offset += 4;
          break;
        case 15:
          offset += 3;
          break;
        default:
          return strings;
      }
    }
    return strings;
  }

  function shouldStripPath(path) {
    const lower = path.toLowerCase();
    if (lower === 'fabric.mod.json' || lower === 'quilt.mod.json') return true;
    if (lower === 'pack.mcmeta') return true;
    if (lower.endsWith('.mixins.json') || lower.endsWith('.accesswidener')) return true;
    if (lower.startsWith('meta-inf/jars/')) return true;
    if (lower.startsWith('meta-inf/') && (lower.endsWith('.sf') || lower.endsWith('.rsa') || lower.endsWith('.dsa') || lower.endsWith('.ec'))) {
      return true;
    }
    if (lower.endsWith('.class')) return true;
    return false;
  }

  function migrateDataPath(path, renameFolders) {
    if (!renameFolders) return path;
    const match = /^data\/([^/]+)\/(.+)$/.exec(path);
    if (!match) return path;
    const ns = match[1];
    let rest = match[2];
    for (const [oldPrefix, newPrefix] of DATA_FOLDER_RENAMES) {
      if (rest.startsWith(oldPrefix)) {
        rest = newPrefix + rest.slice(oldPrefix.length);
        break;
      }
    }
    return `data/${ns}/${rest}`;
  }

  function migrateJsonContent(text, relPath, renameAttrs) {
    const warnings = [];
    let out = text;

    if (renameAttrs) {
      out = out.replace(/minecraft:generic\.([a-z0-9_]+)/g, (full, attr) => {
        return RENAMED_GENERIC_ATTRS.has(attr) ? `minecraft:${attr}` : full;
      });
    }

    const lower = relPath.toLowerCase();
    if (lower.startsWith('data/') && lower.endsWith('.json')) {
      if (/"tag"\s*:/.test(out)) {
        warnings.push(`${relPath}: uses legacy "tag" NBT field (1.20.5+ switched items to components)`);
      }
      if (/apoli:nbt\b|apoli:execute_command\b/.test(out)) {
        warnings.push(`${relPath}: uses apoli:nbt or execute_command (check 1.21 command/NBT syntax)`);
      }
    }

    return { text: out, updatedAttrs: out !== text, warnings };
  }

  function buildModsToml(meta, target, jarEntries) {
    const modId = sanitizeModId(meta.id);
    const version = (meta.version || '1.0.0').replace(/\+.*$/, '');
    const displayName = meta.name || modId;
    const license = extractLicense(meta.license);
    const authors = extractAuthors(meta.authors);
    const description = meta.description || 'Converted from Origins 1.20.1 Fabric addon.';

    let iconPath = '';
    if (typeof meta.icon === 'string') {
      iconPath = meta.icon.replace(/^\/+/, '');
    } else if (meta.icon && typeof meta.icon === 'object') {
      const candidates = Object.values(meta.icon).filter((v) => typeof v === 'string');
      if (candidates.length > 0) iconPath = candidates[candidates.length - 1].replace(/^\/+/, '');
    }
    if (!iconPath || !jarEntries.has(iconPath)) {
      iconPath = jarEntries.has(`assets/${meta.id}/icon.png`)
        ? `assets/${meta.id}/icon.png`
        : '';
    }

    const lines = [
      'modLoader="lowcodefml"',
      'loaderVersion="[1,)"',
      `license=${tomlString(license)}`,
      '',
      '[[mods]]',
      `modId=${tomlString(modId)}`,
      `version=${tomlString(version)}`,
      `displayName=${tomlString(displayName)}`,
    ];

    if (iconPath) lines.push(`iconFile=${tomlString(iconPath)}`);
    if (authors) lines.push(`authors=${tomlString(authors)}`);
    lines.push(`description=${tomlMultiline(description)}`);
    lines.push('');
    lines.push(`[[dependencies.${modId}]]`);
    lines.push('    modId="neoforge"');
    lines.push('    type="required"');
    lines.push(`    versionRange=${tomlString(`[${target.neo},)`)}`);
    lines.push('    ordering="NONE"');
    lines.push('    side="BOTH"');
    lines.push('');
    lines.push(`[[dependencies.${modId}]]`);
    lines.push('    modId="minecraft"');
    lines.push('    type="required"');
    lines.push(`    versionRange=${tomlString(target.mcRange)}`);
    lines.push('    ordering="NONE"');
    lines.push('    side="BOTH"');
    lines.push('');
    lines.push(`[[dependencies.${modId}]]`);
    lines.push('    modId="origins"');
    lines.push('    type="required"');
    lines.push('    versionRange="[1.0.0,)"');
    lines.push('    ordering="AFTER"');
    lines.push('    side="BOTH"');
    lines.push('');

    return { modId, toml: lines.join('\n') };
  }

  function renderReport(title, items) {
    reportEl.classList.remove('hidden');
    const rows = items
      .map((item) => `<li class="tag-${item.level}">${escapeHtml(item.text)}</li>`)
      .join('');
    reportEl.innerHTML = `<div class="report-head">${escapeHtml(title)}</div><ul class="report-list">${rows}</ul>`;
  }

  async function handleFile(file) {
    if (!file) return;
    if (!window.JSZip) {
      renderReport('ERROR', [{ level: 'bad', text: 'JSZip failed to load. Check internet connection and reload.' }]);
      return;
    }

    loadedFile = file;
    loadedZip = null;
    loadedMeta = null;
    convertBtn.disabled = true;

    try {
      const zip = await window.JSZip.loadAsync(file);
      const fabricJsonFile = zip.file('fabric.mod.json');
      if (!fabricJsonFile) {
        dropzone.classList.remove('loaded');
        dropTitle.textContent = file.name;
        dropHint.textContent = 'Missing fabric.mod.json — not a Fabric mod JAR';
        renderReport('INVALID FILE', [
          { level: 'bad', text: 'Could not find fabric.mod.json inside this archive.' },
        ]);
        return;
      }

      const rawJson = await fabricJsonFile.async('string');
      const meta = JSON.parse(rawJson);

      loadedZip = zip;
      loadedMeta = meta;
      dropzone.classList.add('loaded');
      dropTitle.textContent = `${meta.name || meta.id || file.name} (${meta.version || '?'})`;
      dropHint.textContent = `Fabric ID: ${meta.id || 'unknown'} — ready to convert`;
      convertBtn.disabled = false;

      // Quick inspection preview before conversion
      const classFiles = Object.keys(zip.files).filter((p) => !zip.files[p].dir && p.endsWith('.class'));
      const codeHits = [];
      for (const cls of classFiles) {
        const bytes = await zip.file(cls).async('uint8array');
        const strs = extractClassStrings(bytes);
        const hit = CODE_MARKERS.find((marker) => strs.some((s) => s.includes(marker)));
        if (hit) codeHits.push(`${cls} (${hit})`);
      }

      const items = [
        { level: 'ok', text: `Detected Fabric mod "${meta.name || meta.id}" (${meta.id} v${meta.version || '?'})` },
      ];

      const hasOriginsData = Object.keys(zip.files).some(
        (p) => !zip.files[p].dir && /^data\/[^/]+\/(origins|powers|origin_layers)\//.test(p)
      );
      items.push(
        hasOriginsData
          ? { level: 'ok', text: 'Found Origins datapack folders (origins / powers / origin_layers).' }
          : { level: 'warn', text: 'No data/*/origins or data/*/powers JSON files found.' }
      );

      if (codeHits.length > 0) {
        items.push({
          level: 'bad',
          text: `Detected ${codeHits.length} Java class(es) with custom API/Mixin code (e.g. ${codeHits[0]}). Only data/assets can convert — custom Java powers will NOT work.`,
        });
      } else if (classFiles.length > 0) {
        items.push({
          level: 'ok',
          text: `Found ${classFiles.length} stub/initializer .class file(s); safe to strip for NeoForge lowcodefml.`,
        });
      }

      renderReport('INSPECTION READY', items);
    } catch (err) {
      dropzone.classList.remove('loaded');
      dropTitle.textContent = 'DROP FABRIC .JAR HERE';
      dropHint.textContent = 'Failed to read archive';
      renderReport('ERROR', [{ level: 'bad', text: err.message || String(err) }]);
    }
  }

  async function convertAddon() {
    if (!loadedZip || !loadedMeta) return;
    convertBtn.disabled = true;

    const target = getSelectedTarget();
    const renameFolders = optRenameFolders.checked;
    const renameAttrs = optRenameAttributes.checked;

    try {
      const outZip = new window.JSZip();
      const keptPaths = new Set();
      let strippedCount = 0;
      let folderRenameCount = 0;
      let attrUpdatedCount = 0;
      const jsonWarnings = [];

      const entries = Object.entries(loadedZip.files);
      for (const [rawPath, entry] of entries) {
        if (entry.dir) continue;

        if (shouldStripPath(rawPath)) {
          strippedCount++;
          continue;
        }

        if (rawPath === 'META-INF/MANIFEST.MF') {
          outZip.file('META-INF/MANIFEST.MF', 'Manifest-Version: 1.0\n');
          keptPaths.add('META-INF/MANIFEST.MF');
          continue;
        }

        const newPath = migrateDataPath(rawPath, renameFolders);
        if (newPath !== rawPath) folderRenameCount++;
        keptPaths.add(newPath);

        if (newPath.toLowerCase().endsWith('.json')) {
          const text = await entry.async('string');
          const res = migrateJsonContent(text, newPath, renameAttrs);
          if (res.updatedAttrs) attrUpdatedCount++;
          for (const w of res.warnings) jsonWarnings.push(w);
          outZip.file(newPath, res.text);
        } else {
          const data = await entry.async('uint8array');
          outZip.file(newPath, data);
        }
      }

      const { modId, toml } = buildModsToml(loadedMeta, target, keptPaths);
      outZip.file('META-INF/neoforge.mods.toml', toml);

      const blob = await outZip.generateAsync({ type: 'blob', compression: 'DEFLATE' });
      const outName = `${modId}-neoforge-${target.mc}.jar`;

      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = outName;
      document.body.appendChild(a);
      a.click();
      a.remove();
      setTimeout(() => URL.revokeObjectURL(url), 4000);

      const summary = [
        { level: 'ok', text: `Downloaded ${outName} (modId="${modId}", lowcodefml)` },
        { level: 'ok', text: `Stripped ${strippedCount} Fabric metadata/stub entry(s).` },
      ];
      if (folderRenameCount > 0) {
        summary.push({ level: 'ok', text: `Migrated ${folderRenameCount} file path(s) to 1.21+ data folder names.` });
      }
      if (attrUpdatedCount > 0) {
        summary.push({ level: 'ok', text: `Updated minecraft:generic.* attributes in ${attrUpdatedCount} JSON file(s).` });
      }
      if (modId !== loadedMeta.id) {
        summary.push({ level: 'warn', text: `Sanitized modId "${loadedMeta.id}" -> "${modId}" for NeoForge rules.` });
      }
      for (const w of jsonWarnings.slice(0, 6)) {
        summary.push({ level: 'warn', text: w });
      }
      if (jsonWarnings.length > 6) {
        summary.push({ level: 'warn', text: `...and ${jsonWarnings.length - 6} more JSON NBT/command warning(s).` });
      }

      renderReport('CONVERSION COMPLETE', summary);
    } catch (err) {
      renderReport('CONVERSION FAILED', [{ level: 'bad', text: err.message || String(err) }]);
    } finally {
      convertBtn.disabled = false;
    }
  }

  fileInput.addEventListener('change', () => {
    if (fileInput.files && fileInput.files[0]) handleFile(fileInput.files[0]);
  });

  dropzone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropzone.classList.add('dragover');
  });

  dropzone.addEventListener('dragleave', () => {
    dropzone.classList.remove('dragover');
  });

  dropzone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropzone.classList.remove('dragover');
    if (e.dataTransfer && e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFile(e.dataTransfer.files[0]);
    }
  });

  convertBtn.addEventListener('click', convertAddon);
})();
