package ben_mkiv.ocdevices.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ContainerBase extends Container {
    protected void addPlayerSlots(IInventory playerInventory, int yPos) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + yPos;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = yPos + 58;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    protected List<Slot> getInventorySlots(IInventory inventory){
        return getInventorySlots(inventory, false);
    }

    protected List<Slot> getInventorySlots(IInventory inventory, boolean invertRule){
        ArrayList<Slot> slots = new ArrayList<>();
        for(Slot slot : inventorySlots) {
            if (invertRule && slot.inventory.getClass().equals(inventory.getClass()))
                continue;

            if (!invertRule && !slot.inventory.equals(inventory))
                continue;

            slots.add(slot);
        }

        return slots;
    }

    public ArrayList<Slot> getInventorySlots(Class<? extends Slot> slotClass){
        ArrayList<Slot> foo = new ArrayList<>();
        for(Slot slot : inventorySlots)
            if(slotClass.isInstance(slot))
                foo.add(slot);

        return foo;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot){
        Slot sourceSlot = getSlot(slot);

        for(Slot targetSlot : getInventorySlots(sourceSlot.inventory, true))
            if(transferToSlot(sourceSlot, targetSlot) == 0) break;

        return ItemStack.EMPTY; //this has to return empty itemstack, otherwise it runs as loop
    }

    protected int transferToSlot(Slot source, Slot target){
        if(source.getStack().isEmpty())
            return 0;

        if(!target.isItemValid(source.getStack()))
            return source.getStack().getCount();

        //return if stack in slot is not equal to input

        ItemStack stack = source.getStack().copy();
        ItemStack stackOld = source.getStack().copy();

        int transfer = Math.min(target.getSlotStackLimit(), source.getStack().getCount());

        if(target.getHasStack()) {
            if (!ItemHandlerHelper.canItemStacksStack(source.getStack(), target.getStack()))
                return source.getStack().getCount();

            transfer = Math.min(transfer, Math.min(target.getStack().getMaxStackSize(), target.getSlotStackLimit()) - target.getStack().getCount());
            stackOld.shrink(transfer);
            stack = target.getStack();
            stack.grow(transfer);
        }
        else{
            stack.setCount(transfer);
            stackOld.shrink(transfer);
        }
        if(transfer > 0) {
            source.putStack(stackOld);
            target.putStack(stack);
        }

        return source.getStack().getCount();
    }



    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return true;
    }
}
