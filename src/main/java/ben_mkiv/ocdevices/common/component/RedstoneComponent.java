package ben_mkiv.ocdevices.common.component;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import li.cil.oc.server.component.Redstone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class RedstoneComponent extends Redstone.Bundled {

    public RedstoneComponent(EnvironmentHost container){
        super(container);
    }

    @Callback(doc = "function(String:side):boolean; gets the connected block")
    public Object[] getConnectedBlock(Context context, Arguments args) {
        if(args.count() == 0)
            return new Object[] { false, "first argument has to be a valid side" };

        EnumFacing facing = null;

        if(args.isString(0))
            facing = EnumFacing.byName(args.checkString(0));

        if(args.isInteger(0) && args.checkInteger(0) < EnumFacing.values().length)
            facing = EnumFacing.values()[args.checkInteger(0)];

        if(facing == null)
            return new Object[] { false, "invalid side" };

        IBlockState state = redstone().world().getBlockState(getPos().offset(facing));
        String blockName = state.getBlock().getRegistryName().toString();

        return new Object[]{ blockName, state.getBlock().getMetaFromState(state) };
    }

    private BlockPos getPos(){
        return new BlockPos(redstone().xPosition(), redstone().yPosition(), redstone().zPosition());
    }


}

