package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.component.ManagedDatabaseComponent;
import ben_mkiv.ocdevices.config.Config;
import ben_mkiv.ocdevices.utils.ItemUtils;
import ben_mkiv.ocdevices.utils.RecipeHelper;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.UpgradeDatabase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


public class TileEntityRecipeDictionary extends ocComponentHostTE {

    ItemStackHandler itemInventory;

    ArrayList<ManagedDatabaseComponent> databases = new ArrayList<>();

    public TileEntityRecipeDictionary() {
        super("recipedict", 1, false, true, false, Visibility.Network);
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

        databases.add(new ManagedDatabaseComponent(this, 9, "ingredients"));
        databases.add(new ManagedDatabaseComponent(this, 1, "output"));

        itemInventory = new ItemStackHandler(1) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    public ItemStackHandler getComponentInventory(){
        return componentInventory;
    }

    @Callback(doc = "function(String:itemName[, Integer:itemMetaindex, String:tagCompound]):boolean, integer; returns the database slot where the recipe was saved")
    public Object[] getRecipeFromItem(Context context, Arguments args) {
        UpgradeDatabase recipeDatabase = getRecipeDatabase();

        if(recipeDatabase == null)
            return new Object[]{ false, "no recipe database found in recipe dictionary, add one in the gui"};

        int storeToSlot = getEmptyRecipeDatabaseSlot();

        if(storeToSlot == -1)
            return new Object[]{ false, "recipe database is full"};

        ItemStack itemStack = getItemStackFromArguments(args, 0);

        int recipeDatabaseSlot = getRecipeSlotFromDatabase(itemStack);
        if(recipeDatabaseSlot != -1){
            return new Object[]{ false, "recipe already exists in database slot " + (recipeDatabaseSlot + 1) };
        }

        if(!itemStack.isEmpty())
            if(!ItemStack.areItemsEqualIgnoreDurability(itemStack, itemInventory.getStackInSlot(0)))
                if(!Config.getConfig().getCategory("general").get("enableCreativeRecipeLookup").getBoolean())
                    return new Object[]{ false, "'enableCreativeRecipeLookup' lookup is disabled in config" };

        if(itemStack.isEmpty())
            return new Object[]{ false, "no item found"};

        RecipeHelper recipeHandler = new RecipeHelper(itemStack);
        ArrayList<Object> recipes = recipeHandler.getList();

        if(recipeHandler.getCount() == 0)
            return new Object[]{ false, "no recipe found for " + itemStack.getItem().getRegistryName().toString() };

        if(!destroyDamageItemInput())
            return new Object[]{ false, "couldnt disassemble item"};

        storeRecipeInRecipeDatabase(storeToSlot, itemStack.copy());
        getDatabaseByName("output").setStackInSlot(0, itemStack);

        return new Object[]{ true, storeToSlot + 1 }; // recipes.toArray()
    }

    @Callback(doc = "function(Integer:slot[, Integer:recipeIndex]):boolean;")
    public Object[] loadRecipeFromDatabase(Context context, Arguments args) {
        if(args.count() == 0)
            return new Object[]{ false, "specify a slot" };

        return new Object[]{ loadRecipeFromDatabase(args.checkInteger(0) - 1, args.optInteger(1, 0)) };
    }

    private boolean loadRecipeFromDatabase(int slot){
        return loadRecipeFromDatabase(slot, 0);
    }

    private boolean loadRecipeFromDatabase(int slot, int recipeIndex){
        UpgradeDatabase recipeDatabase = getRecipeDatabase();

        if(recipeDatabase == null)
            return false;

        ItemStack itemStack = recipeDatabase.getStackInSlot(slot).copy();
        if(itemStack.isEmpty())
            return false;

        if(!itemStack.hasTagCompound())
            return false;

        if(!itemStack.getTagCompound().hasKey("ocdevices:recipedictionary"))
            return false;

        itemStack.getTagCompound().removeTag("ocdevices:recipedictionary");

        RecipeHelper recipeHandler = new RecipeHelper(itemStack);

        if(recipeIndex > recipeHandler.getCount())
            return false;

        IRecipe recipe  = recipeHandler.recipeCache.get(recipeIndex);

        HashMap<Integer, HashSet<ItemStack>> ingredients = new HashMap<>();

        int recipeWidth = recipe instanceof IShapedRecipe ? ((IShapedRecipe) recipe).getRecipeWidth() : 3;

        int x = 0, y = 0;
        int craftingSlot = 0;
        for(Ingredient ingredient : recipe.getIngredients()){
            ingredients.put(craftingSlot, new HashSet<>(Arrays.asList(ingredient.getMatchingStacks())));

            craftingSlot++;
            x++;

            if(x == recipeWidth){
                y+=1;
                craftingSlot+= recipeWidth - x;
                x=0;
            }
        }

        int maxLength = 0;
        for(HashSet<ItemStack> set : ingredients.values())
            maxLength = Math.max(maxLength, set.size());

        ManagedDatabaseComponent db = getDatabaseByName("ingredients");
        db.setSize(maxLength * 9);

        for(Map.Entry<Integer, HashSet<ItemStack>> slotItems : ingredients.entrySet()){
            int i=0;
            for(ItemStack stack : slotItems.getValue()){
                int dbSlot = slotItems.getKey() + (9*i);
                db.setStackInSlot(dbSlot, stack);
                OCDevices.logger.warning("set: " + dbSlot + " => " + stack.getDisplayName());
                i++;
            }
        }

        getDatabaseByName("output").setStackInSlot(0, itemStack);

        return true;
    }

    private int getRecipeSlotFromDatabase(ItemStack stack){
        UpgradeDatabase recipeDatabase = getRecipeDatabase();

        for(int slot=0; slot < recipeDatabase.size(); slot++)
            if (ItemStack.areItemsEqualIgnoreDurability(recipeDatabase.getStackInSlot(slot), stack))
                return slot;

        return -1;
    }

    private int getEmptyRecipeDatabaseSlot(){
        UpgradeDatabase recipeDatabase = getRecipeDatabase();

        for(int slot=0; slot < recipeDatabase.size(); slot++)
            if (recipeDatabase.getStackInSlot(slot).isEmpty())
                return slot;

        return -1;
    }

    private void storeRecipeInRecipeDatabase(int slot, ItemStack stack){
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());

        stack.getTagCompound().setBoolean("ocdevices:recipedictionary", true);
        getRecipeDatabase().setStackInSlot(slot, stack);
        markDirty();
    }

