package ben_mkiv.ocdevices.proxy;

import ben_mkiv.ocdevices.common.blocks.*;
import ben_mkiv.ocdevices.common.tileentity.ColoredTile;
import ben_mkiv.ocdevices.config.Config;
import li.cil.oc.common.tileentity.traits.Colored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy{
    @Override
    public void preinit() {
        super.preinit();
        Config.clientPreInit();
    }

    @Override
    public void registerBlockColorHandlers(){
        BlockColors mc = Minecraft.getMinecraft().getBlockColors();
        mc.registerBlockColorHandler(new ColorHandler(), BlockCase_ibm_5150.DEFAULTITEM);
        mc.registerBlockColorHandler(new ColorHandler(), BlockCase_slim_oc.DEFAULTITEM);
        mc.registerBlockColorHandler(new ColorHandler(), BlockCase_next.DEFAULTITEM);
        mc.registerBlockColorHandler(new ColorHandler(), BlockCase_workstation.DEFAULTITEM);

        mc.registerBlockColorHandler(new ColorHandler(), BlockKeyboard.DEFAULTITEM);
    }

    @SideOnly(Side.CLIENT)
    public static class ColorHandler implements IBlockColor {

        public ColorHandler() {}

        @Override
        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex){
            TileEntity te = worldIn.getTileEntity(pos);

            int color = te != null && te instanceof ColoredTile ? ((Colored) te).getColor() : 0;

            if(color >= 0 && color <= 15)
                color = EnumDyeColor.values()[color].getColorValue();

            return color;
        }
    }
}
