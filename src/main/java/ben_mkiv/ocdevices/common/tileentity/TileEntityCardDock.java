package ben_mkiv.ocdevices.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityCardDock extends ocComponentHostTE { //implements IAudioSource

    public TileEntityCardDock() {
        super("carddock", 1, true, false, false, Visibility.Neighbors);
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
    }
    
    @Callback(doc = "function():array; returns the address, type of component in the dock and the address, machine its bound to", direct = true)
    public Object[] getComponent(Context context, Arguments args) {
        if(components.get(0).getComponentItem().isEmpty())
            return new Object[]{ false, "no component in card dock" };

        if(components.get(0).node() == null)
            return new Object[]{ false, "no environment available" };

        return new Object[]{
                new Object[]{
                        new Object[]{ components.get(0).node().address(), components.get(0).node().toString()},
                        new Object[]{ components.get(0).getBoundMachine().address(), components.get(0).getBoundMachine()}} };
    }

    @Callback(doc = "function():boolean; binds the component in the dock to the machine that issues this command", direct = true)
    public Object[] bindComponent(Context context, Arguments args) {
        if(components.get(0).getComponentItem().isEmpty())
            return new Object[]{ false, "no component in card dock" };

        if(components.get(0).node() == null)
            return new Object[]{ false, "no environment available" };

        if(components.get(0).getBoundMachine() != null){
            if(components.get(0).getBoundMachine().equals(context.node()))
                return new Object[]{ false, "component already bound to this machine" };
            else
                return new Object[]{ false, "component already bound to another machine" };
        }

        components.get(0).bindMachine(context.node());

        return new Object[]{ true };
    }

    @Callback(doc = "function():boolean; unbinds the component in the dock, this can only be used by the machine the component is bound to", direct = true)
    public Object[] unbindComponent(Context context, Arguments args) {
        if(components.get(0).getComponentItem().isEmpty())
            return new Object[]{ false, "no component in card dock" };

        if(components.get(0).getBoundMachine() == null)
            return new Object[]{ false, "component isnt bound to any machine" };

        if(!components.get(0).getBoundMachine().equals(context.node()))
            return new Object[]{ false, "component bound to another machine" };

        components.get(0).unbindMachine(context.node());

        return new Object[]{ true };
    }

    //combat for old nbt structure, remove in final release
    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);

        if(nbt.hasKey("component"))
            components.get(0).readFromNBT(nbt.getCompoundTag("component"));
    }

    /*

    // Computronics Audio compat
    @Override
    public int getSourceId(){
        if(components.get(0).node() instanceof IAudioSource)
            return ((IAudioSource) components.get(0).node().host()).getSourceId();

        return -1;
    }

    @Override
    public boolean connectsAudio(EnumFacing var1){
        //if(FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT))
        //    return components.get(0) instanceof pl.asie.computronics
        // do something about clients... (if the carddock gui gets opened they disconnect cables)
        return components.get(0).node() != null && components.get(0).node().host() instanceof IAudioSource;
    }
    */


}
