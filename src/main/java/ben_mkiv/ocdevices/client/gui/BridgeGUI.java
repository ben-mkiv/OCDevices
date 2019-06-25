package ben_mkiv.ocdevices.client.gui;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.inventory.BridgeContainer;
import ben_mkiv.ocdevices.common.tileentity.TileEntityBridge;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class BridgeGUI extends ContainerGUI {
    private static final int WIDTH = 175;
    private static final int HEIGHT = 195;

    private static final ResourceLocation background = new ResourceLocation(OCDevices.MOD_ID, "textures/gui/carddock.png");

    private TileEntityBridge te;

    public BridgeGUI(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new BridgeContainer(inventoryPlayer, tileEntity), WIDTH, HEIGHT);
        te = (TileEntityBridge) tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawCenteredString("Bridge", 5, 4210752);

        String brideId = te.linkId.length() > 19 ? te.linkId.substring(0, 19) + "..." : "no tunnel";

        drawCenteredString("bridge: " + brideId, 60, 4210752);
        if(te.linkActive) {
            drawCenteredString("interDimensional: " + te.interDimensional, 70, 4210752);
            if (!te.interDimensional) drawCenteredString("distance: " + te.distance + " blocks", 80, 4210752);
        }
        drawCenteredString("active: " + (te.linkActive ? "§2" : "§4") + te.linkActive, 90, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}

