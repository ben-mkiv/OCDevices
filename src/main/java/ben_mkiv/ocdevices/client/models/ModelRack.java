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

import java.awt.*;

public class ModelRack extends ModelBase {

    private ResourceLocation textureCase = new ResourceLocation("ocdevices:textures/blocks/rack/rack.png");
    private ResourceLocation textureDoor = new ResourceLocation("ocdevices:textures/blocks/rack/front.png");

    private ModelRenderer caseElements, doorElement;

    public ModelRack(){

        int height = 2;

        ModelRenderer innerCaseElements = new ModelRenderer(this, 0, 0);
        innerCaseElements.setTextureSize(64, 64);
        innerCaseElements.addBox( 1,  2,  1,  0, 12, 14, 0); // inner left
        innerCaseElements.addBox(15,  2,  1,  0, 12, 14, 0); // inner right
        innerCaseElements.addBox( 1,  2, 15, 14, 12,  0, 0); // inner backside
        innerCaseElements.addBox( 1,  2,  1, 14,  0, 14, 0); // inner bottom
        innerCaseElements.addBox( 1, 14,  1, 14,  0, 14, 0); // inner top

        ModelRenderer outerCaseElements = new ModelRenderer(this, 0, 32);
        outerCaseElements.setTextureSize(64, 64);
        outerCaseElements.addBox( 0,  0,  1,  0, 16, 15, 0); // outer left
        outerCaseElements.addBox(16,  0,  1,  0, 16, 15, 0); // outer right
        outerCaseElements.addBox( 0,  0,  1, 16,  0, 15, 0); // outer bottom
        outerCaseElements.addBox( 0, 16,  1, 16,  0, 15, 0); // outer top

        ModelRenderer backCaseElements = new ModelRenderer(this, 32, 32);
        backCaseElements.setTextureSize(64, 64);
        backCaseElements.addBox( 0,  0, 16, 16, 16,  0, 0); // outer backside

        ModelRenderer frontCaseElements = new ModelRenderer(this, 48, 48);
        frontCaseElements.setTextureSize(64, 64);
        frontCaseElements.addBox( 0,  0,  1, 16,  2, 0, 0); // front bottom
        frontCaseElements.addBox( 0, 14,  1, 16,  2, 0, 0); // front top
        frontCaseElements.addBox( 0,  2,  1,  1, 12, 0, 0); // front left
        frontCaseElements.addBox( 15, 2,  1,  1, 12, 0, 0); // front right

        caseElements = new ModelRenderer(this, 0, 0);
        caseElements.addChild(innerCaseElements);
        caseElements.addChild(outerCaseElements);
        caseElements.addChild(backCaseElements);
        caseElements.addChild(frontCaseElements);

        doorElement =  new ModelRenderer(this, -2, -1);
        doorElement.setTextureSize(16, 16);
        doorElement.addBox( 0,  0,  0, 16, 16,  1, 0); // door
        doorElement.setRotationPoint(0, 0, 0);
    }


    public void render(TileEntityRack rack, float scale, BufferBuilder buffer){
        Minecraft.getMinecraft().renderEngine.bindTexture(textureCase);

        int col = rack.getColor();

        Color color = new Color(col);

        if(col == 0)
            color = new Color(0.1f, 0.1f, 0.1f);


        GlStateManager.color(1f/255 * color.getRed(), 1f/255 * color.getGreen(), 1f/255 * color.getBlue());
        caseElements.render(scale);


        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        Minecraft.getMinecraft().renderEngine.bindTexture(textureDoor);
        GlStateManager.color(1, 1, 1, 0.7f);

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

        doorElement.render(scale);



        GlStateManager.color(1, 1, 1, 1);
    }


}
