package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.client.renderer.RenderCase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class TileEntityCase_next extends TileEntityCase {
    public TileEntityCase_next(int tier){
        super(tier);
    }

    public TileEntityCase_next(){
        this(getTierFromConfig("tier_workstation"));
    }

    public static RenderCase.statusLED getPowerLED(){
        return new RenderCase.statusLED(new Vec3d(1f/16*1, 0.5001, 1f/16 * 5), 1f/16, 1f/16, EnumFacing.UP);
    }

    public static RenderCase.statusLED getStatusLED(){
        return new RenderCase.statusLED(new Vec3d(-1f/16*1, 0.5001, 1f/16 * 5), 1f/16, 1f/16, EnumFacing.UP);
    }
}
