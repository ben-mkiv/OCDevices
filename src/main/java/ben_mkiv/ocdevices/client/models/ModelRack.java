package ben_mkiv.ocdevices.client.models;

import ben_mkiv.ocdevices.common.tileentity.TileEntityRack;
import ben_mkiv.rendertoolkit.common.widgets.core.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModelRack extends ModelBase {

    private ResourceLocation textureDoor = new ResourceLocation("ocdevices:textures/blocks/rack/front.png");

    private ModelRenderer doorElement;

    public ModelRack(){
        doorElement =  new ModelRenderer(this, -2, -1);
        doorElement.setTextureSize(16, 16);
        doorElement.addBox( 0,  0,  0, 16, 16,  1, 0); // door
        doorElement.setRotationPoint(0, 0, 0);
    }


    public void render(TileEntityRack rack, float scale, BufferBuilder buffer){
        long val = Math.min(System.currentTimeMillis() - rack.doorAnimationProgress, 1000);

        if(rack.isDoorOpened()) {
            float ease = Easing.easeInOut(val, 1000, 0, 1, Easing.EasingType.QUINT);
            doorElement.rotateAngleY = (float) Math.toRadians(90d + 45d) * ease;
            doorElement.setRotationPoint(0, 0, ease);
        }
        else {
            float ease = (1f - Easing.easeInOut(val, 1000, 0, 1, Easing.EasingType.QUINT));
            doorElement.rotateAngleY = (float) Math.toRadians(90d + 45d) * ease;
            doorElement.setRotationPoint(0, 0, ease);
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        Minecraft.getMinecraft().renderEngine.bindTexture(textureDoor);
        GlStateManager.color(1, 1, 1, 0.7f);

        doorElement.render(scale);

        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.disableBlend();
    }


}
