package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase_slim_oc;
import ben_mkiv.ocdevices.utils.AABBHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockCase_slim_oc extends BlockCase {
    public static final String NAME = "case_slim_oc";
    public static Block DEFAULTITEM;
    private static final AxisAlignedBB bb = new AxisAlignedBB(1d/16 * 4, 0, 0, 1d/16 * 12, 1, 1);

    public BlockCase_slim_oc(){
        super(NAME);
    }

    @Deprecated
    @Override
    public @Nonnull AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity te = source.getTileEntity(pos);

        if(te instanceof TileEntityCase_slim_oc)
            return AABBHelper.rotateHorizontal(bb, ((TileEntityCase_slim_oc) te).yaw());

        return FULL_BLOCK_AABB;
    }


    @Override
    public TileEntityCase createTileEntity(World worldIn, IBlockState state) {
        return new TileEntityCase_slim_oc(state.getValue(caseTier));
    }
}
