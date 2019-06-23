package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.ColoredTile;
import ben_mkiv.ocdevices.common.tileentity.IUpgradeBlock;
import ben_mkiv.ocdevices.common.tileentity.TileEntityRack;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Rack;
import li.cil.oc.common.block.property.PropertyRotatable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockRack extends Rack {
    public final static String NAME = "rack";
    public static Block DEFAULTITEM;

    public BlockRack(){
        this(NAME);
    }

    public BlockRack(String rackName){
        super();
        setRegistryName(OCDevices.MOD_ID, rackName);
        setUnlocalizedName(rackName);
        setCreativeTab(OCDevices.creativeTab);
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public @Nonnull AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos){
        return new AxisAlignedBB(0, 0, 0, 1, 1, 1);
    }

    public TileEntityRack getTileEntity(World world, BlockPos pos){
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityRack ? (TileEntityRack) tile : null;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            if(ColoredTile.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ))
                return true;

            if(IUpgradeBlock.onBlockActivated(world, pos, player, hand))
                return true;
        }

        TileEntityRack rack = getTileEntity(world, pos);

        if(side.equals(rack.facing())) {
            if (!rack.isDoorOpened() || player.isSneaking()) {
                if(!world.isRemote)
                    rack.toggleDoor();
                return true;
            }
        }

        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public li.cil.oc.common.tileentity.Rack createNewTileEntity(World world, int metadata) {
        return new TileEntityRack(); // we need our own tile for the TESR
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion){
        TileEntityRack rackTile = getTileEntity(world, pos);
        return rackTile != null ? rackTile.getExplosionResistance() : super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    @Deprecated
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos){
        TileEntityRack rackTile = getTileEntity(worldIn, pos);
        return rackTile != null ? rackTile.getHardness() : super.getBlockHardness(blockState, worldIn, pos);
    }

    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
        ItemStack dropStack = new ItemStack(state.getBlock());

        NBTTagCompound nbt = new NBTTagCompound();
        TileEntity tile = world.getTileEntity(pos);

        if(tile != null && ((TileEntityRack) tile).isBlastResistant())
            nbt.setBoolean("reinforced", true);

        if(!nbt.equals(new NBTTagCompound()))
            dropStack.setTagCompound(nbt);

        drops.add(dropStack);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)    {
        if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool)    {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if(worldIn.isRemote)
            return;

        if(stack.hasTagCompound()) {
            // set tile to be blast resistant if the stack item is
            if(stack.getTagCompound().hasKey("reinforced") && stack.getTagCompound().getBoolean("reinforced")) {
                TileEntityRack tile = getTileEntity(worldIn, pos);
                tile.makeBlastResistant();
            }
        }
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
        if(stack.hasTagCompound()) {
            NBTTagCompound stackTag = stack.getTagCompound();
            if(stackTag.hasKey("reinforced"))
                tooltip.add("blast protected");
        }
    }

    @Override
    public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand){
        EnumFacing yaw = EnumFacing.fromAngle(placer.rotationYaw).getOpposite();
        return getDefaultState().withProperty(PropertyRotatable.Facing(), yaw);
    }

}
