package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.client.renderer.RenderCase;
import li.cil.oc.common.Tier;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class TileEntityCase_workstation extends TileEntityCase {
    public TileEntityCase_workstation(int tier){
        super(tier);
    }

    public TileEntityCase_workstation(){
        this(Tier.One());
    }

    public static RenderCase.statusLED getPowerLED(){
        return new RenderCase.statusLED(new Vec3d(1f/32 * 6, 1f/32 * 11 + 0.001, 1f/32* 14 + 0.0035), 1f/16 * 1.25f, 1f/16, EnumFacing.NORTH);
    }

    public static RenderCase.statusLED getStatusLED(){
        return new RenderCase.statusLED(new Vec3d(1f/32 * 8.5, 1f/32 * 11 + 0.001, 1f/32* 14 + 0.0035), 1f/16 * 1.25f, 1f/16, EnumFacing.NORTH);
    }
}
