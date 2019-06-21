## FlatScreen Panel

acts like Tier 3 Screen (Resolution/Color Depth/Multiblock support and can be dyed)

_Note: Touchscreen Support might be inaccurate at this point, but will probably be fixed in later releases_

![](https://i.imgur.com/3F73Fcm.png)

Recipe
* 4x Piston
* 4x Iron
* 1x Screen Tier 3

### Methods
```lua
component.screen.setDepth(int:depth [, String:side)
-- sets the side depth (all if no side is provided as argument)
-- valid values for depth [0 - 32]
-- valid values for side [top, left, right, bottom, all]
```
```lua
component.screen.setOpacity([boolean:opaque])
component.screen.setOpacity([integer:opacity])
-- sets screen tint
-- valid values [0 - 100] or [true/false]
```