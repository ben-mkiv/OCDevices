package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.TileEntityKeyboard;
import li.cil.oc.common.block.Keyboard;
import li.cil.oc.util.Color;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
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

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

public class BlockKeyboard extends Keyboard {
    public final static String NAME = "keyboard";
    public static Block DEFAULTITEM;

    public BlockKeyboard() {
        super();
        setRegistryName(OCDevices.MOD_ID, NAME);
        setUnlocalizedName(NAME);
        setCreativeTab(OCDevices.creativeTab);
    }

    /* MCMP */
    //@Optional.Method(modid = "mcmultipart")
	//protected IMultipart getMultiPart() { return MCMultiPart.keyboardMultipart; }

    @Override
    public li.cil.oc.common.tileentity.Keyboard createNewTileEntity(World worldIn, int meta) {
        return new TileEntityKeyboard();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getBoundingBox(state, world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
        // hello neighbour, not going anywhere. no matter what you do....
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        // Only execute on the server
        if (world.isRemote) {
            return true;
        }

        TileEntityKeyboard keyboard = getTileEntity(world, pos);

        if (keyboard == null) {
            return false;
        }

        ItemStack stack = player.getHeldItem(hand);

        if(stack.getItem() instanceof ItemDye) {
            keyboard.setColor(Color.dyeColor(stack).getColorValue());
            return true;
        }

        /*
        for(TileEntity tile : MCMultiPart.getMCMPTiles(keyboard).values())
            if(tile instanceof Screen) {
                IBlockState stateScreen = tile.getWorld().getBlockState(tile.getPos());
                Block block = stateScreen.getBlock();
                return ((BlockFlatScreen) block).rightClick(tile.getWorld(), tile.getPos(), player, hand, stack, side, hitX, hitY, hitZ, false);
            }
           */
        return false;
    }

    public static TileEntityKeyboard getTileEntity(IBlockAccess world, BlockPos pos){
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityKeyboard ? (TileEntityKeyboard) te : null;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        //if(true) return;

        if(world.isRemote)
            return;

        // as for some reason the facing isnt set correct, we have to fix it here!?
        TileEntityKeyboard te = getTileEntity(world, pos);
        if(te != null) {
            te.setFromEntityPitchAndYaw(placer);

            EnumFacing pitch = te.pitch();

            if(pitch.equals(UP) || pitch.equals(DOWN)) {
                pitch = pitch.getOpposite();
            }

            EnumFacing yaw = !pitch.equals(UP) ? placer.getHorizontalFacing().getOpposite() : placer.getHorizontalFacing();

            // set the new values
            te.trySetPitchYaw(pitch, yaw);
        }
    }



}
