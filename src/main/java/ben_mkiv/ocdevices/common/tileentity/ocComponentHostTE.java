package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.component.ManagedComponent;
import ben_mkiv.ocdevices.common.component.ManagedComponentHost;
import ben_mkiv.ocdevices.utils.ItemUtils;
import li.cil.oc.api.network.Analyzable;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;

//todo: refactor to interface/class instead of extending the oc tile
public class ocComponentHostTE extends ocComponentTE implements ManagedComponentHost, Analyzable {
    final ComponentInventory componentInventory;

    private final boolean componentInventoryAccessable;

    final ArrayList<ManagedComponent> components = new ArrayList<>();

    public ocComponentHostTE(String name, int componentCount, boolean inventoryAccess, boolean connectToHost, boolean bind, Visibility visibility){
        super(name, visibility);

        componentInventoryAccessable = inventoryAccess;

        for(int i=0; i < componentCount; i++)
            components.add(new ManagedComponent(this, connectToHost, bind));

        componentInventory = new ComponentInventory(componentCount);
    }

    class ComponentInventory extends ItemStackHandler {
        ComponentInventory(int size){
            super(size);
        }

        @Override
        public void onContentsChanged(int slot){
            if(getWorld().isRemote)
                return;

            onInventoryUpdate(slot, getStackInSlot(slot));
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    }

    private void onInventoryUpdate(int slot, ItemStack newStack){
        components.get(slot).set(newStack);
        markDirty();
    }

    @Override
    public void onConnect(Node arg0) {
        super.onConnect(arg0);

        for(ManagedComponent component : components)
            component.onConnect(arg0);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setTag("componentInventory", componentInventory.serializeNBT());

        for(int i=0; i < components.size(); i++)
            nbt.setTag("component"+i, components.get(i).writeToNBT(new NBTTagCompound()));

        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);

        for(int i=0; nbt.hasKey("component"+i); i++)
            components.get(i).readFromNBT(nbt.getCompoundTag("component"+i));

        componentInventory.deserializeNBT(nbt.getCompoundTag("componentInventory"));
    }

    @Override
    public void update(){
        super.update();
        for(ManagedComponent component : components)
            component.update();
    }

    @Override
    public Node[] onAnalyze(EntityPlayer var1, EnumFacing var2, float var3, float var4, float var5){
        if(componentInventoryAccessable)
            for(ManagedComponent component : components)
                if(component.node() != null)
                    return new Node[]{ node(), component.node() };

        return new Node[]{ node() };
    }

    public void removed(){
        for(ManagedComponent component : components)
            component.disconnect();

        // drop componentInventory
        if(componentInventoryAccessable)
            for(int slot = 0; slot < componentInventory.getSlots(); slot++)
                ItemUtils.dropItem(componentInventory.getStackInSlot(slot), getWorld(), getPos(), false, 10);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (componentInventoryAccessable && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.EAST) ||  super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (componentInventoryAccessable && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.EAST) {
            return (T) componentInventory;
        }

        return super.getCapability(capability, facing);
    }

    // Environment Host
    @Override
    public World world(){ return getWorld(); }

    @Override
    public double xPosition(){ return getPos().getX(); }

    @Override
    public double yPosition(){ return getPos().getY(); }

    @Override
    public double zPosition(){ return getPos().getZ(); }

    @Override
    public void markChanged(){}
}
