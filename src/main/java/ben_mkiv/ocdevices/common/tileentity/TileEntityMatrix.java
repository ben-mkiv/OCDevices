package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.matrix.MatrixWidget;
import ben_mkiv.ocdevices.common.matrix.widgetLuaObject;
import li.cil.oc.api.API;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import java.util.HashMap;
import java.util.Map;

public class TileEntityMatrix extends TileEntityMultiblockDisplay {
    public HashMap<Integer, MatrixWidget> widgets = new HashMap<>();

    public TileEntityMatrix(){
        super();
        node = API.network.newNode(this, Visibility.Network).withComponent("matrix").withConnector().create();
    }

    public boolean activated(EnumFacing side, Vec3d hitVec){
        if(!side.equals(facing()))
            return false;

        hitVec = unmapHitVector(hitVec);

        for(Map.Entry<Integer, MatrixWidget> widget : widgets.entrySet()) {
            if (widget.getValue().hovered(hitVec)) {
                node().sendToReachable("computer.signal", "touch", widget.getKey(), widget.getValue().getName());
                return true;
            }
        }

        return false;
    }


    @Callback(doc = "function():boolean; adds a widget")
    public Object[] addWidget(Context context, Arguments args){
        int index = getMaxIndex()+1;
        widgets.put(index, new MatrixWidget("widget" + index));
        markDirty();
        return new Object[] { new widgetLuaObject(index, this) };
    }

    @Callback(doc = "function():table; gets all widgets")
    public Object[] widgets(Context context, Arguments args){
        Map<Object, Object> widgetList = new HashMap<>();

        for(Map.Entry<Integer, MatrixWidget> widget : widgets.entrySet())
            widgetList.put(widget.getValue().getName(), new widgetLuaObject(widget.getKey(), this));

        return new Object[] { widgetList };
    }

    private int getMaxIndex(){
        int max = 0;
        for(int index : widgets.keySet())
            max = Math.max(max, index);

        return max;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        widgets.clear();
        for(int i=0; tag.hasKey("widget"+i); i++){
            NBTTagCompound nbt = tag.getCompoundTag("widget"+i);
            widgets.put(nbt.getInteger("index"), new MatrixWidget(nbt));
        }
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        int i=0;
        for(Map.Entry<Integer, MatrixWidget> widget : widgets.entrySet()) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("index", widget.getKey());
            tag.setTag("widget"+i, widget.getValue().writeToNBT(nbt));
            i++;
        }

        return super.writeToNBT(tag);
    }

}

