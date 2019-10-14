package ben_mkiv.ocdevices.common.inventory;

import ben_mkiv.ocdevices.common.inventory.slots.CardDockSlot;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCardDock;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CardDockContainer extends ContainerBase {
    private TileEntityCardDock te;

    public CardDockContainer(IInventory playerInventory, TileEntity te) {
        this.te = (TileEntityCardDock) te;

        addOwnSlots();
        addPlayerSlots(playerInventory, 114);
    }

    private void addOwnSlots() {
        IItemHandler sideHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.EAST);
        this.addSlotToContainer(new CardDockSlot(te, sideHandler, 0, 80, 36));
    }


}

