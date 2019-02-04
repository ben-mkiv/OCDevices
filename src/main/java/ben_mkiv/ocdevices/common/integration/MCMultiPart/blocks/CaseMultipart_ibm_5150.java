package ben_mkiv.ocdevices.common.integration.MCMultiPart.blocks;

import ben_mkiv.ocdevices.common.blocks.BlockCase_ibm_5150;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.BlockMultipart;
import mcmultipart.api.slot.EnumCenterSlot;
import net.minecraft.block.Block;

public class CaseMultipart_ibm_5150 extends BlockMultipart {
    public CaseMultipart_ibm_5150(){
        super(EnumCenterSlot.CENTER);
    }

    @Override
    public Block getBlock()
    {
        return BlockCase_ibm_5150.DEFAULTITEM;
    }
}
