package ben_mkiv.ocdevices.common.inventory;

import ben_mkiv.ocdevices.common.inventory.slots.ItemBenchSlot;
import ben_mkiv.ocdevices.common.inventory.slots.NanomachinesSlot;
import ben_mkiv.ocdevices.common.tileentity.TileEntityItemBench;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemBenchContainer extends ContainerBase {
    private TileEntityItemBench te;

    public ItemBenchContainer(IInventory playerInventory, TileEntity te) {
        this.te = (TileEntityItemBench) te;

        addOwnSlots();
        addPlayerSlots(playerInventory, 114);
    }


    private void addOwnSlots() {
        IItemHandler inventory = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        this.addSlotToContainer(new ItemBenchSlot(inventory, 0, 80, 36));
        this.addSlotToContainer(new NanomachinesSlot(inventory, 1, 148, 85));
    }
}
