package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenHelper;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import ben_mkiv.ocdevices.common.tileentity.ColoredTile;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.utils.AABBHelper;
import ben_mkiv.ocdevices.utils.UtilsCommon;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.property.PropertyRotatable;
import li.cil.oc.common.block.property.PropertyTile;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

import static ben_mkiv.ocdevices.common.flatscreen.FlatScreen.maxScreenDepth;

public class BlockFlatScreen extends Block implements ITileEntityProvider {
    public final static int tier = Tier.Four();
    public final static String NAME = "flat_screen";
    public static Block DEFAULTITEM;
    public static final int GUI_ID = 4;

    static final AxisAlignedBB minimalBB = new AxisAlignedBB(0, 0, 0.999, 1, 1, 1);
    static final AxisAlignedBB emptyBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public BlockFlatScreen() {
        super(Material.IRON);
        setRegistryName(OCDevices.MOD_ID, NAME);
        setUnlocalizedName(NAME);
        setCreativeTab(OCDevices.creativeTab);
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public @Nonnull AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos){
        if(Minecraft.getMinecraft().player.isSneaking())
            return FULL_BLOCK_AABB.offset(pos);

        return emptyBB;
    }

    @Override
    @Deprecated
    public @Nonnull EnumBlockRenderType getRenderType(IBlockState state) {
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
    public void addCollisionBoxToList(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox,
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
        FlatScreenHelper helper = te.getHelper();
        minDepth = Math.min(Math.min(minDepth, helper.topLeft), helper.bottomLeft);
        minDepth = Math.min(Math.min(minDepth, helper.topRight), helper.bottomRight);

        AxisAlignedBB bb = minDepth > 0 ? new AxisAlignedBB(0, 0, 1d - minDepth, 1, 1, 1) : minimalBB;

        bb = AABBHelper.rotateVertical(bb, te.pitch());
        bb = AABBHelper.rotateHorizontal(bb, te.yaw());

        return bb;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileEntityFlatScreen();
    }

    @Override
    public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand){
        EnumFacing yaw = UtilsCommon.getYawForPlacement(placer, pos, facing);
        EnumFacing pitch = UtilsCommon.getPitchForPlacement(placer, pos, facing);

        IBlockState state = getDefaultState();
        state = state.withProperty(PropertyRotatable.Pitch(), pitch);
        state = state.withProperty(PropertyRotatable.Yaw(), yaw);

        return state;
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest){
        TileEntityFlatScreen screen = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));
        if(screen != null) screen.getMultiblock().split();

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }


    // avoid to connect to fences/glass panes
    @Override
    @Deprecated
    public @Nonnull BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.CENTER;
    }

    public @Nonnull ExtendedBlockState createBlockState() {
        return new ExtendedBlockState(this, ((new IProperty[]{PropertyRotatable.Pitch(), PropertyRotatable.Yaw()})), ((new IUnlistedProperty[]{PropertyTile.Tile()})));
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(PropertyRotatable.Pitch()).ordinal() << 2 | state.getValue(PropertyRotatable.Yaw()).getHorizontalIndex();
    }

    @Deprecated
    public @Nonnull IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PropertyRotatable.Pitch(), EnumFacing.getFront(meta >> 2)).withProperty(PropertyRotatable.Yaw(), EnumFacing.getHorizontal(meta & 3));
    }

    public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (state instanceof IExtendedBlockState && tile instanceof TileEntityFlatScreen) {
            TileEntityFlatScreen screen = (TileEntityFlatScreen)tile;
            return ((IExtendedBlockState) state)
                    .withProperty(PropertyTile.Tile(), screen)
                    .withProperty(PropertyRotatable.Pitch(), screen.pitch())
                    .withProperty(PropertyRotatable.Yaw(), screen.yaw());
        }

        return state;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote)
            return ColoredTile.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);

        // client only
        TileEntityFlatScreen screen = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));
        if (screen == null)
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);

        TileEntityFlatScreen origin = screen.origin();

        boolean touch = !origin.hasKeyboard();
        touch |=  origin.isTouchModeInverted() && !player.isSneaking();
        touch |= !origin.isTouchModeInverted() &&  player.isSneaking();

        if(touch) {
            return screen.touchEvent(player, side, new Vec3d(hitX, hitY, hitZ));
        }

        pos = origin.getPos();

        // open screen gui
        if(origin.buffer().getPowerState()) {
            player.openGui(OCDevices.MOD_ID, GUI_ID, MultiPartHelper.getRealWorld(origin), pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        else {
            player.sendStatusMessage(new TextComponentString("screen isnt powered"), true);
        }

        return false;
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entityIn){
        TileEntityFlatScreen screen = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));
        if (screen == null)
            super.onEntityWalk(world, pos, entityIn);
        else
            screen.walk(entityIn);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn){
        // return if the entity is not supposed to activate the current block (as it can collide with multiple blocks)
        if(!entityIn.getPosition().equals(pos))
            return;

        TileEntityFlatScreen screen = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));
        if (screen == null)
            super.onEntityCollidedWithBlock(world, pos, state, entityIn);
        else if(screen.pitch().equals(EnumFacing.UP))
            screen.walk(entityIn);
    }


}