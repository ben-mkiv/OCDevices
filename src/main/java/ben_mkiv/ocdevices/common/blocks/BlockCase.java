package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import ben_mkiv.ocdevices.common.tileentity.ColoredTile;
import ben_mkiv.ocdevices.common.tileentity.IUpgradeBlock;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import ben_mkiv.ocdevices.utils.ItemUtils;
import com.google.common.base.Optional;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Case;
import li.cil.oc.common.block.property.PropertyRotatable;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class BlockCase extends Case {
    public static final int GUI_ID = 3;

    public static final caseTierProperty caseTier = new caseTierProperty();

    public BlockCase(String caseName){
        super(Tier.One());
        setRegistryName(OCDevices.MOD_ID, caseName);
        setTranslationKey(caseName);
        setCreativeTab(OCDevices.creativeTab);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion){
        TileEntityCase caseTile = getTileEntity(world, pos);
        return caseTile != null ? caseTile.getExplosionResistance() : super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    @Deprecated
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos){
        TileEntityCase caseTile = getTileEntity(worldIn, pos);
        return caseTile != null ? caseTile.getHardness() : super.getBlockHardness(blockState, worldIn, pos);
    }

    @Deprecated
    @Override
    public boolean isFullBlock(IBlockState state){
        return true;
    }

    @Deprecated
    @Override
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    @Deprecated
    public boolean isBlockNormalCube(IBlockState state) {
        return true;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

        if(ColoredTile.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ))
            return true;

        if(IUpgradeBlock.onBlockActivated(world, pos, player, hand))
            return true;

        TileEntityCase caseTile = MultiPartHelper.getCaseFromTile(world.getTileEntity(pos));
        if (caseTile != null) {
            player.openGui(OCDevices.INSTANCE, GUI_ID, MultiPartHelper.getRealWorld(caseTile), pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
        String tierTag = "Tier 1";
        if(stack.hasTagCompound()) {
            NBTTagCompound stackTag = stack.getTagCompound();
            if (stackTag.hasKey("tier"))
                tierTag = "Tier " + (stackTag.getInteger("tier") + 1);

            if(stackTag.hasKey("reinforced"))
                tooltip.add("blast protected");
        }

        tooltip.add(tierTag);
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    private static TileEntityCase getTileEntity(IBlockAccess world, BlockPos pos){
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityCase ? (TileEntityCase) te : null;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PropertyRotatable.Facing()).getHorizontalIndex() << 2 | state.getValue(caseTier);
    }

    @Deprecated
    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(PropertyRotatable.Facing(), EnumFacing.byHorizontalIndex(meta >> 2)).withProperty(caseTier, meta & 3);
    }


    @Override
    public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand){
        int tier = Tier.One();

        ItemStack placingStack = placer.getHeldItem(hand);

        if(placingStack.hasTagCompound() && placingStack.getTagCompound().hasKey("tier"))
            tier = placingStack.getTagCompound().getInteger("tier");

        EnumFacing yaw = EnumFacing.fromAngle(placer.rotationYaw).getOpposite();
        return getDefaultState().withProperty(PropertyRotatable.Facing(), yaw).withProperty(caseTier, tier);
    }

    @Override
    public BlockStateContainer createBlockState() {
        ArrayList<IProperty> properties = new ArrayList<IProperty>(super.createBlockState().getProperties());

        IProperty[] props = new IProperty[properties.size()+1];
        for(int i=0; i < properties.size(); i++)
            props[i] = properties.get(i);

        props[properties.size()] = caseTier;

        return new ExtendedBlockState(this, props, new IUnlistedProperty[]{});
    }

    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
        int tier = state.getValue(caseTier);

        ItemStack dropStack = new ItemStack(state.getBlock());

        NBTTagCompound nbt = new NBTTagCompound();

        if(tier > Tier.One()) {
            nbt.setInteger("tier", tier);
        }

        TileEntity tile = world.getTileEntity(pos);

        if(tile != null && ((TileEntityCase) tile).isBlastResistant())
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
                TileEntityCase tile = getTileEntity(worldIn, pos);
                tile.makeBlastResistant();
            }
        }
    }

    // avoid to connect to fences/glass panes
    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.CENTER;
    }

}
