package ben_mkiv.ocdevices.common;

import ben_mkiv.ocdevices.client.gui.*;
import ben_mkiv.ocdevices.common.blocks.*;
import ben_mkiv.ocdevices.common.inventory.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        switch(ID){
            case BlockCardDock.GUI_ID:
                return new CardDockContainer(player.inventory, te);

            case BlockRecipeDictionary.GUI_ID:
                return new RecipeDictionaryContainer(player.inventory, te);

            case BlockCase.GUI_ID:
                return new CaseContainer(player.inventory, te);

            case BlockBridge.GUI_ID:
                return new BridgeContainer(player.inventory, te);

            case BlockDatabaseAdapter.GUI_ID:
                return new DatabaseAdapterContainer(player.inventory, te);

            case BlockItemBench.GUI_ID:
                return new ItemBenchContainer(player.inventory, te);

            default: return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        switch(ID){
            case BlockCardDock.GUI_ID:
                return new CardDockGUI(player.inventory, te);

            case BlockRecipeDictionary.GUI_ID:
                return new RecipeDictionaryGUI(player.inventory, te);

            case BlockCase.GUI_ID:
                return new CaseGUI(player.inventory, te);

            case BlockFlatScreen.GUI_ID:
                ScreenGUI.screen = te;
                return new ScreenGUI();

            case BlockBridge.GUI_ID:
                return new BridgeGUI(player.inventory, te);

            case BlockDatabaseAdapter.GUI_ID:
                return new DatabaseAdapterGUI(player.inventory, te);

            case BlockItemBench.GUI_ID:
                return new ItemBenchGUI(player.inventory, te);

            default: return null;
        }
    }
}