package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.tileentity.ColoredTile;
import ben_mkiv.ocdevices.common.tileentity.IUpgradeBlock;
import ben_mkiv.ocdevices.common.tileentity.TileEntityRack;
import li.cil.oc.common.block.Rack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

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

        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public li.cil.oc.common.tileentity.Rack createNewTileEntity(World world, int metadata) {
        return new TileEntityRack(); // we need our own tile for the TESR
    }

}
