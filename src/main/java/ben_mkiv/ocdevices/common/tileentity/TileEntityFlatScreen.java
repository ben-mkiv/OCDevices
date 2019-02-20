package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.flatscreen.FlatScreen;
import ben_mkiv.ocdevices.common.blocks.BlockFlatScreen;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenAABB;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenHelper;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenMultiblock;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import li.cil.oc.api.Driver;
import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.api.network.Node;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.property.PropertyRotatable;
import li.cil.oc.common.tileentity.Screen;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;

public class TileEntityFlatScreen extends Screen {
    private final li.cil.oc.api.internal.TextBuffer buffer;

    public ArrayList<AxisAlignedBB> boundingBoxes = new ArrayList<>(Arrays.asList(FULL_BLOCK_AABB));

    private FlatScreen data = new FlatScreen();
    private EnumFacing yaw, pitch;
    private int color = 0;

    public FlatScreenMultiblock flatScreenMultiblock = new FlatScreenMultiblock(this);

    private boolean loaded = false, multiblockInvalid = true;

    private boolean isLoaded(){
        return loaded;
    }

    public TileEntityFlatScreen() {
        super(BlockFlatScreen.tier);

        // OC reads resolution from the settings to initialize the textbuffer, so we got to set it up on our own to make a T4 screen
        // ItemStack screenItem = Items.get("screen1").createItemStack(1);
        ItemStack screenItem = new ItemStack(BlockFlatScreen.DEFAULTITEM, 1);
        buffer = (TextBuffer) Driver.driverFor(screenItem, getClass()).createEnvironment(screenItem, this);
        buffer.setMaximumResolution(160, 50);
        buffer.setMaximumColorDepth(li.cil.oc.api.internal.TextBuffer.ColorDepth.EightBit);
    }


    public FlatScreen getData(){
        return isOrigin() || origin() == null || origin().isInvalid() || !origin().isLoaded() ? data : origin().getData();
    }

    @Override
    public li.cil.oc.api.internal.TextBuffer buffer(){
        return this.buffer;
    } //keep THIS

    public void updateNeighbours(){
        getHelper().refresh(this);

        for(TileEntityFlatScreen screen : getScreens()){
            screen.boundingBoxes = FlatScreenAABB.updateScreenBB(this);
            screen.markDirty();
        }
    }

    @Override
    public void update(){
        super.update();

        if(isLoaded() && multiblockInvalid) {
            getMultiblock().split();
            multiblockInvalid = false;
        }

        if(!getMultiblock().initialized())
            getMultiblock().initialize();
    }

    @Override
    public Node sidedNode(EnumFacing side) {
        return hasKeyboardInSameBlock() || hasKeyboard(side) ? node() : super.sidedNode(side);
    }

    @Override
    public boolean hasKeyboard(){
        for(TileEntityFlatScreen screen : getScreens()){
            if(screen.hasKeyboardInSameBlock())
                return true;

            for(EnumFacing side : EnumFacing.values())
                if(screen.hasKeyboard(side))
                    return true;
        }

        return false;
    }

    private boolean hasKeyboardInSameBlock(){
        return MultiPartHelper.getKeyboardFromTile(this) != null;
    }

    private boolean hasKeyboard(EnumFacing side){
        if(side == null)
            return hasKeyboardInSameBlock();

        TileEntity tile = getWorld().getTileEntity(this.getPos().offset(side));
        return MultiPartHelper.getKeyboardFromTile(tile) != null;
    }

    @Override
    public TileEntityFlatScreen origin() {
        if(getMultiblock() != null && getMultiblock().origin() != null)
            return getMultiblock().origin();
        else
            return MultiPartHelper.getScreenFromTile(this);
    }

    public HashSet<TileEntityFlatScreen> getScreens() {
        return getMultiblock().screens();
    }

    public FlatScreenHelper getHelper(){
        return getMultiblock().getHelper();
    }

