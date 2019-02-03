package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.TileEntityKeyboard;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Keyboard;
import li.cil.oc.util.Color;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockKeyboard extends Keyboard {
    public final static int tier = Tier.Four();
    public final static String NAME = "keyboard";
    public static Block DEFAULTITEM;

    public BlockKeyboard() {
        super();
        setRegistryName(OCDevices.MOD_ID, NAME);
        setUnlocalizedName(NAME);
        setCreativeTab(OCDevices.creativeTab);
    }

    @Override
    public li.cil.oc.common.tileentity.Keyboard createNewTileEntity(World worldIn, int meta) {
        return new TileEntityKeyboard();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){

    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        // Only execute on the server
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityKeyboard)) {
            return false;
        }

        ItemStack stack = player.getHeldItem(hand);

        if(!(stack.getItem() instanceof ItemDye))
            return false;

        ((TileEntityKeyboard) te).setColor(Color.dyeColor(stack).getColorValue());

        return true;
    }

    public static TileEntityKeyboard getTileEntity(IBlockAccess world, BlockPos pos){
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityKeyboard ? (TileEntityKeyboard) te : null;
    }

}
