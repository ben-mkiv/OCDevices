package ben_mkiv.ocdevices.common.component;

import ben_mkiv.ocdevices.common.flatscreen.FlatScreen;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Node;
import li.cil.oc.common.component.Screen;
import net.minecraft.nbt.NBTTagCompound;

public class FlatScreenComponent extends Screen {
    public FlatScreenComponent(EnvironmentHost container){
        super((TileEntityFlatScreen) container);
    }

    int size = 0;

    private void updateAll(){
        ((TileEntityFlatScreen) screen()).updateNeighbours();
    }

    @Override
    public void markInitialized(){
        super.markInitialized();
        updateAll();
    }

    @Override
    public void update(){
        super.update();

        int curSize = screen().width()*screen().height();

        if(curSize != size) {
            size = curSize;
            updateAll();
        }
    }

    public void onConnect(Node node){
        if(false && node.host() instanceof TileEntityFlatScreen){
            TileEntityFlatScreen te = (TileEntityFlatScreen) node.host();
            super.onConnect(node);
            te.updateNeighbours();
        }
        else
            super.onConnect(node);
    }

    public void onDisconnect(Node node){
        if(false && node.host() instanceof TileEntityFlatScreen){
            TileEntityFlatScreen te = (TileEntityFlatScreen) node.host();
            super.onDisconnect(node);
            te.updateNeighbours();
        }
        else
            super.onDisconnect(node);
    }

    private FlatScreen getData(){
        return ((TileEntityFlatScreen) screen()).getData();
    }


    /* OC Callbacks */

    @Callback(doc = "function(int:depth, String:side):boolean; sets the screen sides depth")
    public Object[] setDepth(Context context, Arguments args) {
        int depth = args.optInteger(0, FlatScreen.maxScreenDepth);
        String side = args.optString(1, "all").toLowerCase();
        Object[] returnVals = getData().setDepth(depth, side);
        updateAll();
        return returnVals;
    }
    /*
        @Callback(doc = "function(boolean:frameless):boolean; enables/disables the screen frame")
        public Object[] setFrameless(Context context, Arguments args) {
            data.setFrameless(args.optBoolean(0, true));
            updateAll();
            return new Object[]{ true };
        }
    */
    @Callback(doc = "function(boolean:frameless):boolean; enables/disables the screen model")
    public Object[] setOpaque(Context context, Arguments args) {
        getData().setOpaque(args.optBoolean(0, true));
        updateAll();
        return new Object[]{ true };
    }

    @Override
    public void load(net.minecraft.nbt.NBTTagCompound nbt) {
        super.load(nbt);
        size = nbt.getInteger("size");
    }

    @Override
    public void save(net.minecraft.nbt.NBTTagCompound nbt) {
        nbt.setInteger("size", size);
        super.save(nbt);
    }

}