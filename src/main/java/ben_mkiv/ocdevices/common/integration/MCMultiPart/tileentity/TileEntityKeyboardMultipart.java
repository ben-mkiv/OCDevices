package ben_mkiv.ocdevices.common.integration.MCMultiPart.tileentity;

import ben_mkiv.ocdevices.common.tileentity.TileEntityKeyboard;
import li.cil.oc.server.component.Keyboard;
import mcmultipart.api.multipart.IMultipartTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityKeyboardMultipart implements IMultipartTile, ITickable {
    private TileEntityKeyboard tile;

    public TileEntityKeyboardMultipart(TileEntityKeyboard keyboardTE) {
        this.tile = keyboardTE;
    }

    @Override
    public void update() {
        if(tile.node() != null)
            ((Keyboard) tile.node().host()).update();
    }

    @Override
    public TileEntity getTileEntity() {
        return tile;
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    @Override
    public ITickable getTickable() {
        return this;
    }

}
