package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.TileEntityRecipeDictionary;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRecipeDictionary extends ocComponentBlock implements ITileEntityProvider {
    public static final int GUI_ID = 2;
    public static final String NAME = "recipe_dictionary";
    public static Block DEFAULTITEM;

    public BlockRecipeDictionary() {
        super(NAME, null);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityRecipeDictionary();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        // Only execute on the server
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityRecipeDictionary)) {
            return false;
        }
        player.openGui(OCDevices.INSTANCE, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityRecipeDictionary) {
                ((TileEntityRecipeDictionary) te).removed();
            }
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

}
