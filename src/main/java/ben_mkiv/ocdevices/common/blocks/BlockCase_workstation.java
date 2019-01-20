package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase_workstation;
import ben_mkiv.ocdevices.utils.AABBHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCase_workstation extends BlockCase {
    public static final String NAME = "case_workstation";
    public static Block DEFAULTITEM;
    static final AxisAlignedBB bb = new AxisAlignedBB(1d/16 * 2, 0, 0, 1d/16 * 14, 1, 1);

    public BlockCase_workstation(){
        super(NAME);
    }

    @Deprecated
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity te = source.getTileEntity(pos);

        if(te instanceof TileEntityCase_workstation)
            return AABBHelper.rotateHorizontal(bb, ((TileEntityCase_workstation) te).yaw());

        return FULL_BLOCK_AABB;
    }


    @Override
    public TileEntityCase createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCase_workstation();
    }
}
