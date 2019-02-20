package ben_mkiv.ocdevices.client.gui;

import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import li.cil.oc.client.gui.Case;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class CaseGUI extends Case {
    public CaseGUI(InventoryPlayer inventoryPlayer, TileEntity computer){
        super(inventoryPlayer, MultiPartHelper.getCaseFromTile(computer));
    }
}
