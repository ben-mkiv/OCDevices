package ben_mkiv.ocdevices.common.nanoAnalyzer.events;

import ben_mkiv.ocdevices.common.nanoAnalyzer.NanoAnalyzer;
import ben_mkiv.ocdevices.common.nanoAnalyzer.capability.analyzeCapability;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityEvent {
    @SubscribeEvent
    public static void onCapabilityAttach(AttachCapabilitiesEvent<ItemStack> event){
        if(NanoAnalyzer.isValid(event.getObject()))
            event.addCapability(analyzeCapability.capRL, new analyzeCapability(event.getObject()));
    }
}
