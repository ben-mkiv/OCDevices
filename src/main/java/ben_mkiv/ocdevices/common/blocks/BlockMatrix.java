package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.common.tileentity.TileEntityMatrix;
import ben_mkiv.ocdevices.utils.UtilsCommon;
import li.cil.oc.common.block.property.PropertyRotatable;
import li.cil.oc.common.block.property.PropertyTile;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;

public class BlockMatrix extends ocComponentBlock implements ITileEntityProvider, IOrientableBlock {
    public static final String NAME = "matrix";
    public static Block DEFAULTITEM;

    public BlockMatrix() {
        super(NAME, null);
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