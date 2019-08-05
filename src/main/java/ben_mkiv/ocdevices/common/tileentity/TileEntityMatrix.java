package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.flatscreen.FlatScreen;
import ben_mkiv.ocdevices.common.matrix.*;
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

        if(!isOrigin())
            return ((TileEntityMatrix) origin()).activated(side, hitVec);

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
    public Object[] addBox(Context context, Arguments args){
        int index = getMaxIndex()+1;
        widgets.put(index, new ButtonWidget("widget" + index));
        markDirty();
        return new Object[] { new buttonLuaObject(index, this) };
    }

    @Callback(doc = "function():boolean; adds a widget")
    public Object[] addItem(Context context, Arguments args){
        int index = getMaxIndex()+1;
        widgets.put(index, new ItemWidget("widget" + index));
        markDirty();
        return new Object[] { new itemLuaObject(index, this) };
    }

    @Callback(doc = "function():table; gets all widgets")
    public Object[] widgets(Context context, Arguments args){
        Map<Object, Object> widgetList = new HashMap<>();

        for(Map.Entry<Integer, MatrixWidget> widget : widgets.entrySet())
            widgetList.put(widget.getValue().getName(), new buttonLuaObject(widget.getKey(), this));

        return new Object[] { widgetList };
    }

    @Callback(doc = "function():table; removes all widgets")
    public Object[] removeAll(Context context, Arguments args){
        widgets.clear();
        markDirty();
        return new Object[] { widgets.size() == 0 };
    }

    private int getMaxIndex(){
        int max = 0;
        for(int index : widgets.keySet())
            max = Math.max(max, index);

        return max;
    }

    @Callback(doc = "function(int:depth, String:side):boolean; sets the screen sides depth")
    public Object[] setDepth(Context context, Arguments args) {
        int depth = args.optInteger(0, FlatScreen.maxScreenDepth);
        String side = args.optString(1, "all").toLowerCase();
        Object[] returnVals = getData().setDepth(depth, side);
        updateNeighbours();
        return returnVals;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        widgets.clear();
        for(int i=0; tag.hasKey("widget"+i); i++){
            NBTTagCompound nbt = tag.getCompoundTag("widget"+i);
            int index = nbt.getInteger("index");

            switch(nbt.getString("type")){
                case "item":
                    widgets.put(index, new ItemWidget(nbt));
                    break;
                case "button":
                    widgets.put(index, new ButtonWidget(nbt));
                    break;
            }

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

    @Override
    public boolean canMerge(TileEntityMultiblockDisplay screen){
        return screen instanceof TileEntityMatrix && super.canMerge(screen);
    }

}

