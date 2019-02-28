package ben_mkiv.ocdevices.common.integration.MCMultiPart;

import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipartTile;
import net.minecraft.tileentity.TileEntity;

public class TileEntityMultipart implements IMultipartTile {
    protected final TileEntity tile;
    private String id;
    private IPartInfo partInfo;

    public TileEntityMultipart(TileEntity te) {
        this.tile = te;
    }

    @Override
    public TileEntity getTileEntity() {
        return tile;
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
