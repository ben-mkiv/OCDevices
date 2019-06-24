package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.bridge.Bridge;
import ben_mkiv.ocdevices.common.bridge.BridgeLocation;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.LinkedCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TileEntityBridge extends ocComponentHostTE {

    public String linkId = "";

    public TileEntityBridge(){
        super("bridge", 1, true, true, true, Visibility.Network);
    }

    public void removed(){
        Bridge.remove(this);
    }

    @Override
    public void onChunkUnload(){
        unlink();
        super.onChunkUnload();
    }

    @Callback(doc = "function():", direct = true)
    public Object[] status(Context context, Arguments args){
        HashMap<String, Object> val = new HashMap<>();

        if(linkId.length() > 0)
            val.put("tunnel", linkId);
        else
            val.put("tunnel", "no tunnel");

        Bridge bridge = Bridge.get(linkId);

        if(bridge == null)
            val.put("bridge", "not loaded");
        else {
            HashSet<HashMap> locations = new HashSet<>();
            for(BridgeLocation loc : bridge.locations){
                HashMap<String, Object> hostVals = new HashMap<>();
                hostVals.put("dimension", loc.dimension);
                hostVals.put("x", loc.position.getX());
                hostVals.put("y", loc.position.getY());
                hostVals.put("z", loc.position.getZ());
                TileEntityBridge tile = loc.getLinkedBlock();
                hostVals.put("loaded", tile != null);
                locations.add(hostVals);
            }
            val.put("locations", locations.toArray());
        }

        return new Object[]{ val };
    }

    @Override
    public void markDirty(){
        super.markDirty();

        if(getCard() != null) {
            linkId = Bridge.add(this);
            link();
        }
        else {
            unlink();
            Bridge.remove(this);
            linkId = "";
        }
    }

    private void link(){
        Bridge bridge = Bridge.get(linkId);

        if(bridge == null)
            return;

        ArrayList<TileEntityBridge> tiles = bridge.getTiles();

        if(tiles.size() < 2)
            return;

        tiles.get(0).node().connect(tiles.get(1).node());
    }

    private void unlink(){
        Bridge bridge = Bridge.get(linkId);

        if(bridge == null)
            return;

        ArrayList<TileEntityBridge> tiles = bridge.getTiles();

        if(tiles.size() < 2)
            return;

        tiles.get(0).node().disconnect(tiles.get(1).node());
    }

    public LinkedCard getCard(){
        if(components.get(0) == null || components.get(0).node() == null)
            return null;

        if(!(components.get(0).node().host() instanceof LinkedCard))
            return null;

        return (LinkedCard) components.get(0).node().host();
    }

}
