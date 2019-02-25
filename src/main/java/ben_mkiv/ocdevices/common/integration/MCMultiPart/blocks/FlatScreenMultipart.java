package ben_mkiv.ocdevices.common.integration.MCMultiPart.blocks;

import ben_mkiv.ocdevices.common.blocks.BlockFlatScreen;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.BlockMultipart;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.slot.EnumCenterSlot;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

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

    @Override
    public void onAdded(IPartInfo part){
        TileEntity tile = part.getTile().getTileEntity();

        if(tile instanceof TileEntityFlatScreen)
            ((TileEntityFlatScreen) tile).updateRotation(part.getState());
    }
}

