package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.items.UpgradeItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IUpgradeBlock {
    boolean applyUpgrade(ItemStack stack);

    default boolean isValidUpgradeItem(ItemStack stack){
        return stack.getItem() instanceof UpgradeItem;
    }

    static boolean onBlockActivated(World world, BlockPos pos, EntityPlayer player, EnumHand hand){
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = world.getTileEntity(pos);

        if(tile instanceof IUpgradeBlock && ((IUpgradeBlock) tile).applyUpgrade(stack)) {
            ItemStack newStack = stack.copy();
            newStack.setCount(stack.getCount()-1);
            player.setHeldItem(hand, newStack);
            return true;
        }

        return false;
    }
}
