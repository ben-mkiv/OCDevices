package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.common.tileentity.TileEntityItemBench;
import ben_mkiv.rendertoolkit.common.widgets.component.world.Item3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

import static ben_mkiv.rendertoolkit.surface.ClientSurface.vec3d000;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class RenderItemBench extends TileEntitySpecialRenderer<TileEntityItemBench> {
    Item3D item3D = new Item3D();

    @Override
    public void render(TileEntityItemBench tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack stack = tile.getCapability(ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0);

        if(stack.isEmpty()) {
            item3D = new Item3D();
            return;
        }

        if(!stack.getItem().equals(item3D.getItem())) {
            item3D = new Item3D();
            item3D.setItem(stack);
            item3D.WidgetModifierList.addScale(0.8f, 0.8f, 0.8f);
            int foo = item3D.WidgetModifierList.addTranslate(0, 0, 0);
            item3D.WidgetModifierList.addEasing(foo, "SINE", "INOUT", 1000, "y", 0, 0.01f, "LOOP");
            item3D.WidgetModifierList.addRotate(90, 1, 0, 0);
        }

        GlStateManager.pushAttrib();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x+0.1, y+1, z+0.1);
        item3D.getRenderable().render(Minecraft.getMinecraft().player, vec3d000, ~0L);

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
}
