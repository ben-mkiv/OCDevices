package ben_mkiv.ocdevices.common.integration.MCMultiPart.blocks;

import ben_mkiv.ocdevices.common.blocks.BlockFlatScreen;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.BlockMultipart;
import mcmultipart.api.slot.EnumCenterSlot;
import net.minecraft.block.Block;

public class FlatScreenMultipart extends BlockMultipart
{
    public FlatScreenMultipart(){
        super(EnumCenterSlot.CENTER);
    }

    @Override
    public Block getBlock()
    {
        return BlockFlatScreen.DEFAULTITEM;
    }
}

