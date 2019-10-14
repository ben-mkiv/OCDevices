package ben_mkiv.ocdevices.common.inventory;

import ben_mkiv.ocdevices.common.inventory.slots.DatabaseAdapterSlot;
import ben_mkiv.ocdevices.common.tileentity.TileEntityDatabaseAdapter;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class DatabaseAdapterContainer extends ContainerBase {

    private TileEntityDatabaseAdapter te;

    public DatabaseAdapterContainer(IInventory playerInventory, TileEntity te) {
        this.te = (TileEntityDatabaseAdapter) te;

        addOwnSlots();
        addPlayerSlots(playerInventory, 114);
    }


    private void addOwnSlots() {
        IItemHandler sideHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.EAST);
        this.addSlotToContainer(new DatabaseAdapterSlot(te, sideHandler, 0, 80, 36));
    }

}
