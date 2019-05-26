package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenHelper;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMultiblockDisplay;
import ben_mkiv.ocdevices.utils.Triangle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

abstract class RenderMultiblockDisplay extends TileEntitySpecialRenderer<TileEntityMultiblockDisplay> {

    float borderWidth = 0f;//1f/32;

    boolean shouldRender(TileEntityMultiblockDisplay screen){
        return screen.isOrigin() && screen.getMultiblock().initialized();
    }

    void preRender(double x, double y, double z, FlatScreenHelper helper){
        if(OCDevices.Albedo || OCDevices.Optifine)
            GlStateManager.disableLighting();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.disableTexture2D();
        renderScreenModelTESR(helper);

        renderScreenBackground(helper);
    }

    void postRender(){
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }


    void rotateByBlockOrigin(FlatScreenHelper screenHelper){
        GlStateManager.translate(0.5, 0.5, 0.5);

        switch(screenHelper.facing){
            case WEST: GlStateManager.rotate(-90, 0, 1, 0); break;
            case NORTH: GlStateManager.rotate(180, 0, 1, 0); break;
            case EAST: GlStateManager.rotate(90, 0, 1, 0); break;
        }

        switch(screenHelper.pitch){
            case DOWN: GlStateManager.rotate(90, 1, 0, 0); break;
            case UP: GlStateManager.rotate(-90, 1, 0, 0); break;
        }

        GlStateManager.translate(-0.5, -0.5, -0.5);
    }

    void renderScreenBackground(FlatScreenHelper screenHelper){
        float opacity = 1f/100f * (float) screenHelper.opacity;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.pushMatrix();

        rotateByBlockOrigin(screenHelper);

        if(screenHelper.opacity != 100 && screenHelper.opacity > 0) {
            GlStateManager.disableCull();
            //GlStateManager.depthMask(false); // to fix rendering of water behind/next to the screen, anyways look into this if more problems occur https://github.com/CoFH/ThermalExpansion/tree/1.12/src/main/java/cofh/thermalexpansion/render
        }

        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // front
        buff.pos(screenHelper.screenCountX-borderWidth, screenHelper.screenCountY-borderWidth, screenHelper.topRight-borderWidth).color(0f, 0f, 0f, opacity).endVertex();
        buff.pos(borderWidth, screenHelper.screenCountY-borderWidth, screenHelper.topLeft-borderWidth).color(0f, 0f, 0f, opacity).endVertex();
        buff.pos(borderWidth, borderWidth, screenHelper.bottomLeft-borderWidth).color(0f, 0f, 0f, opacity).endVertex();
        buff.pos(screenHelper.screenCountX-borderWidth, borderWidth, screenHelper.bottomRight-borderWidth).color(0f, 0f, 0f, opacity).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
    }

