package ben_mkiv.ocdevices.common.bridge;

import ben_mkiv.ocdevices.common.tileentity.TileEntityBridge;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class BridgeLocation{
    public int dimension;
    public BlockPos position;

    BridgeLocation(TileEntityBridge tile){
        dimension = tile.getWorld().provider.getDimension();
        position = tile.getPos();
    }

    public boolean equals(BridgeLocation bridge){
        return bridge.dimension == dimension && bridge.position.equals(position);
    }

    public TileEntityBridge getLinkedBlock(){
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension);

        if(!world.isBlockLoaded(position))
            return null;

        TileEntity tile = world.getTileEntity(position);

        return tile instanceof TileEntityBridge ? (TileEntityBridge) tile : null;
    }


}