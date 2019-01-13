package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenHelper;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.utils.AABBHelper;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Screen;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

public class BlockFlatScreen extends Screen {
    private final static int tier = Tier.Four();
    public final static String NAME = "flat_screen";
    public static Block DEFAULTITEM;

    static final AxisAlignedBB minimalBB = new AxisAlignedBB(0, 0, 0.999, 1, 1, 1);
    static final AxisAlignedBB emptyBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public BlockFlatScreen() {
        super(tier);
        setRegistryName(OCDevices.MOD_ID, NAME);
        setUnlocalizedName(NAME);
        setCreativeTab(OCDevices.creativeTab);
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos){
        if(Minecraft.getMinecraft().player.isSneaking())
            return FULL_BLOCK_AABB.offset(pos);

        return emptyBB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }


    @Override
    @Deprecated
    public void addCollisionBoxToList(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, AxisAlignedBB entityBox,
                                      @Nonnull List<AxisAlignedBB> collidingBoxes, Entity entity, boolean advanced) {

        TileEntity te = world.getTileEntity(pos);

        if(!(te instanceof TileEntityFlatScreen)) {
            collidingBoxes.add(getBoundingBox(state, world, pos));
            return;
        }

        for(AxisAlignedBB bb : ((TileEntityFlatScreen) te).boundingBoxes)
            addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)    {
        TileEntity te = source.getTileEntity(pos);

        if(!(te instanceof TileEntityFlatScreen))
            return FULL_BLOCK_AABB;

        FlatScreenHelper fsh = new FlatScreenHelper((TileEntityFlatScreen) te);
        float minDepth = 1;
        for(float f : fsh.getDepthForBlock((TileEntityFlatScreen) te))
            if(f < minDepth) minDepth = f;

        AxisAlignedBB bb = minDepth > 0 ? new AxisAlignedBB(0, 0, 1d-minDepth, 1, 1, 1) : minimalBB;

        bb = AABBHelper.rotateVertical(bb, ((TileEntityFlatScreen) te).pitch());
        bb = AABBHelper.rotateHorizontal(bb, ((TileEntityFlatScreen) te).yaw());

        return bb;
    }

    @Override
    public TileEntityFlatScreen createNewTileEntity(World worldIn, int meta) {
        return new TileEntityFlatScreen(tier);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if(world.isRemote)
            return;

        // as for some reason the facing isnt set correct, we have to fix it here!?
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityFlatScreen) {
            TileEntityFlatScreen screen = (TileEntityFlatScreen) te;
            // set the pitch/yaw first, which will be inverted
            screen.setFromEntityPitchAndYaw(placer);


            EnumFacing pitch = screen.pitch();
            if(pitch.equals(UP) || pitch.equals(DOWN))
                pitch = pitch.getOpposite();

            // set the new values
            screen.trySetPitchYaw(pitch, placer.getHorizontalFacing().getOpposite());
        }
    }



}