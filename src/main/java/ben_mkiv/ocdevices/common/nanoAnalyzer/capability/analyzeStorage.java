package ben_mkiv.ocdevices.common.nanoAnalyzer.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class analyzeStorage implements Capability.IStorage<IanalyzeCapability> {
    @Override
    public void readNBT(Capability<IanalyzeCapability> capability, IanalyzeCapability instance, EnumFacing side, NBTBase nbt){
        instance.readFromNBT((NBTTagCompound) nbt);
    }

    @Override
    public NBTBase writeNBT(Capability<IanalyzeCapability> capability, IanalyzeCapability instance, EnumFacing side){
        return instance.writeToNBT(new NBTTagCompound());
    }

}


