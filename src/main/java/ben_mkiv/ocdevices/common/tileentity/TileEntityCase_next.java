package ben_mkiv.ocdevices.common.tileentity;

public class TileEntityCase_next extends TileEntityCase {
    public TileEntityCase_next(int tier){
        super(tier);
    }

    public TileEntityCase_next(){
        this(getTierFromConfig("tier_workstation"));
    }
}
