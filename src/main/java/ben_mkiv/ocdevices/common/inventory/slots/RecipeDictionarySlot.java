package ben_mkiv.ocdevices.common.inventory.slots;

import ben_mkiv.ocdevices.common.tileentity.TileEntityRecipeDictionary;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.integration.opencomputers.DriverUpgradeDatabase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class RecipeDictionarySlot extends SlotItemHandler {
    protected TileEntityRecipeDictionary tileEntity;

    public RecipeDictionarySlot(TileEntityRecipeDictionary tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        tileEntity.markDirty();
    }

}

