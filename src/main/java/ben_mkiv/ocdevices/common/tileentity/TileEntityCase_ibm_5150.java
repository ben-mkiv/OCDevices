package ben_mkiv.ocdevices.common.tileentity;

import net.minecraft.util.EnumFacing;

public class TileEntityCase_ibm_5150 extends TileEntityCase {
    public TileEntityCase_ibm_5150(){
        super();
        connectToSides.remove(EnumFacing.UP);
        connectToSides.add(EnumFacing.EAST);
        connectToSides.add(EnumFacing.WEST);
    }
}
