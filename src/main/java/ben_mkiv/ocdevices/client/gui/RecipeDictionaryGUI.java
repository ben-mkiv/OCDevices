package ben_mkiv.ocdevices.client.gui;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.inventory.RecipeDictionaryContainer;
import ben_mkiv.ocdevices.common.tileentity.TileEntityRecipeDictionary;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RecipeDictionaryGUI extends ContainerGUI {
    private static final int WIDTH = 175;
    private static final int HEIGHT = 195;

    private final TileEntityRecipeDictionary te;

    private static final ResourceLocation background = new ResourceLocation(OCDevices.MOD_ID, "textures/gui/recipe_dictionary.png");

    public RecipeDictionaryGUI(InventoryPlayer playerInventory, TileEntity tileEntity) {
        super(new RecipeDictionaryContainer(playerInventory, tileEntity), WIDTH, HEIGHT);
        te = (TileEntityRecipeDictionary) tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

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


