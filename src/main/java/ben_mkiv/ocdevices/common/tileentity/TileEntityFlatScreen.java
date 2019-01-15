package ben_mkiv.ocdevices.common.tileentity;


import ben_mkiv.ocdevices.common.flatscreen.FlatScreen;
import ben_mkiv.ocdevices.common.blocks.BlockFlatScreen;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenHelper;
import ben_mkiv.ocdevices.utils.AABBHelper;
import li.cil.oc.api.Driver;
import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.common.Tier;
import li.cil.oc.common.tileentity.Screen;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static ben_mkiv.ocdevices.common.flatscreen.FlatScreen.precision;
import static net.minecraft.block.Block.FULL_BLOCK_AABB;

public class TileEntityFlatScreen extends Screen {
    private final li.cil.oc.api.internal.TextBuffer buffer;

    public ArrayList<AxisAlignedBB> boundingBoxes = new ArrayList<>();

    public FlatScreen data = new FlatScreen();

    public TileEntityFlatScreen() {
        super(BlockFlatScreen.tier);

        // OC reads resolution from the settings to initialize the textbuffer, so we got to set it up on our own to make a T4 screen
        //ItemStack screenItem = Items.get("screen1").createItemStack(1);
        ItemStack screenItem = new ItemStack(BlockFlatScreen.DEFAULTITEM, 1);
        buffer = (TextBuffer) Driver.driverFor(screenItem, getClass()).createEnvironment(screenItem, this);
        buffer.setMaximumResolution(160, 50);
        buffer.setMaximumColorDepth(li.cil.oc.api.internal.TextBuffer.ColorDepth.EightBit);

        // add one default bounding box
        if(boundingBoxes.size() == 0)
            boundingBoxes.add(FULL_BLOCK_AABB);
    }

    public FlatScreen getData(){
        return data;
    }

    @Override
    public li.cil.oc.api.internal.TextBuffer buffer(){
        return this.buffer;
    } //keep THIS

    public void updateScreenBB(){
        boundingBoxes.clear();

        FlatScreenHelper helper = new FlatScreenHelper(this);

        float[] l = helper.getDepthForBlock(this); // 0 = sTopLeft, 1 = sTopRight, 2 = sBottomLeft, 3 = sBottomRight
        float sliceDepth;
        final int sliceCount = 8;
        final float sliceResolution = 1f/sliceCount;

        int start = 0;
        if(helper.topRight > helper.topLeft || helper.topLeft < helper.bottomLeft)
            start = sliceCount-1;

        switch(getData().tiltAxis){
            case X:
                for(int c = 0, slice = start; c < sliceCount; c++, slice = Math.abs(start-c)) {
                    sliceDepth = Math.min(l[0], l[2]) + c * sliceResolution * (Math.max(l[0], l[2]) - Math.min(l[0], l[2]));

                    AxisAlignedBB bb;

                    bb = new AxisAlignedBB(0d, slice * sliceResolution, 1d-sliceDepth, 1d, (slice+1) * sliceResolution, 1d);

                    bb = AABBHelper.rotateVertical(bb, pitch());
                    bb = AABBHelper.rotateHorizontal(bb, yaw());

                    boundingBoxes.add(bb);
                }
                break;

            case Y:
                for(int c = 0, slice = start; c < sliceCount; c++, slice = Math.abs(c - start)) {
                    sliceDepth = Math.min(l[0], l[1]) + c * sliceResolution * (Math.max(l[0], l[1]) - Math.min(l[0], l[1]));

                    AxisAlignedBB bb;

                    bb = new AxisAlignedBB(slice * sliceResolution, 0d, 1d-sliceDepth, (slice+1) * sliceResolution, 1d, 1d);
                    bb = AABBHelper.rotateVertical(bb, pitch());
                    bb = AABBHelper.rotateHorizontal(bb, yaw());

                    boundingBoxes.add(bb);
                }
                break;

            default:
            case NONE:
                AxisAlignedBB bb = new AxisAlignedBB(0, 0, 1d-precision * getData().screenDepthTop, 1, 1, 1d);
                bb = AABBHelper.rotateVertical(bb, pitch());
                bb = AABBHelper.rotateHorizontal(bb, yaw());
                boundingBoxes.add(bb);
        }
    }

    public void updateNeighbours(){
        for(TileEntityFlatScreen screen : FlatScreenHelper.getScreens(this)){
            screen.data = getData();
            screen.updateScreenBB();
            screen.markDirty();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        tag.setTag("screenData", getData().writeToNBT(new NBTTagCompound()));

        return super.writeToNBT(tag);
    }

    boolean updateBB = false;

    @Override
    public void update(){
        super.update();

        if(updateBB){
            updateBB = false;
            updateScreenBB();
        }
    }


    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);

        if(tag.hasKey("screenData"))
            data.readFromNBT(tag.getCompoundTag("screenData"));

        // update bb in update() handler, otherwise it wont work
        updateBB = true;
    }


    @Override
    public void markDirty(){
        IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        super.markDirty();
    }


    //override the tier tag as its limited to t1-t3 in Screen.class
    @Override
    public void readFromNBTForServer(NBTTagCompound tag){
        super.readFromNBTForServer(tag);
        tier_$eq(Tier.Four());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readFromNBTForClient(NBTTagCompound tag){
        super.readFromNBTForClient(tag);
        tier_$eq(Tier.Four());
    }


    @Override
    public NBTTagCompound getUpdateTag() {
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
