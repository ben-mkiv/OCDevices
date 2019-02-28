package ben_mkiv.ocdevices.client.gui;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.inventory.CardDockContainer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class CardDockGUI extends GuiContainer {
    private static final int WIDTH = 175;
    private static final int HEIGHT = 195;

    private static final ResourceLocation background = new ResourceLocation(OCDevices.MOD_ID, "textures/gui/carddock.png");

    public CardDockGUI(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new CardDockContainer(inventoryPlayer, tileEntity));
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    private void drawCenteredString(String string, int y, int color){
        FontRenderer fr = mc.fontRenderer;
        fr.drawString(string, getXSize()/2 - fr.getStringWidth(string)/2, y, color);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        drawCenteredString("Card Dock", 5, 4210752);
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}

