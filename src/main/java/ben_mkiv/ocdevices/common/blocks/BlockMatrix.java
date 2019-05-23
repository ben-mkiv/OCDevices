package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.common.matrix.MatrixWidget;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMatrix;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockMatrix extends ocComponentBlock implements ITileEntityProvider, IOrientableBlock, IScreenBlock {
    public static final String NAME = "matrix";
    public static Block DEFAULTITEM;

    public BlockMatrix() {
        super(NAME, null);
    }

    public int maxScreenDepth(){
        return MatrixWidget.matrixResolution;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileEntityMatrix();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!hand.equals(EnumHand.MAIN_HAND))
            return false;

        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMatrix) {
                return ((TileEntityMatrix) te).activated(side, new Vec3d(hitX, hitY, hitZ));
            }
        }

        return true;
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
    @Deprecated
    public @Nonnull
    EnumBlockRenderType getRenderType(IBlockState state) {
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

    @Deprecated
    @SideOnly(Side.CLIENT)
    public @Nonnull
    AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos){
        return getSelectionBox(pos);
    }

    @Override
    public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand){
        return IOrientableBlock.getStateForPlacement(getDefaultState(), pos, facing, placer);
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


}