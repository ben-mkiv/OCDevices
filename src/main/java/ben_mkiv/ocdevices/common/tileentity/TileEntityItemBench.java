package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.nanoAnalyzer.NanoAnalyzer;
import ben_mkiv.ocdevices.utils.ItemUtils;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static ben_mkiv.ocdevices.common.nanoAnalyzer.capability.analyzeCapability.ANALYZE;

public class TileEntityItemBench extends ocComponentTE implements IButtonCallback {
    private static final String NAME = "ocd_itembench";

    private ItemBenchInventory inventory = new ItemBenchInventory();

    private NBTTagCompound stackTags = new NBTTagCompound();

    class ItemBenchInventory extends ItemStackHandler {
        public ItemBenchInventory(){
            super(2);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack){
            switch(slot){
                case 0: return NanoAnalyzer.isValid(stack);
                case 1: return stack.getItem().getRegistryName().equals(new ResourceLocation("opencomputers", "tool")) && stack.getMetadata() == 5;
                default: return false;
            }
        }

        @Override
        public void onContentsChanged(int slot){
            super.onContentsChanged(slot);
            updateStackTags(getStackInSlot(slot));
        }
    }

    @Override
    public void buttonCallback(NBTTagCompound nbt){
        if(nbt.hasKey("action")){
            switch(nbt.getString("action")){
                case "infuse":
                    if(!inventory.getStackInSlot(0).hasCapability(ANALYZE, null))
                        return;

                    if(inventory.getStackInSlot(1).isEmpty())
                        return;

                    inventory.getStackInSlot(1).shrink(1);
                    inventory.getStackInSlot(0).getCapability(ANALYZE, null).setAttached(true);

                    markDirty();

                    break;

            }

        }


    }

    public TileEntityItemBench(){
        super(NAME, Visibility.Network);
    }

    public void removed(){
        for(int slot = 0; slot < inventory.getSlots(); slot++)
            ItemUtils.dropItem(inventory.getStackInSlot(slot), getWorld(), getPos(), false, 10);
    }

    private void updateStackTags(ItemStack stack){
        if(stack.hasCapability(ANALYZE, null))
            stackTags = stack.getCapability(ANALYZE, null).writeToNBT(new NBTTagCompound());
        else
            stackTags = new NBTTagCompound();
    }

    public NBTTagCompound getStackTags() {
        return stackTags;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setTag("inventory", inventory.serializeNBT());

        if(!inventory.getStackInSlot(0).isEmpty()){
            nbt.setTag("stackTags", stackTags);
        }


        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"));

        stackTags = nbt.hasKey("stackTags") ? nbt.getCompoundTag("stackTags") : new NBTTagCompound();
    }


    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||  super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventory;
        }

        return super.getCapability(capability, facing);
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
