package ben_mkiv.ocdevices.common.tileentity;

import li.cil.oc.common.Tier;
import li.cil.oc.common.tileentity.Case;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.HashSet;

public class TileEntityCase extends Case {

    HashSet<EnumFacing> connectToSides = new HashSet<>();

    public TileEntityCase(){
        super(Tier.Three());
        setColor(0);
        connectToSides.add(EnumFacing.UP);
        connectToSides.add(EnumFacing.DOWN);
        connectToSides.add(EnumFacing.NORTH);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        if(!connectToSides.contains(toLocal(facing)))
            return false;

        return super.hasCapability(capability, facing);
    }

}

