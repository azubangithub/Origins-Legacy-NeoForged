(() => {
  'use strict';

  // Supported NeoForge branches in azubangithub/Origins-Legacy-NeoForged
  const TARGET_VERSIONS = [
    { mc: '1.21.1',  neo: '21.1.234',       mcRange: '[1.21.1]',  legacyPrefixAttrs: true,  itemModelDef: false, cmdCompound: false },
    { mc: '1.21.4',  neo: '21.4.161',       mcRange: '[1.21.4]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
    { mc: '1.21.5',  neo: '21.5.101',       mcRange: '[1.21.5]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
    { mc: '1.21.6',  neo: '21.6.20-beta',   mcRange: '[1.21.6]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
    { mc: '1.21.7',  neo: '21.7.25-beta',   mcRange: '[1.21.7]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
    { mc: '1.21.8',  neo: '21.8.57',        mcRange: '[1.21.8]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
    { mc: '1.21.11', neo: '21.11.48',       mcRange: '[1.21.11]', legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
    { mc: '26.1',    neo: '26.1.0.19-beta', mcRange: '[26.1]',    legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
    { mc: '26.1.1',  neo: '26.1.1.15-beta', mcRange: '[26.1.1]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
    { mc: '26.1.2',  neo: '26.1.2.126',     mcRange: '[26.1.2]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
    { mc: '26.2',    neo: '26.2.0.93',      mcRange: '[26.2]',    legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
    { mc: '26.3',    neo: '26.3.0.101',     mcRange: '[26.3]',    legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true  },
  ];

  // Built-in power types registered in Origins-Legacy-NeoForged (origins:* aliases apoli:*)
  const KNOWN_APOLI_POWERS = new Set([
    'action_on_being_used', 'action_on_block_break', 'action_on_block_use', 'action_on_callback',
    'action_on_entity_use', 'action_on_hit', 'action_on_item_use', 'action_on_land', 'action_on_wake_up',
    'action_over_time', 'action_when_damage_taken', 'action_when_hit', 'active_self',
    'attacker_action_when_hit', 'attribute', 'attribute_modify_transfer', 'burn', 'climbing',
    'conditioned_attribute', 'conditioned_restrict_armor', 'conduit_power_on_land', 'cooldown',
    'creative_flight', 'damage_over_time', 'disable_regen', 'effect_immunity', 'elytra_flight',
    'entity_glow', 'entity_group', 'exhaust', 'fire_immunity', 'fire_projectile', 'freeze',
    'grounded', 'ignore_water', 'inventory', 'invisibility', 'invulnerability', 'item_on_item',
    'keep_inventory', 'launch', 'lava_vision', 'like_water', 'master_of_webs_no_slowdown',
    'model_color', 'modify_air_speed', 'modify_attribute', 'modify_behavior', 'modify_block_render',
    'modify_break_speed', 'modify_camera_submersion', 'modify_crafting', 'modify_damage_dealt',
    'modify_damage_taken', 'modify_exhaustion', 'modify_falling', 'modify_fluid_render', 'modify_food',
    'modify_grindstone', 'modify_harvest', 'modify_healing', 'modify_insomnia_ticks', 'modify_jump',
    'modify_lava_speed', 'modify_player_spawn', 'modify_projectile_damage', 'modify_slipperiness',
    'modify_status_effect_amplifier', 'modify_status_effect_duration', 'modify_swim_speed',
    'modify_velocity', 'modify_xp_gain', 'multiple', 'night_vision', 'no_cobweb_slowdown', 'overlay',
    'particle', 'phasing', 'player_ability', 'prevent_being_used', 'prevent_block_selection',
    'prevent_block_use', 'prevent_death', 'prevent_elytra_flight', 'prevent_entity_collision',
    'prevent_entity_render', 'prevent_entity_use', 'prevent_feature_render', 'prevent_game_event',
    'prevent_item_use', 'prevent_sleep', 'prevent_sprinting', 'recipe', 'replace_loot_table',
    'resource', 'restrict_armor', 'scare_creepers', 'self_action_on_hit', 'self_action_on_kill',
    'self_action_when_hit', 'self_glow', 'shader', 'shaking', 'simple', 'stacking_status_effect',
    'starting_equipment', 'status_bar_texture', 'swimming', 'target_action_on_hit',
    'toggle', 'toggle_night_vision', 'tooltip', 'walk_on_fluid', 'water_breathing', 'water_vision',
  ]);

  const SCALE_POWER_TYPES = new Set([
    'extraorigins:modify_size',
    'pehkui:modify_size',
    'pehkui:scale',
    'origins:modify_size',
    'apoli:modify_size',
    'origins:scale',
  ]);

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

  const PREFIXED_VANILLA_ATTRS = new Set([
    'generic.armor', 'generic.armor_toughness', 'generic.attack_damage', 'generic.attack_knockback',
    'generic.attack_speed', 'generic.burning_time', 'generic.explosion_knockback_resistance',
    'generic.fall_damage_multiplier', 'generic.flying_speed', 'generic.follow_range', 'generic.gravity',
    'generic.jump_strength', 'generic.knockback_resistance', 'generic.luck', 'generic.max_absorption',
    'generic.max_health', 'generic.movement_efficiency', 'generic.movement_speed', 'generic.oxygen_bonus',
    'generic.safe_fall_distance', 'generic.scale', 'generic.step_height', 'generic.water_movement_efficiency',
    'player.block_break_speed', 'player.block_interaction_range', 'player.entity_interaction_range',
    'player.mining_efficiency', 'player.sneaking_speed', 'player.submerged_mining_speed',
    'player.sweeping_damage_ratio', 'zombie.spawn_reinforcements',
  ]);

  const CODE_MARKERS = [
    'PowerFactory', 'ConditionFactory', 'ActionFactory', 'org/spongepowered/asm/mixin',
    'net/fabricmc/fabric/api', 'net/minecraft/world/item/Item', 'net/minecraft/world/level/block/Block',
    'net/minecraft/world/entity/EntityType', 'BuiltInRegistries', 'ServerPlayNetworking',
    'ClientPlayNetworking', 'KeyMapping',
  ];

  // Build a valid Java 21 (major=65) JVM classfile with @net.neoforged.fml.common.Mod("<modId>")
  function buildModStubClass(internalClassName, modId) {
    const encoder = new TextEncoder();
    const out = [];

    const u1 = (v) => out.push(v & 0xff);
    const u2 = (v) => out.push((v >>> 8) & 0xff, v & 0xff);
    const u4 = (v) => out.push((v >>> 24) & 0xff, (v >>> 16) & 0xff, (v >>> 8) & 0xff, v & 0xff);
    const utf8Entry = (str) => {
      const bytes = encoder.encode(str);
      u1(1);
      u2(bytes.length);
      for (let i = 0; i < bytes.length; i++) u1(bytes[i]);
    };

    // Magic + minor(0) + major(65 = Java 21, accepted by 1.21.1 - 26.3)
    u4(0xcafebabe);
    u2(0);
    u2(65);

    // Constant pool: 16 entries => count = 17
    u2(17);
    u1(10); u2(2); u2(3);          // #1 Methodref java/lang/Object.<init>:()V
    u1(7);  u2(4);                 // #2 Class java/lang/Object
    u1(12); u2(5); u2(6);          // #3 NameAndType <init>:()V
    utf8Entry('java/lang/Object'); // #4
    utf8Entry('<init>');           // #5
    utf8Entry('()V');              // #6
    u1(7);  u2(8);                 // #7 Class <internalClassName>
    utf8Entry(internalClassName);  // #8
    utf8Entry('Code');             // #9
    utf8Entry('SourceFile');       // #10
    utf8Entry('AddonMod.java');    // #11
    utf8Entry('RuntimeVisibleAnnotations');       // #12
    utf8Entry('Lnet/neoforged/fml/common/Mod;');  // #13
    utf8Entry('value');            // #14
    utf8Entry(modId);              // #15
    utf8Entry('()');               // #16 unused slot? No, let's keep 16 entries total => count 16!

    // Rewrite cleanly with exact 15 entries (count = 16) without #16 dummy
    out.length = 0;
    u4(0xcafebabe);
    u2(0);
    u2(65);
    u2(16);                        // constant_pool_count = 16 (#1..#15)
    u1(10); u2(2); u2(3);          // #1 Methodref
    u1(7);  u2(4);                 // #2 Class java/lang/Object
    u1(12); u2(5); u2(6);          // #3 NameAndType <init>:()V
    utf8Entry('java/lang/Object'); // #4
    utf8Entry('<init>');           // #5
    utf8Entry('()V');              // #6
    u1(7);  u2(8);                 // #7 Class this_class
    utf8Entry(internalClassName);  // #8
    utf8Entry('Code');             // #9
    utf8Entry('SourceFile');       // #10
    utf8Entry('AddonMod.java');    // #11
    utf8Entry('RuntimeVisibleAnnotations');      // #12
    utf8Entry('Lnet/neoforged/fml/common/Mod;'); // #13
    utf8Entry('value');            // #14
    utf8Entry(modId);              // #15

    // Class header
    u2(0x0021); // ACC_PUBLIC | ACC_SUPER
    u2(7);      // this_class
    u2(2);      // super_class
    u2(0);      // interfaces_count
    u2(0);      // fields_count

    // Methods: public AddonMod() { super(); }
    u2(1);      // methods_count
    u2(0x0001); // ACC_PUBLIC
    u2(5);      // name <init>
    u2(6);      // descriptor ()V
    u2(1);      // attributes_count
    u2(9);      // Code
    u4(17);     // attribute_length (2+2+4+5+2+2)
    u2(1);      // max_stack
    u2(1);      // max_locals
    u4(5);      // code_length
    u1(0x2a);   // aload_0
    u1(0xb7); u2(1); // invokespecial #1
    u1(0xb1);   // return
    u2(0);      // exception_table_length
    u2(0);      // code attributes_count

    // Class attributes (2): SourceFile + RuntimeVisibleAnnotations
    u2(2);
    u2(10); u4(2); u2(11); // SourceFile: AddonMod.java

    u2(12); u4(11); // RuntimeVisibleAnnotations (length 11 bytes)
    u2(1);          // num_annotations = 1
    u2(13);         // type_index = Lnet/neoforged/fml/common/Mod;
    u2(1);          // num_element_value_pairs = 1
    u2(14);         // element_name_index = "value"
    u1(0x73);       // tag 's' (String)
    u2(15);         // const_value_index = <modId>

    return new Uint8Array(out);
  }

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
        case 3: case 4: offset += 4; break;
        case 5: case 6: offset += 8; i++; break;
        case 7: case 8: case 16: case 19: case 20: offset += 2; break;
        case 9: case 10: case 11: case 12: case 17: case 18: offset += 4; break;
        case 15: offset += 3; break;
        default: return strings;
      }
    }
    return strings;
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

  function parseFabricJson(raw) {
    try {
      return JSON.parse(raw);
    } catch {
      const stripped = raw
        .replace(/^\uFEFF/, '')
        .replace(/,\s*([}\]])/g, '$1');
      return JSON.parse(stripped);
    }
  }

  function shouldStripPath(path) {
    const lower = path.toLowerCase();
    if (lower === 'fabric.mod.json' || lower === 'quilt.mod.json' || lower === 'pack.mcmeta') return true;
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

  function mapAttributeId(attr, target, stats) {
    if (typeof attr !== 'string') return attr;
    let next = attr;

    if (next === 'reach-entity-attributes:reach') {
      next = target.legacyPrefixAttrs ? 'minecraft:player.block_interaction_range' : 'minecraft:block_interaction_range';
      stats.reaMapped++;
      return next;
    }
    if (next === 'reach-entity-attributes:attack_range') {
      next = target.legacyPrefixAttrs ? 'minecraft:player.entity_interaction_range' : 'minecraft:entity_interaction_range';
      stats.reaMapped++;
      return next;
    }
    if (
      next === 'step-height-entity-attribute:step_height' ||
      next === 'forge:step_height_addition' ||
      next === 'neoforge:step_height' ||
      next === 'porting_lib:step_height_addition'
    ) {
      next = target.legacyPrefixAttrs ? 'minecraft:generic.step_height' : 'minecraft:step_height';
      stats.attrsMapped++;
      return next;
    }
    if (!target.legacyPrefixAttrs && next.startsWith('minecraft:')) {
      const sub = next.slice('minecraft:'.length);
      if (PREFIXED_VANILLA_ATTRS.has(sub)) {
        next = 'minecraft:' + sub.replace(/^(generic|player|zombie)\./, '');
        stats.attrsMapped++;
      }
    }
    return next;
  }

  function convertScalePowerNode(node, target, keyHint, stats) {
    if (!node || typeof node !== 'object' || typeof node.type !== 'string') return node;
    if (!SCALE_POWER_TYPES.has(node.type)) return node;

    const rawScale = typeof node.scale === 'number'
      ? node.scale
      : typeof node.value === 'number'
        ? node.value
        : null;
    if (rawScale === null) return node;

    const delta = rawScale > 0 ? Number((rawScale - 1).toFixed(4)) : Number(rawScale.toFixed(4));
    const scaleAttr = target.legacyPrefixAttrs ? 'minecraft:generic.scale' : 'minecraft:scale';
    const scaleTypes = Array.isArray(node.scale_types) ? node.scale_types.map(String) : [];

    const modifiers = [{
      name: `Converted scale (${keyHint})`,
      attribute: scaleAttr,
      value: delta,
      operation: 'multiply_total',
    }];

    if (scaleTypes.some((t) => t.includes('reach') || t.includes('block_reach'))) {
      modifiers.push({
        name: `Converted block reach (${keyHint})`,
        attribute: target.legacyPrefixAttrs ? 'minecraft:player.block_interaction_range' : 'minecraft:block_interaction_range',
        value: delta,
        operation: 'multiply_total',
      });
    }
    if (scaleTypes.some((t) => t.includes('entity_reach') || t.includes('attack_range'))) {
      modifiers.push({
        name: `Converted entity reach (${keyHint})`,
        attribute: target.legacyPrefixAttrs ? 'minecraft:player.entity_interaction_range' : 'minecraft:entity_interaction_range',
        value: delta,
        operation: 'multiply_total',
      });
    }

    const out = {
      type: node.condition ? 'origins:conditioned_attribute' : 'origins:attribute',
    };
    if (node.name !== undefined) out.name = node.name;
    if (node.description !== undefined) out.description = node.description;
    if (node.hidden !== undefined) out.hidden = node.hidden;
    if (node.badges !== undefined) out.badges = node.badges;
    if (node.condition !== undefined) out.condition = node.condition;
    if (modifiers.length === 1) out.modifier = modifiers[0];
    else out.modifiers = modifiers;

    stats.scalePowersConverted++;
    return out;
  }

  // Lightweight SNBT -> Data Components converter for common 1.20.1 item "tag" strings
  function tryConvertItemNbtTag(tagStr, target) {
    if (typeof tagStr !== 'string') return null;
    const s = tagStr.trim();
    if (!s.startsWith('{') || !s.endsWith('}')) return null;

    const components = {};
    let recognized = false;
    let remaining = s.slice(1, -1).trim();

    const potionMatch = /(?:^|,)\s*Potion\s*:\s*"([^"]+)"/.exec(remaining);
    if (potionMatch) {
      components['minecraft:potion_contents'] = { potion: potionMatch[1] };
      remaining = remaining.replace(potionMatch[0], '');
      recognized = true;
    }

    const cmdMatch = /(?:^|,)\s*CustomModelData\s*:\s*(-?\d+)/.exec(remaining);
    if (cmdMatch) {
      const num = Number(cmdMatch[1]);
      components['minecraft:custom_model_data'] = target.cmdCompound ? { floats: [num] } : num;
      remaining = remaining.replace(cmdMatch[0], '');
      recognized = true;
    }

    const unbreakMatch = /(?:^|,)\s*Unbreakable\s*:\s*(1b?|true)/i.exec(remaining);
    if (unbreakMatch) {
      components['minecraft:unbreakable'] = {};
      remaining = remaining.replace(unbreakMatch[0], '');
      recognized = true;
    }

    const enchMatch = /(?:^|,)\s*(?:StoredEnchantments|Enchantments)\s*:\s*\[([^\]]*)\]/.exec(remaining);
    if (enchMatch) {
      const levels = {};
      const itemRe = /\{\s*id\s*:\s*"([^"]+)"\s*,\s*lvl\s*:\s*(\d+)[bsilBSIL]?\s*\}/g;
      let m;
      while ((m = itemRe.exec(enchMatch[1])) !== null) {
        levels[m[1]] = Number(m[2]);
      }
      if (Object.keys(levels).length > 0) {
        const compKey = enchMatch[0].includes('StoredEnchantments')
          ? 'minecraft:stored_enchantments'
          : 'minecraft:enchantments';
        components[compKey] = { levels };
        remaining = remaining.replace(enchMatch[0], '');
        recognized = true;
      }
    }

    const nameMatch = /(?:^|,)\s*display\s*:\s*\{\s*Name\s*:\s*'([^']*)'\s*\}/.exec(remaining);
    if (nameMatch) {
      components['minecraft:custom_name'] = nameMatch[1];
      remaining = remaining.replace(nameMatch[0], '');
      recognized = true;
    }

    const leftover = remaining.replace(/[\s,]+/g, '');
    return { recognized, complete: leftover.length === 0, components };
  }

  function walkAndTransformJson(value, ctx, keyHint = 'root') {
    if (Array.isArray(value)) {
      return value.map((item, idx) => walkAndTransformJson(item, ctx, `${keyHint}_${idx}`));
    }
    if (!value || typeof value !== 'object') {
      return value;
    }

    let obj = { ...value };

    // Convert power type if in powers/ or embedded sub-power
    if (ctx.migratePowers && typeof obj.type === 'string' && ctx.isPowerFile) {
      if (SCALE_POWER_TYPES.has(obj.type)) {
        obj = convertScalePowerNode(obj, ctx.target, keyHint, ctx.stats);
      }
    }

    // Rewrite attributes inside modifier objects
    if (ctx.migratePowers && typeof obj.attribute === 'string') {
      obj.attribute = mapAttributeId(obj.attribute, ctx.target, ctx.stats);
    }

    // Upgrade 1.20.1 recipe "result" schema
    if (ctx.migrateItems && ctx.isRecipeFile && keyHint === 'result') {
      if (typeof obj.item === 'string' && !obj.id) {
        obj.id = obj.item;
        delete obj.item;
        ctx.stats.recipesUpdated++;
      }
    }

    // Upgrade item { item: "...", tag: "{...}" } -> { ..., components: {...} }
    if (ctx.migrateItems && typeof obj.tag === 'string' && (typeof obj.item === 'string' || typeof obj.id === 'string')) {
      const conv = tryConvertItemNbtTag(obj.tag, ctx.target);
      if (conv && conv.recognized) {
        obj.components = { ...(obj.components || {}), ...conv.components };
        if (conv.complete) {
          delete obj.tag;
          ctx.stats.tagsConverted++;
        } else {
          ctx.warnings.push(`${ctx.relPath}: partially converted item "tag" NBT (extra SNBT fields kept)`);
        }
      } else {
        ctx.warnings.push(`${ctx.relPath}: item uses legacy "tag" NBT (${obj.tag.slice(0, 48)})`);
      }
    }

    for (const key of Object.keys(obj)) {
      if (key === 'result' && ctx.migrateItems && ctx.isRecipeFile && typeof obj[key] === 'string') {
        obj[key] = { id: obj[key] };
        ctx.stats.recipesUpdated++;
        continue;
      }
      obj[key] = walkAndTransformJson(obj[key], ctx, key);
    }

    return obj;
  }

  function inspectPowerTypesInJson(obj, relPath, analysis) {
    if (!obj || typeof obj !== 'object' || Array.isArray(obj)) return;

    if (typeof obj.type === 'string') {
      const id = obj.type;
      const [ns, name] = id.includes(':') ? id.split(':', 2) : ['minecraft', id];
      if ((ns === 'origins' || ns === 'apoli') && KNOWN_APOLI_POWERS.has(name)) {
        analysis.supportedPowers.add(id);
      } else if (SCALE_POWER_TYPES.has(id)) {
        analysis.autoConvertiblePowers.add(id);
      } else {
        if (!analysis.unsupportedPowers.has(id)) analysis.unsupportedPowers.set(id, []);
        analysis.unsupportedPowers.get(id).push(relPath);
      }

      if (name === 'multiple') {
        for (const [k, v] of Object.entries(obj)) {
          if (['type', 'name', 'description', 'hidden', 'badges', 'loading_priority'].includes(k)) continue;
          if (v && typeof v === 'object' && typeof v.type === 'string') {
            inspectPowerTypesInJson(v, `${relPath}#${k}`, analysis);
          }
        }
      }
    }
  }

  function buildModsToml(meta, target, jarEntries) {
    const modId = sanitizeModId(meta.id);
    const version = (meta.version || '1.0.0').replace(/\+.*$/, '');
    const displayName = meta.name || modId;
    const license = extractLicense(meta.license);
    const authors = extractAuthors(meta.authors);
    const baseDescription = typeof meta.description === 'string' ? meta.description.trim() : '';
    const convertedNotice = `[Converted from Fabric 1.20.1 to NeoForge ${target.mc} via Origins Addon Converter. For personal use only — do not publish or redistribute on any platform.]`;
    const description = baseDescription ? `${baseDescription}\n\n${convertedNotice}` : convertedNotice;

    let iconPath = '';
    if (typeof meta.icon === 'string') {
      iconPath = meta.icon.replace(/^\/+/, '');
    } else if (meta.icon && typeof meta.icon === 'object') {
      const candidates = Object.values(meta.icon).filter((v) => typeof v === 'string');
      if (candidates.length > 0) iconPath = candidates[candidates.length - 1].replace(/^\/+/, '');
    }
    if (!iconPath || !jarEntries.has(iconPath)) {
      iconPath = jarEntries.has(`assets/${meta.id}/icon.png`) ? `assets/${meta.id}/icon.png` : '';
    }

    const lines = [
      'modLoader="javafml"',
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

  // DOM wiring (only active inside browser)
  if (typeof document !== 'undefined') {
    const dropzone = document.getElementById('dropzone');
    const fileInput = document.getElementById('fileInput');
    const dropTitle = document.getElementById('dropTitle');
    const dropHint = document.getElementById('dropHint');
    const targetSelect = document.getElementById('targetVersion');
    const optRenameFolders = document.getElementById('optRenameFolders');
    const optMigratePowers = document.getElementById('optMigratePowers');
    const optMigrateItems = document.getElementById('optMigrateItems');
    const convertBtn = document.getElementById('convertBtn');
    const reportEl = document.getElementById('report');

    let loadedZip = null;
    let loadedMeta = null;

    for (const v of TARGET_VERSIONS) {
      const opt = document.createElement('option');
      opt.value = v.mc;
      opt.textContent = `Minecraft ${v.mc} (NeoForge ${v.neo})`;
      if (v.mc === '1.21.11') opt.selected = true;
      targetSelect.appendChild(opt);
    }

    const escapeHtml = (str) => String(str)
      .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');

    const getSelectedTarget = () =>
      TARGET_VERSIONS.find((v) => v.mc === targetSelect.value) || TARGET_VERSIONS[0];

    const renderReport = (title, items) => {
      reportEl.classList.remove('hidden');
      const rows = items.map((item) => `<li class="tag-${item.level}">${escapeHtml(item.text)}</li>`).join('');
      reportEl.innerHTML = `<div class="report-head">${escapeHtml(title)}</div><ul class="report-list">${rows}</ul>`;
    };

    async function handleFile(file) {
      if (!file) return;
      if (!window.JSZip) {
        renderReport('ERROR', [{ level: 'bad', text: 'JSZip failed to load. Check internet connection and reload.' }]);
        return;
      }

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
          renderReport('INVALID FILE', [{ level: 'bad', text: 'Could not find fabric.mod.json inside this archive.' }]);
          return;
        }

        const meta = parseFabricJson(await fabricJsonFile.async('string'));
        loadedZip = zip;
        loadedMeta = meta;
        dropzone.classList.add('loaded');
        dropTitle.textContent = `${meta.name || meta.id || file.name} (${meta.version || '?'})`;
        dropHint.textContent = `Fabric ID: ${meta.id || 'unknown'} — ready to convert`;
        convertBtn.disabled = false;

        const classFiles = Object.keys(zip.files).filter((p) => !zip.files[p].dir && p.endsWith('.class'));
        const codeHits = [];
        for (const cls of classFiles) {
          const strs = extractClassStrings(await zip.file(cls).async('uint8array'));
          const hit = CODE_MARKERS.find((m) => strs.some((s) => s.includes(m)));
          if (hit) codeHits.push(`${cls} (${hit})`);
        }

        const analysis = {
          supportedPowers: new Set(),
          autoConvertiblePowers: new Set(),
          unsupportedPowers: new Map(),
        };
        const powerPaths = Object.keys(zip.files).filter(
          (p) => !zip.files[p].dir && /^data\/[^/]+\/powers\/.+\.json$/i.test(p)
        );
        for (const p of powerPaths) {
          try {
            const parsed = JSON.parse(await zip.file(p).async('string'));
            inspectPowerTypesInJson(parsed, p, analysis);
          } catch { /* reported during convert if malformed */ }
        }

        const items = [
          { level: 'ok', text: `Detected Fabric mod "${meta.name || meta.id}" (${meta.id} v${meta.version || '?'})` },
        ];
        if (powerPaths.length > 0) {
          items.push({
            level: 'ok',
            text: `Scanned ${powerPaths.length} power JSON(s): ${analysis.supportedPowers.size} built-in Apoli type(s).`,
          });
        }
        if (analysis.autoConvertiblePowers.size > 0) {
          items.push({
            level: 'ok',
            text: `Will auto-convert custom scale power(s): ${[...analysis.autoConvertiblePowers].join(', ')} -> minecraft:scale`,
          });
        }
        if (analysis.unsupportedPowers.size > 0) {
          const list = [...analysis.unsupportedPowers.keys()].slice(0, 5).join(', ');
          items.push({
            level: 'warn',
            text: `${analysis.unsupportedPowers.size} Java-only power type(s) have no Apoli equivalent (${list}) and will be skipped in-game.`,
          });
        }
        if (codeHits.length > 0) {
          items.push({
            level: 'warn',
            text: `Archive has ${codeHits.length} custom Java class(es) (stripped; replaced with javafml @Mod stub).`,
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
      const migratePowers = optMigratePowers.checked;
      const migrateItems = optMigrateItems.checked;

      try {
        const outZip = new window.JSZip();
        const keptPaths = new Set();
        const stats = {
          strippedCount: 0,
          folderRenames: 0,
          scalePowersConverted: 0,
          reaMapped: 0,
          attrsMapped: 0,
          tagsConverted: 0,
          recipesUpdated: 0,
          itemModelsGenerated: 0,
        };
        const warnings = [];
        const analysis = {
          supportedPowers: new Set(),
          autoConvertiblePowers: new Set(),
          unsupportedPowers: new Map(),
        };

        for (const [rawPath, entry] of Object.entries(loadedZip.files)) {
          if (entry.dir) continue;

          if (shouldStripPath(rawPath)) {
            stats.strippedCount++;
            continue;
          }

          if (rawPath === 'META-INF/MANIFEST.MF') {
            outZip.file('META-INF/MANIFEST.MF', 'Manifest-Version: 1.0\n');
            keptPaths.add('META-INF/MANIFEST.MF');
            continue;
          }

          const newPath = migrateDataPath(rawPath, renameFolders);
          if (newPath !== rawPath) stats.folderRenames++;
          keptPaths.add(newPath);

          if (newPath.toLowerCase().endsWith('.json')) {
            const rawText = await entry.async('string');
            try {
              const parsed = JSON.parse(rawText);
              const isPowerFile = /^data\/[^/]+\/powers\/.+\.json$/i.test(newPath);
              const isRecipeFile = /^data\/[^/]+\/(recipe|recipes)\/.+\.json$/i.test(newPath);
              const transformed = walkAndTransformJson(parsed, {
                target,
                migratePowers,
                migrateItems,
                isPowerFile,
                isRecipeFile,
                relPath: newPath,
                stats,
                warnings,
              });
              if (isPowerFile) inspectPowerTypesInJson(transformed, newPath, analysis);
              outZip.file(newPath, JSON.stringify(transformed, null, 2));
            } catch {
              outZip.file(newPath, rawText);
              if (/^data\//i.test(newPath)) warnings.push(`${newPath}: skipped JSON AST transform (non-standard JSON)`);
            }
          } else {
            outZip.file(newPath, await entry.async('uint8array'));
          }
        }

        // Auto-generate 1.21.4+ client item model definitions if missing
        if (migrateItems && target.itemModelDef) {
          for (const p of [...keptPaths]) {
            const m = /^assets\/([^/]+)\/models\/item\/(.+)\.json$/i.exec(p);
            if (!m) continue;
            const itemDefPath = `assets/${m[1]}/items/${m[2]}.json`;
            if (!keptPaths.has(itemDefPath)) {
              outZip.file(itemDefPath, JSON.stringify({
                model: { type: 'minecraft:model', model: `${m[1]}:item/${m[2]}` },
              }, null, 2));
              keptPaths.add(itemDefPath);
              stats.itemModelsGenerated++;
            }
          }
        }

        const { modId, toml } = buildModsToml(loadedMeta, target, keptPaths);
        const classInternalName = `org/originsconverter/generated/${modId}/AddonMod`;
        outZip.file(`${classInternalName}.class`, buildModStubClass(classInternalName, modId));
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
          { level: 'ok', text: `Downloaded ${outName} (modId="${modId}", javafml @Mod generated)` },
          { level: 'ok', text: `Stripped ${stats.strippedCount} Fabric metadata/class file(s).` },
        ];
        if (stats.folderRenames > 0) summary.push({ level: 'ok', text: `Migrated ${stats.folderRenames} 1.20.1 data folder path(s).` });
        if (stats.scalePowersConverted > 0) summary.push({ level: 'ok', text: `Converted ${stats.scalePowersConverted} modify_size/scale power(s) -> vanilla scale attribute.` });
        if (stats.reaMapped > 0) summary.push({ level: 'ok', text: `Normalized ${stats.reaMapped} Reach Entity Attributes reference(s).` });
        if (stats.attrsMapped > 0) summary.push({ level: 'ok', text: `Updated ${stats.attrsMapped} vanilla/step attribute ID(s).` });
        if (stats.tagsConverted > 0) summary.push({ level: 'ok', text: `Converted ${stats.tagsConverted} item NBT "tag" object(s) -> "components".` });
        if (stats.recipesUpdated > 0) summary.push({ level: 'ok', text: `Updated ${stats.recipesUpdated} recipe result schema(s) to 1.21+ format.` });
        if (stats.itemModelsGenerated > 0) summary.push({ level: 'ok', text: `Generated ${stats.itemModelsGenerated} 1.21.4+ client item model definition(s).` });
        if (analysis.unsupportedPowers.size > 0) {
          summary.push({
            level: 'warn',
            text: `Unmapped custom Java power types (kept in JSON, skipped by Apoli): ${[...analysis.unsupportedPowers.keys()].join(', ')}`,
          });
        }
        for (const w of warnings.slice(0, 6)) summary.push({ level: 'warn', text: w });
        if (warnings.length > 6) summary.push({ level: 'warn', text: `...and ${warnings.length - 6} more warning(s).` });

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
    dropzone.addEventListener('dragover', (e) => { e.preventDefault(); dropzone.classList.add('dragover'); });
    dropzone.addEventListener('dragleave', () => dropzone.classList.remove('dragover'));
    dropzone.addEventListener('drop', (e) => {
      e.preventDefault();
      dropzone.classList.remove('dragover');
      if (e.dataTransfer && e.dataTransfer.files && e.dataTransfer.files[0]) handleFile(e.dataTransfer.files[0]);
    });
    convertBtn.addEventListener('click', convertAddon);
  }

  // Export helpers for Node unit checks
  if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
      TARGET_VERSIONS,
      buildModStubClass,
      walkAndTransformJson,
      migrateDataPath,
      buildModsToml,
      inspectPowerTypesInJson,
      sanitizeModId,
    };
  }
})();
