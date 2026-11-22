(() => {
  'use strict';

  // Supported NeoForge branches in azubangithub/Origins-Legacy-NeoForged
  const TARGET_VERSIONS = [
    { mc: '1.21.1',  neo: '21.1.234',       mcRange: '[1.21.1]',  legacyPrefixAttrs: true,  itemModelDef: false, cmdCompound: false, modernRecipes: false, tooltipDisplay: false },
    { mc: '1.21.4',  neo: '21.4.161',       mcRange: '[1.21.4]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: false },
    { mc: '1.21.5',  neo: '21.5.101',       mcRange: '[1.21.5]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: true  },
    { mc: '1.21.6',  neo: '21.6.20-beta',   mcRange: '[1.21.6]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: true  },
    { mc: '1.21.7',  neo: '21.7.25-beta',   mcRange: '[1.21.7]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: true  },
    { mc: '1.21.8',  neo: '21.8.57',        mcRange: '[1.21.8]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: true  },
    { mc: '1.21.11', neo: '21.11.48',       mcRange: '[1.21.11]', legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: true  },
    { mc: '26.1',    neo: '26.1.0.19-beta', mcRange: '[26.1]',    legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: true  },
    { mc: '26.1.1',  neo: '26.1.1.15-beta', mcRange: '[26.1.1]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: true  },
    { mc: '26.1.2',  neo: '26.1.2.126',     mcRange: '[26.1.2]',  legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: true  },
    { mc: '26.2',    neo: '26.2.0.93',      mcRange: '[26.2]',    legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: true  },
    { mc: '26.3',    neo: '26.3.0.101',     mcRange: '[26.3]',    legacyPrefixAttrs: false, itemModelDef: true,  cmdCompound: true,  modernRecipes: true,  tooltipDisplay: true  },
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
    'model_color', 'modify_air_speed', 'modify_attribute', 'modify_block_render',
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
  const EXACT_EXTRA_POWERS = new Set(['apoli_legacy:modify_behavior']);

  // Registered Apoli/Origins condition & action IDs from Origins-Legacy-NeoForged
  const CONDITION_REGISTRIES = {
    entity: new Set([
      'advancement', 'air', 'and', 'attribute', 'biome', 'block_collision', 'block_in_radius',
      'brightness', 'climbing', 'collided_horizontally', 'command', 'constant', 'creative_flying',
      'daytime', 'dimension', 'elytra_flight_possible', 'enchantment', 'entity_group', 'entity_type',
      'equipped_item', 'exists', 'exposed_to_sky', 'exposed_to_sun', 'fall_distance', 'fall_flying',
      'fluid_height', 'food_level', 'gamemode', 'glowing', 'health', 'in_block', 'in_block_anywhere',
      'in_rain', 'in_tag', 'inventory', 'invisible', 'living', 'moving', 'nbt', 'on_block', 'on_fire',
      'or', 'origin', 'passenger', 'passenger_recursive', 'power', 'power_active', 'power_type',
      'predicate', 'raycast', 'relative_health', 'resource', 'riding', 'riding_recursive',
      'riding_root', 'saturation_level', 'scoreboard', 'sneaking', 'sprinting', 'status_effect',
      'submerged_in', 'swimming', 'tamed', 'time_of_day', 'using_effective_tool', 'using_item',
      'xp_levels', 'xp_points', 'apoli_legacy:distance_to_ground',
    ]),
    bientity: new Set([
      'actor_condition', 'and', 'attack_target', 'attacker', 'both', 'can_see', 'constant',
      'distance', 'either', 'invert', 'or', 'owner', 'relative_rotation', 'riding',
      'riding_recursive', 'riding_root', 'target_condition', 'undirected',
    ]),
    block: new Set([
      'adjacent', 'and', 'attachable', 'blast_resistance', 'block', 'block_entity', 'block_state',
      'constant', 'exposed_to_sky', 'fluid', 'hardness', 'height', 'in_tag', 'light_blocking',
      'light_level', 'material', 'movement_blocking', 'nbt', 'offset', 'or', 'replacable',
      'slipperiness', 'water_loggable',
    ]),
    biome: new Set([
      'and', 'category', 'constant', 'high_humidity', 'in_tag', 'or', 'precipitation', 'temperature',
    ]),
    damage: new Set([
      'amount', 'and', 'attacker', 'bypasses_armor', 'constant', 'explosive', 'fire', 'from_falling',
      'in_tag', 'name', 'or', 'out_of_world', 'projectile', 'unblockable',
    ]),
    fluid: new Set(['and', 'constant', 'empty', 'fluid', 'in_tag', 'or', 'still']),
    item: new Set([
      'amount', 'and', 'armor_value', 'constant', 'durability', 'empty', 'enchantable', 'enchantment',
      'fireproof', 'food', 'harvest_level', 'has_power', 'ingredient', 'is_damageable', 'is_equippable',
      'meat', 'nbt', 'or', 'power_count', 'relative_durability', 'smeltable',
    ]),
  };

  const ACTION_REGISTRIES = {
    entity: new Set([
      'add_velocity', 'add_xp', 'apply_effect', 'area_of_effect', 'block_action_at', 'change_resource',
      'clear_effect', 'crafting_table', 'damage', 'dismount', 'drop_inventory', 'emit_game_event',
      'ender_chest', 'equipped_item_action', 'execute_command', 'exhaust', 'explode', 'extinguish',
      'feed', 'fire_projectile', 'gain_air', 'give', 'grant_advancement', 'grant_power', 'heal',
      'modify_death_ticks', 'modify_inventory', 'modify_resource', 'modify_stat', 'passenger_action',
      'play_sound', 'raycast', 'replace_inventory', 'revoke_advancement', 'revoke_power', 'riding_action',
      'selector_action', 'set_fall_distance', 'set_on_fire', 'set_resource', 'spawn_effect_cloud',
      'spawn_entity', 'spawn_particles', 'swing_hand', 'toggle', 'trigger_cooldown',
    ]),
    bientity: new Set(['actor_action', 'add_velocity', 'invert', 'mount', 'set_in_love', 'tame', 'target_action']),
    block: new Set(['add_block', 'area_of_effect', 'bonemeal', 'execute_command', 'explode', 'modify_block_state', 'offset', 'set_block']),
    item: new Set(['consume', 'damage', 'holder', 'merge_nbt', 'modify', 'remove_enchantment']),
  };

  const KEY_TO_DIAGNOSTIC_DOMAIN = {
    condition: 'entity_cond',
    entity_condition: 'entity_cond',
    actor_condition: 'entity_cond',
    target_condition: 'entity_cond',
    bientity_condition: 'bientity_cond',
    block_condition: 'block_cond',
    biome_condition: 'biome_cond',
    damage_condition: 'damage_cond',
    fluid_condition: 'fluid_cond',
    item_condition: 'item_cond',
    held_item_condition: 'item_cond',
    result_item_condition: 'item_cond',
    entity_action: 'entity_act',
    action: 'entity_act',
    entity_action_added: 'entity_act',
    entity_action_chosen: 'entity_act',
    entity_action_gained: 'entity_act',
    entity_action_lost: 'entity_act',
    entity_action_removed: 'entity_act',
    entity_action_respawned: 'entity_act',
    entity_action_on_hit: 'entity_act',
    self_action: 'entity_act',
    attacker_action: 'entity_act',
    actor_action: 'entity_act',
    target_action: 'entity_act',
    hit_action: 'entity_act',
    miss_action: 'entity_act',
    bientity_action: 'bientity_act',
    block_action: 'block_act',
    item_action: 'item_act',
    held_item_action: 'item_act',
    result_item_action: 'item_act',
    before_action: 'entity_act',
    after_action: 'entity_act',
  };

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
    ['predicates/', 'predicate/'],
    ['item_modifiers/', 'item_modifier/'],
    ['functions/', 'function/'],
    ['tags/blocks/', 'tags/block/'],
    ['tags/items/', 'tags/item/'],
    ['tags/entity_types/', 'tags/entity_type/'],
    ['tags/fluids/', 'tags/fluid/'],
    ['tags/game_events/', 'tags/game_event/'],
    ['tags/functions/', 'tags/function/'],
    ['tags/structures/', 'tags/structure/'],
    ['tags/damage_types/', 'tags/damage_type/'],
    ['tags/worldgen/biomes/', 'tags/worldgen/biome/'],
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

  // Vanilla 1.20.1 -> 1.21+ item/block ID renames
  const VANILLA_ID_RENAMES = {
    'minecraft:scute': 'minecraft:turtle_scute',
    'minecraft:grass': 'minecraft:short_grass',
  };

  // Legacy Forge common tag paths that migrated to c: in 1.21 NeoForge
  const FORGE_COMMON_TAG_PREFIXES = [
    'ingots', 'gems', 'ores', 'storage_blocks', 'dusts', 'nuggets',
    'rods', 'tools', 'armors', 'foods', 'raw_materials', 'crops', 'seeds',
  ];

  const LEGACY_TYPE_REMAPPINGS = {
    'origins:modify_behavior': 'apoli_legacy:modify_behavior',
    'apoli:modify_behavior': 'apoli_legacy:modify_behavior',
    'origins:distance_to_ground': 'apoli_legacy:distance_to_ground',
    'apoli:distance_to_ground': 'apoli_legacy:distance_to_ground',
    'apoli:origin': 'origins:origin',
    'extraorigins:modify_air_strafing_speed': 'origins:modify_air_speed',
  };

  const CONVERTIBLE_EXTRA_POWERS = new Set([
    ...SCALE_POWER_TYPES,
    'extraorigins:modify_air_strafing_speed',
    'extraorigins:mob_neutrality',
    'extraorigins:food_effect_immunity',
    'extraorigins:start_riding',
    'extraorigins:radial_menu',
  ]);

  const CONVERTIBLE_EXTRA_NODES = new Set([
    'extraorigins:piglin_safe',
    'extraorigins:block_action_looking_at',
    'extraorigins:radial_menu_direction',
  ]);

  const ACTIVE_POWER_SHORT_NAMES = new Set([
    'active_self', 'launch', 'fire_projectile', 'inventory', 'toggle', 'toggle_night_vision',
  ]);

  const VALID_KEYBIND_NAMES = new Set([
    'key.origins.primary_active', 'key.origins.secondary_active', 'key.origins.view_origin',
    'key.attack', 'key.use', 'key.forward', 'key.back', 'key.left', 'key.right', 'key.jump',
    'key.sneak', 'key.sprint', 'key.drop', 'key.inventory', 'key.swapOffhand', 'key.pickItem',
    'key.playerlist', 'key.chat', 'key.command', 'key.socialInteractions', 'key.advancements',
    'key.spectatorOutlines', 'key.screenshot', 'key.smoothCamera', 'key.fullscreen',
    'key.togglePerspective', 'key.loadToolbarActivator', 'key.saveToolbarActivator',
    'key.hotbar.1', 'key.hotbar.2', 'key.hotbar.3', 'key.hotbar.4', 'key.hotbar.5',
    'key.hotbar.6', 'key.hotbar.7', 'key.hotbar.8', 'key.hotbar.9',
  ]);

  const RADIAL_DIR_TO_INDEX = { up: 0, right: 1, down: 2, left: 3 };
  const RADIAL_INDEX_LABELS = ['UP', 'RIGHT', 'DOWN', 'LEFT'];

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

    u2(0x0021); // ACC_PUBLIC | ACC_SUPER
    u2(7);      // this_class
    u2(2);      // super_class
    u2(0);      // interfaces_count
    u2(0);      // fields_count

    u2(1);      // methods_count
    u2(0x0001); // ACC_PUBLIC
    u2(5);      // name <init>
    u2(6);      // descriptor ()V
    u2(1);      // attributes_count
    u2(9);      // Code
    u4(17);     // attribute_length
    u2(1);      // max_stack
    u2(1);      // max_locals
    u4(5);      // code_length
    u1(0x2a);   // aload_0
    u1(0xb7); u2(1); // invokespecial #1
    u1(0xb1);   // return
    u2(0);      // exception_table_length
    u2(0);      // code attributes_count

    u2(2);
    u2(10); u4(2); u2(11); // SourceFile

    u2(12); u4(11); // RuntimeVisibleAnnotations
    u2(1);
    u2(13);
    u2(1);
    u2(14);
    u1(0x73);
    u2(15);

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

  // Parses standard JSON and falls back cleanly on UTF-8 BOM / trailing commas / // comments outside strings
  function parseJsonLenient(raw) {
    try {
      return JSON.parse(raw);
    } catch {
      let cleaned = String(raw).replace(/^\uFEFF/, '');
      let out = '';
      let inStr = false;
      let escaped = false;
      for (let i = 0; i < cleaned.length; i++) {
        const ch = cleaned[i];
        const next = cleaned[i + 1];
        if (inStr) {
          out += ch;
          if (escaped) {
            escaped = false;
          } else if (ch === '\\') {
            escaped = true;
          } else if (ch === '"') {
            inStr = false;
          }
          continue;
        }
        if (ch === '"') {
          inStr = true;
          out += ch;
          continue;
        }
        if (ch === '/' && next === '/') {
          while (i < cleaned.length && cleaned[i] !== '\n') i++;
          out += '\n';
          continue;
        }
        if (ch === '/' && next === '*') {
          i += 2;
          while (i < cleaned.length && !(cleaned[i] === '*' && cleaned[i + 1] === '/')) i++;
          i++;
          continue;
        }
        out += ch;
      }
      out = out.replace(/,\s*([}\]])/g, '$1');
      return JSON.parse(out);
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

  function rewriteResourceString(str, keyHint, ctx) {
    if (typeof str !== 'string') return str;
    if (ctx.isLangFile || keyHint === 'name' || keyHint === 'description' || keyHint === 'text') {
      return str;
    }
    const isHashTag = str.startsWith('#');
    const bare = isHashTag ? str.slice(1) : str;

    if (Object.prototype.hasOwnProperty.call(VANILLA_ID_RENAMES, bare)) {
      ctx.stats.idsRenamed = (ctx.stats.idsRenamed || 0) + 1;
      return (isHashTag ? '#' : '') + VANILLA_ID_RENAMES[bare];
    }
    if (bare.startsWith('forge:')) {
      const sub = bare.slice('forge:'.length);
      if (FORGE_COMMON_TAG_PREFIXES.some((p) => sub === p || sub.startsWith(`${p}/`))) {
        ctx.stats.idsRenamed = (ctx.stats.idsRenamed || 0) + 1;
        return `${isHashTag ? '#' : ''}c:${sub}`;
      }
    }
    return str;
  }

  function mapAttributeId(attr, target, stats) {
    if (typeof attr !== 'string') return attr;
    let next = attr;

    if (next === 'reach-entity-attributes:reach' || next === 'forge:reach_distance' || next === 'forge:block_reach') {
      next = target.legacyPrefixAttrs ? 'minecraft:player.block_interaction_range' : 'minecraft:block_interaction_range';
      stats.reaMapped++;
      return next;
    }
    if (next === 'reach-entity-attributes:attack_range' || next === 'forge:attack_range' || next === 'forge:entity_reach') {
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
    if (next === 'forge:entity_gravity') {
      next = target.legacyPrefixAttrs ? 'minecraft:generic.gravity' : 'minecraft:gravity';
      stats.attrsMapped++;
      return next;
    }
    if (next === 'forge:swim_speed') {
      stats.attrsMapped++;
      return 'neoforge:swim_speed';
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
    const pref = (name, player = false) =>
      target.legacyPrefixAttrs ? `minecraft:${player ? 'player' : 'generic'}.${name}` : `minecraft:${name}`;
    const scaleTypes = Array.isArray(node.scale_types) ? node.scale_types.map(String) : [];

    const modifiers = [{
      name: `Converted scale (${keyHint})`,
      attribute: pref('scale'),
      value: delta,
      operation: 'multiply_total',
    }];

    const addScaleTypeAttr = (attrName, player, label) => {
      modifiers.push({
        name: `Converted ${label} (${keyHint})`,
        attribute: pref(attrName, player),
        value: delta,
        operation: 'multiply_total',
      });
    };

    if (scaleTypes.some((t) => t.includes('reach') || t.includes('block_reach'))) {
      addScaleTypeAttr('block_interaction_range', true, 'block reach');
    }
    if (scaleTypes.some((t) => t.includes('entity_reach') || t.includes('attack_range'))) {
      addScaleTypeAttr('entity_interaction_range', true, 'entity reach');
    }
    if (scaleTypes.some((t) => t.includes('step_height'))) {
      addScaleTypeAttr('step_height', false, 'step height');
    }
    if (scaleTypes.some((t) => t.includes('jump_height'))) {
      addScaleTypeAttr('jump_strength', false, 'jump strength');
    }
    if (scaleTypes.some((t) => t.includes('motion'))) {
      addScaleTypeAttr('movement_speed', false, 'movement speed');
    }

    const out = {
      type: node.condition ? 'origins:conditioned_attribute' : 'origins:attribute',
    };
    for (const k of ['name', 'description', 'hidden', 'badges', 'condition', 'loading_priority']) {
      if (node[k] !== undefined) out[k] = node[k];
    }
    if (modifiers.length === 1) out.modifier = modifiers[0];
    else out.modifiers = modifiers;

    stats.scalePowersConverted++;
    return out;
  }

  // Recursive-descent SNBT parser -> plain JS values
  function parseSnbt(raw) {
    if (typeof raw !== 'string') return null;
    const s = raw.trim();
    let i = 0;

    const skipWs = () => {
      while (i < s.length && /\s/.test(s[i])) i++;
    };

    const parseQuotedString = (quote) => {
      i++; // consume opening quote
      let out = '';
      while (i < s.length) {
        const ch = s[i++];
        if (ch === '\\' && i < s.length) {
          const esc = s[i++];
          if (esc === 'n') out += '\n';
          else if (esc === 't') out += '\t';
          else if (esc === 'r') out += '\r';
          else out += esc;
        } else if (ch === quote) {
          return out;
        } else {
          out += ch;
        }
      }
      throw new Error('Unterminated SNBT string');
    };

    const parseUnquotedToken = (allowColon = true) => {
      const re = allowColon ? /[A-Za-z0-9_.+\-:]/ : /[A-Za-z0-9_.+\-]/;
      const start = i;
      while (i < s.length && re.test(s[i])) i++;
      return s.slice(start, i);
    };

    const parsePrimitive = () => {
      skipWs();
      if (s[i] === '"' || s[i] === "'") return parseQuotedString(s[i]);
      const tok = parseUnquotedToken(true);
      if (!tok) throw new Error(`Unexpected char at ${i}`);
      if (tok === 'true') return true;
      if (tok === 'false') return false;
      const numMatch = /^([+-]?(?:\d+(?:\.\d*)?|\.\d+)(?:[eE][+-]?\d+)?)[bBsSlLfFdD]?$/.exec(tok);
      if (numMatch) return Number(numMatch[1]);
      return tok;
    };

    const parseKey = () => {
      skipWs();
      if (s[i] === '"' || s[i] === "'") return parseQuotedString(s[i]);
      const tok = parseUnquotedToken(false);
      if (!tok) throw new Error(`Expected key at ${i}`);
      return tok;
    };

    const parseCompound = () => {
      i++; // {
      const obj = {};
      skipWs();
      if (s[i] === '}') {
        i++;
        return obj;
      }
      while (i < s.length) {
        const key = parseKey();
        skipWs();
        if (s[i] !== ':') throw new Error(`Expected ':' at ${i}`);
        i++;
        obj[key] = parseValue();
        skipWs();
        if (s[i] === ',') {
          i++;
          skipWs();
          if (s[i] === '}') {
            i++;
            return obj;
          }
        } else if (s[i] === '}') {
          i++;
          return obj;
        } else {
          throw new Error(`Expected ',' or '}' at ${i}`);
        }
      }
      throw new Error('Unterminated compound');
    };

    const parseList = () => {
      i++; // [
      skipWs();
      if ((s[i] === 'B' || s[i] === 'I' || s[i] === 'L') && s[i + 1] === ';') {
        i += 2;
      }
      const arr = [];
      skipWs();
      if (s[i] === ']') {
        i++;
        return arr;
      }
      while (i < s.length) {
        arr.push(parseValue());
        skipWs();
        if (s[i] === ',') {
          i++;
          skipWs();
          if (s[i] === ']') {
            i++;
            return arr;
          }
        } else if (s[i] === ']') {
          i++;
          return arr;
        } else {
          throw new Error(`Expected ',' or ']' at ${i}`);
        }
      }
      throw new Error('Unterminated list');
    };

    const parseValue = () => {
      skipWs();
      if (i >= s.length) throw new Error('Unexpected end of SNBT');
      if (s[i] === '{') return parseCompound();
      if (s[i] === '[') return parseList();
      return parsePrimitive();
    };

    try {
      const result = parseValue();
      skipWs();
      if (i !== s.length) return null;
      return result;
    } catch {
      return null;
    }
  }

  function convertEnchantmentList(list) {
    if (!Array.isArray(list)) return null;
    const levels = {};
    for (const entry of list) {
      if (entry && typeof entry.id === 'string' && typeof entry.lvl === 'number') {
        levels[entry.id] = entry.lvl;
      }
    }
    return Object.keys(levels).length > 0 ? { levels } : null;
  }

  function convertTextValue(val, target) {
    if (typeof val !== 'string') return val;
    if (target && target.tooltipDisplay) {
      try {
        const parsed = JSON.parse(val);
        if (parsed && typeof parsed === 'object') return parsed;
      } catch { /* plain text */ }
    }
    return val;
  }

  // Converts parsed SNBT item tag object -> 1.20.5+ / 1.21+ data components
  function tryConvertItemNbtTag(tagStr, target, stats) {
    const parsed = parseSnbt(tagStr);
    if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) return null;

    const components = {};
    const unhandled = [];
    let recognized = false;

    const consume = (key, fn) => {
      if (parsed[key] !== undefined) {
        fn(parsed[key]);
        recognized = true;
      }
    };

    // Potion contents
    const potionContents = {};
    consume('Potion', (v) => { potionContents.potion = String(v); });
    consume('CustomPotionColor', (v) => { if (typeof v === 'number') potionContents.custom_color = v; });
    const rawEffects = parsed.CustomPotionEffects ?? parsed.custom_potion_effects;
    if (Array.isArray(rawEffects)) {
      recognized = true;
      potionContents.custom_effects = rawEffects.map((e) => ({
        id: e.Id ?? e.id,
        amplifier: e.Amplifier ?? e.amplifier ?? 0,
        duration: e.Duration ?? e.duration ?? 200,
        ambient: Boolean(e.Ambient ?? e.ambient ?? false),
        show_particles: e.ShowParticles ?? e.show_particles ?? true,
        show_icon: e.ShowIcon ?? e.show_icon ?? true,
      })).filter((e) => typeof e.id === 'string');
    }
    if (Object.keys(potionContents).length > 0) {
      components['minecraft:potion_contents'] = potionContents;
    }

    consume('CustomModelData', (v) => {
      if (typeof v === 'number') {
        components['minecraft:custom_model_data'] = target.cmdCompound ? { floats: [v] } : v;
      }
    });
    consume('Unbreakable', (v) => {
      if (v) components['minecraft:unbreakable'] = {};
    });
    consume('Damage', (v) => {
      if (typeof v === 'number') components['minecraft:damage'] = v;
    });
    consume('RepairCost', (v) => {
      if (typeof v === 'number') components['minecraft:repair_cost'] = v;
    });
    consume('Enchantments', (v) => {
      const mapped = convertEnchantmentList(v);
      if (mapped) components['minecraft:enchantments'] = mapped;
    });
    consume('StoredEnchantments', (v) => {
      const mapped = convertEnchantmentList(v);
      if (mapped) components['minecraft:stored_enchantments'] = mapped;
    });

    if (parsed.display && typeof parsed.display === 'object') {
      recognized = true;
      if (parsed.display.Name !== undefined) {
        components['minecraft:custom_name'] = convertTextValue(parsed.display.Name, target);
      }
      if (Array.isArray(parsed.display.Lore)) {
        components['minecraft:lore'] = parsed.display.Lore.map((l) => convertTextValue(l, target));
      }
      if (typeof parsed.display.color === 'number') {
        components['minecraft:dyed_color'] = target.tooltipDisplay
          ? parsed.display.color
          : { rgb: parsed.display.color };
      }
    }

    if (parsed.SkullOwner !== undefined) {
      recognized = true;
      if (typeof parsed.SkullOwner === 'string') {
        components['minecraft:profile'] = { name: parsed.SkullOwner };
      } else if (parsed.SkullOwner && typeof parsed.SkullOwner === 'object' && parsed.SkullOwner.Name) {
        components['minecraft:profile'] = { name: String(parsed.SkullOwner.Name) };
      }
    }

    if (Array.isArray(parsed.AttributeModifiers)) {
      recognized = true;
      const modifiers = parsed.AttributeModifiers.map((m) => ({
        type: mapAttributeId(m.AttributeName || m.type || '', target, stats || { reaMapped: 0, attrsMapped: 0 }),
        id: m.Name ? sanitizeModId(m.Name) : 'converted:modifier',
        amount: Number(m.Amount ?? 0),
        operation: m.Operation === 1 ? 'add_multiplied_base' : m.Operation === 2 ? 'add_multiplied_total' : 'add_value',
        ...(m.Slot ? { slot: m.Slot } : {}),
      })).filter((m) => m.type);
      if (modifiers.length > 0) {
        components['minecraft:attribute_modifiers'] = modifiers;
      }
    }

    if (typeof parsed.HideFlags === 'number' && parsed.HideFlags > 0) {
      recognized = true;
      if (target.tooltipDisplay) {
        const hidden = [];
        if (parsed.HideFlags & 1) hidden.push('minecraft:enchantments');
        if (parsed.HideFlags & 2) hidden.push('minecraft:attribute_modifiers');
        if (parsed.HideFlags & 4) hidden.push('minecraft:unbreakable');
        if (parsed.HideFlags & 32) hidden.push('minecraft:potion_contents');
        if (parsed.HideFlags & 64) hidden.push('minecraft:dyed_color');
        components['minecraft:tooltip_display'] = { hide_tooltip: false, hidden_components: hidden };
      } else {
        components['minecraft:hide_additional_tooltip'] = {};
      }
    }

    if (typeof parsed.title === 'string' || typeof parsed.author === 'string' || Array.isArray(parsed.pages)) {
      recognized = true;
      const book = {
        title: String(parsed.title ?? 'Book'),
        author: String(parsed.author ?? ''),
        pages: Array.isArray(parsed.pages) ? parsed.pages.map((p) => String(p)) : [],
      };
      if (typeof parsed.generation === 'number') book.generation = parsed.generation;
      components['minecraft:written_book_content'] = book;
    }

    const handledKeys = new Set([
      'Potion', 'CustomPotionColor', 'CustomPotionEffects', 'custom_potion_effects',
      'CustomModelData', 'Unbreakable', 'Damage', 'RepairCost', 'Enchantments',
      'StoredEnchantments', 'display', 'SkullOwner', 'AttributeModifiers', 'HideFlags',
      'title', 'author', 'pages', 'generation',
    ]);
    const unhandledObj = {};
    for (const k of Object.keys(parsed)) {
      if (!handledKeys.has(k)) {
        unhandled.push(k);
        unhandledObj[k] = parsed[k];
      }
    }

    return { recognized, complete: unhandled.length === 0, components, unhandled, unhandledObj };
  }

  function jsToSnbt(val) {
    if (val === null || val === undefined) return '""';
    if (typeof val === 'boolean') return val ? 'true' : 'false';
    if (typeof val === 'number') return Number.isFinite(val) ? String(val) : '0';
    if (typeof val === 'string') return JSON.stringify(val);
    if (Array.isArray(val)) return '[' + val.map(jsToSnbt).join(',') + ']';
    if (typeof val === 'object') {
      return '{' + Object.entries(val).map(([k, v]) => {
        const safeKey = /^[A-Za-z0-9_.\-+]+$/.test(k) ? k : JSON.stringify(k);
        return `${safeKey}:${jsToSnbt(v)}`;
      }).join(',') + '}';
    }
    return String(val);
  }

  function upgradeItemNbtInCommand(cmd, target, stats) {
    const itemRe = /((?:minecraft|[a-z0-9_.-]+):[a-z0-9_./-]+)\s*\{/g;
    let out = '';
    let last = 0;
    let m;

    while ((m = itemRe.exec(cmd)) !== null) {
      const fullId = m[1];
      const braceStart = m.index + m[0].length - 1;
      if (fullId.endsWith('dust') || fullId.endsWith('dust_color_transition') || fullId.endsWith('block') || fullId.endsWith('item')) {
        continue;
      }
      // Check preceding token so we don't touch `nbt={...}` or `scores={...}`
      const before = cmd.slice(Math.max(0, m.index - 6), m.index);
      if (/=\s*$/.test(before)) continue;

      // Scan balanced `{...}` respecting strings
      let depth = 0;
      let quote = null;
      let esc = false;
      let end = -1;
      for (let i = braceStart; i < cmd.length; i++) {
        const ch = cmd[i];
        if (quote) {
          if (esc) esc = false;
          else if (ch === '\\') esc = true;
          else if (ch === quote) quote = null;
          continue;
        }
        if (ch === '"' || ch === "'") {
          quote = ch;
        } else if (ch === '{') {
          depth++;
        } else if (ch === '}') {
          depth--;
          if (depth === 0) {
            end = i;
            break;
          }
        }
      }
      if (end === -1) continue;

      const rawSnbt = cmd.slice(braceStart, end + 1);
      const conv = tryConvertItemNbtTag(rawSnbt, target, stats);
      if (!conv) continue;

      const parts = [];
      for (const [ck, cv] of Object.entries(conv.components)) {
        parts.push(`${ck}=${jsToSnbt(cv)}`);
      }
      if (Object.keys(conv.unhandledObj).length > 0) {
        parts.push(`minecraft:custom_data=${jsToSnbt(conv.unhandledObj)}`);
      }
      const nextId = VANILLA_ID_RENAMES[fullId] || fullId;
      out += cmd.slice(last, m.index) + (parts.length > 0 ? `${nextId}[${parts.join(',')}]` : nextId);
      last = end + 1;
      itemRe.lastIndex = last;
      stats.commandsUpdated = (stats.commandsUpdated || 0) + 1;
    }

    return last === 0 ? cmd : out + cmd.slice(last);
  }

  function mapPehkuiCommandToAttr(scaleType, value, target) {
    const cleanType = String(scaleType || 'pehkui:base').replace(/^pehkui:/, '');
    const pref = (n, player = false) =>
      target.legacyPrefixAttrs ? `minecraft:${player ? 'player' : 'generic'}.${n}` : `minecraft:${n}`;

    switch (cleanType) {
      case 'base':
      case 'width':
      case 'height':
      case 'hitbox_width':
      case 'hitbox_height':
      case 'model_width':
      case 'model_height':
      case 'eye_height':
        return { attr: pref('scale'), val: value, def: 1 };
      case 'reach':
      case 'block_reach':
        return { attr: pref('block_interaction_range', true), val: Number((value * 4.5).toFixed(4)), def: 4.5 };
      case 'entity_reach':
      case 'attack_range':
        return { attr: pref('entity_interaction_range', true), val: Number((value * 3.0).toFixed(4)), def: 3.0 };
      case 'step_height':
        return { attr: pref('step_height'), val: Number((value * 0.6).toFixed(4)), def: 0.6 };
      case 'jump_height':
        return { attr: pref('jump_strength'), val: Number((value * 0.42).toFixed(4)), def: 0.42 };
      case 'motion':
        return { attr: pref('movement_speed'), val: Number((value * 0.1).toFixed(4)), def: 0.1 };
      default:
        return null;
    }
  }

  function upgradeCommandLine(rawLine, target, stats) {
    const trimmed = String(rawLine || '').trim();
    if (!trimmed || trimmed.startsWith('#')) return rawLine;

    const leadSlash = trimmed.startsWith('/') ? '/' : '';
    let prefix = '';
    let core = leadSlash ? trimmed.slice(1) : trimmed;

    // Peel off outer `execute ... run ` chain so nested particle/scale/give rewrite cleanly
    const execMatch = /^(execute\s+.*?\brun\s+)(.+)$/i.exec(core);
    if (execMatch) {
      prefix = execMatch[1];
      core = execMatch[2];
    }

    // 1. Pehkui `scale` commands -> vanilla `/attribute`
    if (/^scale\s+/i.test(core)) {
      const tokens = core.split(/\s+/);
      const sub = (tokens[1] || '').toLowerCase();
      if (sub === 'set') {
        const rest = tokens.slice(2);
        let scaleType = 'pehkui:base';
        const filtered = [];
        for (const t of rest) {
          if (t.includes(':')) scaleType = t;
          else filtered.push(t);
        }
        const numIdx = filtered.findIndex((t) => /^[+-]?(?:\d+\.?\d*|\.\d+)$/.test(t));
        if (numIdx !== -1) {
          const num = Number(filtered[numIdx]);
          const targetSel = filtered.find((_, i) => i !== numIdx) || '@s';
          const mapped = mapPehkuiCommandToAttr(scaleType, num, target);
          if (mapped) {
            stats.commandsUpdated = (stats.commandsUpdated || 0) + 1;
            return `${leadSlash}${prefix}attribute ${targetSel} ${mapped.attr} base set ${mapped.val}`;
          }
        }
      } else if (sub === 'reset') {
        const rest = tokens.slice(2);
        const scaleType = rest.find((t) => t.includes(':')) || 'pehkui:base';
        const targetSel = rest.find((t) => !t.includes(':')) || '@s';
        const mapped = mapPehkuiCommandToAttr(scaleType, 1, target);
        if (mapped) {
          stats.commandsUpdated = (stats.commandsUpdated || 0) + 1;
          return `${leadSlash}${prefix}attribute ${targetSel} ${mapped.attr} base set ${mapped.def}`;
        }
      }
      stats.commandsUpdated = (stats.commandsUpdated || 0) + 1;
      return `# [Origins Converter] Unsupported Pehkui command skipped: ${trimmed}`;
    }

    // 2. 1.20.5+ `/particle dust` and `/particle dust_color_transition` syntax
    const dustMatch = /^(particle\s+)(?:minecraft:)?dust\s+([+-]?(?:\d+\.?\d*|\.\d+))\s+([+-]?(?:\d+\.?\d*|\.\d+))\s+([+-]?(?:\d+\.?\d*|\.\d+))\s+([+-]?(?:\d+\.?\d*|\.\d+))(\s+.*)?$/i.exec(core);
    if (dustMatch) {
      stats.commandsUpdated = (stats.commandsUpdated || 0) + 1;
      return `${leadSlash}${prefix}${dustMatch[1]}minecraft:dust{color:[${dustMatch[2]},${dustMatch[3]},${dustMatch[4]}],scale:${dustMatch[5]}}${dustMatch[6] || ''}`;
    }

    const dustTransMatch = /^(particle\s+)(?:minecraft:)?dust_color_transition\s+([+-]?(?:\d+\.?\d*|\.\d+))\s+([+-]?(?:\d+\.?\d*|\.\d+))\s+([+-]?(?:\d+\.?\d*|\.\d+))\s+([+-]?(?:\d+\.?\d*|\.\d+))\s+([+-]?(?:\d+\.?\d*|\.\d+))\s+([+-]?(?:\d+\.?\d*|\.\d+))\s+([+-]?(?:\d+\.?\d*|\.\d+))(\s+.*)?$/i.exec(core);
    if (dustTransMatch) {
      stats.commandsUpdated = (stats.commandsUpdated || 0) + 1;
      return `${leadSlash}${prefix}${dustTransMatch[1]}minecraft:dust_color_transition{from_color:[${dustTransMatch[2]},${dustTransMatch[3]},${dustTransMatch[4]}],to_color:[${dustTransMatch[6]},${dustTransMatch[7]},${dustTransMatch[8]}],scale:${dustTransMatch[5]}}${dustTransMatch[9] || ''}`;
    }

    // 3. Item `{NBT}` syntax inside give/clear/item commands
    if (/^(give|clear|item)\s+/i.test(core)) {
      const upgraded = upgradeItemNbtInCommand(core, target, stats);
      if (upgraded !== core) {
        return `${leadSlash}${prefix}${upgraded}`;
      }
    }

    return rawLine;
  }

  function upgradeMcFunctionText(rawText, target, stats) {
    const lines = String(rawText).split(/\r?\n/);
    const out = [];
    for (let i = 0; i < lines.length; i++) {
      // Join backslash line-continuations first if any? Rare in 1.20.1, transform line-by-line cleanly
      out.push(upgradeCommandLine(lines[i], target, stats));
    }
    return out.join('\n');
  }

  function isAirIngredientEntry(entry) {
    if (!entry || typeof entry !== 'object' || Array.isArray(entry)) return false;
    if (entry.tag) return false;
    return entry.item === 'minecraft:air' || entry.item === 'air';
  }

  // Convert 1.20.1 recipe ingredient entry to 1.21.4+ flat string form when needed
  function modernizeRecipeIngredient(entry, ctx) {
    if (typeof entry === 'string') return rewriteResourceString(entry, 'ingredient', ctx);
    if (Array.isArray(entry)) {
      return entry.map((sub) => modernizeRecipeIngredient(sub, ctx));
    }
    if (entry && typeof entry === 'object') {
      if (typeof entry.item === 'string' && !entry.type) {
        ctx.stats.recipesUpdated++;
        return rewriteResourceString(entry.item, 'item', ctx);
      }
      if (typeof entry.tag === 'string' && !entry.type) {
        ctx.stats.recipesUpdated++;
        return rewriteResourceString('#' + entry.tag, 'tag', ctx);
      }
    }
    return entry;
  }

  function convertSingleFabricCondition(cond, ctx) {
    if (!cond || typeof cond !== 'object' || typeof cond.condition !== 'string') return null;
    const type = cond.condition;
    if (type === 'fabric:all_mods_loaded' && Array.isArray(cond.values)) {
      const mods = cond.values.filter((m) => m && m !== 'origins' && m !== 'fabric-api' && m !== 'fabric');
      if (mods.length === 0) return { type: 'neoforge:true' };
      if (mods.length === 1) return { type: 'neoforge:mod_loaded', modid: mods[0] };
      return { type: 'neoforge:and', values: mods.map((m) => ({ type: 'neoforge:mod_loaded', modid: m })) };
    }
    if (type === 'fabric:any_mod_loaded' && Array.isArray(cond.values)) {
      const mods = cond.values.map(String);
      if (mods.includes('origins')) return { type: 'neoforge:true' };
      if (mods.length === 1) return { type: 'neoforge:mod_loaded', modid: mods[0] };
      return { type: 'neoforge:or', values: mods.map((m) => ({ type: 'neoforge:mod_loaded', modid: m })) };
    }
    if (type === 'fabric:not' && cond.value) {
      const inner = convertSingleFabricCondition(cond.value, ctx);
      return inner ? { type: 'neoforge:not', value: inner } : null;
    }
    if ((type === 'fabric:and' || type === 'fabric:or') && Array.isArray(cond.values)) {
      const values = cond.values.map((v) => convertSingleFabricCondition(v, ctx)).filter(Boolean);
      return values.length ? { type: type === 'fabric:and' ? 'neoforge:and' : 'neoforge:or', values } : null;
    }
    if (type === 'fabric:true') return { type: 'neoforge:true' };
    if (type === 'fabric:false') return { type: 'neoforge:false' };
    ctx.warnings.push(`${ctx.relPath}: dropped unsupported fabric load condition (${type})`);
    return null;
  }

  function makePowerStub(node) {
    const out = { type: 'origins:simple' };
    if (node.name !== undefined) out.name = node.name;
    if (node.description !== undefined) out.description = node.description;
    if (node.hidden !== undefined) out.hidden = node.hidden;
    if (node.badges !== undefined) out.badges = node.badges;
    if (node.loading_priority !== undefined) out.loading_priority = node.loading_priority;
    return out;
  }

  function powerPathToId(relPath) {
    const m = /^data\/([^/]+)\/powers\/(.+)\.json$/i.exec(String(relPath || ''));
    return m ? `${m[1]}:${m[2]}` : null;
  }

  function mapKeyIdString(rawKey, isRadialMenu = false) {
    const s = String(rawKey || '').trim();
    if (!s || s === 'none' || s === 'key.layers.primary_active') {
      return 'key.origins.primary_active';
    }
    if (VALID_KEYBIND_NAMES.has(s)) return s;
    if (/primary/i.test(s)) return 'key.origins.primary_active';
    if (!isRadialMenu && /secondary|ternary|quaternary|alt|off|menu|radial/i.test(s)) {
      return 'key.origins.secondary_active';
    }
    return isRadialMenu ? 'key.origins.primary_active' : 'key.origins.primary_active';
  }

  function normalizeKeyValue(rawKey, ctx, isRadialMenu = false) {
    const isObj = rawKey && typeof rawKey === 'object' && !Array.isArray(rawKey);
    const current = isObj ? rawKey.key : rawKey;
    const mapped = mapKeyIdString(current, isRadialMenu);
    if (mapped !== current && ctx?.stats) {
      ctx.stats.keysRemapped = (ctx.stats.keysRemapped || 0) + 1;
      if (ctx.warnings && current && current !== 'none' && !VALID_KEYBIND_NAMES.has(current)) {
        const msg = `Remapped unsupported keybind "${current}" -> "${mapped}"`;
        if (!ctx.warnings.includes(msg)) ctx.warnings.push(msg);
      }
    }
    return isObj ? { ...rawKey, key: mapped } : mapped;
  }

  function convertExtraNode(obj, ctx, keyHint = 'root') {
    if (!obj || typeof obj.type !== 'string') return obj;
    const keepMeta = (src, target) => {
      for (const k of ['name', 'description', 'hidden', 'badges', 'condition', 'loading_priority']) {
        if (src[k] !== undefined) target[k] = src[k];
      }
      return target;
    };

    if (obj.type === 'extraorigins:radial_menu') {
      const basePowerId = (ctx && ctx.currentPowerId) || powerPathToId(ctx?.relPath) || 'extraorigins:radial_menu';
      const selfPowerId = keyHint !== 'root' && ctx?.insideMultiple ? `${basePowerId}_${keyHint}` : basePowerId;
      const resourceId = (ctx?.radialMenus && ctx.radialMenus.get(selfPowerId)?.resourceId) || `${selfPowerId}_mode`;
      const normalizedKey = normalizeKeyValue(obj.key ?? 'key.origins.primary_active', ctx, true);
      const useSnbt = Boolean(ctx?.target?.cmdCompound);

      const modeActions = RADIAL_INDEX_LABELS.map((label, idx) => ({
        condition: { type: 'apoli:resource', resource: resourceId, comparison: '==', compare_to: idx },
        action: {
          type: 'apoli:execute_command',
          command: useSnbt
            ? `title @s actionbar {text:"Mode: ${label}",color:"aqua"}`
            : `title @s actionbar {"text":"Mode: ${label}","color":"aqua"}`,
        },
      }));

      const cyclePower = {
        type: 'origins:active_self',
        key: typeof normalizedKey === 'string' ? { key: normalizedKey } : normalizedKey,
        cooldown: 4,
        entity_action: {
          type: 'apoli:and',
          actions: [
            {
              type: 'apoli:if_else',
              condition: { type: 'apoli:resource', resource: resourceId, comparison: '==', compare_to: 3 },
              if_action: { type: 'apoli:change_resource', resource: resourceId, change: 0, operation: 'set' },
              else_action: { type: 'apoli:change_resource', resource: resourceId, change: 1, operation: 'add' },
            },
            { type: 'apoli:play_sound', sound: 'minecraft:ui.button.click', volume: 0.4, pitch: 1.2 },
            { type: 'apoli:if_else_list', actions: modeActions },
          ],
        },
      };

      const modeResource = {
        type: 'origins:resource',
        min: 0,
        max: 3,
        start_value: 0,
        hud_render: { should_render: true, bar_index: 0 },
      };

      ctx.stats.extraPowersConverted = (ctx.stats.extraPowersConverted || 0) + 1;
      return keepMeta(obj, {
        type: 'origins:multiple',
        mode: modeResource,
        cycle: cyclePower,
      });
    }

    if (obj.type === 'extraorigins:radial_menu_direction') {
      const rawPower = typeof obj.power === 'string' ? obj.power : null;
      const mappedRes = (rawPower && ctx?.radialMenus?.get(rawPower)?.resourceId)
        || (rawPower ? `${rawPower}_mode` : null)
        || ctx?.defaultRadialResource
        || 'extraorigins:magic_spores_mode';

      const toIdx = (dir) => {
        const k = String(dir || 'up').toLowerCase();
        return Object.prototype.hasOwnProperty.call(RADIAL_DIR_TO_INDEX, k) ? RADIAL_DIR_TO_INDEX[k] : 0;
      };

      ctx.stats.extraPowersConverted = (ctx.stats.extraPowersConverted || 0) + 1;
      if (Array.isArray(obj.directions) && obj.directions.length > 0) {
        const out = {
          type: 'apoli:or',
          conditions: obj.directions.map((d) => ({
            type: 'apoli:resource',
            resource: mappedRes,
            comparison: '==',
            compare_to: toIdx(d),
          })),
        };
        if (obj.inverted !== undefined) out.inverted = obj.inverted;
        return out;
      }

      const out = {
        type: 'apoli:resource',
        resource: mappedRes,
        comparison: '==',
        compare_to: toIdx(obj.direction),
      };
      if (obj.inverted !== undefined) out.inverted = obj.inverted;
      return out;
    }

    if (obj.type === 'extraorigins:mob_neutrality') {
      let bientityCond = obj.bientity_condition;
      if (!bientityCond && obj.entity_condition) {
        bientityCond = { type: 'apoli:target_condition', condition: obj.entity_condition };
      } else if (!bientityCond && Array.isArray(obj.entity_types)) {
        const list = obj.entity_types.map((t) => ({ type: 'apoli:entity_type', entity_type: t }));
        bientityCond = {
          type: 'apoli:target_condition',
          condition: list.length === 1 ? list[0] : { type: 'apoli:or', conditions: list },
        };
      } else if (!bientityCond) {
        bientityCond = { type: 'apoli:constant', value: true };
      }
      ctx.stats.extraPowersConverted = (ctx.stats.extraPowersConverted || 0) + 1;
      return keepMeta(obj, {
        type: 'apoli_legacy:modify_behavior',
        behavior_type: 'neutral',
        bientity_condition: bientityCond,
      });
    }

    if (obj.type === 'extraorigins:food_effect_immunity') {
      const out = keepMeta(obj, { type: 'origins:modify_food', prevent_effects: true });
      if (obj.item_condition !== undefined) out.item_condition = obj.item_condition;
      ctx.stats.extraPowersConverted = (ctx.stats.extraPowersConverted || 0) + 1;
      return out;
    }

    if (obj.type === 'extraorigins:start_riding') {
      const out = keepMeta(obj, {
        type: 'origins:action_on_entity_use',
        bientity_action: { type: 'apoli:mount' },
      });
      for (const k of ['bientity_condition', 'item_condition', 'hands', 'priority']) {
        if (obj[k] !== undefined) out[k] = obj[k];
      }
      ctx.stats.extraPowersConverted = (ctx.stats.extraPowersConverted || 0) + 1;
      return out;
    }

    if (obj.type === 'extraorigins:piglin_safe') {
      const out = { type: 'apoli:dimension', dimension: 'minecraft:the_nether' };
      if (obj.inverted !== undefined) out.inverted = obj.inverted;
      ctx.stats.extraPowersConverted = (ctx.stats.extraPowersConverted || 0) + 1;
      return out;
    }

    if (obj.type === 'extraorigins:block_action_looking_at') {
      const out = {
        type: 'apoli:raycast',
        distance: typeof obj.distance === 'number' ? obj.distance : (typeof obj.reach === 'number' ? obj.reach : 4.5),
        block: true,
        entity: false,
      };
      if (obj.block_action !== undefined) out.block_action = obj.block_action;
      if (obj.fluid_handling !== undefined) out.fluid_handling = obj.fluid_handling;
      ctx.stats.extraPowersConverted = (ctx.stats.extraPowersConverted || 0) + 1;
      return out;
    }

    return obj;
  }

  function isSupportedPowerType(id) {
    if (typeof id !== 'string') return false;
    if (EXACT_EXTRA_POWERS.has(id) || CONVERTIBLE_EXTRA_POWERS.has(id)) return true;
    const [ns, name] = id.includes(':') ? id.split(':', 2) : ['minecraft', id];
    return (ns === 'origins' || ns === 'apoli') && KNOWN_APOLI_POWERS.has(name);
  }

  function walkAndTransformJson(value, ctx, keyHint = 'root') {
    if (Array.isArray(value)) {
      return value.map((item, idx) => walkAndTransformJson(item, ctx, `${keyHint}_${idx}`));
    }
    if (typeof value === 'string') {
      return rewriteResourceString(value, keyHint, ctx);
    }
    if (!value || typeof value !== 'object') {
      return value;
    }

    let obj = { ...value };

    // Convert `fabric:load_conditions` -> `neoforge:conditions`
    if (ctx.migratePowers && Array.isArray(obj['fabric:load_conditions'])) {
      const converted = obj['fabric:load_conditions']
        .map((c) => convertSingleFabricCondition(c, ctx))
        .filter((c) => c && c.type !== 'neoforge:true');
      delete obj['fabric:load_conditions'];
      if (converted.length > 0 && !obj['neoforge:conditions']) {
        obj['neoforge:conditions'] = converted;
      }
      ctx.stats.loadConditionsFixed = (ctx.stats.loadConditionsFixed || 0) + 1;
    }

    // Remap legacy/namespace differences & ExtraOrigins equivalents
    if (ctx.migratePowers && typeof obj.type === 'string') {
      if (LEGACY_TYPE_REMAPPINGS[obj.type]) {
        obj.type = LEGACY_TYPE_REMAPPINGS[obj.type];
        ctx.stats.attrsMapped++;
      } else if (CONVERTIBLE_EXTRA_NODES.has(obj.type)) {
        obj = convertExtraNode(obj, ctx, keyHint);
      }
    }

    // Replace `apoli:ingredient` containing `minecraft:air` with `apoli:empty` (avoids 1.21.1 packet crash)
    if (
      ctx.migrateItems &&
      (obj.type === 'apoli:ingredient' || obj.type === 'origins:ingredient') &&
      obj.ingredient !== undefined
    ) {
      if (isAirIngredientEntry(obj.ingredient)) {
        const nextObj = { ...obj, type: 'apoli:empty' };
        delete nextObj.ingredient;
        obj = nextObj;
        ctx.stats.airIngredientsFixed = (ctx.stats.airIngredientsFixed || 0) + 1;
      } else if (Array.isArray(obj.ingredient) && obj.ingredient.some(isAirIngredientEntry)) {
        const nonAir = obj.ingredient.filter((e) => !isAirIngredientEntry(e));
        if (nonAir.length === 0) {
          const nextObj = { ...obj, type: 'apoli:empty' };
          delete nextObj.ingredient;
          obj = nextObj;
        } else {
          const nextObj = {
            type: 'apoli:or',
            conditions: [
              { type: obj.type, ingredient: nonAir.length === 1 ? nonAir[0] : nonAir },
              { type: 'apoli:empty' },
            ],
          };
          if (obj.inverted !== undefined) nextObj.inverted = obj.inverted;
          obj = nextObj;
        }
        ctx.stats.airIngredientsFixed = (ctx.stats.airIngredientsFixed || 0) + 1;
      }
    }

    // Convert power type if in powers/ or embedded sub-power
    if (ctx.migratePowers && typeof obj.type === 'string' && ctx.isPowerFile) {
      if (SCALE_POWER_TYPES.has(obj.type)) {
        obj = convertScalePowerNode(obj, ctx.target, keyHint, ctx.stats);
      } else if (CONVERTIBLE_EXTRA_POWERS.has(obj.type)) {
        obj = convertExtraNode(obj, ctx, keyHint);
      } else if (
        ctx.stubUnsupported &&
        (keyHint === 'root' || ctx.insideMultiple) &&
        !isSupportedPowerType(obj.type)
      ) {
        ctx.stats.powersStubbed = (ctx.stats.powersStubbed || 0) + 1;
        if (ctx.stubbedTypes) ctx.stubbedTypes.add(obj.type);
        return makePowerStub(obj);
      }
    }

    // Ensure Active powers always have a valid NeoForge keybinding (missing/"none"/custom mod keys fail silently)
    if (ctx.migratePowers && ctx.isPowerFile && typeof obj.type === 'string') {
      const shortType = obj.type.includes(':') ? obj.type.split(':', 2)[1] : obj.type;
      if (ACTIVE_POWER_SHORT_NAMES.has(shortType)) {
        if (obj.key === undefined) {
          obj.key = { key: 'key.origins.primary_active' };
          ctx.stats.keysRemapped = (ctx.stats.keysRemapped || 0) + 1;
        } else {
          obj.key = normalizeKeyValue(obj.key, ctx, false);
        }
      } else if (obj.key !== undefined) {
        obj.key = normalizeKeyValue(obj.key, ctx, false);
      }
    }

    // Rewrite attributes inside modifier objects
    if (ctx.migratePowers && typeof obj.attribute === 'string') {
      obj.attribute = mapAttributeId(obj.attribute, ctx.target, ctx.stats);
    }

    // Upgrade 1.20.1 recipe schemas (result for 1.21+, plus ingredients/key for 1.21.4+)
    if (ctx.migrateItems && ctx.isRecipeFile) {
      if (keyHint === 'result' && typeof obj.item === 'string' && !obj.id) {
        obj.id = obj.item;
        delete obj.item;
        ctx.stats.recipesUpdated++;
      }
      if (ctx.target.modernRecipes) {
        if (Array.isArray(obj.ingredients)) {
          obj.ingredients = obj.ingredients.map((e) => modernizeRecipeIngredient(e, ctx));
        }
        if (obj.ingredient !== undefined) {
          obj.ingredient = modernizeRecipeIngredient(obj.ingredient, ctx);
        }
        for (const k of ['template', 'base', 'addition']) {
          if (obj[k] !== undefined && typeof obj[k] === 'object') {
            obj[k] = modernizeRecipeIngredient(obj[k], ctx);
          }
        }
        if (keyHint === 'root' && obj.key && typeof obj.key === 'object' && !Array.isArray(obj.key)) {
          const nextKey = {};
          for (const [sym, val] of Object.entries(obj.key)) {
            nextKey[sym] = modernizeRecipeIngredient(val, ctx);
          }
          obj.key = nextKey;
        }
      }
    }

    // Upgrade 1.20.5+ loot table functions & conditions
    if (ctx.migrateItems && ctx.isLootTableFile) {
      if (obj.function === 'minecraft:set_nbt' && typeof obj.tag === 'string') {
        const conv = tryConvertItemNbtTag(obj.tag, ctx.target, ctx.stats);
        if (conv && conv.recognized && conv.complete) {
          obj.function = 'minecraft:set_components';
          obj.components = { ...(obj.components || {}), ...conv.components };
          delete obj.tag;
        } else {
          obj.function = 'minecraft:set_custom_data';
        }
        ctx.stats.lootTablesUpdated = (ctx.stats.lootTablesUpdated || 0) + 1;
      } else if (obj.function === 'minecraft:looting_enchant') {
        obj.function = 'minecraft:enchanted_count_increase';
        obj.enchantment = 'minecraft:looting';
        ctx.stats.lootTablesUpdated = (ctx.stats.lootTablesUpdated || 0) + 1;
      } else if (obj.condition === 'minecraft:random_chance_with_looting') {
        obj.condition = 'minecraft:random_chance_with_enchanted_bonus';
        obj.enchantment = 'minecraft:looting';
        if (typeof obj.chance === 'number' && obj.unenchanted_chance === undefined) {
          obj.unenchanted_chance = obj.chance;
          delete obj.chance;
        }
        ctx.stats.lootTablesUpdated = (ctx.stats.lootTablesUpdated || 0) + 1;
      }
    }

    // Upgrade item { item: "...", tag: "{...}" } -> { ..., components: {...} }
    if (ctx.migrateItems && typeof obj.tag === 'string' && (typeof obj.item === 'string' || typeof obj.id === 'string')) {
      const conv = tryConvertItemNbtTag(obj.tag, ctx.target, ctx.stats);
      if (conv && conv.recognized) {
        obj.components = { ...(obj.components || {}), ...conv.components };
        if (conv.complete) {
          delete obj.tag;
          ctx.stats.tagsConverted++;
        } else {
          ctx.warnings.push(`${ctx.relPath}: partially converted item "tag" NBT (unmapped: ${conv.unhandled.join(', ')})`);
        }
      } else {
        ctx.warnings.push(`${ctx.relPath}: item uses legacy "tag" NBT (${obj.tag.slice(0, 48)})`);
      }
    }

    const isMultiple = ctx.isPowerFile && typeof obj.type === 'string' && (obj.type === 'origins:multiple' || obj.type === 'apoli:multiple');
    const skipMultipleMeta = new Set([
      'type', 'loading_priority', 'name', 'description', 'hidden',
      'badges', 'condition', 'neoforge:conditions', 'fabric:load_conditions',
    ]);

    for (const key of Object.keys(obj)) {
      if (
        (ctx.migrateItems || ctx.migratePowers) &&
        ctx.isPowerFile &&
        (key === 'command' || key === 'command_at_hit' || key === 'command_along_ray') &&
        typeof obj[key] === 'string'
      ) {
        obj[key] = upgradeCommandLine(obj[key], ctx.target, ctx.stats);
        continue;
      }
      if (key === 'result' && ctx.migrateItems && ctx.isRecipeFile && typeof obj[key] === 'string') {
        obj[key] = { id: rewriteResourceString(obj[key], key, ctx) };
        ctx.stats.recipesUpdated++;
        continue;
      }
      const childCtx = isMultiple
        ? { ...ctx, insideMultiple: !skipMultipleMeta.has(key) && !key.startsWith('$') }
        : { ...ctx, insideMultiple: false };
      obj[key] = walkAndTransformJson(obj[key], childCtx, key);
    }

    return obj;
  }

  function checkRegistryId(domain, id) {
    if (typeof id !== 'string') return true;
    if (CONVERTIBLE_EXTRA_NODES.has(id)) return true;
    const remapped = LEGACY_TYPE_REMAPPINGS[id] || id;
    const [ns, name] = remapped.includes(':') ? remapped.split(':', 2) : ['minecraft', remapped];

    const lookup = (set) =>
      set.has(remapped) || ((ns === 'origins' || ns === 'apoli') && set.has(name));

    switch (domain) {
      case 'entity_cond': return lookup(CONDITION_REGISTRIES.entity);
      case 'bientity_cond': return lookup(CONDITION_REGISTRIES.bientity);
      case 'block_cond': return lookup(CONDITION_REGISTRIES.block);
      case 'biome_cond': return lookup(CONDITION_REGISTRIES.biome);
      case 'damage_cond': return lookup(CONDITION_REGISTRIES.damage);
      case 'fluid_cond': return lookup(CONDITION_REGISTRIES.fluid);
      case 'item_cond': return lookup(CONDITION_REGISTRIES.item);
      case 'entity_act': return lookup(ACTION_REGISTRIES.entity);
      case 'bientity_act': return lookup(ACTION_REGISTRIES.bientity);
      case 'block_act': return lookup(ACTION_REGISTRIES.block);
      case 'item_act': return lookup(ACTION_REGISTRIES.item);
      default: return true;
    }
  }

  function inspectNestedDiagnostics(node, relPath, currentDomain, analysis) {
    if (Array.isArray(node)) {
      for (const item of node) inspectNestedDiagnostics(item, relPath, currentDomain, analysis);
      return;
    }
    if (!node || typeof node !== 'object') return;

    if (currentDomain && typeof node.type === 'string') {
      if (!checkRegistryId(currentDomain, node.type)) {
        if (!analysis.unsupportedDetails.has(node.type)) analysis.unsupportedDetails.set(node.type, new Set());
        analysis.unsupportedDetails.get(node.type).add(relPath);
      }
    }

    for (const [k, v] of Object.entries(node)) {
      if (k === 'conditions' || k === 'actions' || k === 'if_action' || k === 'else_action' || k === 'branches') {
        inspectNestedDiagnostics(v, relPath, currentDomain, analysis);
        continue;
      }
      const nextDomain = KEY_TO_DIAGNOSTIC_DOMAIN[k] || null;
      if (nextDomain) {
        inspectNestedDiagnostics(v, relPath, nextDomain, analysis);
      } else if (v && typeof v === 'object') {
        inspectNestedDiagnostics(v, relPath, null, analysis);
      }
    }
  }

  function inspectPowerTypesInJson(obj, relPath, analysis) {
    if (!obj || typeof obj !== 'object' || Array.isArray(obj)) return;
    if (!analysis.unsupportedDetails) analysis.unsupportedDetails = new Map();

    if (typeof obj.type === 'string') {
      const id = obj.type;
      const normalized = LEGACY_TYPE_REMAPPINGS[id] || id;
      const [ns, name] = normalized.includes(':') ? normalized.split(':', 2) : ['minecraft', normalized];
      if (EXACT_EXTRA_POWERS.has(normalized) || ((ns === 'origins' || ns === 'apoli') && KNOWN_APOLI_POWERS.has(name))) {
        analysis.supportedPowers.add(id);
      } else if (CONVERTIBLE_EXTRA_POWERS.has(id)) {
        analysis.autoConvertiblePowers.add(id);
      } else {
        if (!analysis.unsupportedPowers.has(id)) analysis.unsupportedPowers.set(id, []);
        analysis.unsupportedPowers.get(id).push(relPath);
      }

      if (name === 'multiple') {
        const nonSubPowerKeys = new Set([
          'type', 'loading_priority', 'name', 'description', 'hidden',
          'badges', 'condition', 'fabric:load_conditions', 'neoforge:conditions',
        ]);
        for (const [k, v] of Object.entries(obj)) {
          if (nonSubPowerKeys.has(k) || k.startsWith('$')) {
            if (k === 'condition') inspectNestedDiagnostics(v, relPath, 'entity_cond', analysis);
            continue;
          }
          if (v && typeof v === 'object' && typeof v.type === 'string') {
            inspectPowerTypesInJson(v, `${relPath}#${k}`, analysis);
          }
        }
        return;
      }
    }

    inspectNestedDiagnostics(obj, relPath, null, analysis);
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

    if (iconPath) lines.push(`logoFile=${tomlString(iconPath)}`);
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

  async function inspectSingleArchive(file) {
    const zip = await window.JSZip.loadAsync(file);
    const fabricJsonFile = zip.file('fabric.mod.json');
    if (!fabricJsonFile) {
      throw new Error(`${file.name}: Missing fabric.mod.json — not a Fabric mod JAR.`);
    }

    const meta = parseJsonLenient(await fabricJsonFile.async('string'));
    const classFiles = Object.keys(zip.files).filter((p) => !zip.files[p].dir && p.endsWith('.class'));
    const codeHits = [];
    for (const cls of classFiles) {
      const strs = extractClassStrings(await zip.file(cls).async('uint8array'));
      const hit = CODE_MARKERS.find((m) => strs.some((s) => s.includes(m)));
      if (hit) codeHits.push(`${cls} (${hit})`);
    }

    const nestedJars = [];
    for (const p of Object.keys(zip.files)) {
      if (!zip.files[p].dir && /^META-INF\/jars\/.+\.jar$/i.test(p)) {
        try {
          const innerZip = await window.JSZip.loadAsync(await zip.file(p).async('uint8array'));
          const innerMetaFile = innerZip.file('fabric.mod.json');
          const innerMeta = innerMetaFile ? parseJsonLenient(await innerMetaFile.async('string')) : null;
          const hasData = Object.keys(innerZip.files).some((k) => /^data\//i.test(k));
          const hasClasses = Object.keys(innerZip.files).some((k) => k.endsWith('.class'));
          nestedJars.push({
            path: p,
            id: innerMeta?.id || p.split('/').pop(),
            hasData,
            hasClasses,
          });
        } catch {
          nestedJars.push({ path: p, id: p.split('/').pop(), hasData: false, hasClasses: false });
        }
      }
    }

    const analysis = {
      supportedPowers: new Set(),
      autoConvertiblePowers: new Set(),
      unsupportedPowers: new Map(),
      unsupportedDetails: new Map(),
    };
    const radialMenus = new Map();
    const powerPaths = Object.keys(zip.files).filter(
      (p) => !zip.files[p].dir && /^data\/[^/]+\/powers\/.+\.json$/i.test(p)
    );
    for (const p of powerPaths) {
      try {
        const parsed = parseJsonLenient(await zip.file(p).async('string'));
        inspectPowerTypesInJson(parsed, p, analysis);
        const baseId = powerPathToId(p);
        if (parsed && parsed.type === 'extraorigins:radial_menu' && baseId) {
          radialMenus.set(baseId, { resourceId: `${baseId}_mode` });
        } else if (parsed && (parsed.type === 'origins:multiple' || parsed.type === 'apoli:multiple') && baseId) {
          for (const [k, v] of Object.entries(parsed)) {
            if (v && typeof v === 'object' && v.type === 'extraorigins:radial_menu') {
              radialMenus.set(`${baseId}_${k}`, { resourceId: `${baseId}_${k}_mode` });
            }
          }
        }
      } catch { /* reported on convert */ }
    }

    return { fileName: file.name, zip, meta, codeHits, nestedJars, powerPaths, radialMenus, analysis };
  }

  async function convertSingleArchive(loaded, options) {
    const { target, renameFolders, migratePowers, migrateItems, stubUnsupported } = options;
    const { zip, meta } = loaded;
    const radialMenus = loaded.radialMenus || new Map();
    const defaultRadialResource = radialMenus.size > 0 ? radialMenus.values().next().value.resourceId : null;

    const outZip = new window.JSZip();
    const keptPaths = new Set();
    const stats = {
      strippedCount: 0,
      nestedDataMerged: 0,
      folderRenames: 0,
      scalePowersConverted: 0,
      extraPowersConverted: 0,
      reaMapped: 0,
      attrsMapped: 0,
      keysRemapped: 0,
      loadConditionsFixed: 0,
      tagsConverted: 0,
      recipesUpdated: 0,
      lootTablesUpdated: 0,
      idsRenamed: 0,
      airIngredientsFixed: 0,
      powersStubbed: 0,
      commandsUpdated: 0,
      itemModelsGenerated: 0,
    };
    const warnings = [];
    const stubbedTypes = new Set();
    const analysis = {
      supportedPowers: new Set(),
      autoConvertiblePowers: new Set(),
      unsupportedPowers: new Map(),
      unsupportedDetails: new Map(),
    };

    const processEntry = async (rawPath, rawBytesOrStringFn, isNested = false) => {
      if (shouldStripPath(rawPath)) {
        stats.strippedCount++;
        return;
      }
      if (rawPath === 'META-INF/MANIFEST.MF') {
        if (!isNested) {
          outZip.file('META-INF/MANIFEST.MF', 'Manifest-Version: 1.0\n');
          keptPaths.add('META-INF/MANIFEST.MF');
        }
        return;
      }

      const newPath = migrateDataPath(rawPath, renameFolders);
      if (isNested && keptPaths.has(newPath)) return;
      if (newPath !== rawPath) stats.folderRenames++;
      keptPaths.add(newPath);
      if (isNested) stats.nestedDataMerged++;

      if (newPath.toLowerCase().endsWith('.json')) {
        const rawText = await rawBytesOrStringFn('string');
        try {
          const parsed = parseJsonLenient(rawText);
          const isPowerFile = /^data\/[^/]+\/powers\/.+\.json$/i.test(newPath);
          const isRecipeFile = /^data\/[^/]+\/(recipe|recipes)\/.+\.json$/i.test(newPath);
          const isLootTableFile = /^data\/[^/]+\/(loot_table|loot_tables)\/.+\.json$/i.test(newPath);
          const isLangFile = /^assets\/[^/]+\/lang\/.+\.json$/i.test(newPath);
          const transformed = walkAndTransformJson(parsed, {
            target,
            migratePowers,
            migrateItems,
            stubUnsupported,
            isPowerFile,
            isRecipeFile,
            isLootTableFile,
            isLangFile,
            relPath: newPath,
            currentPowerId: isPowerFile ? powerPathToId(newPath) : null,
            radialMenus,
            defaultRadialResource,
            stats,
            warnings,
            stubbedTypes,
          });
          if (isPowerFile) inspectPowerTypesInJson(transformed, newPath, analysis);
          outZip.file(newPath, JSON.stringify(transformed, null, 2));
        } catch {
          outZip.file(newPath, rawText);
          if (/^data\//i.test(newPath)) warnings.push(`${newPath}: skipped JSON AST transform (malformed JSON)`);
        }
      } else if ((migrateItems || migratePowers) && /\.mcfunction$/i.test(newPath) && /^data\/[^/]+\/(function|functions)\//i.test(newPath)) {
        const rawText = await rawBytesOrStringFn('string');
        outZip.file(newPath, upgradeMcFunctionText(rawText, target, stats));
      } else {
        outZip.file(newPath, await rawBytesOrStringFn('uint8array'));
      }
    };

    for (const [rawPath, entry] of Object.entries(zip.files)) {
      if (entry.dir) continue;
      await processEntry(rawPath, (t) => entry.async(t), false);
    }

    // Merge pure data/assets from nested data-only JARs (e.g. bundled datapack libraries)
    for (const nested of loaded.nestedJars) {
      if (!nested.hasData || nested.hasClasses) continue;
      const entry = zip.file(nested.path);
      if (!entry) continue;
      const innerZip = await window.JSZip.loadAsync(await entry.async('uint8array'));
      for (const [p, innerEntry] of Object.entries(innerZip.files)) {
        if (innerEntry.dir) continue;
        if (/^(data|assets)\//i.test(p)) {
          await processEntry(p, (t) => innerEntry.async(t), true);
        }
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

    const { modId, toml } = buildModsToml(meta, target, keptPaths);
    const classInternalName = `org/originsconverter/generated/${modId}/AddonMod`;
    outZip.file(`${classInternalName}.class`, buildModStubClass(classInternalName, modId));
    outZip.file('META-INF/neoforge.mods.toml', toml);

    const outName = `${modId}-neoforge-${target.mc}.jar`;
    return { modId, outName, outZip, stats, warnings, stubbedTypes, analysis };
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
    const optStubUnsupported = document.getElementById('optStubUnsupported');
    const convertBtn = document.getElementById('convertBtn');
    const reportEl = document.getElementById('report');

    let loadedItems = [];

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

    const renderReport = (title, items, details = []) => {
      reportEl.classList.remove('hidden');
      const rows = items.map((item) => `<li class="tag-${item.level}">${escapeHtml(item.text)}</li>`).join('');
      const detailsHtml = details.map((d) => `
        <details class="report-details">
          <summary>${escapeHtml(d.summary)} (${d.lines.length})</summary>
          <ul>${d.lines.map((l) => `<li>${escapeHtml(l)}</li>`).join('')}</ul>
        </details>
      `).join('');
      reportEl.innerHTML = `<div class="report-head">${escapeHtml(title)}</div><ul class="report-list">${rows}</ul>${detailsHtml}`;
    };

    const triggerDownload = (blob, filename) => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      setTimeout(() => URL.revokeObjectURL(url), 4000);
    };

    async function handleFiles(fileList) {
      const files = [...(fileList || [])].filter(Boolean);
      if (files.length === 0) return;
      if (!window.JSZip) {
        renderReport('ERROR', [{ level: 'bad', text: 'JSZip failed to load. Check internet connection and reload.' }]);
        return;
      }

      loadedItems = [];
      convertBtn.disabled = true;

      try {
        const items = [];
        const details = [];
        for (const f of files) {
          const inspected = await inspectSingleArchive(f);
          loadedItems.push(inspected);
          const { meta, powerPaths, analysis, codeHits, nestedJars } = inspected;
          const prefix = files.length > 1 ? `[${meta.id}] ` : '';

          items.push({ level: 'ok', text: `${prefix}Detected "${meta.name || meta.id}" (${meta.id} v${meta.version || '?'})` });
          if (powerPaths.length > 0) {
            items.push({ level: 'ok', text: `${prefix}Scanned ${powerPaths.length} power JSON(s): ${analysis.supportedPowers.size} built-in Apoli type(s).` });
          }
          if (analysis.autoConvertiblePowers.size > 0) {
            items.push({ level: 'ok', text: `${prefix}Auto-convertible power(s): ${[...analysis.autoConvertiblePowers].join(', ')}` });
          }
          if (analysis.unsupportedPowers.size > 0) {
            const list = [...analysis.unsupportedPowers.keys()].slice(0, 5).join(', ');
            items.push({ level: 'warn', text: `${prefix}${analysis.unsupportedPowers.size} Java-only power type(s) (${list}).` });
            details.push({
              summary: `${prefix}Java-only Power Types`,
              lines: [...analysis.unsupportedPowers.entries()].map(([id, paths]) => `${id} -> ${paths.slice(0, 3).join(', ')}${paths.length > 3 ? ` (+${paths.length - 3} more)` : ''}`),
            });
          }
          if (analysis.unsupportedDetails.size > 0) {
            items.push({ level: 'warn', text: `${prefix}${analysis.unsupportedDetails.size} nested condition/action type(s) are not registered in Apoli.` });
            details.push({
              summary: `${prefix}Unregistered Nested Conditions / Actions`,
              lines: [...analysis.unsupportedDetails.entries()].map(([id, set]) => `${id} -> ${[...set].slice(0, 3).join(', ')}${set.size > 3 ? ` (+${set.size - 3} more)` : ''}`),
            });
          }
          if (nestedJars.length > 0) {
            const names = nestedJars.map((n) => `${n.id}${n.hasData && !n.hasClasses ? ' [data-only: will merge]' : ''}`).join(', ');
            items.push({ level: 'warn', text: `${prefix}Embedded JAR-in-JAR(s): ${names}` });
          }
          if (codeHits.length > 0) {
            items.push({ level: 'warn', text: `${prefix}${codeHits.length} custom Java class(es) will be stripped.` });
          }
        }

        dropzone.classList.add('loaded');
        if (loadedItems.length === 1) {
          const m = loadedItems[0].meta;
          dropTitle.textContent = `${m.name || m.id || files[0].name} (${m.version || '?'})`;
          dropHint.textContent = `Fabric ID: ${m.id || 'unknown'} — ready to convert`;
          convertBtn.textContent = 'CONVERT TO NEOFORGE .JAR';
        } else {
          dropTitle.textContent = `${loadedItems.length} FABRIC ADDONS LOADED`;
          dropHint.textContent = loadedItems.map((x) => x.meta.id).join(', ');
          convertBtn.textContent = `CONVERT ALL ${loadedItems.length} TO NEOFORGE .ZIP`;
        }
        convertBtn.disabled = false;
        renderReport('INSPECTION READY', items, details);
      } catch (err) {
        loadedItems = [];
        dropzone.classList.remove('loaded');
        dropTitle.textContent = 'DROP FABRIC .JAR(S) HERE';
        dropHint.textContent = 'Failed to read archive';
        renderReport('ERROR', [{ level: 'bad', text: err.message || String(err) }]);
      }
    }

    async function convertAll() {
      if (loadedItems.length === 0) return;
      convertBtn.disabled = true;

      const target = getSelectedTarget();
      const options = {
        target,
        renameFolders: optRenameFolders.checked,
        migratePowers: optMigratePowers.checked,
        migrateItems: optMigrateItems.checked,
        stubUnsupported: optStubUnsupported.checked,
      };

      try {
        const summary = [];
        const details = [];
        const multiBundle = loadedItems.length > 1 ? new window.JSZip() : null;

        for (const item of loadedItems) {
          const res = await convertSingleArchive(item, options);
          const { modId, outName, outZip, stats, warnings, stubbedTypes, analysis } = res;
          const prefix = loadedItems.length > 1 ? `[${modId}] ` : '';

          if (multiBundle) {
            multiBundle.file(outName, await outZip.generateAsync({ type: 'uint8array', compression: 'DEFLATE' }));
            summary.push({ level: 'ok', text: `${prefix}Packaged ${outName} (javafml @Mod)` });
          } else {
            const blob = await outZip.generateAsync({ type: 'blob', compression: 'DEFLATE' });
            triggerDownload(blob, outName);
            summary.push({ level: 'ok', text: `Downloaded ${outName} (modId="${modId}", javafml @Mod)` });
          }

          summary.push({ level: 'ok', text: `${prefix}Stripped ${stats.strippedCount} Fabric metadata/class file(s).` });
          if (stats.nestedDataMerged > 0) summary.push({ level: 'ok', text: `${prefix}Merged ${stats.nestedDataMerged} file(s) from embedded data JAR(s).` });
          if (stats.folderRenames > 0) summary.push({ level: 'ok', text: `${prefix}Migrated ${stats.folderRenames} 1.20.1 data folder path(s).` });
          if (stats.scalePowersConverted > 0) summary.push({ level: 'ok', text: `${prefix}Converted ${stats.scalePowersConverted} modify_size/scale power(s) -> vanilla attributes.` });
          if (stats.extraPowersConverted > 0) summary.push({ level: 'ok', text: `${prefix}Transpiled ${stats.extraPowersConverted} custom addon power/condition/action node(s) -> built-in Apoli.` });
          if (stats.reaMapped > 0) summary.push({ level: 'ok', text: `${prefix}Normalized ${stats.reaMapped} Reach Entity Attributes reference(s).` });
          if (stats.attrsMapped > 0) summary.push({ level: 'ok', text: `${prefix}Updated ${stats.attrsMapped} attribute/namespace ID(s).` });
          if (stats.keysRemapped > 0) summary.push({ level: 'ok', text: `${prefix}Normalized ${stats.keysRemapped} keybind(s) to NeoForge Primary/Secondary keys.` });
          if (stats.loadConditionsFixed > 0) summary.push({ level: 'ok', text: `${prefix}Converted ${stats.loadConditionsFixed} fabric:load_conditions block(s) -> neoforge:conditions.` });
          if (stats.tagsConverted > 0) summary.push({ level: 'ok', text: `${prefix}Converted ${stats.tagsConverted} item NBT "tag" object(s) -> components.` });
          if (stats.recipesUpdated > 0) summary.push({ level: 'ok', text: `${prefix}Updated ${stats.recipesUpdated} recipe field(s) to ${target.mc} schema.` });
          if (stats.lootTablesUpdated > 0) summary.push({ level: 'ok', text: `${prefix}Updated ${stats.lootTablesUpdated} loot table node(s) to 1.21+ schema.` });
          if (stats.idsRenamed > 0) summary.push({ level: 'ok', text: `${prefix}Renamed ${stats.idsRenamed} 1.20.1 vanilla/tag ID(s).` });
          if (stats.airIngredientsFixed > 0) summary.push({ level: 'ok', text: `${prefix}Rewrote ${stats.airIngredientsFixed} air-ingredient condition(s) -> apoli:empty.` });
          if (stats.powersStubbed > 0) summary.push({ level: 'ok', text: `${prefix}Stubbed ${stats.powersStubbed} unmapped Java power(s) as info-only origins:simple (${[...stubbedTypes].join(', ')}).` });
          if (stats.commandsUpdated > 0) summary.push({ level: 'ok', text: `${prefix}Upgraded ${stats.commandsUpdated} 1.20.1 command/mcfunction line(s) (Pehkui scale, particles, item NBT).` });
          if (stats.itemModelsGenerated > 0) summary.push({ level: 'ok', text: `${prefix}Generated ${stats.itemModelsGenerated} 1.21.4+ client item model definition(s).` });

          if (analysis.unsupportedPowers.size > 0) {
            summary.push({ level: 'warn', text: `${prefix}Kept unmapped Java power types: ${[...analysis.unsupportedPowers.keys()].join(', ')}` });
          }
          if (analysis.unsupportedDetails.size > 0) {
            details.push({
              summary: `${prefix}Unregistered Nested Conditions / Actions`,
              lines: [...analysis.unsupportedDetails.entries()].map(([id, set]) => `${id} -> ${[...set].join(', ')}`),
            });
          }
          if (warnings.length > 0) {
            details.push({ summary: `${prefix}Conversion Warnings`, lines: warnings });
          }
        }

        if (multiBundle) {
          const zipName = `origins-converted-neoforge-${target.mc}.zip`;
          const blob = await multiBundle.generateAsync({ type: 'blob', compression: 'DEFLATE' });
          triggerDownload(blob, zipName);
          summary.unshift({ level: 'ok', text: `Downloaded ${zipName} containing ${loadedItems.length} converted JAR(s).` });
        }

        renderReport('CONVERSION COMPLETE', summary, details);
      } catch (err) {
        renderReport('CONVERSION FAILED', [{ level: 'bad', text: err.message || String(err) }]);
      } finally {
        convertBtn.disabled = false;
      }
    }

    fileInput.addEventListener('change', () => {
      if (fileInput.files && fileInput.files.length > 0) handleFiles(fileInput.files);
    });
    dropzone.addEventListener('dragover', (e) => { e.preventDefault(); dropzone.classList.add('dragover'); });
    dropzone.addEventListener('dragleave', () => dropzone.classList.remove('dragover'));
    dropzone.addEventListener('drop', (e) => {
      e.preventDefault();
      dropzone.classList.remove('dragover');
      if (e.dataTransfer && e.dataTransfer.files && e.dataTransfer.files.length > 0) handleFiles(e.dataTransfer.files);
    });
    convertBtn.addEventListener('click', convertAll);
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
      parseSnbt,
      tryConvertItemNbtTag,
      parseJsonLenient,
      upgradeCommandLine,
      upgradeMcFunctionText,
    };
  }
})();
