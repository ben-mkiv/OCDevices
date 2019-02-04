package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.integration.MCMultiPart.MCMultiPart;
import li.cil.oc.api.network.Node;
import li.cil.oc.common.component.Screen;
import li.cil.oc.common.tileentity.Keyboard;
import li.cil.oc.common.tileentity.traits.Colored;
import li.cil.oc.server.PacketSender;
import mcmultipart.api.slot.IPartSlot;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.runtime.TraitSetter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TileEntityKeyboard extends Keyboard implements Colored, ColoredTile {
    String colorTag = "", colorTagCompat = "";
    int color = 0;

    @Override
    public void li$cil$oc$common$tileentity$traits$Colored$_setter_$li$cil$oc$common$tileentity$traits$Colored$$RenderColorTag_$eq(String var1){
        colorTag = var1;
    }

    @Override
    public void li$cil$oc$common$tileentity$traits$Colored$_setter_$li$cil$oc$common$tileentity$traits$Colored$$RenderColorTagCompat_$eq(String var1){
        colorTagCompat = var1;
    }

    @Override
    public int li$cil$oc$common$tileentity$traits$Colored$$_color(){
        return getColor();
    }

    @TraitSetter
    @Override
    public void li$cil$oc$common$tileentity$traits$Colored$$_color_$eq(int var1){
        setColor(var1);
    }

    @Override
    public boolean consumesDye(){ return false; }

    @Override
    public boolean controlsConnectivity(){
        return false;
    }

    @Override
    public int getColor(){ return color; }

    @Override
    public void setColor(int var1){
        boolean updateTile = color != var1;

        color = var1;

        if(updateTile) onColorChanged();
    }

    @Override
    public void onColorChanged(){
        if(!getWorld().isRemote) {
            markDirty();
            PacketSender.sendColorChange(this);
        }
    }


    @Override
    public void onConnect(Node node){
        if(node.host() instanceof Screen){
            for(TileEntity tile : MCMultiPart.getMCMPTiles(this).values()){
                if(tile instanceof li.cil.oc.common.tileentity.Screen && ((li.cil.oc.common.tileentity.Screen) tile).node().equals(node)){
                    node.connect(node());
                }
            }
        }

        super.onConnect(node);
    }


    @SideOnly(Side.CLIENT)
    @Override
    public boolean canConnect(EnumFacing side) {
        return true;
        //return super.canConnect(side);
    }

    @Override
    public String li$cil$oc$common$tileentity$traits$Colored$$RenderColorTag(){
        return colorTag;
    }

    @Override
    public String li$cil$oc$common$tileentity$traits$Colored$$RenderColorTagCompat(){
        return colorTagCompat;
    }

    @Override
    public void readFromNBTForServer(NBTTagCompound var1){
        super.readFromNBTForServer(var1);
        readColorFromNBT(var1);
    }

    @Override
    public void writeToNBTForServer(NBTTagCompound var1){
        super.writeToNBTForServer(writeColorToNBT(var1));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void readFromNBTForClient(NBTTagCompound var1){
        super.readFromNBTForClient(var1);
        readColorFromNBT(var1);
    }

    @Override
    public void writeToNBTForClient(NBTTagCompound var1){
        super.writeToNBTForClient(writeColorToNBT(var1));
    }

    private NBTTagCompound writeColorToNBT(NBTTagCompound nbt){
        nbt.setInteger("color", color);
        nbt.setString("colorTag", colorTag);
        nbt.setString("colorTagCompat", colorTagCompat);
        return nbt;
    }

    private void readColorFromNBT(NBTTagCompound nbt){
        color = nbt.getInteger("color");
        colorTag = nbt.getString("colorTag");
        colorTagCompat = nbt.getString("colorTagCompat");
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        return super.writeToNBT(writeColorToNBT(nbt));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        readColorFromNBT(nbt);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
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
