package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MCMultiPart;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import ben_mkiv.ocdevices.common.tileentity.ColoredTile;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Case;
import li.cil.oc.common.block.property.PropertyRotatable;
import mcmultipart.util.MCMPWorldWrapper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockCase extends Case {
    public static final int tier = Tier.Three();
    public static final int GUI_ID = 3;

    public BlockCase(String caseName){
        super(tier);
        setRegistryName(OCDevices.MOD_ID, caseName);
        setUnlocalizedName(caseName);
        setCreativeTab(OCDevices.creativeTab);
    }

    @Deprecated
    @Override
    public boolean isFullBlock(IBlockState state){
        return true;
    }

    @Deprecated
    @Override
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    @Deprecated
    public boolean isBlockNormalCube(IBlockState state) {
        return true;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            if(ColoredTile.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ))
                return true;
        }

        // from here client only, use our own gui handler to unwrap multiparts before opening the container gui
        TileEntityCase caseTile = MultiPartHelper.getCaseFromTile(world.getTileEntity(pos));
        if (caseTile != null) {
            if (world instanceof MCMPWorldWrapper)
                world = MultiPartHelper.getRealWorld(caseTile);

            player.openGui(OCDevices.INSTANCE, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        return false;
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
    public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand){
        EnumFacing yaw = EnumFacing.fromAngle(placer.rotationYaw).getOpposite();
        return getDefaultState().withProperty(PropertyRotatable.Facing(), yaw);
    }

    // avoid to connect to fences/glass panes
    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.CENTER;
    }

}
