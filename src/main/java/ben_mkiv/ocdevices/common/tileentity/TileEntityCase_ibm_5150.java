package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.client.renderer.RenderCase;
import li.cil.oc.common.Tier;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class TileEntityCase_ibm_5150 extends TileEntityCase {
    public TileEntityCase_ibm_5150(int tier){
        super(tier);
        connectToSides.remove(EnumFacing.UP);
        connectToSides.add(EnumFacing.EAST);
        connectToSides.add(EnumFacing.WEST);
    }

    public TileEntityCase_ibm_5150(){
        this(Tier.One());
    }

    public static RenderCase.statusLED getPowerLED(){
        return new RenderCase.statusLED(new Vec3d(1f/32*7, -1f/32 * 9, 1f/32 * 14 + 0.001), 1f/32, 1f/32, EnumFacing.NORTH);
    }

    public static RenderCase.statusLED getStatusLED(){
        return new RenderCase.statusLED(new Vec3d(1f/32*7, -1f/32 * 12, 1f/32 * 14 + 0.001), 1f/32, 1f/32, EnumFacing.NORTH);
    }
}
