package ben_mkiv.ocdevices.common.integration.MCMultiPart;

import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipartTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityMultipart implements IMultipartTile, ITickable {
    protected TileEntity tile;
    private String id;
    private IPartInfo partInfo;
    private boolean canTick;

    public TileEntityMultipart(TileEntity te) {
        this.tile = te;
        canTick = te instanceof ITickable;
    }

    @Override
    public void update() {
        if(canTick) ((ITickable) tile).update();
    }

    @Override
    public TileEntity getTileEntity() {
        return tile;
    }

    @Override
    public boolean isTickable() {
        return canTick;
    }

    @Override
    public ITickable getTickable() {
        return canTick ? this : null;
    }

    @Override
    public void setPartInfo(IPartInfo info){
        partInfo = info;
    }

    public String getID(){
        return id;
    }

    public IPartInfo getInfo(){
        return partInfo;
    }

}
