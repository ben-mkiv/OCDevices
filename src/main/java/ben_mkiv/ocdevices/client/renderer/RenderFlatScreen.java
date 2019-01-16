package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.common.flatscreen.FlatScreenHelper;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.utils.Triangle;
import li.cil.oc.api.internal.TextBuffer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class RenderFlatScreen extends TileEntitySpecialRenderer<TileEntityFlatScreen>{

    private FlatScreenHelper screen;
    float borderWidth = 0f;//1f/32;

    @Override
    public void render(TileEntityFlatScreen tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha){

        if(!tileEntity.isOrigin())
            return;

        screen = new FlatScreenHelper(tileEntity);

        GlStateManager.disableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        renderScreenModelTESR();

        renderScreenBackground();

        if(tileEntity.buffer().isRenderingEnabled())
            renderScreenContent(tileEntity.buffer());

        GlStateManager.popMatrix();

        GlStateManager.enableTexture2D();
    }

    private void rotateByBlockOrigin(){
        GlStateManager.translate(0.5, 0.5, 0.5);

        switch(screen.facing){
            case WEST: GlStateManager.rotate(-90, 0, 1, 0); break;
            case NORTH: GlStateManager.rotate(180, 0, 1, 0); break;
            case EAST: GlStateManager.rotate(90, 0, 1, 0); break;
        }

        switch(screen.pitch){
            case DOWN: GlStateManager.rotate(90, 1, 0, 0); break;
            case UP: GlStateManager.rotate(-90, 1, 0, 0); break;
        }

        GlStateManager.translate(-0.5, -0.5, -0.5);
    }


    private void renderScreenContent(TextBuffer buf){
        rotateByBlockOrigin();

        // Fit area to screen (bottom left = bottom left).
        // and Slightly offset the text on the z-axis so it doesn't clip into the screen.
        GlStateManager.translate(0, 1f, 1.0001f);


        GlStateManager.translate(0, screen.screenCountY-1, 0);

        GlStateManager.translate(screen.tiltRenderOffset.x, screen.tiltRenderOffset.y, screen.tiltRenderOffset.z);

        // Flip text upside down.
        GlStateManager.scale(1, -1, -1);

        float sizeX = buf.renderWidth();
        float sizeY = buf.renderHeight();
        float scaleX = screen.screenCountX / sizeX;
        float scaleY = screen.screenCountY / sizeY;

        // rotate the matrix to align to the panel tilt
        GlStateManager.rotate(screen.tiltRotationVector.getX(), -1, 0, 0);
        GlStateManager.rotate(screen.tiltRotationVector.getY(), 0, -1, 0);

        // align the rendering on the center/middle of the tilted screen
        GlStateManager.translate((screen.displayWidth-screen.screenCountX)/2f, (screen.displayHeight-screen.screenCountY)/2f, borderWidth);

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

        // finally render the TextBuffer
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.disableStandardItemLighting();
        setLightmapDisabled(true);

        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.pushMatrix();

        buf.renderText();

        if(screen.opacity != 100){
            // render the whole text again on the backside as the oc method disables the depth mask
            GlStateManager.translate(0, 0, .0002);
            GlStateManager.disableCull();
            buf.renderText();
            GlStateManager.enableCull();
        }

        GlStateManager.popMatrix();


        RenderHelper.enableStandardItemLighting();
        setLightmapDisabled(false);
        GlStateManager.disableBlend();
    }


    private void renderScreenBackground(){
        float opacity = 1f/100f * (float) screen.opacity;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.pushMatrix();

        rotateByBlockOrigin();


        GlStateManager.depthMask(false); // to fix rendering of water behind/next to the screen, anyways look into this if more problems occur https://github.com/CoFH/ThermalExpansion/tree/1.12/src/main/java/cofh/thermalexpansion/render

        if(screen.opacity != 100 && screen.opacity > 0)
            GlStateManager.disableCull();

        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // front
        buff.pos(screen.screenCountX-borderWidth, screen.screenCountY-borderWidth, screen.topRight-borderWidth).color(0f, 0f, 0f, opacity).endVertex();
        buff.pos(borderWidth, screen.screenCountY-borderWidth, screen.topLeft-borderWidth).color(0f, 0f, 0f, opacity).endVertex();
        buff.pos(borderWidth, borderWidth, screen.bottomLeft-borderWidth).color(0f, 0f, 0f, opacity).endVertex();
        buff.pos(screen.screenCountX-borderWidth, borderWidth, screen.bottomRight-borderWidth).color(0f, 0f, 0f, opacity).endVertex();



        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();

    }

    private void renderScreenModelTESR(){
        if(screen.opacity != 100)
            return;


        float r = 1f/255 * screen.color.getRed() * 0.3f;
        float g = 1f/255 * screen.color.getGreen() * 0.3f;
        float b = 1f/255 * screen.color.getBlue() * 0.3f;



        GlStateManager.pushMatrix();

        rotateByBlockOrigin();

        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // front
        buff.pos(screen.screenCountX-borderWidth, screen.screenCountY-borderWidth, screen.topRight-borderWidth).color(0f, 0f, 0f, 1).endVertex();
        buff.pos(borderWidth, screen.screenCountY-borderWidth, screen.topLeft-borderWidth).color(0f, 0f, 0f, 1).endVertex();
        buff.pos(borderWidth, borderWidth, screen.bottomLeft-borderWidth).color(0f, 0f, 0f, 1).endVertex();
        buff.pos(screen.screenCountX-borderWidth, borderWidth, screen.bottomRight-borderWidth).color(0f, 0f, 0f, 1).endVertex();

        // back
        buff.pos(screen.screenCountX-borderWidth, borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(borderWidth, borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(borderWidth, screen.screenCountY-borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(screen.screenCountX-borderWidth, screen.screenCountY-borderWidth, 0).color(r, g, b, 1).endVertex();



        if(borderWidth == 0f){
            // top
            buff.pos(screen.screenCountX, screen.screenCountY, 0).color(r, g, b, 1).endVertex();
            buff.pos(0, screen.screenCountY, 0).color(r, g, b, 1).endVertex();
            buff.pos(0, screen.screenCountY, screen.topLeft).color(r, g, b, 1).endVertex();
            buff.pos(screen.screenCountX, screen.screenCountY, screen.topRight).color(r, g, b, 1).endVertex();

            // bottom
            buff.pos(screen.screenCountX-borderWidth, 0, screen.bottomRight).color(r, g, b, 1).endVertex();
            buff.pos(borderWidth, 0, screen.bottomLeft).color(r, g, b, 1).endVertex();
            buff.pos(borderWidth, 0, 0).color(r, g, b, 1).endVertex();
            buff.pos(screen.screenCountX-borderWidth, 0, 0).color(r, g, b, 1).endVertex();

            // left
            buff.pos(screen.screenCountX, screen.screenCountY, 0).color(r, g, b, 1).endVertex();
            buff.pos(screen.screenCountX, screen.screenCountY, screen.topRight).color(r, g, b, 1).endVertex();
            buff.pos(screen.screenCountX, 0, screen.bottomRight).color(r, g, b, 1).endVertex();
            buff.pos(screen.screenCountX, 0, 0).color(r, g, b, 1).endVertex();

            // right
            buff.pos(0, 0, 0).color(r, g, b, 1).endVertex();
            buff.pos(0, 0, screen.bottomLeft).color(r, g, b, 1).endVertex();
            buff.pos(0, screen.screenCountY, screen.topLeft).color(r, g, b, 1).endVertex();
            buff.pos(0, screen.screenCountY, 0).color(r, g, b, 1).endVertex();
        }

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();

        if(borderWidth != 0f) {
            renderScreenModelFrameLeftRightTESR(0);
            renderScreenModelFrameLeftRightTESR(screen.screenCountX - borderWidth);
            renderScreenModelFrameTopTESR(screen.screenCountY-borderWidth);
            renderScreenModelFrameBottomTESR(0);
        }
    }

    private void renderScreenModelFrameTopTESR(float posY){
        if(screen.opacity != 100)
            return;

        float r = 1f/255 * screen.color.getRed() * 0.5f;
        float g = 1f/255 * screen.color.getGreen() * 0.5f;
        float b = 1f/255 * screen.color.getBlue() * 0.5f;
        GlStateManager.pushMatrix();

        rotateByBlockOrigin();

        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        float topLeft = Math.min(screen.topLeft, screen.topRight) + Triangle.SubB(borderWidth, screen.topLeft/screen.topRight);

        // front
        buff.pos(screen.screenCountX, posY+borderWidth, topLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, topLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, topLeft).color(r, g, b, 1).endVertex();
        buff.pos(screen.screenCountX, posY, screen.topRight).color(r, g, b, 1).endVertex();

        // back
        buff.pos(screen.screenCountX, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(screen.screenCountX, posY+borderWidth, 0).color(r, g, b, 1).endVertex();

        // top
        buff.pos(screen.screenCountX, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, screen.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(screen.screenCountX, posY+borderWidth, screen.topLeft).color(r, g, b, 1).endVertex();

        // bottom
        buff.pos(screen.screenCountX, posY, screen.topRight).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, screen.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(screen.screenCountX, posY, 0).color(r, g, b, 1).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
    }

    private void renderScreenModelFrameBottomTESR(float posY){
        if(screen.opacity != 100)
            return;

        float r = 1f/255 * screen.color.getRed() * 0.5f;
        float g = 1f/255 * screen.color.getGreen() * 0.5f;
        float b = 1f/255 * screen.color.getBlue() * 0.5f;
        GlStateManager.pushMatrix();

        rotateByBlockOrigin();

        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // front
        buff.pos(screen.screenCountX, posY+borderWidth, screen.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, screen.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, screen.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(screen.screenCountX, posY, screen.bottomRight).color(r, g, b, 1).endVertex();

        // back
        buff.pos(screen.screenCountX, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(screen.screenCountX, posY+borderWidth, 0).color(r, g, b, 1).endVertex();

        // bottom
        buff.pos(screen.screenCountX, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, 0).color(r, g, b, 1).endVertex();
        buff.pos(0, posY+borderWidth, screen.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(screen.screenCountX, posY+borderWidth, screen.bottomLeft).color(r, g, b, 1).endVertex();

        // bottom
        buff.pos(screen.screenCountX, posY, screen.bottomRight).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, screen.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(0, posY, 0).color(r, g, b, 1).endVertex();
        buff.pos(screen.screenCountX, posY, 0).color(r, g, b, 1).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
    }




    private void renderScreenModelFrameLeftRightTESR(float posX){
        if(screen.opacity != 100)
            return;

        float r = 1f/255 * screen.color.getRed() * 0.5f;
        float g = 1f/255 * screen.color.getGreen() * 0.5f;
        float b = 1f/255 * screen.color.getBlue() * 0.5f;
        GlStateManager.pushMatrix();

        rotateByBlockOrigin();

        BufferBuilder buff = Tessellator.getInstance().getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // front
        buff.pos(posX+borderWidth, screen.screenCountY, screen.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX, screen.screenCountY, screen.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX, 0, screen.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, 0, screen.bottomRight).color(r, g, b, 1).endVertex();

        // back
        buff.pos(posX+borderWidth, 0, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX, 0, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX, screen.screenCountY, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, screen.screenCountY, 0).color(r, g, b, 1).endVertex();

        // top
        buff.pos(posX+borderWidth, screen.screenCountY, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX, screen.screenCountY, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX, screen.screenCountY, screen.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, screen.screenCountY, screen.topRight).color(r, g, b, 1).endVertex();

        // bottom
        buff.pos(posX+borderWidth, 0, screen.bottomRight).color(r, g, b, 1).endVertex();
        buff.pos(posX, 0, screen.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX, 0, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, 0, 0).color(r, g, b, 1).endVertex();

        // left
        buff.pos(posX+borderWidth, screen.screenCountY, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, screen.screenCountY, screen.topRight).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, 0, screen.bottomRight).color(r, g, b, 1).endVertex();
        buff.pos(posX+borderWidth, 0, 0).color(r, g, b, 1).endVertex();

        // right
        buff.pos(posX, 0, 0).color(r, g, b, 1).endVertex();
        buff.pos(posX, 0, screen.bottomLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX, screen.screenCountY, screen.topLeft).color(r, g, b, 1).endVertex();
        buff.pos(posX, screen.screenCountY, 0).color(r, g, b, 1).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
    }

}
