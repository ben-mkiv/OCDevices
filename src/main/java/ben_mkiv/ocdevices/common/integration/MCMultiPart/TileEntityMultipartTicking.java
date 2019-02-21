package ben_mkiv.ocdevices.common.integration.MCMultiPart;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityMultipartTicking extends TileEntityMultipart implements ITickable {
    public TileEntityMultipartTicking(TileEntity tile){
        super(tile);
    }

    @Override
    public void update() {
        ((ITickable) tile).update();
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
