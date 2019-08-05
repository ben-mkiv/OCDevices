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
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

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
        RenderHelper.disableStandardItemLighting();

        for(MatrixWidget widget : ((TileEntityMatrix) matrix.origin()).widgets.values()) {
            renderWidget(matrix, widget, scaleFactor);
        }

        setLightmapDisabled(false);
    }

    private void renderWidget(TileEntityMatrix matrix, MatrixWidget widget, float scaleFactor){
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        renderBox(matrix, widget, scaleFactor);
        GlStateManager.enableTexture2D();

        if(widget instanceof ItemWidget)
            renderItem(matrix, (ItemWidget) widget, scaleFactor);

        if(widget instanceof ButtonWidget)
            renderLabel(matrix, (ButtonWidget) widget, scaleFactor);

        GlStateManager.popMatrix();
    }

    private void renderBox(TileEntityMatrix matrix, MatrixWidget widget, float scaleFactor){
        ModelCube box = new ModelCube(Math.min(matrix.width() * MatrixWidget.matrixResolution, widget.x + widget.width)  * scaleFactor, Math.min(matrix.height() * MatrixWidget.matrixResolution, widget.y + widget.height)  * scaleFactor, 0, widget.x * scaleFactor, widget.y * scaleFactor, 0.001f);

        Color bgColor = new Color(widget.backgroundColor);

        float alpha = (float) (widget.backgroundColor >> 24 & 255) / 255f;

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(bgColor.getRed()/255f, bgColor.getGreen()/255f, bgColor.getBlue()/255f, alpha);

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

        if(scaleFactor * widget.x + scaleFactor * widget.width > matrix.width())
            return;

        if(scaleFactor * widget.y + scaleFactor * widget.height > matrix.height())
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(scaleFactor * widget.x, scaleFactor * widget.y, -scaleFactor - 0.5 * scaleFactor);
        GlStateManager.scale(1f/16*scaleFactor * widget.width, 1f/16*scaleFactor * widget.height, 0.00093);

        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(widget.stack, 0, 0);

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

        while(widget.x * scaleFactor + ((2 * paddingLeft + fontRenderer.getStringWidth(text)) * (scaleFactor/12 + fontSize)) > matrix.width())
            text = text.substring(0, text.length()-1);

        double textWidth = fontRenderer.getStringWidth(text) * fontSize * (scaleFactor/12);

        switch (widget.textAlignment){
            case RIGHT:
                paddingLeft = (int) (((double) widget.width/scaleFactor) - textWidth) - paddingLeft;
                break;
            case CENTER:
                paddingLeft = (int) ((widget.width/2 - textWidth / 2f)/scaleFactor);
                break;
            case LEFT:
            default:
               break;
        }
        paddingLeft = (int) ((widget.width/2 - textWidth / 2f)/scaleFactor);
        fontRenderer.drawString(text, paddingLeft, paddingTop, widget.foregroundColor);

        GlStateManager.popMatrix();
    }

}
