package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Case;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        return false;
    }

    @Deprecated
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @Deprecated
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }


    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if(world.isRemote)
            return;

        // as for some reason the facing isnt set correct, we have to fix it here!?
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityCase) {
            TileEntityCase caseTile = (TileEntityCase) te;

            caseTile.setFromFacing(placer.getHorizontalFacing().getOpposite());
        }
    }


}
