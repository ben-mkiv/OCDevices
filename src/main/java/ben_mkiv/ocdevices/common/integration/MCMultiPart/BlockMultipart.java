package ben_mkiv.ocdevices.common.integration.MCMultiPart;

import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.slot.IPartSlot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public abstract class BlockMultipart implements IMultipart
{
    private IPartSlot slot;

    public BlockMultipart(IPartSlot slot1){
        super();
        slot = slot1;
    }

    @Override
    public IPartSlot getSlotForPlacement(World world, BlockPos pos, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ, EntityLivingBase placer){
        return slot;
    }

    @Override
    public IPartSlot getSlotFromWorld(IBlockAccess world, BlockPos pos, IBlockState state){
        return slot;
    }

    @Override
    public void onPartPlacedBy(IPartInfo part, EntityLivingBase placer, ItemStack stack) {
        part.getState().getBlock().onBlockPlacedBy(part.getPartWorld(), part.getPartPos(), part.getState(), placer, stack);
    }

    @Override
    public List<AxisAlignedBB> getOcclusionBoxes(IPartInfo part) {
        return Collections.singletonList(part.getState().getBoundingBox(part.getPartWorld(), part.getPartPos()));
    }
}