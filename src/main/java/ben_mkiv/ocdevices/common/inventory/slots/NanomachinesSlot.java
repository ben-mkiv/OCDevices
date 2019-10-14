package ben_mkiv.ocdevices.common.inventory.slots;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NanomachinesSlot extends SlotItemHandler implements ISlotTooltip {

    public NanomachinesSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public List<String> getTooltip(){
        return new ArrayList<>(Arrays.asList("Accepted Items:", "OpenComputers Nanomachines"));
    }
}
