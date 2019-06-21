package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.client.models.ModelCube;
import ben_mkiv.ocdevices.common.matrix.ButtonWidget;
import ben_mkiv.ocdevices.common.matrix.ItemWidget;
import ben_mkiv.ocdevices.common.matrix.MatrixWidget;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMatrix;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMultiblockDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static ben_mkiv.rendertoolkit.surface.ClientSurface.vec3d000;

public class RenderMatrix extends RenderMultiblockDisplay {
    @Override
    public void render(TileEntityMultiblockDisplay matrix, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(!shouldRender(matrix))
            return;

        preRender(x, y, z, matrix.getHelper());
        if(matrix.shouldRenderContent())
            renderContent(matrix, matrix.getHelper().screenCountX, matrix.getHelper().screenCountY, partialTicks);
        postRender();
    }

    @Override
    public void renderScreenContent(TileEntityMultiblockDisplay screen, float partialTicks){
        renderMatrixContent((TileEntityMatrix) screen);
    }

    private void renderMatrixContent(TileEntityMatrix matrix){
        float scaleFactor = 1f/MatrixWidget.matrixResolution;

        GlStateManager.scale(1, 1, -1);
        GlStateManager.disableLighting();
        setLightmapDisabled(true);

        for(MatrixWidget widget : ((TileEntityMatrix) matrix.origin()).widgets.values()) {
            renderWidget(matrix, widget, scaleFactor);
        }
    }

    private void renderWidget(TileEntityMatrix matrix, MatrixWidget widget, float scaleFactor){
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        renderBox(matrix, widget, scaleFactor);
        GlStateManager.enableTexture2D();

        if(widget instanceof ItemWidget)
            renderItem(matrix, (ItemWidget) widget, scaleFactor);
        else if(widget instanceof ButtonWidget)
            renderLabel(matrix, (ButtonWidget) widget, scaleFactor);

        GlStateManager.popMatrix();
    }

    private void renderBox(TileEntityMatrix matrix, MatrixWidget widget, float scaleFactor){
        ModelCube box = new ModelCube(Math.min(matrix.width() * MatrixWidget.matrixResolution, widget.x + widget.width)  * scaleFactor, Math.min(matrix.height() * MatrixWidget.matrixResolution, widget.y + widget.height)  * scaleFactor, 0, widget.x * scaleFactor, widget.y * scaleFactor, 0.001f);

        Color bgColor = new Color(widget.backgroundColor);
        GlStateManager.color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());

        box.drawCube();
    }

    private void renderLabel(TileEntityMatrix matrix, ButtonWidget widget, float scaleFactor){
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(widget.getLabel().split("\n")));

        for(int line = 0; line < lines.size(); line++)
            renderLine(matrix, widget, lines.get(line), scaleFactor, line);
    }

    private void renderItem(TileEntityMatrix matrix, ItemWidget widget, float scaleFactor){
        if(widget.renderable == null)
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(scaleFactor * widget.x, scaleFactor * widget.y, 0.0115);

        /*
        GlStateManager.translate(0.5, 0.5, 0);
        GlStateManager.rotate(180, 1, 1, 0);
        GlStateManager.translate(-0.5, -0.5, 0);
        */

        GlStateManager.disableNormalize();

        GlStateManager.scale(scaleFactor * widget.width, -scaleFactor * widget.height, 0.001);

        if(widget.stack.getItem() instanceof ItemBlock) {
            GlStateManager.translate(0.5, -0.5, 0.5);
            GlStateManager.scale(0.75, 0.75, 0.75);
            GlStateManager.rotate(180, 0f, 1f, 0.20f);
            GlStateManager.rotate(45, 0f, -1, 0f);
        }

        //GlStateManager.disableDepth();
        //GlStateManager.depthMask(false);

        //GlStateManager.enableRescaleNormal();

        //widget.renderable.render(Minecraft.getMinecraft().player, vec3d000, ~0L);

        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(widget.stack);


        //GL11.glEnable(GL11.GL_SCISSOR_TEST);
        //GL11.glEnable(GL11.GL_SCISSOR_BOX);
        //GL11.glScissor(0, 0, 16, 16);
        Minecraft.getMinecraft().getRenderItem().renderItem(widget.stack, model);

        //GL11.glDisable(GL11.GL_SCISSOR_BOX);
        //GL11.glDisable(GL11.GL_SCISSOR_TEST);

        //Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, widget.stack, 0, 0, "what");

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();

        GlStateManager.popMatrix();
    }

    private void renderLine(TileEntityMatrix matrix, MatrixWidget widget, String text, float scaleFactor, int line){
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        float fontSize = (widget.fontSize - 12)/30f * scaleFactor;

        GlStateManager.pushMatrix();
        GlStateManager.translate(scaleFactor * widget.x, scaleFactor * line + scaleFactor * widget.y, 0.0015f);
        GlStateManager.scale(scaleFactor/12 + fontSize, scaleFactor/12 + fontSize, scaleFactor/12 + fontSize);
        int paddingLeft, paddingTop;
        paddingLeft = (int) (scaleFactor + (int) Math.round(0.25D * fontRenderer.FONT_HEIGHT));
        paddingTop = (int) (scaleFactor + (int) Math.round(0.25D * fontRenderer.FONT_HEIGHT));

        while(widget.x * scaleFactor + ((2 * paddingLeft + fontRenderer.getStringWidth(text)) * scaleFactor/12) > matrix.width())
            text = text.substring(0, text.length()-1);

        switch (widget.textAlignment){
            case RIGHT:
                paddingLeft = (int) (((double) widget.width/scaleFactor) - 2 * fontRenderer.getStringWidth(text)) - paddingLeft;
                break;
            case CENTER:
                paddingLeft = (int) (widget.width/scaleFactor/2 - fontRenderer.getStringWidth(text));
                break;
            case LEFT:
            default:
               break;
        }

        fontRenderer.drawString(text, paddingLeft, paddingTop, widget.foregroundColor);

        GlStateManager.popMatrix();
    }

}
