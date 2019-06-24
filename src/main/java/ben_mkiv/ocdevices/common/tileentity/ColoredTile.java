package ben_mkiv.ocdevices.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ColoredTile {
    int getColor();
    void setColor(int color);
    void onColorChanged();

    static boolean isColoringItem(ItemStack stack) {
        return stack.getItem() instanceof ItemDye;
    }

    static int getColorFromStack(ItemStack stack){
        float[] vals = li.cil.oc.util.Color.dyeColor(stack).getColorComponentValues();
        return new java.awt.Color(vals[0], vals[1], vals[2]).getRGB();
    }

    default boolean setColor(ItemStack stack){
        if(!isColoringItem(stack))
            return false;

        setColor(getColorFromStack(stack));
        return true;
    }

    static boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof ColoredTile) {
            if(world.isRemote)
                return isColoringItem(player.getHeldItem(hand));
            else
                return ((ColoredTile) tile).setColor(player.getHeldItem(hand));
        }

        return false;
    }
}
