package ben_mkiv.ocdevices.common.nanoAnalyzer.capability;


import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public interface IanalyzeCapability {

    void setAttached(boolean state);
    boolean isAttached();

    void updateEvent(Entity entity, ItemStack stack);
    void setActive(Entity entity, ItemStack stack);
    void setInactive();

    UUID getUniqueId();
    String getBoundToHost();

    int getNetworkPort();

    NBTTagCompound writeToNBT(NBTTagCompound nbt);
    void readFromNBT(NBTTagCompound nbt);
}