    private boolean destroyDamageItemInput(){
        if(!Config.getConfig().getCategory("general").get("damageOrDestroyOnRecipeLookup").getBoolean())
            return true;

        ItemStack stack = itemInventory.getStackInSlot(0);

        if(stack.isItemStackDamageable()){
            // add random damage to the item
            int damage = getWorld().rand.nextInt(stack.getMaxDamage() - stack.getItemDamage())-1;
            stack.setItemDamage(stack.getItemDamage() + damage);
        }
        else if(getWorld().rand.nextBoolean())
            stack = ItemStack.EMPTY;

        itemInventory.setStackInSlot(0, stack);
        return true;
    }

    /*
    @Callback(doc = "function(String:database, Integer:slot, String:itemName[, Integer:itemMetaindex, String:tagCompound]):boolean;")
    public Object[] itemToDatabase(Context context, Arguments args) {
        if(args.count() < 2)
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

        Node n = node().network().node(dbAddress);
        if(n == null)
            return new Object[]{ false, "couldn't connect to database"};

        if(!(n.host() instanceof UpgradeDatabase))
            return new Object[]{ false, "no database found at given address"};

        UpgradeDatabase db = DatabaseAccess.database(node(), dbAddress);

        db.setStackInSlot(slot, itemStack);

        return new Object[]{ ItemStack.areItemStacksEqual(db.getStackInSlot(slot), itemStack) };
    }*/

    @Callback(doc = "function(Integer:index):boolean;")
    public Object[] getDatabase(Context context, Arguments args) {
        ArrayList<Object> list = new ArrayList<>();

        int dbId = args.count() == 1 && args.isInteger(0) ? args.checkInteger(0) : -1;
        String dbName = args.count() == 1 && args.isString(0) ? args.checkString(0) : "";

        int i=0;
        for(ManagedDatabaseComponent db : databases) {
            Map<String, Object> dbData = new HashMap<>();
            dbData.put("name", db.getDbName());
            dbData.put("address", db.node().address());

            if(dbId == -1 && dbName.length() == 0)
                list.add(dbData);
            else if(dbId == i || dbName.equals(db.getDbName()))
                return new Object[]{ dbData };

            i++;
        }

        return new Object[]{ list.toArray() };
    }

