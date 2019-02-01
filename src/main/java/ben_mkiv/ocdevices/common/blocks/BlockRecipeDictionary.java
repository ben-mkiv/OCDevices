package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.common.tileentity.TileEntityRecipeDictionary;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRecipeDictionary extends ocComponentBlock implements ITileEntityProvider {
    public static final int GUI_ID = 1;
    public static final String NAME = "recipe_dictionary";
    public static Block DEFAULTITEM;

    public BlockRecipeDictionary() {
        super(NAME, null);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityRecipeDictionary();
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
