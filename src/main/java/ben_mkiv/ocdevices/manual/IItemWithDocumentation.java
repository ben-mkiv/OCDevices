package ben_mkiv.ocdevices.manual;

import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public interface IItemWithDocumentation {

    public String getDocumentationName(ItemStack stack);
}