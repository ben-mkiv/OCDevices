package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import ben_mkiv.ocdevices.common.items.UpgradeBlastResistance;
import ben_mkiv.ocdevices.config.Config;
import li.cil.oc.common.Tier;
import li.cil.oc.common.tileentity.Case;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;
import java.util.HashSet;

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
        this(Tier.Three());
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
        saveComponents();
        ArrayList<ItemStack> oldInventory = new ArrayList<>();
        for(int i=0; i < getSizeInventory(); i++)
            if(!getStackInSlot(i).isEmpty()) {
                oldInventory.add(getStackInSlot(i));
                setInventorySlotContents(i, ItemStack.EMPTY);
            }
        disconnectComponents();
        dropAllSlots();

        tier_$eq(newTier);
    }

    static int getTierFromConfig(String caseName){
        return Config.getConfig().getCategory("cases").get(caseName).getInt() - 1;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        explosionResistance = tag.getFloat("ocd:blastResistant");
        hardness = tag.getFloat("ocd:hardness");
        //tier_$eq(tag.getInteger("ocd:tier"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        //tag.setInteger("ocd:tier", tier());
        tag.setFloat("ocd:blastResistant", getExplosionResistance());
        tag.setFloat("ocd:hardness", getHardness());
        return super.writeToNBT(tag);
    }

    @Override
    public boolean applyUpgrade(ItemStack stack){
        if(ItemStack.areItemsEqual(UpgradeBlastResistance.DEFAULT_STACK, stack) && explosionResistance != 100000f) {
            explosionResistance = 100000f;
            hardness = 2f;
            return true;
        }

        /*
        if(ItemStack.areItemsEqual(UpgradeTier2.DEFAULT_STACK, stack) && tier() == Tier.One()) {
            setTier(Tier.Two());
            return true;
        }

        if(ItemStack.areItemsEqual(UpgradeTier3.DEFAULT_STACK, stack) && tier() == Tier.Two()) {
            setTier(Tier.Three());
            return true;
        }*/

        return false;
    }



}

