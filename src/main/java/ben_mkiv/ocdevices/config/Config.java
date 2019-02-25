package ben_mkiv.ocdevices.config;

/**
 * @author ben_mkiv, based on MinecraftByExample Templates
 */
import java.io.File;
import java.util.HashMap;

import ben_mkiv.ocdevices.OCDevices;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.PermissionAPI;

//todo: check what config values have to be sent by a server so that client methods are aware of server configuration
public class Config extends PermissionAPI {
    private static Configuration config = null;

    static HashMap<String, Property> configOptions = new HashMap<>();

    public static void preInit(){
        File configFile = new File(Loader.instance().getConfigDir(), OCDevices.MOD_ID + ".cfg");
        config = new Configuration(configFile);

        syncConfig(true);
    }

    public static void clientPreInit() {
        MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
    }

    public static Configuration getConfig() {
        return config;
    }

    private static void syncConfig(boolean loadConfigFromFile) {
        if (loadConfigFromFile)
            config.load();

        boolean isClient = FMLCommonHandler.instance().getEffectiveSide().isClient();

        Property enableCreativeRecipeLookup = config.get("recipedict", "enableCreativeRecipeLookup", false);
        enableCreativeRecipeLookup.setLanguageKey("gui.config.recipedict.enableCreativeRecipeLookup");
        enableCreativeRecipeLookup.setComment("if set to true the recipe dictionary doesnt require an item to lookup recipes");

        Property damageOrDestroyOnRecipeLookup = config.get("recipedict", "damageOrDestroyOnRecipeLookup", false);
        damageOrDestroyOnRecipeLookup.setLanguageKey("gui.config.recipedict.damageOrDestroyOnRecipeLookup");
        damageOrDestroyOnRecipeLookup.setComment("if set to true items will be damaged on recipe lookup, or destroyed if they cant handle damage");

        Property tier_ibm5150 = config.get("cases", "tier_ibm5150", 3);
        tier_ibm5150.setLanguageKey("gui.config.cases.tier_ibm5150");
        tier_ibm5150.setComment("downgrading on existing worlds might result in losing already installed components");
        tier_ibm5150.setMinValue(1);
        tier_ibm5150.setMaxValue(3);
        if(isClient)
            tier_ibm5150.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

        Property tier_next = config.get("cases", "tier_next", 3);
        tier_next.setLanguageKey("gui.config.cases.tier_next");
        tier_next.setComment("downgrading on existing worlds might result in losing already installed components");
        tier_next.setMinValue(1);
        tier_next.setMaxValue(3);
        if(isClient)
            tier_next.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

        Property tier_oc_slim = config.get("cases", "tier_oc_slim", 3);
        tier_oc_slim.setLanguageKey("gui.config.cases.tier_oc_slim");
        tier_oc_slim.setComment("downgrading on existing worlds might result in losing already installed components");
        tier_oc_slim.setMinValue(1);
        tier_oc_slim.setMaxValue(3);
        if(isClient)
            tier_oc_slim.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

        Property tier_workstation = config.get("cases", "tier_workstation", 3);
        tier_workstation.setLanguageKey("gui.config.cases.tier_workstation");
        tier_workstation.setComment("downgrading on existing worlds might result in losing already installed components");
        tier_workstation.setMinValue(1);
        tier_workstation.setMaxValue(3);
        if(isClient)
            tier_workstation.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

        if (config.hasChanged())
            config.save();
    }

    public static class ConfigEventHandler{
        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void onEvent(ConfigChangedEvent.OnConfigChangedEvent event){
            if (!event.getModID().equals(OCDevices.MOD_ID))
                return;

            syncConfig(false);
        }
    }
}