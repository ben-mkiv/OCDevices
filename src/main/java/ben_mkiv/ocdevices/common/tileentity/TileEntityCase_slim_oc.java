package ben_mkiv.ocdevices.common.tileentity;

public class TileEntityCase_slim_oc extends TileEntityCase {
    public TileEntityCase_slim_oc(int tier){
        super(tier);
    }

    public TileEntityCase_slim_oc(){
        this(getTierFromConfig("tier_oc_slim"));
    }
}
