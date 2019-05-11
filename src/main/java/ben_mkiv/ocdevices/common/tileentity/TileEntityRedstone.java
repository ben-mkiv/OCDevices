package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.component.RedstoneComponent;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.TileEntityEnvironment;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRedstone extends TileEntityEnvironment implements IComponentTile {
    RedstoneComponent component = new RedstoneComponent(this);

    public TileEntityRedstone(){
        node = component.node();
    }

    public TileEntity getTileEntity(){
        return this;
    }

    @Override
    public ManagedEnvironment component(){
        return component;
    }

}
