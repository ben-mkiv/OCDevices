package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.component.FlatScreenComponent;
import ben_mkiv.ocdevices.common.drivers.FlatScreenDriver;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreen;
import ben_mkiv.ocdevices.common.blocks.BlockFlatScreen;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenAABB;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenHelper;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenMultiblock;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import li.cil.oc.api.Driver;
import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.api.network.*;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.property.PropertyRotatable;
import li.cil.oc.common.tileentity.Screen;
import net.minecraft.block.state.IBlockState;
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
    private final FlatScreenComponent buffer;

    public ArrayList<AxisAlignedBB> boundingBoxes = new ArrayList<>(Arrays.asList(FULL_BLOCK_AABB));

    private FlatScreen data = new FlatScreen();
    private EnumFacing yaw, pitch;
    private int color = 0;

    public FlatScreenMultiblock flatScreenMultiblock = new FlatScreenMultiblock(this);;

    private boolean loaded = false, multiblockInvalid = false;

    private boolean isLoaded(){
        return loaded;
    }

    public TileEntityFlatScreen() {
        super(Tier.Four());
        buffer = FlatScreenDriver.driver.createEnvironment(MultiPartHelper.getScreenFromTile(this));
    }

    public FlatScreen getData(){
        return isOrigin() || origin() == null || origin().isInvalid() || !origin().isLoaded() ? data : origin().getData();
    }

    @Override
    public li.cil.oc.api.internal.TextBuffer buffer(){
        return this.buffer;
    }

    public void updateNeighbours(){
        getHelper().refresh(this);

        for(TileEntityFlatScreen screen : getScreens()){
            screen.boundingBoxes = FlatScreenAABB.updateScreenBB(screen);
            screen.markDirty();
        }
    }

    @Override
    public void update(){
        if(!isLoaded()) //for some reason this fails for multiparts on clientside sometimes, so we have to check it -.-
            onLoad();

        if(!getMultiblock().initialized())
            getMultiblock().initialize();

        if(multiblockInvalid) {
            getMultiblock().split();
            multiblockInvalid = false;
        }

        if(isClient() || isConnected())
            buffer().update();
    }

    @Override
    public Node sidedNode(EnumFacing side) {
        return hasKeyboardInSameBlock() || hasKeyboard(side) || !facing().equals(side) ? node() : super.sidedNode(side);
    }

    @Override
    public EnumFacing facing(){
        return pitch().getAxis().equals(EnumFacing.Axis.Y) ? pitch() : yaw();
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

    public void setConnectivity(){
        if (isOrigin())
            ((Component) buffer().node()).setVisibility(Visibility.Network);
        else
            ((Component) buffer().node()).setVisibility(Visibility.None);
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
        updateRotation(getWorld().getBlockState(getPos()));
        loaded = true;
    }

    public void updateRotation(IBlockState state){
        if(yaw != null && pitch != null)
            return;

        setYaw(state.getValue(PropertyRotatable.Yaw()));
        setPitch(state.getValue(PropertyRotatable.Pitch()));
    }

    public void setPitch(EnumFacing pitchIn){
        pitch = pitchIn;
    }

    public void setYaw(EnumFacing yawIn){
        yaw = yawIn;
    }

    @Override
    public void onColorChanged() {
        super.onColorChanged();
        // nbt is parsed in network thread so we have update() to do the actual work
        multiblockInvalid = true;
    }

    @Override
    public void onRotationChanged(){
        updateRotation(getWorld().getBlockState(getPos()));
        super.onRotationChanged();
        // nbt is parsed in network thread so we have update() to do the actual work
        multiblockInvalid = true;
    }

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

    // yes we have to override them... -.-
    @Override
    public void writeToNBTForServer(NBTTagCompound nbt){
        super.writeToNBTForServer(nbt); //required to not break multipart support
        nbt.setInteger("ocd:casecolor", getColor());
        nbt.setInteger("ocd:yaw", yaw().ordinal());
        nbt.setInteger("ocd:pitch", pitch().ordinal());

        nbt.setTag("ocd:screenData", getData().writeToNBT(new NBTTagCompound()));

        buffer().save(nbt);
    }

    @Override
    public void readFromNBTForServer(NBTTagCompound nbt){
        //super.readFromNBTForServer(nbt);
        setYaw(EnumFacing.values()[nbt.getInteger("ocd:yaw")]);
        setPitch(EnumFacing.values()[nbt.getInteger("ocd:pitch")]);

        invertTouchMode_$eq(nbt.getBoolean("oc:invertTouchMode"));
        hadRedstoneInput_$eq(nbt.getBoolean("oc:hadRedstoneInput"));

        if(nbt.hasKey("ocd:screenData"))
            data.readFromNBT(nbt.getCompoundTag("ocd:screenData"));

        setColor(nbt.getInteger("ocd:casecolor"));

        buffer().load(nbt);
    }

    @Override
    public Node node(){
        return buffer().node();
    }

    @Override
    public void writeToNBTForClient(NBTTagCompound nbt){
        super.writeToNBTForClient(nbt); //required to not break multipart support
        nbt.setInteger("ocd:casecolor", getColor());
        nbt.setInteger("ocd:yaw", yaw.ordinal());
        nbt.setInteger("ocd:pitch", pitch.ordinal());

        if(isOrigin())
            nbt.setTag("ocd:screenData", getData().writeToNBT(new NBTTagCompound()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readFromNBTForClient(NBTTagCompound nbt){
        super.readFromNBTForClient(nbt);
        setColor(nbt.getInteger("ocd:casecolor"));
        invertTouchMode_$eq(nbt.getBoolean("oc:invertTouchMode"));

        setYaw(EnumFacing.values()[nbt.getInteger("ocd:yaw")]);
        setPitch(EnumFacing.values()[nbt.getInteger("ocd:pitch")]);

        if(nbt.hasKey("ocd:screenData")) {
            getData().readFromNBT(nbt.getCompoundTag("ocd:screenData"));
            getHelper().refresh(this);
            for(TileEntityFlatScreen screen : getScreens())
                screen.boundingBoxes = FlatScreenAABB.updateScreenBB(screen);
        }

        buffer().load(nbt);
    }

    //nbt overrides (probably useless)
    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);

        if(isClient())
            readFromNBTForClient(nbt);
        else
            readFromNBTForServer(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        super.writeToNBT(nbt);

        if(isClient())
            writeToNBTForClient(nbt);
        else
            writeToNBTForServer(nbt);

        return nbt;
    }

    @Override
    public void markDirty(){
        IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        super.markDirty();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = new NBTTagCompound(); //super.getUpdateTag();
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
        handleUpdateTag(packet.getNbtCompound());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleUpdateTag(NBTTagCompound nbt){
        readFromNBTForClient(nbt);
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
        color = newColor;
        super.setColor(newColor);
    }

    // checks if the specified screen has the same color and facing
    public boolean canMerge(TileEntityFlatScreen screen){
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
