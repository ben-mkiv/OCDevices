package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.client.renderer.RenderCase;
import li.cil.oc.common.Tier;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class TileEntityCase_slim_oc extends TileEntityCase {
    public TileEntityCase_slim_oc(int tier){
        super(tier);
    }

    public TileEntityCase_slim_oc(){
        this(Tier.One());
    }

    public static RenderCase.statusLED getPowerLED(){
        return new RenderCase.statusLED(new Vec3d(-1f/16 * 3, 1f/16 * 6, 1f/16* 8 + 0.001), 1f/16 * 2, 1f/16, EnumFacing.NORTH);
    }

    public static RenderCase.statusLED getStatusLED(){
        return new RenderCase.statusLED(new Vec3d(1f/16 * 2, 1f/16 * 6, 1f/16* 8 + 0.001), 1f/16, 1f/16, EnumFacing.NORTH);
    }
}
