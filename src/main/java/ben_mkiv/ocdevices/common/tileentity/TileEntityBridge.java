package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.bridge.Bridge;
import ben_mkiv.ocdevices.common.bridge.BridgeLocation;
import ben_mkiv.ocdevices.common.component.ManagedComponent;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.LinkedCard;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TileEntityBridge extends ocComponentHostTE {

    public String linkId = "";
    public boolean linkActive = false;
    public boolean interDimensional = false;
    public int distance = 0;
    public static boolean exposeCoords = true;

    public TileEntityBridge(){
        super("bridge", 1, true, true, true, Visibility.Network);
        if(node() != null)
            ((Connector) node()).setLocalBufferSize(500);
    }

    public void removed(){
        Bridge.remove(this);
    }

    @Override
    public void update(){

        if(linkActive && !getWorld().isRemote){
            if(interDimensional){
                if(!((Connector) node()).tryChangeBuffer(-250))
                    unlink();
            }
            else {
                if(!((Connector) node()).tryChangeBuffer(-Math.max(10, Math.min(100, Math.ceil(distance * 0.1)))))
                    unlink();
            }
        }

        super.update();
    }

    @Override
    public void onLoad(){
        exposeCoords = !FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getGameRules().getBoolean("reducedDebugInfo");
        updateLink();
        super.onLoad();
    }

    @Override
    public void onInventoryUpdate(int slot, ItemStack newStack){
        super.onInventoryUpdate(slot, newStack);
        updateLink();
    }

    private void updateLink(){
        if(getCard() != null) {
            linkId = Bridge.add(this);
            linkActive = link();
        }
        else {
            unlink();
            Bridge.remove(this);
            linkId = "";
            linkActive = false;
        }
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
                if(exposeCoords) {
                    hostVals.put("dimension", loc.dimension);
                    hostVals.put("x", loc.position.getX());
                    hostVals.put("y", loc.position.getY());
                    hostVals.put("z", loc.position.getZ());
                }
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
        if(getWorld().isRemote) {
            super.markDirty();
            return;
        }

        IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);

        super.markDirty();
    }

    private boolean link(){
        Bridge bridge = Bridge.get(linkId);

        if(bridge == null)
            return false;

        ArrayList<TileEntityBridge> tiles = bridge.getTiles();

        if(tiles.size() < 2)
            return false;

        tiles.get(0).node().connect(tiles.get(1).node());

        interDimensional = tiles.get(0).getWorld().provider.getDimension() != tiles.get(1).getWorld().provider.getDimension();

        BlockPos otherPos = tiles.get(1).getPos();
        distance = interDimensional ? 0 : (int) Math.ceil(tiles.get(0).getPos().getDistance(otherPos.getX(), otherPos.getY(), otherPos.getZ()));

        markDirty();
        return true;
    }

    private void unlink(){
        Bridge bridge = Bridge.get(linkId);

        if(bridge == null)
            return;

        ArrayList<TileEntityBridge> tiles = bridge.getTiles();

        if(tiles.size() < 2)
            return;

        tiles.get(0).node().disconnect(tiles.get(1).node());
        markDirty();
    }

    public LinkedCard getCard(){
        if(components.get(0) == null || components.get(0).node() == null)
            return null;

        if(!(components.get(0).node().host() instanceof LinkedCard))
            return null;

        return (LinkedCard) components.get(0).node().host();
    }

    @Override
    public Node[] onAnalyze(EntityPlayer var1, EnumFacing var2, float var3, float var4, float var5){
        return new Node[]{ node() };
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        if(getWorld() != null && getWorld().isRemote) {
            // this data should only be parsed clientside for the GUI, and shouldnt mess with server logic
            linkActive = nbt.getBoolean("linkActive");
            linkId = nbt.getString("linkedTo");
            interDimensional = nbt.getBoolean("interDimensional");
            distance = nbt.getInteger("distance");
        }
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setString("linkedTo", linkId);
        nbt.setBoolean("linkActive", linkActive);
        nbt.setBoolean("interDimensional", interDimensional);
        nbt.setInteger("distance", distance);
        return super.writeToNBT(nbt);
    }

    @Override
    public @Nonnull
    NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }


}
