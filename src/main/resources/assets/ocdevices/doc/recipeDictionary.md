# Recipe Dictionary

with the power of a disassembler, a database and a texture picker its now possible to extract recipes out of items.

![](https://i.imgur.com/2AV1d8x.png)

Recipe:
* 4x Iron Ingot
* 1x OpenComputers Disassembler
* 1x OpenComputers Texture Picker
* 1x OpenComputers Microchip Tier 3
* 1x OpenComputers PCB
* 1x OpenComputers Database Upgrade Tier 3

### GUI
![](https://i.imgur.com/sMcxEZK.png)

* middle slot => holds Items which can be scanned for their recipe with `getRecipeFromItem()` 
* bottom right slot => holds a database upgrade which stores the recipes _(note: the stackdata gets modified so you cant set an item in the database gui and just use the recipe, also it would probably make trouble when used for other use cases, so clear the database with sneak + rightclick if you want to use it for something else)_

### technical notes
the dictionary hosts 2 internal databases, which connect as managed_database to your network, and can be used to configure AE2 devices for example

`output` => contains the current loaded recipe output

`ingredients` =>  holds a list of all possible items for the current loaded recipe


## methods
```lua
getDatabase([Integer:databaseIndex])
getDatabase([String:databaseName])
-- returns the internal databases name and address
```
```lua
getRecipeFromItem():integer
-- tries to break down the current item into its recipe
-- recipe gets stored in the recipe database
-- returns the database slot in which the recipe got stored
```
```lua
getRecipeFromItem(String:itemName[, Integer:stackMeta[, String:stackNBT]]):integer
-- same as above, but wont need an item in the internal slot
-- this method is only available if enabled in the mod config
```
```lua
loadRecipeFromDatabase(Integer:slot, [Integer:recipeIndex)
-- loads a known recipe from the specified slot
-- from the recipe database to the internal databases
-- recipeIndex is a optional parameter to use a alternative recipe
```

## Database Layout

### output database
only holds one stack, which is the current recipe output

### ingredients database
each column represents one slot of the current recipe, while every additional row has alternative material inputs

if these images wont help, well then go play fortnite
_(the example below is for minecraft:stick recipe)_

![](https://i.imgur.com/gUez8lV.png)
![](https://i.imgur.com/BMLQ8I9.png)
