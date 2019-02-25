package ben_mkiv.ocdevices.common.tileentity;

public class TileEntityCase_workstation extends TileEntityCase {
    public TileEntityCase_workstation(int tier){
        super(tier);
    }

    public TileEntityCase_workstation(){
        this(getTierFromConfig("tier_workstation"));
    }
}
