package ben_mkiv.ocdevices.common.inventory.slots;

import ben_mkiv.ocdevices.common.tileentity.TileEntityCardDock;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.item.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardDockSlot extends SlotItemHandler implements ISlotTooltip {
    private final TileEntityCardDock tileEntity;

    public CardDockSlot(TileEntityCardDock tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        DriverItem driver = Driver.driverFor(stack);
        return  driver != null && driver.slot(stack).equals(Slot.Card);
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        tileEntity.markDirty();
    }

    @Override
    public List<String> getTooltip(){
        return new ArrayList<>(Arrays.asList("Accepted Items:", "OpenComputers cards", "addon cards"));
    }

}

