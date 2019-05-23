package ben_mkiv.ocdevices.common.blocks.computronics;

import ben_mkiv.ocdevices.common.blocks.BlockCardDock;
import ben_mkiv.ocdevices.common.tileentity.computronics.TileEntityCardDockComputronics;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockCardDockComputronics extends BlockCardDock {
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileEntityCardDockComputronics();
    }
}
