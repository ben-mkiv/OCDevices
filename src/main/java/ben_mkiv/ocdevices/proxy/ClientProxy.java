package ben_mkiv.ocdevices.proxy;

import ben_mkiv.ocdevices.common.blocks.*;
import ben_mkiv.ocdevices.config.Config;
import ben_mkiv.ocdevices.utils.ColorHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy{
    @Override
    public void preinit() {
        super.preinit();
        Config.clientPreInit();

        //RenderingRegistry.registerEntityRenderingHandler(BugEntity.class, RenderBug.FACTORY);
    }

    @Override
    public void registerBlockColorHandlers(){
        BlockColors mc = Minecraft.getMinecraft().getBlockColors();
        mc.registerBlockColorHandler(new ColorHandler(), BlockCase_ibm_5150.DEFAULTITEM);
        mc.registerBlockColorHandler(new ColorHandler(), BlockCase_slim_oc.DEFAULTITEM);
        mc.registerBlockColorHandler(new ColorHandler(), BlockCase_next.DEFAULTITEM);
        mc.registerBlockColorHandler(new ColorHandler(), BlockCase_workstation.DEFAULTITEM);

        mc.registerBlockColorHandler(new ColorHandler(), BlockRack.DEFAULTITEM);

        mc.registerBlockColorHandler(new ColorHandler(), BlockKeyboard.DEFAULTITEM);
    }


}
