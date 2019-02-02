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

        Property enableCreativeRecipeLookup = config.get("general", "enableCreativeRecipeLookup", false);
        enableCreativeRecipeLookup.setLanguageKey("gui.config.general.enableCreativeRecipeLookup");
        enableCreativeRecipeLookup.setComment("if set to true the recipe dictionary doesnt require an item to lookup recipes");

        Property damageOrDestroyOnRecipeLookup = config.get("general", "damageOrDestroyOnRecipeLookup", true);
        damageOrDestroyOnRecipeLookup.setLanguageKey("gui.config.general.damageOrDestroyOnRecipeLookup");
        damageOrDestroyOnRecipeLookup.setComment("if set to true items will be damaged on recipe lookup, or destroyed if they cant handle damage");

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