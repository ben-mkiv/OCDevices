package ben_mkiv.ocdevices.common.nanoAnalyzer;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.nanoAnalyzer.events.CapabilityEvent;
import ben_mkiv.ocdevices.common.nanoAnalyzer.events.PlayerTick;
import ben_mkiv.ocdevices.common.nanoAnalyzer.events.TooltipEvent;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.MinecraftForge;
import techguns.items.guns.GenericGun;

import static net.minecraftforge.energy.CapabilityEnergy.ENERGY;

public class NanoAnalyzer {
    public static void registerEvents(){
        MinecraftForge.EVENT_BUS.register(PlayerTick.class);
        MinecraftForge.EVENT_BUS.register(CapabilityEvent.class);
    }

    public static void registerClientEvents(){
        MinecraftForge.EVENT_BUS.register(TooltipEvent.class);
    }

    public static boolean isValid(ItemStack stack){
        boolean valid = false;

        valid|=stack.getItem() instanceof ItemArmor;
        valid|=stack.getItem() instanceof ItemSword;

        valid|=(OCDevices.Techguns && stack.getItem() instanceof GenericGun);

        try {
            valid |= stack.hasCapability(ENERGY, null);
            valid |= stack.getMaxDamage() != 0;
        } catch(Throwable failFish){
            OCDevices.logger.info("couldnt safely determine if item '"+stack.getItem().getRegistryName()+"' could be used with NanoAnalyzer, skipping.");
        }

        return valid;
    }

}
