package ben_mkiv.ocdevices.common.component;

import ben_mkiv.ocdevices.common.flatscreen.FlatScreen;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MCMultiPart;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Node;
import li.cil.oc.common.component.Screen;
import li.cil.oc.common.tileentity.Keyboard;
import net.minecraft.tileentity.TileEntity;

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

        if(node.host() instanceof li.cil.oc.server.component.Keyboard){
            for(TileEntity tile : MCMultiPart.getMCMPTiles(screen()).values()){
                if(tile instanceof Keyboard && ((Keyboard) tile).node().equals(node)){
                    node.connect(node());
                }
            }
        }

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
    @Callback(doc = "function(boolean:opaque):boolean; enables/disables opacity")
    public Object[] setOpaque(Context context, Arguments args) {
        return new Object[]{ setOpacity(context, args), "this method is deprecated, please use setOpacity() instead" };
    }

    @Callback(doc = "function(boolean:opaque OR integer:opacity):boolean; sets opacity, true/false or value from 0-100")
    public Object[] setOpacity(Context context, Arguments args) {
        if(args.isBoolean(0))
            getData().setOpaque(args.checkBoolean(0) ? 100 : 0);
        else if(args.isInteger(0))
            getData().setOpaque(Math.max(0, Math.min(args.checkInteger(0), 100)));
        else
            getData().setOpaque(100);

        updateAll();
        return new Object[]{ true };
    }

    /*
    @Callback(doc = "function(integer:padding):boolean; sets screen padding")
    public Object[] setPadding(Context context, Arguments args) {
        getData().setPadding(args.optInteger(0, 0));
        updateAll();
        return new Object[]{ true };
    }*/

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