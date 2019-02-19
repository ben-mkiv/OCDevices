package ben_mkiv.ocdevices.common.integration.MCMultiPart;

import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.common.tileentity.TileEntityKeyboard;
import net.minecraft.tileentity.TileEntity;

public class MultiPartHelper {

    public static TileEntityFlatScreen getScreenFromTile(TileEntity tile){
        if(tile instanceof TileEntityFlatScreen && !tile.isInvalid())
            return (TileEntityFlatScreen) tile;

        for(TileEntity mpTile : MCMultiPart.getMCMPTiles(tile).values())
            if(getScreenFromTile(mpTile) != null)
                return getScreenFromTile(mpTile);

        return null;
    }

    public static TileEntityKeyboard getKeyboardFromTile(TileEntity tile){
        if(tile instanceof TileEntityKeyboard)
            return (TileEntityKeyboard) tile;

        for(TileEntity mpTile : MCMultiPart.getMCMPTiles(tile).values())
            if(getKeyboardFromTile(mpTile) != null)
                return getKeyboardFromTile(mpTile);

        return null;
    }

}
