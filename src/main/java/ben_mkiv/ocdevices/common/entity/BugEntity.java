package ben_mkiv.ocdevices.common.entity;

import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.world.World;

public class BugEntity extends EntitySilverfish {
    public static final String NAME = "bug";
    public BugEntity(World worldIn){
        super(worldIn);
        this.setSize(0.4F, 0.3F);
    }


}