    ManagedDatabaseComponent getDatabaseByName(String name){
        for(ManagedDatabaseComponent db : databases)
            if(db.getDbName().equals(name))
                return db;

            return null;
    }

    private ItemStack getItemStackFromArguments(Arguments args, int argumentsOffsets){
        if(args.count() == 0) {
            ItemStack stack = itemInventory.getStackInSlot(0).copy();

            // use non damaged stack for lookup
            if(stack.isItemStackDamageable())
                stack.setItemDamage(stack.getItemDamage() - stack.getMaxDamage());

            return stack;
        }

        try {
            Item item = Item.getByNameOrId(args.checkString(0+argumentsOffsets));
            ItemStack itemStack = new ItemStack(item, 1, args.optInteger(1+argumentsOffsets, 0));
            return itemStack;
        } catch (Exception ex){
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void onConnect(Node var1){
        super.onConnect(var1);

        for(ManagedDatabaseComponent db : databases) {
            node().connect(db.node());
            db.onConnect(var1);
        }
    }

    @Override
    public void onDisconnect(Node var1){
        super.onDisconnect(var1);

        for(ManagedDatabaseComponent db : databases) {
            //node().disconnect(db.node());
            db.onDisconnect(var1);
        }
    }

    @Override
    public void onMessage(Message var1){
        super.onMessage(var1);

        for(ManagedDatabaseComponent db : databases)
            db.onMessage(var1);
    }

    UpgradeDatabase getRecipeDatabase(){
        if(components.get(0) == null || components.get(0).node() == null)
            return null;

        return ((UpgradeDatabase) components.get(0).node().host());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setTag("itemInventory", itemInventory.serializeNBT());

        UpgradeDatabase recipeDatabase = getRecipeDatabase();
        if(recipeDatabase != null) {
            nbt.setInteger("recipeSlots", recipeDatabase.size());
            int slotsUsed = 0;
            for(int slot=0; slot < recipeDatabase.size(); slot++)
                if(!recipeDatabase.getStackInSlot(slot).isEmpty())
                    slotsUsed++;
            nbt.setInteger("recipeSlotsUsed", slotsUsed);
        } else {
            nbt.setInteger("recipeSlots", 0);
            nbt.setInteger("recipeSlotsUsed", 0);
        }


        int i=0;
        for(ManagedDatabaseComponent db : databases){
            NBTTagCompound dbTag = new NBTTagCompound();
            dbTag.setString("name", db.getDbName());
            dbTag.setTag("data", db.writeToNBT(new NBTTagCompound()));

            nbt.setTag("database"+i, dbTag);
            i++;
        }

        return super.writeToNBT(nbt);
    }

    public int recipeSlots = 0, recipeSlotsUsed = 0; //only cached for the client gui to show usage

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);

        itemInventory.deserializeNBT(nbt.getCompoundTag("itemInventory"));

        recipeSlots = nbt.getInteger("recipeSlots");
        recipeSlotsUsed = nbt.getInteger("recipeSlotsUsed");

        for(int i=0; nbt.hasKey("database"+i); i++) {
            NBTTagCompound dbTag = nbt.getCompoundTag("database"+i);
            getDatabaseByName(dbTag.getString("name")).readFromNBT(dbTag.getCompoundTag("data"));
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||  super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) itemInventory;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void removed(){
        super.removed();

        for(int slot = 0; slot < itemInventory.getSlots(); slot++)
            ItemUtils.dropItem(itemInventory.getStackInSlot(slot), getWorld(), getPos(), false, 10);

        // we have to drop the database as its set to not be accessable from "outside"
        for(int slot = 0; slot < componentInventory.getSlots(); slot++)
            ItemUtils.dropItem(componentInventory.getStackInSlot(slot), getWorld(), getPos(), false, 10);
    }


    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }


    @Override
    public void markDirty(){
        IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        super.markDirty();
    }

}

