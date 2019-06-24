package ben_mkiv.ocdevices.common.bridge;

import ben_mkiv.ocdevices.common.tileentity.TileEntityBridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Bridge {
    private static HashMap<String, Bridge> bridges = new HashMap<>();

    private String linkId;
    public HashSet<BridgeLocation> locations = new HashSet<>();

    private Bridge(TileEntityBridge tile){
        linkId = tile.getCard().tunnel();
        addHost(tile);
    }

    public BridgeLocation get(BridgeLocation locNew){
        for(BridgeLocation loc : locations)
            if(loc.equals(locNew))
                return loc;

        return null;
    }

    public static Bridge get(String linkId){
        return bridges.get(linkId);
    }

    public static String add(TileEntityBridge tile){
        Bridge br = new Bridge(tile);
        if(!bridges.containsKey(br.linkId)){
            bridges.put(br.linkId, br);
        }
        else {
            bridges.get(br.linkId).addHost(tile);
        }

        return br.linkId;
    }

    public static void remove(TileEntityBridge tile){
        BridgeLocation locTile = new BridgeLocation(tile);

        for(Bridge br : bridges.values()){
            br.removeLocation(locTile);

            if(br.locations.size() == 0) {
                bridges.remove(tile.linkId);
                return;
            }
        }
    }

    private void removeLocation(BridgeLocation locRemove){
        for(BridgeLocation loc : locations) {
            if (loc.equals(locRemove)) {
                locations.remove(locRemove);
                return;
            }
        }
    }

    private void addHost(TileEntityBridge tile){
        BridgeLocation locNew = new BridgeLocation(tile);

        if(get(locNew) == null)
            locations.add(locNew);
    }

    public ArrayList<TileEntityBridge> getTiles(){
        ArrayList<TileEntityBridge> tiles = new ArrayList<>();
        for(BridgeLocation location : locations){
            TileEntityBridge tile = location.getLinkedBlock();
            if(tile != null)
                tiles.add(tile);
        }

        return tiles;
    }
}