    public FlatScreenMultiblock getMultiblock(){
        return flatScreenMultiblock;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(){
        return getMultiblock().getBoundingBox();
    }


    /* OC Screen overrides */

    @Override
    public boolean isOrigin(){
        return this.equals(origin());
    }

    @Override
    public int width(){
        return getMultiblock().width();
    }

    @Override
    public int height(){
        return getMultiblock().height();
    }

    @Override
    public EnumFacing yaw(){
        return yaw != null ? yaw : EnumFacing.NORTH;
    }

    @Override
    public EnumFacing pitch(){
        return pitch != null ? pitch : EnumFacing.NORTH;
    }

    @Override
    public void onLoad(){
        super.onLoad();
        updateRotation();
        multiblockInvalid = true;
        loaded = true;
    }

    private void updateRotation(){
        if(yaw != null && pitch != null)
            return;

        yaw = getWorld().getBlockState(getPos()).getValue(PropertyRotatable.Yaw());
        pitch = getWorld().getBlockState(getPos()).getValue(PropertyRotatable.Pitch());
    }

    @Override
    public void onColorChanged() {
        super.onColorChanged();
        // nbt is parsed in network thread so we have update() to do the actual work
        multiblockInvalid = true;
    }

    @Override
    public void onRotationChanged(){
        updateRotation();
        super.onRotationChanged();
        // nbt is parsed in network thread so we have update() to do the actual work
        multiblockInvalid = true;
    }

    @Override
    public void walk(Entity in){}


    /* overrides for MCMP compat */
    @Override
    public int[] getOutput(){ return new int[]{ 0, 0, 0, 0, 0, 0 }; }

    @Override
    public int getOutput(EnumFacing side) {
        return 0;
    }

    @Override
    public boolean shouldCheckForMultiBlock() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt = super.writeToNBT(nbt);
        writeToNBTForServer(nbt);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        readFromNBTForServer(nbt);
    }

    // yes we have to override them... -.-
    @Override
    public void writeToNBTForServer(NBTTagCompound nbt){
        super.writeToNBTForServer(nbt); //required to not break multipart support
        nbt.setBoolean("invertTouchMode", invertTouchMode());
        nbt.setInteger("color", getColor());
        nbt.setBoolean("hadRedstone", hadRedstoneInput());
        buffer().save(nbt);

        nbt.setTag("screenData", getData().writeToNBT(new NBTTagCompound()));

        nbt.setInteger("yaw", yaw.ordinal());
        nbt.setInteger("pitch", pitch.ordinal());

        //nbt.setInteger("tier", tier()); //listen here stupid, THIS IS TIER4, not 3, not int2, but Tier4(int3)
    }

    @Override
    public void readFromNBTForServer(NBTTagCompound nbt){
        setColor(nbt.getInteger("color"));
        invertTouchMode_$eq(nbt.getBoolean("invertTouchMode"));
        hadRedstoneInput_$eq(nbt.getBoolean("hadRedstone"));
        buffer().load(nbt);

        if(nbt.hasKey("screenData"))
            data.readFromNBT(nbt.getCompoundTag("screenData"));

        yaw = EnumFacing.values()[nbt.getInteger("yaw")];
        pitch = EnumFacing.values()[nbt.getInteger("pitch")];

        //tier_$eq(nbt.getInteger("tier"));
    }

    @Override
    public void writeToNBTForClient(NBTTagCompound nbt){
        super.writeToNBTForClient(nbt); //required to not break multipart support
        nbt.setBoolean("invertTouchMode", invertTouchMode());
        nbt.setInteger("color", getColor());
        buffer().save(nbt);

        nbt.setInteger("yaw", yaw.ordinal());
        nbt.setInteger("pitch", pitch.ordinal());

        //nbt.setInteger("tier", tier());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readFromNBTForClient(NBTTagCompound nbt){
        setColor(nbt.getInteger("color"));
        invertTouchMode_$eq(nbt.getBoolean("invertTouchMode"));
        buffer().load(nbt);

        yaw = EnumFacing.values()[nbt.getInteger("yaw")];
        pitch = EnumFacing.values()[nbt.getInteger("pitch")];

        //tier_$eq(nbt.getInteger("tier"));
    }

    @Override
    public void markDirty(){
        IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        super.markDirty();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        writeToNBTForClient(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBTForClient(packet.getNbtCompound());
    }

    @Override
    public int tier(){
        return Tier.Four();
    }

    @Override
    public int getColor(){
        return color;
    }

    @Override
    public void setColor(int newColor){
        super.setColor(newColor);
        color = newColor;
        onColorChanged();
    }

    // checks if the specified screen has the same color and facing
    public boolean canMerge(TileEntityFlatScreen screen){
        //if(true) return true;
        if(screen == null || screen.isInvalid())
            return false;

        if(!screen.yaw().equals(origin().yaw()))
            return false;

        if(!screen.pitch().equals(origin().pitch()))
            return false;

        if(screen.tier() != origin().tier())
            return false;

        if(screen.getColor() != origin().getColor())
            return false;

        return true;
    }


}
