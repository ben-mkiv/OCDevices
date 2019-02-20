package ben_mkiv.ocdevices.common.inventory;

import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import li.cil.oc.common.container.Case;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class CaseContainer extends Case {
    public CaseContainer(InventoryPlayer playerInventory, TileEntity computer) {
        super(playerInventory, MultiPartHelper.getCaseFromTile(computer));
    }
}
