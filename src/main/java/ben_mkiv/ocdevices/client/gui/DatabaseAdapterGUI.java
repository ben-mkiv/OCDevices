package ben_mkiv.ocdevices.client.gui;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.inventory.DatabaseAdapterContainer;
import ben_mkiv.ocdevices.common.tileentity.TileEntityDatabaseAdapter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class DatabaseAdapterGUI extends ContainerGUI {
    private static final int WIDTH = 175;
    private static final int HEIGHT = 195;

    private static final ResourceLocation background = new ResourceLocation(OCDevices.MOD_ID, "textures/gui/carddock.png");

    private TileEntityDatabaseAdapter te;

    public DatabaseAdapterGUI(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new DatabaseAdapterContainer(inventoryPlayer, tileEntity), WIDTH, HEIGHT);
        te = (TileEntityDatabaseAdapter) tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawCenteredString("Database Adapter", 5, 4210752);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
