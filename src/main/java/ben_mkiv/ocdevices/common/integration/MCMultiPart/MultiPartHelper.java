package ben_mkiv.ocdevices.common.integration.MCMultiPart;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import ben_mkiv.ocdevices.common.tileentity.TileEntityKeyboard;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMultiblockDisplay;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class MultiPartHelper {
    public static boolean isMultipartCapability(Capability cap){
        return OCDevices.MCMultiPart && MCMultiPart.isMultipartCapability(cap);
    }

    public static World getRealWorld(TileEntity tile){
        return OCDevices.MCMultiPart ? MCMultiPart.getRealWorld(tile) : tile.getWorld();
    }

    public static TileEntityMultiblockDisplay getScreenFromTile(TileEntity tile){
        if(tile == null || tile.isInvalid())
            return null;

        if(tile instanceof TileEntityMultiblockDisplay)
            return (TileEntityMultiblockDisplay) tile;

        if(OCDevices.MCMultiPart)
            for(TileEntity mpTile : MCMultiPart.getMCMPTiles(tile).values())
                if(getScreenFromTile(mpTile) != null)
                    return getScreenFromTile(mpTile);

        return null;
    }

    public static TileEntityKeyboard getKeyboardFromTile(TileEntity tile){
        if(tile == null || tile.isInvalid())
            return null;

        if(tile instanceof TileEntityKeyboard)
            return (TileEntityKeyboard) tile;

        if(OCDevices.MCMultiPart)
            for(TileEntity mpTile : MCMultiPart.getMCMPTiles(tile).values())
                if(getKeyboardFromTile(mpTile) != null)
                    return getKeyboardFromTile(mpTile);

        return null;
    }

    public static TileEntityCase getCaseFromTile(TileEntity tile){
        if(tile == null || tile.isInvalid())
            return null;

        if(tile instanceof TileEntityCase)
            return (TileEntityCase) tile;

        if(OCDevices.MCMultiPart)
            for(TileEntity mpTile : MCMultiPart.getMCMPTiles(tile).values())
                if(getCaseFromTile(mpTile) != null)
                    return getCaseFromTile(mpTile);

        return null;
    }

}