    void renderScreenModelTESR(FlatScreenHelper screenHelper){
        if(screenHelper.opacity != 100)
            return;

        float r = 1f/255 * screenHelper.color.getRed() * 0.3f;
        float g = 1f/255 * screenHelper.color.getGreen() * 0.3f;
        float b = 1f/255 * screenHelper.color.getBlue() * 0.3f;

        GlStateManager.pushMatrix();

        rotateByBlockOrigin(screenHelper);

        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // front
        buff.pos(screenHelper.screenCountX-borderWidth, screenHelper.screenCountY-borderWidth, screenHelper.topRight-borderWidth).color(0f, 0f, 0f, 1).endVertex();
        buff.pos(borderWidth, screenHelper.screenCountY-borderWidth, screenHelper.topLeft-borderWidth).color(0f, 0f, 0f, 1).endVertex();
        buff.pos(borderWidth, borderWidth, screenHelper.bottomLeft-borderWidth).color(0f, 0f, 0f, 1).endVertex();
        buff.pos(screenHelper.screenCountX-borderWidth, borderWidth, screenHelper.bottomRight-borderWidth).color(0f, 0f, 0f, 1).endVertex();

        // back
        buff.pos(screenHelper.screenCountX-borderWidth, borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(borderWidth, borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(borderWidth, screenHelper.screenCountY-borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(screenHelper.screenCountX-borderWidth, screenHelper.screenCountY-borderWidth, 0).color(r, g, b, 1).endVertex();

        if(borderWidth == 0f){
            // top
            buff.pos(screenHelper.screenCountX, screenHelper.screenCountY, 0).color(r, g, b, 1).endVertex();
            buff.pos(0, screenHelper.screenCountY, 0).color(r, g, b, 1).endVertex();
            buff.pos(0, screenHelper.screenCountY, screenHelper.topLeft).color(r, g, b, 1).endVertex();
            buff.pos(screenHelper.screenCountX, screenHelper.screenCountY, screenHelper.topRight).color(r, g, b, 1).endVertex();

            // bottom
            buff.pos(screenHelper.screenCountX-borderWidth, 0, screenHelper.bottomRight).color(r, g, b, 1).endVertex();
            buff.pos(borderWidth, 0, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();
            buff.pos(borderWidth, 0, 0).color(r, g, b, 1).endVertex();
            buff.pos(screenHelper.screenCountX-borderWidth, 0, 0).color(r, g, b, 1).endVertex();

            // left
            buff.pos(screenHelper.screenCountX, screenHelper.screenCountY, 0).color(r, g, b, 1).endVertex();
            buff.pos(screenHelper.screenCountX, screenHelper.screenCountY, screenHelper.topRight).color(r, g, b, 1).endVertex();
            buff.pos(screenHelper.screenCountX, 0, screenHelper.bottomRight).color(r, g, b, 1).endVertex();
            buff.pos(screenHelper.screenCountX, 0, 0).color(r, g, b, 1).endVertex();

            // right
            buff.pos(0, 0, 0).color(r, g, b, 1).endVertex();
            buff.pos(0, 0, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();
            buff.pos(0, screenHelper.screenCountY, screenHelper.topLeft).color(r, g, b, 1).endVertex();
            buff.pos(0, screenHelper.screenCountY, 0).color(r, g, b, 1).endVertex();
        }

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();

        if(borderWidth != 0f) {
            renderScreenModelFrameLeftRightTESR(screenHelper, 0);
            renderScreenModelFrameLeftRightTESR(screenHelper, screenHelper.screenCountX - borderWidth);
            renderScreenModelFrameTopTESR(screenHelper, screenHelper.screenCountY-borderWidth);
            renderScreenModelFrameBottomTESR(screenHelper, 0);
        }
    }

    private void renderScreenModelFrameTopTESR(FlatScreenHelper screenHelper, float posY){
        if(screenHelper.opacity != 100)
            return;

        float r = 1f/255 * screenHelper.color.getRed() * 0.5f;
        float g = 1f/255 * screenHelper.color.getGreen() * 0.5f;
        float b = 1f/255 * screenHelper.color.getBlue() * 0.5f;
        GlStateManager.pushMatrix();

        rotateByBlockOrigin(screenHelper);

        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        float topLeft = Math.min(screenHelper.topLeft, screenHelper.topRight) + Triangle.SubB(borderWidth, screenHelper.topLeft/screenHelper.topRight);

        // front
        buff.pos(screenHelper.screenCountX, posY+borderWidth, topLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, topLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, topLeft).color(r, g, b, 1).endVertex();
        buff.pos(screenHelper.screenCountX, posY, screenHelper.topRight).color(r, g, b, 1).endVertex();

        // back
        buff.pos(screenHelper.screenCountX, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(screenHelper.screenCountX, posY+borderWidth, 0).color(r, g, b, 1).endVertex();

        // top
        buff.pos(screenHelper.screenCountX, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, screenHelper.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(screenHelper.screenCountX, posY+borderWidth, screenHelper.topLeft).color(r, g, b, 1).endVertex();

        // bottom
        buff.pos(screenHelper.screenCountX, posY, screenHelper.topRight).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, screenHelper.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(screenHelper.screenCountX, posY, 0).color(r, g, b, 1).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
    }

    private void renderScreenModelFrameBottomTESR(FlatScreenHelper screenHelper, float posY){
        if(screenHelper.opacity != 100)
            return;

        float r = 1f/255 * screenHelper.color.getRed() * 0.5f;
        float g = 1f/255 * screenHelper.color.getGreen() * 0.5f;
        float b = 1f/255 * screenHelper.color.getBlue() * 0.5f;
        GlStateManager.pushMatrix();

        rotateByBlockOrigin(screenHelper);

        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // front
        buff.pos(screenHelper.screenCountX, posY+borderWidth, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(screenHelper.screenCountX, posY, screenHelper.bottomRight).color(r, g, b, 1).endVertex();

        // back
        buff.pos(screenHelper.screenCountX, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(screenHelper.screenCountX, posY+borderWidth, 0).color(r, g, b, 1).endVertex();

        // bottom
        buff.pos(screenHelper.screenCountX, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(screenHelper.screenCountX, posY+borderWidth, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();

        // bottom
        buff.pos(screenHelper.screenCountX, posY, screenHelper.bottomRight).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(screenHelper.screenCountX, posY, 0).color(r, g, b, 1).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
    }

    private void renderScreenModelFrameLeftRightTESR(FlatScreenHelper screenHelper, float posX){
        if(screenHelper.opacity != 100)
            return;

        float r = 1f/255 * screenHelper.color.getRed() * 0.5f;
        float g = 1f/255 * screenHelper.color.getGreen() * 0.5f;
        float b = 1f/255 * screenHelper.color.getBlue() * 0.5f;
        GlStateManager.pushMatrix();

        rotateByBlockOrigin(screenHelper);

        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // front
        buff.pos(posX+borderWidth, screenHelper.screenCountY, screenHelper.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX, screenHelper.screenCountY, screenHelper.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX, 0, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, 0, screenHelper.bottomRight).color(r, g, b, 1).endVertex();

        // back
        buff.pos(posX+borderWidth, 0, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX, 0, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX, screenHelper.screenCountY, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, screenHelper.screenCountY, 0).color(r, g, b, 1).endVertex();

        // top
        buff.pos(posX+borderWidth, screenHelper.screenCountY, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX, screenHelper.screenCountY, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX, screenHelper.screenCountY, screenHelper.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, screenHelper.screenCountY, screenHelper.topRight).color(r, g, b, 1).endVertex();

        // bottom
        buff.pos(posX+borderWidth, 0, screenHelper.bottomRight).color(r, g, b, 1).endVertex();
        buff.pos(posX, 0, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX, 0, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, 0, 0).color(r, g, b, 1).endVertex();

        // left
        buff.pos(posX+borderWidth, screenHelper.screenCountY, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, screenHelper.screenCountY, screenHelper.topRight).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, 0, screenHelper.bottomRight).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, 0, 0).color(r, g, b, 1).endVertex();

        // right
        buff.pos(posX, 0, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX, 0, screenHelper.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX, screenHelper.screenCountY, screenHelper.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX, screenHelper.screenCountY, 0).color(r, g, b, 1).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
    }

    void renderContent(TileEntityMultiblockDisplay screen, float sizeX, float sizeY, float partialTicks){
        rotateByBlockOrigin(screen.getHelper());

        // Fit area to screen (bottom left = bottom left).
        // and Slightly offset the text on the z-axis so it doesn't clip into the screen.
        GlStateManager.translate(0, 1f, 1.0001f);


        GlStateManager.translate(0, screen.getHelper().screenCountY-1, 0);

        GlStateManager.translate(screen.getHelper().tiltRenderOffset.x, screen.getHelper().tiltRenderOffset.y, screen.getHelper().tiltRenderOffset.z);

        // Flip text upside down.
        GlStateManager.scale(1, -1, -1);

        float scaleX = screen.getHelper().screenCountX / sizeX;
        float scaleY = screen.getHelper().screenCountY / sizeY;

        // rotate the matrix to align to the panel tilt
        GlStateManager.rotate((float) screen.getHelper().tiltRotationVector.x, -1, 0, 0);
        GlStateManager.rotate((float) screen.getHelper().tiltRotationVector.y, 0, -1, 0);

        // align the rendering on the center/middle of the tilted screen
        GlStateManager.translate(0, 0, borderWidth);

        //float tiltedScale = displayWidth > displayHeight ? displayWidth/screenCountX : displayHeight/screenCountY;
        //GlStateManager.scale(tiltedScale, tiltedScale, 1);

        // align content to center/middle
        if (scaleX > scaleY) {
            GlStateManager.translate(sizeX * 0.5f * (scaleX - scaleY), 0, 0);
            GlStateManager.scale(scaleY, scaleY, 1);
        }
        else {
            GlStateManager.translate(0, sizeY * 0.5f * (scaleY - scaleX), 0);
            GlStateManager.scale(scaleX, scaleX, 1);
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.disableStandardItemLighting();
        setLightmapDisabled(true);

        GlStateManager.color(1, 1, 1, 1);


        GlStateManager.pushMatrix();

        renderScreenContent(screen, partialTicks);

        GlStateManager.popMatrix();


        RenderHelper.enableStandardItemLighting();
        setLightmapDisabled(false);
        GlStateManager.disableBlend();
    }


    abstract void renderScreenContent(TileEntityMultiblockDisplay screen, float partialTicks);

}
