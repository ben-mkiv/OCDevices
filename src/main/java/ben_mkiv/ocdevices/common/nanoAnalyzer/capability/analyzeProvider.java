package ben_mkiv.ocdevices.common.nanoAnalyzer.capability;

import ben_mkiv.ocdevices.common.nanoAnalyzer.NanoAnalyzeComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class analyzeProvider implements IanalyzeCapability {
    UUID uuid = null; // "component" uuid

    int networkPort = 333;
    String boundToHost = "";

    NanoAnalyzeComponent component;

    public analyzeProvider() {}

    @Override
    public void setAttached(boolean state){
        if(state)
            uuid = UUID.randomUUID();
        else
            uuid = null;
    }

    public boolean isAttached(){
        return uuid != null;
    }

    @Override
    public void updateEvent(Entity entity, ItemStack stack){
        if(uuid == null)
            return;

        if(component != null)
            component.update(entity, this, stack);
    }

    @Override
    public void setActive(Entity entity, ItemStack stack){
        if(component != null)
            return;

        component = new NanoAnalyzeComponent(entity, this, stack);
    }

    @Override
    public void setInactive(){
        if(component == null)
            return;

        component.disconnect(this);
        component = null;
    }

    @Override
    public String getBoundToHost(){
        return boundToHost;
    }

    @Override
    public UUID getUniqueId(){
        return uuid;
    }

    @Override
    public int getNetworkPort(){
        return networkPort;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if(uuid != null){
            nbt.setBoolean("attached", true);
            nbt.setUniqueId("uuid", getUniqueId());
            nbt.setInteger("port", getNetworkPort());
            if(boundToHost.length() > 0)
                nbt.setString("boundTo", boundToHost);
        }
        else nbt.removeTag("attached");

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if(nbt.hasKey("attached")) {
            uuid = nbt.getUniqueId("uuid");
            networkPort = nbt.getInteger("port");
            if(nbt.hasKey("boundTo"))
                boundToHost = nbt.getString("boundTo");
        }
    }



}
