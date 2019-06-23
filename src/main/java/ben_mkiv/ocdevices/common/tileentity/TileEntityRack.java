package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.items.UpgradeBlastResistance;
import li.cil.oc.common.tileentity.Rack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityRack extends Rack implements ColoredTile, IUpgradeBlock {
    private int color = 0;

    private float hardness = 0.5f;
    private float explosionResistance = 1f;
    private boolean doorOpened = false;
    public long doorAnimationProgress = 0;

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


    public boolean isBlastResistant(){
        return getExplosionResistance() == 100000f;
    }

    public void makeBlastResistant(){
        explosionResistance = 100000f;
        hardness = 2f;
        markDirty();
    }


    @Override
    public boolean applyUpgrade(ItemStack stack) {
        if (ItemStack.areItemsEqual(UpgradeBlastResistance.DEFAULT_STACK, stack) && !isBlastResistant()) {
            makeBlastResistant();
            return true;
        }

        return false;
    }

    public float getHardness(){
        return hardness;
    }

    public float getExplosionResistance(){
        return explosionResistance;
    }

    public boolean isDoorOpened(){
        return doorOpened;
    }

    public void toggleDoor(){
        doorOpened = !doorOpened;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        this.color = nbt.getInteger("color");
        explosionResistance = nbt.getFloat("ocd:blastResistant");
        hardness = nbt.getFloat("ocd:hardness");
        doorOpened = nbt.getBoolean("ocd:doorOpened");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setFloat("ocd:blastResistant", getExplosionResistance());
        nbt.setFloat("ocd:hardness", getHardness());
        nbt.setBoolean("ocd:doorOpened", doorOpened);
        nbt.setInteger("color", this.color);
        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBTForClient(NBTTagCompound nbt){
        super.readFromNBTForClient(nbt);
        this.color = nbt.getInteger("color");
        boolean newDoorState = nbt.getBoolean("ocd:doorOpened");
        if(newDoorState != isDoorOpened())
            doorAnimationProgress = System.currentTimeMillis();

        doorOpened = newDoorState;
    }

    @Override
    public void writeToNBTForClient(NBTTagCompound nbt){
        super.writeToNBTForClient(nbt);
        nbt.setInteger("color", this.color);
        nbt.setBoolean("ocd:doorOpened", isDoorOpened());
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
