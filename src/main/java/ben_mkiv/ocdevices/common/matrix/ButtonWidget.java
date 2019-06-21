package ben_mkiv.ocdevices.common.matrix;

import net.minecraft.nbt.NBTTagCompound;

public class ButtonWidget extends MatrixWidget {
    public ButtonWidget(String name){
        super(name);
    }

    public ButtonWidget(NBTTagCompound nbt){
        super(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        tag.setString("type", "button");

        return super.writeToNBT(tag);
    }
}
