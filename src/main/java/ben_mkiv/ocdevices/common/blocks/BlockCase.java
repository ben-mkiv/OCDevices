package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import li.cil.oc.client.ColorHandler;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Case;
import li.cil.oc.util.Color;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockCase extends Case {
    public static final int tier = Tier.Three();

    public BlockCase(String caseName){
        super(tier);
        setRegistryName(OCDevices.MOD_ID, caseName);
        setUnlocalizedName(caseName);
        setCreativeTab(OCDevices.creativeTab);
    }

    @Deprecated
    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return true;
    }

    @Deprecated
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return true;
    }

    @Override
    @Deprecated
    public boolean isBlockNormalCube(IBlockState state) {
        return true;
    }


    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    private static TileEntityCase getTileEntity(IBlockAccess world, BlockPos pos){
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityCase ? (TileEntityCase) te : null;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if(world.isRemote)
            return;

        // as for some reason the facing isnt set correct, we have to fix it here!?
        TileEntityCase te = getTileEntity(world, pos);
        if(te != null) {
            te.setFromFacing(placer.getHorizontalFacing().getOpposite());
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ColorHandler implements IBlockColor {
        private final Block computer;

        public ColorHandler(Block computer) {
            this.computer = computer;
        }

        @Override
        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex){
            TileEntityCase te = getTileEntity(worldIn, pos);
            int color = te == null ? 0 : te.getColor();

            if(color >= 0 && color <= 15)
                color = EnumDyeColor.values()[color].getColorValue();

            return color;
        }
    }



}
