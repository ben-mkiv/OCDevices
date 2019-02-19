package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Case;
import li.cil.oc.common.block.property.PropertyRotatable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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
        return true;
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
        EnumFacing yaw = EnumFacing.fromAngle(placer.rotationYaw).getOpposite();
        return getDefaultState().withProperty(PropertyRotatable.Facing(), yaw);
    }

}
