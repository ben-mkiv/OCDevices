package ben_mkiv.ocdevices.client.gui;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.inventory.RecipeDictionaryContainer;
import ben_mkiv.ocdevices.common.tileentity.TileEntityRecipeDictionary;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RecipeDictionaryGUI extends GuiContainer {
    private static final int WIDTH = 175;
    private static final int HEIGHT = 195;

    private final TileEntityRecipeDictionary te;

    private static final ResourceLocation background = new ResourceLocation(OCDevices.MOD_ID, "textures/gui/recipe_dictionary.png");

    public RecipeDictionaryGUI(InventoryPlayer playerInventory, TileEntity tileEntity) {
        super(new RecipeDictionaryContainer(playerInventory, tileEntity));
        te = (TileEntityRecipeDictionary) tileEntity;
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    private void drawCenteredString(String string, int y, int color){
        mc.fontRenderer.drawString(string, getXSize()/2 - mc.fontRenderer.getStringWidth(string)/2, y, color);
    }

    void drawString(String string, int x, int y, int color){
        mc.fontRenderer.drawString(string, x, y, color);
    }

    private void drawStringAlignRight(String string, int x, int y, int color){
        mc.fontRenderer.drawString(string, x-mc.fontRenderer.getStringWidth(string), y, color);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        FontRenderer fr = mc.fontRenderer;

        drawCenteredString("Recipe Dictionary", 5, 4210752);

        ItemStack dbStack = te.getComponentInventory().getStackInSlot(0);
        if(dbStack.isEmpty())
            drawStringAlignRight("no database installed", getXSize() - 38, 97, 4210752);
        else {
            drawStringAlignRight("usage: "+te.recipeSlotsUsed+"/"+te.recipeSlots + " slots", getXSize() - 38, 97, 4210752);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}


