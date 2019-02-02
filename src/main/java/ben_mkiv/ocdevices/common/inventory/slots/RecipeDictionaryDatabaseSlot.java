package ben_mkiv.ocdevices.common.inventory.slots;

import ben_mkiv.ocdevices.common.tileentity.TileEntityRecipeDictionary;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.integration.opencomputers.DriverUpgradeDatabase$;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class RecipeDictionaryDatabaseSlot extends RecipeDictionarySlot {

    public RecipeDictionaryDatabaseSlot(TileEntityRecipeDictionary tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(tileEntity, itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        DriverItem driver = Driver.driverFor(stack);
        return  driver != null && driver.getClass().equals(DriverUpgradeDatabase$.class);
    }

}
