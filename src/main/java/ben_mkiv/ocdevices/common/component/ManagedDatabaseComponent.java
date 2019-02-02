package ben_mkiv.ocdevices.common.component;

import li.cil.oc.api.Network;
import li.cil.oc.api.internal.Database;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;

public class ManagedDatabaseComponent implements Database, ManagedEnvironment {
    final static String NAME = "managed_database";

    ItemStackHandler inventory;
    EnvironmentHost host;
    Node node;
    String databaseName;

    public ManagedDatabaseComponent(EnvironmentHost container, int size, String dbName){
        inventory = new ItemStackHandler(size);
        host = container;
        node = Network.newNode(this, Visibility.Network).withComponent(NAME).withConnector(16).create();
        databaseName = dbName;
    }

    @Callback(doc = "function():String; returns the internal name of the database")
    public Object[] getName(Context context, Arguments args) {
        return new Object[]{ getDbName() };
    }

    @Callback(doc = "function(Integer:slot):String; returns informations about the stack in the provided slot")
    public Object[] get(Context context, Arguments args) {
        if(args.count() == 0)
            return new Object[]{ false, "please provide a slot index as argument"};

        int slotIndex = args.checkInteger(0) - 1; // -1 for lua

        if(slotIndex > inventory.getSlots() || slotIndex < 0)
            return new Object[]{ false, "slot index out of range"};

        return new Object[]{ getStackData(getStackInSlot(slotIndex)) };
    }

    @Callback(doc = "function():Integer; returns the database slot count")
    public Object[] getSlots(Context context, Arguments args) {
        return new Object[]{ inventory.getSlots() };
    }

    @Callback(doc = "function(Integer:slot):boolean; clears the specified slot")
    public Object[] clear(Context context, Arguments args) {
        if(args.count() == 0)
            return new Object[]{ false, "please provide a slot index as argument"};

        int slotIndex = args.checkInteger(0) - 1; // -1 for lua

        if(slotIndex > inventory.getSlots() || slotIndex < 0)
            return new Object[]{ false, "slot index out of range"};

        inventory.setStackInSlot(slotIndex, ItemStack.EMPTY);
        return new Object[]{ true };
    }

    public void setSize(int newSize){
        inventory = new ItemStackHandler(newSize);
    }

    public String getDbName(){
        return this.databaseName;
    }

    @Override
    public int size(){
        return inventory.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int var1){
        return inventory.getStackInSlot(var1);
    }

    @Override
    public void setStackInSlot(int var1, ItemStack var2){
        inventory.setStackInSlot(var1, var2);
    }

    @Override
    public int findStackWithHash(String var1){
        return -1;
    }

    public void readFromNBT(NBTTagCompound tag){
        load(tag);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        save(tag);
        return tag;
    }

    @Override
    public void load(NBTTagCompound tag){
        inventory.deserializeNBT(tag.getCompoundTag("inventory"));
        databaseName = tag.getString("databaseName");

        if(node != null && tag.hasKey("oc:node"))
            node.load(tag.getCompoundTag("oc:node"));
    }

    @Override
    public void save(NBTTagCompound tag){
        tag.setTag("inventory", inventory.serializeNBT());
        tag.setString("databaseName", getDbName());
        NBTTagCompound nodeTag = new NBTTagCompound();
        node.save(nodeTag);

        tag.setTag("oc:node", nodeTag);
    }

    public static Map<String, Object> getStackData(ItemStack stack){
        Map<String, Object> stackData = new HashMap<>();
        stackData.put("damage", stack.getItemDamage());
        stackData.put("hasTag", stack.hasTagCompound());
        stackData.put("label", stack.getDisplayName());
        stackData.put("maxDamage", stack.getMaxDamage());
        stackData.put("maxSize", stack.getMaxStackSize());
        stackData.put("name", stack.getItem().getRegistryName());
        stackData.put("size", stack.getCount());
        return stackData;
    }

    @Override
    public boolean canUpdate(){ return false;}

    @Override
    public void update(){}


    @Override
    public Node node(){ return this.node; }

    @Override
    public void onConnect(Node var1){}

    @Override
    public void onDisconnect(Node var1){}

    @Override
    public void onMessage(Message var1){}
}
