package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.utils.RecipeHelper;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.UpgradeDatabase;
import li.cil.oc.util.DatabaseAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;


public class TileEntityRecipeDictionary extends ocComponentTE {
    ItemStackHandler inventory;

    public TileEntityRecipeDictionary() {
        super("recipedict");
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
    }

    @Callback(doc = "function(String:itemName[, Integer:itemMetaindex, String:tagCompound]):array;")
    public Object[] getRecipe(Context context, Arguments args) {
        ItemStack itemStack = getItemStackFromArguments(args, 0);

        if(itemStack.isEmpty())
            return new Object[]{ false, "no item found for name '" + args.checkString(0) + "'"};

        RecipeHelper recipeHandler = new RecipeHelper(itemStack);
        ArrayList<Object> recipes = recipeHandler.getList();

        if(recipeHandler.getCount() == 0)
            return new Object[]{ false, "no recipe found for " + itemStack.getItem().getRegistryName().toString() };

        return new Object[]{ recipes.toArray() };
    }

    @Callback(doc = "function(String:database, Integer:slot, String:itemName[, Integer:itemMetaindex, String:tagCompound]):boolean;")
    public Object[] itemToDatabase(Context context, Arguments args) {
        if(args.count() < 3)
            return new Object[]{ false, "missing arguments"};

        String dbAddress = args.checkString(0);
        int slot = args.checkInteger(1);
        ItemStack itemStack = getItemStackFromArguments(args, 2);

        if(args.count() == 5) try {
            NBTTagCompound nbt = JsonToNBT.getTagFromJson(args.checkString(4));
            itemStack.setTagCompound(nbt);
        } catch (Exception ex){
            return new Object[]{ false, "invalid nbt format (json required)"};
        }

        if(itemStack.isEmpty())
            return new Object[]{ false, "no item found for name '" + args.checkString(0) + "'"};

        UpgradeDatabase db = DatabaseAccess.database(node(), dbAddress);

        db.setStackInSlot(slot, itemStack);

        return new Object[]{ ItemStack.areItemStacksEqual(db.getStackInSlot(slot), itemStack) };
    }

    ItemStack getItemStackFromArguments(Arguments args, int argumentsOffsets){
        try {
            Item item = Item.getByNameOrId(args.checkString(0+argumentsOffsets));
            ItemStack itemStack = new ItemStack(item, 1, args.optInteger(1+argumentsOffsets, 0));
            return itemStack;
        } catch (Exception ex){
            return ItemStack.EMPTY;
        }
    }

    public void removed(){}

}
