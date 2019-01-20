package ben_mkiv.ocdevices.proxy;

import ben_mkiv.ocdevices.common.blocks.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy{
    @Override
    public void registerBlockColorHandlers(){
        BlockColors mc = Minecraft.getMinecraft().getBlockColors();
        mc.registerBlockColorHandler(new BlockCase.ColorHandler(BlockCase_ibm_5150.DEFAULTITEM), BlockCase_ibm_5150.DEFAULTITEM);
        mc.registerBlockColorHandler(new BlockCase.ColorHandler(BlockCase_slim_oc.DEFAULTITEM), BlockCase_slim_oc.DEFAULTITEM);
        mc.registerBlockColorHandler(new BlockCase.ColorHandler(BlockCase_next.DEFAULTITEM), BlockCase_next.DEFAULTITEM);
        mc.registerBlockColorHandler(new BlockCase.ColorHandler(BlockCase_workstation.DEFAULTITEM), BlockCase_workstation.DEFAULTITEM);
    }
}
