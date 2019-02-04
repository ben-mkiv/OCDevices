package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.EnvironmentProvider;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.common.Tier;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ocComponentBlock extends Block implements DriverItem, HostAware, EnvironmentProvider {
    private Class componentClass;
    private int tier = Tier.One();

    public static ocComponentBlock blockDefault;
    public static Item itemDefault;

    public ocComponentBlock(String name, Class clazz) {
        super(Material.IRON);
        setCreativeTab(OCDevices.creativeTab);
        setRegistryName(name);
        setHardness(3.0F);
        setLightLevel(0.3F);
        setUnlocalizedName(name);
        this.componentClass = clazz;
    }

    public String getDocumentationName(World world, BlockPos pos){
        return itemDefault.getRegistryName().toString();
    }

    public String getDocumentationName(ItemStack stack){
        return itemDefault.getRegistryName().toString();
    }

    @Override
    public boolean worksWith(ItemStack stack, Class<? extends EnvironmentHost> host) {
        return this.worksWith(stack);
    }

    @Override
    public String slot(ItemStack var1){ return Slot.Upgrade; }

    @Override
    public NBTTagCompound dataTag(ItemStack var1){
        return new NBTTagCompound();
    }

    @Override
    public int tier(ItemStack var1){ return this.tier; }

    @Override
    public Class<?> getEnvironment(@Nonnull ItemStack stack) {
        if(worksWith(stack))
            return componentClass;
        return null;
    }

    @Override
    public boolean worksWith(@Nonnull ItemStack stack) {
        if(stack.isEmpty())
            return false;

        return false;
    }

    public static void register(){
        li.cil.oc.api.Driver.add((li.cil.oc.api.driver.DriverItem) blockDefault);
    }

    @Override
    public ManagedEnvironment createEnvironment(@Nonnull ItemStack stack, EnvironmentHost host) {
        return null;
    }

}
