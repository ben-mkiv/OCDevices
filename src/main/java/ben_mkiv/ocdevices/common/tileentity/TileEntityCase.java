package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import ben_mkiv.ocdevices.common.items.UpgradeBlastResistance;
import ben_mkiv.ocdevices.common.items.UpgradeTier2;
import ben_mkiv.ocdevices.common.items.UpgradeTier3;
import ben_mkiv.ocdevices.common.items.UpgradeTier4;
import ben_mkiv.ocdevices.utils.ItemUtils;
import li.cil.oc.common.Tier;
import li.cil.oc.common.tileentity.Case;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;
import java.util.HashSet;

import static ben_mkiv.ocdevices.common.blocks.BlockCase.caseTier;

public class TileEntityCase extends Case implements ColoredTile, IUpgradeBlock {
    final HashSet<EnumFacing> connectToSides = new HashSet<>();

    private float hardness = 0.5f;
    private float explosionResistance = 1f;

    public TileEntityCase(int tier){
        super(tier);
        setColor(0);
        connectToSides.add(EnumFacing.UP);
        connectToSides.add(EnumFacing.DOWN);
        connectToSides.add(EnumFacing.NORTH);
    }

    public TileEntityCase(){
        this(Tier.One());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        if(MultiPartHelper.isMultipartCapability(capability))
            return true;

        if(!connectToSides.contains(toLocal(facing)))
            return false;

        return super.hasCapability(capability, facing);
    }

    public float getHardness(){
        return hardness;
    }

    public float getExplosionResistance(){
        return explosionResistance;
    }

    public void setTier(int newTier){
        machine().stop();

        saveComponents();
        ArrayList<ItemStack> oldInventory = new ArrayList<>();
        for(int i=0; i < getSizeInventory(); i++)
            if(!getStackInSlot(i).isEmpty()) {
                oldInventory.add(getStackInSlot(i));
                setInventorySlotContents(i, ItemStack.EMPTY);
            }
        disconnectComponents();
        dropAllSlots();

        int caseColor = getColor();
        boolean wasBlastResistant = isBlastResistant();

        try {
            IBlockState oldState = getWorld().getBlockState(getPos());
            IBlockState newState = oldState.withProperty(caseTier, newTier);
            getWorld().setBlockState(getPos(), newState);

            TileEntity tile = getWorld().getTileEntity(getPos());
            if(tile instanceof TileEntityCase){
                for(ItemStack leftOverItem :  ((TileEntityCase) tile).insertItems(oldInventory)){
                    ItemUtils.dropItem(leftOverItem, getWorld(), getPos(), true, 0);
                }

                ((TileEntityCase) tile).setColor(caseColor);
                if(wasBlastResistant)
                    ((TileEntityCase) tile).makeBlastResistant();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<ItemStack> insertItems(ArrayList<ItemStack> items){
        ArrayList<ItemStack> leftOvers = new ArrayList<>();
        for(ItemStack item : items)
            if(item.getCount() > 1 || !insertItem(item))
                leftOvers.add(item);

        return leftOvers;
    }

    private boolean insertItem(ItemStack item){
        for(int i=0; i < getSizeInventory(); i++){
            if(getStackInSlot(i).isEmpty() && isItemValidForSlot(i, item)) {
                setInventorySlotContents(i, item);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate){
        if(!oldState.getBlock().equals(Blocks.AIR) && !newSate.getBlock().equals(Blocks.AIR))
            return !oldState.getValue(caseTier).equals(newSate.getValue(caseTier)) || super.shouldRefresh(world, pos, oldState, newSate);

        return super.shouldRefresh(world, pos, oldState, newSate);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        explosionResistance = tag.getFloat("ocd:blastResistant");
        hardness = tag.getFloat("ocd:hardness");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        tag.setFloat("ocd:blastResistant", getExplosionResistance());
        tag.setFloat("ocd:hardness", getHardness());
        return super.writeToNBT(tag);
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
    public boolean applyUpgrade(ItemStack stack){
        if(ItemStack.areItemsEqual(UpgradeBlastResistance.DEFAULT_STACK, stack) && !isBlastResistant()) {
            makeBlastResistant();
            return true;
        }

        if(ItemStack.areItemsEqual(UpgradeTier2.DEFAULT_STACK, stack) && tier() < Tier.Two()) {
            setTier(Tier.Two());
            return true;
        }

        if(ItemStack.areItemsEqual(UpgradeTier3.DEFAULT_STACK, stack) && tier() < Tier.Three()) {
            setTier(Tier.Three());
            return true;
        }

        if(ItemStack.areItemsEqual(UpgradeTier4.DEFAULT_STACK, stack) && tier() < Tier.Four()) {
            setTier(Tier.Four());
            return true;
        }

        return false;
    }



}

