package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.component.ManagedDatabaseComponent;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.UpgradeDatabase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEntityDatabaseAdapter extends ocComponentHostTE {
    ManagedDatabaseComponent database;

    public TileEntityDatabaseAdapter() {
        super("database_adapter", 1, true, true, true, Visibility.Network);
    }

    public void contentsChanged(){
        super.onInventoryUpdate(0, componentInventory.getStackInSlot(0));
    }

    private UpgradeDatabase getDatabase(){
        return (UpgradeDatabase) components.get(0).node().host();
    }

    @Callback(doc = "function(Integer:index):boolean;")
    public Object[] setItem(Context context, Arguments args){
        int slot = args.checkInteger(0);
        ItemStack stack;
        try {
            Item item = Item.getByNameOrId(args.checkString(1));
            stack = item != null ? new ItemStack(item, 1, args.optInteger(2, 0)) : ItemStack.EMPTY;
        } catch (Exception ex){
            return new Object[]{ false, "item not found" };
        }

        if(getDatabase() == null)
            return new Object[]{ false, "database not found" };

        getDatabase().setStackInSlot(slot - 1, stack);

        return new Object[]{ true };
    }

    @Callback(doc = "function():string; -- returns the address of the database or false when theres no DB in the inventory")
    public Object[] getDatabase(Context context, Arguments args){
        return new Object[] { !componentInventory.getStackInSlot(0).isEmpty() ? getDatabase().node().address() : false } ;
    }
}
