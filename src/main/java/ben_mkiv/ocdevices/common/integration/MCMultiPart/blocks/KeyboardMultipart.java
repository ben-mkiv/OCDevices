package ben_mkiv.ocdevices.common.integration.MCMultiPart.blocks;

import ben_mkiv.ocdevices.common.blocks.BlockKeyboard;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.api.slot.IPartSlot;
import net.minecraft.block.Block;
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

public class KeyboardMultipart implements IMultipart
{
    @Override
    public IPartSlot getSlotForPlacement(World world, BlockPos pos, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ, EntityLivingBase placer){
        return EnumCenterSlot.CENTER;
    }

    @Override
    public IPartSlot getSlotFromWorld(IBlockAccess world, BlockPos pos, IBlockState state){
        return EnumCenterSlot.CENTER;
    }

    @Override
    public Block getBlock()
    {
        return BlockKeyboard.DEFAULTITEM;
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

