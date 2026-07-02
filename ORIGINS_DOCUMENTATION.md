# Origins Addon & Datapack Development Guide

This guide explains how to create addons or datapacks for the Origins mod, specifically focusing on the data-driven system used in version 1.21.1.

## 1. Project Structure

Origins and its power system (Apoli) are entirely data-driven. This means you can add new content by placing JSON files in the correct directories.

### Folder Layout:
```text
data/
└── [your_namespace]/
    ├── origins/          <-- Origin definitions
    ├── origin_layers/   <-- Which origins appear in which menu
    ├── powers/           <-- Power definitions
    ├── entity_conditions/ <-- Custom entity conditions
    └── entity_actions/    <-- Custom entity actions
```

---

## 2. Creating an Origin

An Origin is defined by a JSON file in `data/[namespace]/origins/[origin_id].json`.

### Example: `data/my_addon/origins/vampire.json`
```json
{
	"powers": [
		"my_addon:burn_in_sun",
		"my_addon:lifesteal",
		"origins:night_vision"
	],
	"icon": {
		"item": "minecraft:potion",
		"components": {
			"minecraft:potion_contents": {
				"potion": "minecraft:harming"
			}
		}
	},
	"order": 10,
	"impact": 3,
	"name": "Vampire",
	"description": "A creature of the night that feasts on blood."
}
```

### Key Fields:
- `powers`: A list of power IDs.
- `icon`: The item that represents the origin. (Note: 1.20.5+ uses components instead of NBT).
- `order`: Determines the position in the menu.
- `impact`: 0 to 3 (visual difficulty indicator).
- `unchoosable`: (Optional) If true, the origin can only be given via command or other origins.

---

## 3. Creating a Power (Apoli System)

Powers are the logic behind an origin. They are defined in `data/[namespace]/powers/[power_id].json`.

### Example: `data/my_addon/powers/burn_in_sun.json`
```json
{
	"type": "origins:burn",
	"interval": 20,
	"burn_duration": 4,
	"condition": {
		"type": "origins:exposed_to_sun"
	},
	"name": "Photosensitivity",
	"description": "You begin to burn when exposed to direct sunlight."
}
```

### Building Blocks of Powers:
1. **Power Type**: The "Template" of the power (e.g., `origins:attribute`, `origins:toggle`, `origins:action_on_callback`).
2. **Conditions**: Checks if something is true (e.g., `origins:on_fire`, `origins:health`, `origins:biome`).
3. **Actions**: Does something (e.g., `origins:damage`, `origins:heal`, `origins:execute_command`).

---

## 4. Adding to the Selection Menu (Layers)

To make your origin show up in the "Origins" selection screen, you must add it to the `origins:origin` layer.

### Example: `data/origins/origin_layers/origin.json`
```json
{
  "replace": false,
  "origins": [
    "my_addon:vampire"
  ]
}
```
*Setting `"replace": false` ensures you are appending to the list.*

---

## 5. Advanced: Custom Java Power Types

If you are developing a Java mod addon, you can register your own Power Types, Actions, and Conditions.

### Registration Example:
```java
public static final Registry<PowerFactory> POWER_FACTORY = ApoliRegistries.POWER_FACTORY;

public static void register() {
    Registry.register(POWER_FACTORY, new ResourceLocation("my_addon", "my_custom_power"),
        new PowerFactory<>(new ResourceLocation("my_addon", "my_custom_power"),
            new SerializableData()
                .add("value", SerializableDataTypes.INT),
            data -> (type, entity) -> new MyCustomPower(type, entity, data.getInt("value")))
    );
}
```

---

## 6. Localization

Always use translation keys for names and descriptions to support multiple languages.
File: `assets/[namespace]/lang/en_us.json`

```json
{
  "origin.my_addon.vampire.name": "Vampire",
  "origin.my_addon.vampire.description": "A blood-drinking creature of the night.",
  "power.my_addon.burn_in_sun.name": "Sunlight Sensitivity",
  "power.my_addon.burn_in_sun.description": "You take burn damage in direct sunlight."
}
```
