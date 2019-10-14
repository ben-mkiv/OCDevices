package ben_mkiv.ocdevices.common.inventory;

import ben_mkiv.ocdevices.common.inventory.slots.BridgeSlot;
import ben_mkiv.ocdevices.common.tileentity.TileEntityBridge;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BridgeContainer extends ContainerBase {

    private TileEntityBridge te;

    public BridgeContainer(IInventory playerInventory, TileEntity te) {
        this.te = (TileEntityBridge) te;

        addOwnSlots();
        addPlayerSlots(playerInventory, 114);
    }

    private void addOwnSlots() {
        IItemHandler sideHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.EAST);
        this.addSlotToContainer(new BridgeSlot(te, sideHandler, 0, 80, 36));
    }

}
