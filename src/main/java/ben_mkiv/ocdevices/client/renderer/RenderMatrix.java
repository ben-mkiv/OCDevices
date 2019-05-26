package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.client.models.ModelCube;
import ben_mkiv.ocdevices.common.matrix.MatrixWidget;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMatrix;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMultiblockDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

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

        for(MatrixWidget widget : ((TileEntityMatrix) matrix.origin()).widgets.values())
            renderWidget(matrix, widget, scaleFactor);
    }

    private void renderWidget(TileEntityMatrix matrix, MatrixWidget widget, float scaleFactor){
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        renderBox(matrix, widget, scaleFactor);
        GlStateManager.enableTexture2D();
        renderLabel(matrix, widget, scaleFactor);
        GlStateManager.popMatrix();
    }

    private void renderBox(TileEntityMatrix matrix, MatrixWidget widget, float scaleFactor){
        ModelCube box = new ModelCube(Math.min(matrix.width() * MatrixWidget.matrixResolution, widget.x + widget.width)  * scaleFactor, Math.min(matrix.height() * MatrixWidget.matrixResolution, widget.y + widget.height)  * scaleFactor, 0, widget.x * scaleFactor, widget.y * scaleFactor, 0.001f);

        Color bgColor = new Color(widget.backgroundColor);
        GlStateManager.color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());

        box.drawCube();
    }

    private void renderLabel(TileEntityMatrix matrix, MatrixWidget widget, float scaleFactor){
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(widget.getLabel().split("\n")));

        for(int line = 0; line < lines.size(); line++)
            renderLine(matrix, widget, lines.get(line), scaleFactor, line);
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
