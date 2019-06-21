package ben_mkiv.ocdevices.manual;

import ben_mkiv.ocdevices.OCDevices;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

import java.util.HashSet;

public class Manual {
    private static ResourceLocation iconResourceLocation = new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/workstation/case_front.png");
    private static String tooltip = "OCDevices";
    private static String homepage = "assets/" + OCDevices.MOD_ID + "/doc/_Sidebar";

    public static HashSet<Item> items = new HashSet<>();


    public static void preInit(){
        if(Loader.isModLoaded("rtfm")) {
            new ManualPathProviderRTFM().initialize(iconResourceLocation, tooltip, homepage);
            items.add(ManualPathProviderRTFM.getManualItem().setUnlocalizedName("manual").setRegistryName("manual").setCreativeTab(OCDevices.creativeTab));
        }

        if(Loader.isModLoaded("opencomputers"))
            new ManualPathProviderOC().initialize(iconResourceLocation, tooltip, homepage);
    }

}
