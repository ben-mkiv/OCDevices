package ben_mkiv.ocdevices.client.gui;

import ben_mkiv.guitoolkit.client.widget.prettyButton;
import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.inventory.ItemBenchContainer;
import ben_mkiv.ocdevices.common.nanoAnalyzer.capability.IanalyzeCapability;
import ben_mkiv.ocdevices.common.tileentity.TileEntityItemBench;
import ben_mkiv.ocdevices.network.OCDevicesNetwork;
import ben_mkiv.ocdevices.network.messages.GuiButtonClick;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import java.io.IOException;

import static ben_mkiv.ocdevices.common.nanoAnalyzer.capability.analyzeCapability.ANALYZE;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class ItemBenchGUI extends ContainerGUI {
    private static final int WIDTH = 175;
    private static final int HEIGHT = 195;

    private static final ResourceLocation background = new ResourceLocation(OCDevices.MOD_ID, "textures/gui/recipe_dictionary.png");

    TileEntityItemBench tileEntity;

    prettyButton infuseButton;

    public ItemBenchGUI(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new ItemBenchContainer(inventoryPlayer, tileEntity), WIDTH, HEIGHT);
        this.tileEntity = (TileEntityItemBench) tileEntity;

        infuseButton = new prettyButton(buttonList.size(), 0, 0, 120, 20, "infuse nanomachines");
        infuseButton.setVisible(false);
        infuseButton.action = "infuse";

    }

    @Override
    public void initGui(){
        super.initGui();

        infuseButton.setX(guiLeft + 20);
        infuseButton.setY(guiTop + 85);

        addButton(infuseButton);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button instanceof prettyButton)
            OCDevicesNetwork.channel.sendToServer(new GuiButtonClick(button, tileEntity));
        else
            super.actionPerformed(button);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        drawCenteredString("ItemBench", 5, 4210752);

        IItemHandler inventory = tileEntity.getCapability(ITEM_HANDLER_CAPABILITY, null);

        ItemStack stack = inventory.getStackInSlot(0);
        if(!stack.isEmpty() && stack.hasCapability(ANALYZE, null)){
            IanalyzeCapability cap = stack.getCapability(ANALYZE, null);

            if(cap.isAttached()) {
                infuseButton.setVisible(false);

                drawString("NanoAnalyzer available", 8, 80, 4210752);
                drawString("port: "+cap.getNetworkPort(), 8, 90, 4210752);
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5, 0.5, 0.5);
                drawString("uuid: "+cap.getUniqueId().toString(), 15, 200, 4210752);
                GlStateManager.popMatrix();
            }
            else {
                infuseButton.setVisible(true);
                infuseButton.enabled = !inventory.getStackInSlot(1).isEmpty();
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
