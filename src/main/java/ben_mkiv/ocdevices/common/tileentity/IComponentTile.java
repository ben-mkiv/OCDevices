package ben_mkiv.ocdevices.common.tileentity;

import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IComponentTile extends ManagedEnvironment, EnvironmentHost {
    ManagedEnvironment component();
    TileEntity getTileEntity();

    default void onDisconnect(Node node){
        component().onDisconnect(node);
    }

    default void onConnect(Node node){
        component().onConnect(node);
    }

    default void onMessage(Message message){
        component().onMessage(message);
    }

    default boolean canUpdate(){
        return false;
    }

    default void markChanged(){}

    default void update(){
        component().update();
    }

    default void load(NBTTagCompound nbt) {
        component().load(nbt);
    }

    default void save(NBTTagCompound nbt) {
        component().save(nbt);
    }

    default World world(){ return getTileEntity().getWorld(); }

    default double xPosition(){ return getTileEntity().getPos().getX(); }

    default double yPosition(){ return getTileEntity().getPos().getY(); }

    default double zPosition(){ return getTileEntity().getPos().getZ(); }

}
