package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreen;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import ben_mkiv.ocdevices.common.tileentity.ColoredTile;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMultiblockDisplay;
import li.cil.oc.common.Tier;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockFlatScreen extends Block implements ITileEntityProvider, IScreenBlock {
    public final static int tier = Tier.Four();
    public final static String NAME = "flat_screen";
    public static Block DEFAULTITEM;
    public static final int GUI_ID = 4;


    public BlockFlatScreen() {
        super(Material.IRON);
        setRegistryName(OCDevices.MOD_ID, NAME);
        setUnlocalizedName(NAME);
        setCreativeTab(OCDevices.creativeTab);
    }

    public int maxScreenDepth(){
        return FlatScreen.maxScreenDepth;
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public @Nonnull AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos){
        return getSelectionBox(pos);
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
        collidingBoxes.addAll(getAABBList(state, world, pos, entityBox));
    }

    @Override
    @Deprecated
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)    {
        return getAABB(state, source, pos);
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileEntityFlatScreen();
    }

    @Override
    public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand){
        return IOrientableBlock.getStateForPlacement(getDefaultState(), pos, facing, placer);
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest){
        return removedByPlayer(world, pos) && super.removedByPlayer(state, world, pos, player, willHarvest);
    }


    // avoid to connect to fences/glass panes
    @Override
    @Deprecated
    public @Nonnull BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.CENTER;
    }

    @Override
    public @Nonnull ExtendedBlockState createBlockState() {
        return IOrientableBlock.createBlockState(this);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return IOrientableBlock.getMetaFromState(state);
    }

    @Override
    @Deprecated
    public @Nonnull IBlockState getStateFromMeta(int meta) {
        return IOrientableBlock.getStateFromMeta(getDefaultState(), meta);
    }

    @Override
    public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        return IOrientableBlock.getExtendedState(state, world, pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote)
            return ColoredTile.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);

        // client only
        TileEntityMultiblockDisplay screen = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));
        if (screen == null || !(screen instanceof TileEntityFlatScreen))
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);

        TileEntityFlatScreen origin = (TileEntityFlatScreen) screen.origin();

        boolean touch = !origin.hasKeyboard();
        touch |=  origin.isTouchModeInverted() && !player.isSneaking();
        touch |= !origin.isTouchModeInverted() &&  player.isSneaking();

        if(touch) {
            return ((TileEntityFlatScreen) screen).touchEvent(player, side, new Vec3d(hitX, hitY, hitZ));
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
        TileEntityMultiblockDisplay screen = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));
        if (screen instanceof TileEntityFlatScreen)
            ((TileEntityFlatScreen) screen).walk(entityIn);
        else
            super.onEntityWalk(world, pos, entityIn);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn){
        // return if the entity is not supposed to activate the current block (as it can collide with multiple blocks)
        if(!entityIn.getPosition().equals(pos))
            return;

        TileEntityMultiblockDisplay screen = MultiPartHelper.getScreenFromTile(world.getTileEntity(pos));
        if (screen instanceof TileEntityFlatScreen)
            ((TileEntityFlatScreen) screen).walk(entityIn);
        else if(screen.pitch().equals(EnumFacing.UP))
            super.onEntityCollidedWithBlock(world, pos, state, entityIn);
    }

}