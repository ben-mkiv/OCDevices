package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.TileEntityItemBench;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class BlockItemBench extends ocComponentBlock implements ITileEntityProvider {
    public static final String NAME = "itembench";
    public static final int GUI_ID = 7;
    public static Block DEFAULTITEM;

    public BlockItemBench() {
        super(NAME, null);
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileEntityItemBench();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        // Only execute on the server
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityItemBench)) {
            return false;
        }
        player.openGui(OCDevices.INSTANCE, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest){
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityItemBench) {
                ((TileEntityItemBench) te).removed();
            }
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Deprecated
    @Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

}
