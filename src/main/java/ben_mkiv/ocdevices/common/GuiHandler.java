package ben_mkiv.ocdevices.common;

import ben_mkiv.ocdevices.client.gui.CardDockGUI;
import ben_mkiv.ocdevices.common.inventory.CardDockContainer;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCardDock;
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
        if (te instanceof TileEntityCardDock) {
            return new CardDockContainer(player.inventory, (TileEntityCardDock) te);
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te instanceof TileEntityCardDock) {
            TileEntityCardDock containerTileEntity = (TileEntityCardDock) te;
            return new CardDockGUI(containerTileEntity, new CardDockContainer(player.inventory, containerTileEntity));
        }
        return null;
    }
}