# Cobbleworks
Use a config file to define what gets spawned when water & lava interact

### Config
Config is controled by `.json` files in the `./config/cobbleworks/` directory.  
The default (mildly opinionated) config is as follows:
```json
{
	"definition": [
		{
			"type": "stone[variant=diorite]",
			"distance": "*",
			"chance": 0.05
		},
		{
			"type": "stone[variant=andesite]",
			"distance": "*",
			"chance": 0.05
		},
		{
			"type": "stone[variant=granite]",
			"distance": "*",
			"chance": 0.05
		}
	]
}
```

### Config explanation
The config file consists of a `definition` key which is an array of blocks that can be spawned.  
Each element in the definition is independent and mostly doesn't rely on others<sup>1</sup>

Valid keys within a definition element:
 - `type`/`types` (`String`/`Array[String]`, required) - The block that should be spawned. The format is `base_block_name` with the optional extension `base_block_name[meta_key=meta_value]` if you need to specify a meta state<sup>2</sup>. If both `type` and `types` are present, `types` takes priority. When a `types` array is present, a random element from the array will be chosen when the override takes place.
 - `distance` (`String`, required) - The level of the water interacting with lava, if only using a single water source, this is just the distance apart that the lava and water source are from the interaction point. The value `"*"` acts as a wildcard for all distances.
 - `chance` (`float`, default: `1.0`) - The chance that the replacement will occur. Valid range `[0.0,1.0]` with 0.0 being never and 1.0 being always
 - `priority` (`int`, default: `1`) - Controls ordering of overrides, higher priorities apply later (and thus the highest priority will never be overwritten) 

### Larger example config
For an example of a larger valid config, see [example.json](example.json).

### Notes
1: The chances are sequential, thus entries at the top of the definition are less likely than those at the bottom, due to later entries overwriting them some % of the time, this can be modified by using the `priority` key  
2: In order to find the desired meta state for a block, hit f3 in game and look at a block. In the upper right, you should see a value showing the block name (eg: `nuclearcraft:ore`) and then directly below it a second value that is in the format `meta_key: meta_value`.