## Card Dock

allows to connect an external Card to any machine in the Network


![](https://i.imgur.com/GLRbKsQ.png)

Recipe
* 2x Iron
* 2x Microchip Tier2
* 1x Microchip Tier1
* 1x Component Bus Tier1
* 1x Card Container Tier3
* 1x PCB
* 1x Cable



### Methods
```lua
bindComponent():boolean
-- binds the component in the dock to the machine that issues this command
```
```lua
unbindComponent():boolean
-- unbinds the component in the dock
-- this can only be used by the machine the component is bound to
```
```lua
getComponent():array
-- returns address/type of component in the dock
-- and the address/machine the component is bound to
```