package ben_mkiv.ocdevices.common.component;

import ben_mkiv.ocdevices.common.flatscreen.FlatScreen;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Node;
import li.cil.oc.common.component.TextBuffer;
import net.minecraft.tileentity.TileEntity;


public class FlatScreenComponent extends TextBuffer {
    public FlatScreenComponent(EnvironmentHost container){
        super(container);
        setMaximumResolution(160, 50);
        setMaximumColorDepth(li.cil.oc.api.internal.TextBuffer.ColorDepth.EightBit);
    }

    @Override
    public void markInitialized(){
        super.markInitialized();
        flatScreen().updateNeighbours();
    }

    public void onConnect(Node node){
        if(node.host() instanceof li.cil.oc.server.component.Keyboard)
            if(isKeyboardAdjacent(node))
                node.connect(node());

        super.onConnect(node);
    }

    public TileEntityFlatScreen screen(){
        return (TileEntityFlatScreen) host();
    }

    private boolean isKeyboardAdjacent(Node node){
        for(TileEntity keyboard : flatScreen().getKeyboards())
            if(keyboard instanceof Environment && ((Environment) keyboard).node() != null && ((Environment) keyboard).node().equals(node))
                return true;

        return false;
    }

    private TileEntityFlatScreen flatScreen(){
        return (TileEntityFlatScreen) super.host();
    }

    private FlatScreen getData(){
        return flatScreen().getData();
    }

    /* OC Callbacks */

    @Callback(doc = "function(int:depth, String:side):boolean; sets the screen sides depth")
    public Object[] setDepth(Context context, Arguments args) {
        int depth = args.optInteger(0, FlatScreen.maxScreenDepth);
        String side = args.optString(1, "all").toLowerCase();
        Object[] returnVals = getData().setDepth(depth, side);
        flatScreen().updateNeighbours();
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

        flatScreen().updateNeighbours();
        return new Object[]{ true };
    }

    /*
    @Callback(doc = "function(integer:padding):boolean; sets screen padding")
    public Object[] setPadding(Context context, Arguments args) {
        getData().setPadding(args.optInteger(0, 0));
        updateAll();
        return new Object[]{ true };
    }*/

    @Callback(direct = true, doc = "function():boolean -- Whether touch mode is inverted (sneak-activate opens GUI, instead of normal activate).")
    public Object[] isTouchModeInverted(Context computer, Arguments args) {
        return new Object[]{ screen().isTouchModeInverted() };
    }

    @Callback(doc = "function(value:boolean):boolean -- Sets whether to invert touch mode (sneak-activate opens GUI, instead of normal activate).")
    public Object[] setTouchModeInverted(Context computer, Arguments args) {
        boolean newValue = args.checkBoolean(0);
        boolean oldValue = this.screen().isTouchModeInverted();

        screen().setTouchModeInverted(newValue);

        return new Object[]{ screen().isTouchModeInverted() };
    }


}