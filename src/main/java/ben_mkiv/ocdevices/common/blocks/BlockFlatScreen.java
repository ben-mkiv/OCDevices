package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.utils.AABBHelper;
import ben_mkiv.ocdevices.utils.UtilsCommon;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Screen;
import li.cil.oc.common.block.property.PropertyRotatable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

import static ben_mkiv.ocdevices.common.flatscreen.FlatScreen.maxScreenDepth;
import static ben_mkiv.ocdevices.common.flatscreen.FlatScreen.precision;

public class BlockFlatScreen extends Screen {
    public final static int tier = Tier.Three();
    public final static String NAME = "flat_screen";
    public static Block DEFAULTITEM;

    static final AxisAlignedBB minimalBB = new AxisAlignedBB(0, 0, 0.999, 1, 1, 1);
    static final AxisAlignedBB emptyBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public BlockFlatScreen() {
        super(tier);
        setRegistryName(OCDevices.MOD_ID, NAME);
        setUnlocalizedName(NAME);
        setCreativeTab(OCDevices.creativeTab);
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos){
        if(Minecraft.getMinecraft().player.isSneaking())
            return FULL_BLOCK_AABB.offset(pos);

        return emptyBB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, AxisAlignedBB entityBox,
                                      @Nonnull List<AxisAlignedBB> collidingBoxes, Entity entity, boolean advanced) {

        TileEntityFlatScreen te = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));

        if(te == null) {
            collidingBoxes.add(getBoundingBox(state, world, pos));
            return;
        }

        for(AxisAlignedBB bb : te.boundingBoxes)
            addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
    }

    @Override
    @Deprecated
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)    {
        TileEntityFlatScreen te = MultiPartHelper.getScreenFromTile(source.getTileEntity(pos));

        if(te == null)
            return FULL_BLOCK_AABB;

        float minDepth = maxScreenDepth;
        for(float f : te.getHelper().getDepthForBlock(te))
            if(f < minDepth) minDepth = f;

        AxisAlignedBB bb = minDepth > 0 ? new AxisAlignedBB(0, 0, 1d - (precision*minDepth), 1, 1, 1) : minimalBB;

        bb = AABBHelper.rotateVertical(bb, te.pitch());
        bb = AABBHelper.rotateHorizontal(bb, te.yaw());

        return bb;
    }

    @Override
    public TileEntityFlatScreen createNewTileEntity(World worldIn, int meta) {
        return new TileEntityFlatScreen();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
        EnumFacing yaw = UtilsCommon.getYawForPlacement(placer, pos, facing);
        EnumFacing pitch = UtilsCommon.getPitchForPlacement(placer, pos, facing);

        IBlockState state = getDefaultState();
        state = state.withProperty(PropertyRotatable.Pitch(), pitch);
        state = state.withProperty(PropertyRotatable.Yaw(), yaw);

        return state;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
        TileEntityFlatScreen screen = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));
        screen.getMultiblock().split();

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }


    // avoid to connect to fences/glass panes
    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.CENTER;
    }


}