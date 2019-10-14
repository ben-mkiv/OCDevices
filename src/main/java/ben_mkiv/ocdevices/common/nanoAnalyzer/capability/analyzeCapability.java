package ben_mkiv.ocdevices.common.nanoAnalyzer.capability;

import ben_mkiv.ocdevices.OCDevices;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public class analyzeCapability  implements ICapabilitySerializable<NBTBase> {
    protected analyzeProvider instance = null;
    protected static Capability cap;

    public static ResourceLocation capRL = new ResourceLocation(OCDevices.MOD_ID, "ITEM_ANALYZE");

    @CapabilityInject(IanalyzeCapability.class)
    public static Capability<IanalyzeCapability> ANALYZE = null;

    public analyzeCapability(ItemStack item){
        instance = new analyzeProvider();
    }

    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability.equals(ANALYZE);
    }

    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(!capability.equals(ANALYZE))
            return null;

        return ANALYZE.<T> cast(instance);
    }

    @Override
    public NBTBase serializeNBT(){
        if(instance == null)
            return new NBTTagCompound();

        return ANALYZE.getStorage().writeNBT(ANALYZE, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt){
        if(instance != null)
            ANALYZE.getStorage().readNBT(ANALYZE, instance, null, nbt);
    }


}

