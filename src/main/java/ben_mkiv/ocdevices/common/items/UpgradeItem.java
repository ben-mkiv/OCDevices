package ben_mkiv.ocdevices.common.items;

import ben_mkiv.ocdevices.OCDevices;
import net.minecraft.item.Item;

public class UpgradeItem extends Item {
    public UpgradeItem(int tier, String name){
        setUnlocalizedName(name);
        setRegistryName(OCDevices.MOD_ID, name);
        setCreativeTab(OCDevices.creativeTab);
    }

}
