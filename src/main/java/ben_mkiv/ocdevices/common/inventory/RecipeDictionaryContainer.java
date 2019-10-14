package ben_mkiv.ocdevices.common.inventory;

import ben_mkiv.ocdevices.common.inventory.slots.RecipeDictionaryDatabaseSlot;
import ben_mkiv.ocdevices.common.inventory.slots.RecipeDictionarySlot;
import ben_mkiv.ocdevices.common.tileentity.TileEntityRecipeDictionary;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class RecipeDictionaryContainer extends ContainerBase {

    private final TileEntityRecipeDictionary te;

    public RecipeDictionaryContainer(IInventory playerInventory, TileEntity te) {
        this.te = (TileEntityRecipeDictionary) te;
        addOwnSlots();
        addPlayerSlots(playerInventory, 114);
    }

    private void addOwnSlots() {
        IItemHandler itemInventory = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        IItemHandler componentInventory = this.te.getComponentInventory();
        this.addSlotToContainer(new RecipeDictionarySlot(te, itemInventory, 0, 80, 36));
        this.addSlotToContainer(new RecipeDictionaryDatabaseSlot(te, componentInventory, 0, 148, 85));
    }

}


