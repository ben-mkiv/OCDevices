package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.flatscreen.FlatScreenAABB;
import li.cil.oc.common.tileentity.Rack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityRack extends Rack implements ColoredTile {
    int color = 0;

    public TileEntityRack(){
        super();
    }

    @Override
    public int getColor(){
        return this.color;
    }

    @Override
    public void setColor(int color){
        if(this.color != color) {
            this.color = color;
            onColorChanged();
        }
    }

    @Override
    public void onColorChanged(){
        markDirty();
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        this.color = nbt.getInteger("color");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setInteger("color", this.color);
        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBTForClient(NBTTagCompound nbt){
        super.readFromNBTForClient(nbt);
        this.color = nbt.getInteger("color");
    }

    @Override
    public void writeToNBTForClient(NBTTagCompound nbt){
        super.writeToNBTForClient(nbt);
        nbt.setInteger("color", this.color);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        handleUpdateTag(packet.getNbtCompound());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleUpdateTag(@Nonnull NBTTagCompound nbt){
        readFromNBTForClient(nbt);
    }


    @Override
    public void markDirty(){
        if(getWorld() == null) return;
        IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        super.markDirty();
    }

    @Override
    public @Nonnull NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        writeToNBTForClient(nbt);
        return nbt;
    }

}
