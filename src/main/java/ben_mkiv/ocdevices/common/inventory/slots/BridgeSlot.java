package ben_mkiv.ocdevices.common.inventory.slots;

import ben_mkiv.ocdevices.common.tileentity.TileEntityBridge;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.integration.opencomputers.DriverLinkedCard$;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BridgeSlot extends SlotItemHandler implements ISlotTooltip {
    private final TileEntityBridge tileEntity;

    public BridgeSlot(TileEntityBridge tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        DriverItem driver = Driver.driverFor(stack);
        return  driver != null && driver instanceof DriverLinkedCard$;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        tileEntity.markDirty();
    }

    @Override
    public List<String> getTooltip(){
        return new ArrayList<>(Arrays.asList(new String[]{"Accepted Items:", "OpenComputers Linked Card"}));
    }

}


