package ben_mkiv.ocdevices.common.matrix;

import ben_mkiv.rendertoolkit.common.widgets.IRenderableWidget;
import ben_mkiv.rendertoolkit.common.widgets.component.world.Item3D;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ItemWidget extends ButtonWidget {
    public ItemStack stack;

    public IRenderableWidget renderable;

    public boolean isBlock = false;

    public ItemWidget(String name){
        super(name);
        width = height = 8;
    }

    public ItemWidget(NBTTagCompound nbt){
        super(nbt);
    }

    public boolean setItem(String name, int meta){
        Item item = Item.getByNameOrId(name);
        if(item == null) {
            renderable = null;
            return false;
        }

        stack = new ItemStack(item, 1, meta);

        isBlock = !Block.getBlockFromItem(stack.getItem()).equals(Blocks.AIR);

        if(FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT)){
            Item3D item3D = new Item3D();
            item3D.setItem(stack);

            renderable = item3D.getRenderable();
        }

        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        if(stack != null) {
            tag.setString("itemName", stack.getItem().getRegistryName().toString());
            tag.setInteger("itemMeta", stack.getMetadata());
        }

        tag = super.writeToNBT(tag);
        tag.setString("type", "item");

        return tag;
    }

    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        if(tag.hasKey("itemName"))
            setItem(tag.getString("itemName"), tag.getInteger("itemMeta"));
    }
}
