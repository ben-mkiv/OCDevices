package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.TileEntityBridge;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockBridge extends ocComponentBlock implements ITileEntityProvider {
    public static final int GUI_ID = 5;
    public static final String NAME = "bridge";
    public static Block DEFAULTITEM;

    public BlockBridge() {
        super(NAME, null);
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileEntityBridge();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        // Only execute on the server
        if (world.isRemote) {
            return true;
        }

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityBridge)) {
            return false;
        }

        player.openGui(OCDevices.INSTANCE, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

}

