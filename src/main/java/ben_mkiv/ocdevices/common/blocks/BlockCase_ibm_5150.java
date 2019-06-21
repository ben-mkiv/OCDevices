package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase_ibm_5150;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockCase_ibm_5150 extends BlockCase {
    public static final String NAME = "case_ibm_5150";
    public static Block DEFAULTITEM;
    private static final AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, 1d/16 * 6, 1);

    public BlockCase_ibm_5150(){
        super(NAME);
    }

    @Deprecated
    @Override
    public @Nonnull AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return bb;
    }

    @Override
    public TileEntityCase createTileEntity(World worldIn, IBlockState state) {
        return new TileEntityCase_ibm_5150(state.getValue(caseTier));
    }
}
