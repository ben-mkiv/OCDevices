package ben_mkiv.ocdevices.common.integration.MCMultiPart.blocks;

import ben_mkiv.ocdevices.common.blocks.BlockKeyboard;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.BlockMultipart;
import mcmultipart.api.slot.EnumFaceSlot;
import net.minecraft.block.Block;

public class KeyboardMultipart extends BlockMultipart
{
    public KeyboardMultipart(){
        super(EnumFaceSlot.DOWN);
    }

    @Override
    public Block getBlock()
    {
        return BlockKeyboard.DEFAULTITEM;
    }
}

