package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.common.flatscreen.FlatScreenHelper;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMultiblockDisplay;
import ben_mkiv.ocdevices.utils.AABBHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;

public interface IScreenBlock {
    AxisAlignedBB minimalBB = new AxisAlignedBB(0, 0, 0.999, 1, 1, 1);
    AxisAlignedBB emptyBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    int maxScreenDepth();

    default AxisAlignedBB getSelectionBox(BlockPos pos){
        if(Minecraft.getMinecraft().player.isSneaking())
            return FULL_BLOCK_AABB.offset(pos);

        return emptyBB;
    }

    default AxisAlignedBB getAABB(IBlockState state, IBlockAccess source, BlockPos pos)    {
        TileEntityMultiblockDisplay te = MultiPartHelper.getScreenFromTile(source.getTileEntity(pos));

        if(te == null)
            return FULL_BLOCK_AABB;

        float minDepth = maxScreenDepth();
        FlatScreenHelper helper = te.getHelper();
        minDepth = Math.min(Math.min(minDepth, helper.topLeft), helper.bottomLeft);
        minDepth = Math.min(Math.min(minDepth, helper.topRight), helper.bottomRight);

        AxisAlignedBB bb = minDepth > 0 ? new AxisAlignedBB(0, 0, 1d - minDepth, 1, 1, 1) : minimalBB;

        bb = AABBHelper.rotateVertical(bb, te.pitch());
        bb = AABBHelper.rotateHorizontal(bb, te.yaw());

        return bb;
    }

    @Deprecated
    default List<AxisAlignedBB> getAABBList(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox) {
        List<AxisAlignedBB> collidingBoxes = new ArrayList<>();
        TileEntityMultiblockDisplay te = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));

        if(te == null) {
            for(AxisAlignedBB bb : te.boundingBoxes)
                if(entityBox.intersects(bb.offset(pos)))
                    collidingBoxes.add(bb.offset(pos));
        } else {
            AxisAlignedBB bb = getAABB(state, world, pos).offset(pos);
            if(entityBox.intersects(bb))
                collidingBoxes.add(bb);
        }

        return collidingBoxes;
    }

    default boolean removedByPlayer(World world, @Nonnull BlockPos pos){
        TileEntityMultiblockDisplay screen = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));
        if(screen != null) {
            screen.getMultiblock().split();
            return true;
        }

        return false;
    }
}
