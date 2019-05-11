package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.common.component.RedstoneComponent;
import ben_mkiv.ocdevices.common.tileentity.TileEntityRedstone;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRedstone extends ocComponentBlock implements ITileEntityProvider {
    public static final String NAME = "redstone";
    public static Block DEFAULTITEM;

    public BlockRedstone(){
        super(NAME, RedstoneComponent.class);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityRedstone();
    }
}
