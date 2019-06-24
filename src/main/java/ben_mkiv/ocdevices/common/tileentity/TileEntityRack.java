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
    private int[] serverColors = new int[]{ 0, 0, 0, 0 };

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

    public int getServerColor(int slot){
        return serverColors[slot];
    }

    @Override
    public void setColor(int color){
        if(this.color != color) {
            this.color = color;
            onColorChanged();
        }
    }

    public void setColorServer(int slot, int color){
        if(this.serverColors[slot] != color) {
            this.serverColors[slot] = color;
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

    public void setDoor(boolean state){
        if(state == isDoorOpened())
            return;

        doorAnimationProgress = System.currentTimeMillis();
        doorOpened = state;
    }

    private void readNBT(NBTTagCompound nbt){
        this.color = nbt.getInteger("color");

        for(int slot=0; slot < getSizeInventory(); slot++)
            serverColors[slot] = nbt.getInteger("serverColor"+slot);

        explosionResistance = nbt.getFloat("ocd:blastResistant");
        hardness = nbt.getFloat("ocd:hardness");
        setDoor(nbt.getBoolean("ocd:doorOpened"));
    }

    private NBTTagCompound writeNBT(NBTTagCompound nbt){
        nbt.setFloat("ocd:blastResistant", getExplosionResistance());
        nbt.setFloat("ocd:hardness", getHardness());
        nbt.setBoolean("ocd:doorOpened", doorOpened);
        nbt.setInteger("color", this.color);

        for(int slot=0; slot < getSizeInventory(); slot++)
            nbt.setInteger("serverColor"+slot, serverColors[slot]);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        this.readNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        return super.writeToNBT(this.writeNBT(nbt));
    }

    @Override
    public void readFromNBTForClient(NBTTagCompound nbt){
        super.readFromNBTForClient(nbt);
        this.readNBT(nbt);
    }

    @Override
    public void writeToNBTForClient(NBTTagCompound nbt){
        super.writeToNBTForClient(this.writeNBT(nbt));
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

    // no idea why this gets an invalid index sometimes, so we catch it here for now
    @Override
    public NBTTagCompound getMountableData(int var1){
        if(var1 >= 0 && var1 <= getSizeInventory())
            return super.getMountableData(var1);
        else
            return new NBTTagCompound();
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
