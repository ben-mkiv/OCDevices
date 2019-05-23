package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.matrix.MatrixWidget;
import ben_mkiv.ocdevices.common.matrix.widgetLuaObject;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.common.block.property.PropertyRotatable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TileEntityMatrix extends ocComponentTE implements IMultiblockScreen {

    public HashMap<Integer, MatrixWidget> widgets = new HashMap<>();

    private double depth = 0;

    private EnumFacing yaw, pitch;

    public TileEntityMatrix(){
        super("matrix", Visibility.Network);
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

    public IMultiblockScreen origin(){
        return this;
    }

    public void setPitch(EnumFacing pitch){
        this.pitch = pitch;
    }

    public void setYaw(EnumFacing yaw){
        this.yaw = yaw;
    }

    @Override
    public void onLoad(){
        super.onLoad();
        IBlockState state = getWorld().getBlockState(getPos());
        pitch = state.getValue(PropertyRotatable.Pitch());
        yaw = state.getValue(PropertyRotatable.Yaw());
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
        readFacingFromNBT(tag);
        for(int i=0; tag.hasKey("widget"+i); i++){
            NBTTagCompound nbt = tag.getCompoundTag("widget"+i);
            widgets.put(nbt.getInteger("index"), new MatrixWidget(nbt));

            depth = Math.max(depth, nbt.getDouble("depth"));
        }
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        tag = writeFacingToNBT(tag);

        int i=0;
        for(Map.Entry<Integer, MatrixWidget> widget : widgets.entrySet()) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("index", widget.getKey());
            tag.setTag("widget"+i, widget.getValue().writeToNBT(nbt));
            i++;
        }

        return super.writeToNBT(tag);
    }

    public EnumFacing yaw(){
        return yaw != null ? yaw : EnumFacing.NORTH;
    }

    public EnumFacing pitch(){
        return pitch != null ? pitch : EnumFacing.NORTH;
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

    @Override
    public void markDirty(){
        IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        super.markDirty();
    }


}

