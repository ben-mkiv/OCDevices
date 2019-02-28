package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase_next;
import ben_mkiv.ocdevices.utils.AABBHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockCase_next extends BlockCase {
    public static final String NAME = "case_next";
    public static Block DEFAULTITEM;
    private static final AxisAlignedBB bb = new AxisAlignedBB(1d/16 * 5, 0, 0, 1d/16 * 11, 1, 1);

    public BlockCase_next(){
        super(NAME);
    }

    @Deprecated
    @Override
    public @Nonnull AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity te = source.getTileEntity(pos);

        if(te instanceof TileEntityCase_next)
            return AABBHelper.rotateHorizontal(bb, ((TileEntityCase_next) te).yaw());

        return FULL_BLOCK_AABB;
    }


    @Override
    public TileEntityCase createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCase_next();
    }
}
