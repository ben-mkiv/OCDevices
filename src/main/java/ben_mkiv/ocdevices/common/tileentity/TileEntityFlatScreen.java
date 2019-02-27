package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.blocks.BlockFlatScreen;
import ben_mkiv.ocdevices.common.component.FlatScreenComponent;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreen;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenAABB;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenHelper;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenMultiblock;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MCMultiPart;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import li.cil.oc.api.API;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.network.*;
import li.cil.oc.api.prefab.TileEntityEnvironment;
import li.cil.oc.common.block.property.PropertyRotatable;
import li.cil.oc.common.tileentity.Screen;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;

public class TileEntityFlatScreen extends TileEntityEnvironment implements EnvironmentHost, ITickable, Analyzable {
    private FlatScreenComponent buffer;

    public ArrayList<AxisAlignedBB> boundingBoxes = new ArrayList<>(Arrays.asList(FULL_BLOCK_AABB));

    private FlatScreen data = new FlatScreen();
    private EnumFacing yaw, pitch;
    private int color = 0;

    public FlatScreenMultiblock flatScreenMultiblock = new FlatScreenMultiblock(this);

    private boolean loaded = false, multiblockInvalid = false;

    private boolean isLoaded(){
        return loaded;
    }

    public TileEntityFlatScreen() {
        super();
        buffer = new FlatScreenComponent(this);
    }

    public FlatScreen getData(){
        return isOrigin() || origin() == null || origin().isInvalid() || !origin().isLoaded() ? data : origin().getData();
    }

    public FlatScreenComponent buffer(){
        return buffer;
    }

    public void updateNeighbours(){
        getHelper().refresh(this);

        for(TileEntityFlatScreen screen : getScreens()){
            screen.boundingBoxes = FlatScreenAABB.updateScreenBB(screen);
            screen.markDirty();
        }
    }

    private void pauseMachine(){
        if(node() == null || node().network() == null)
            return;

        for (Node node : node().network().nodes()) {
            if(node.host() instanceof Machine) {
                Machine computer = (Machine) node.host();
                if(computer.isRunning())
                    computer.pause(1);
            }
        }
    }

    public boolean powered(){
        return true;
    }

    boolean isConnected(){
        return node() != null && node().network() != null;
    }

    void joinNetwork(){
        if(!isConnected()) API.network.joinOrCreateNetwork(this);
    }

    boolean isClient(){
        return FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT);
    }

    @Override
    public void update(){
        if(!isLoaded()) //for some reason this fails for multiparts on clientside sometimes, so we have to check it -.-
            onLoad();

        if(!getMultiblock().initialized()) {
            getMultiblock().initialize();
        }

        if(multiblockInvalid) {
            getMultiblock().split();
            multiblockInvalid = false;
        }

        if(!isOrigin())
            return;

        if(isClient() || isConnected())
            buffer().update();
    }

    public EnumFacing facing(){
        return pitch().getAxis().equals(EnumFacing.Axis.Y) ? pitch() : yaw();
    }

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

    public TileEntityFlatScreen origin() {
        if(getMultiblock() != null && getMultiblock().origin() != null)
            return getMultiblock().origin();
        else
            return MultiPartHelper.getScreenFromTile(this);
    }

    private HashSet<TileEntityFlatScreen> getScreens() {
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
            ((ComponentConnector) node()).setVisibility(Visibility.Network);
        else
            ((ComponentConnector) node()).setVisibility(Visibility.None);
    }

    /* OC Screen overrides */
    public boolean isOrigin(){
        return this.equals(origin());
    }

    public int width(){
        return getMultiblock().width();
    }

    public int height(){
        return getMultiblock().height();
    }

    public EnumFacing yaw(){
        return yaw != null ? yaw : EnumFacing.NORTH;
    }

    public EnumFacing pitch(){
        return pitch != null ? pitch : EnumFacing.NORTH;
    }

    @Override
    public void onLoad(){
        super.onLoad();
        joinNetwork();
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

    public void onColorChanged() {
        // nbt is parsed in network thread so we have update() to do the actual work
        multiblockInvalid = true;
    }

    public void onRotationChanged(){
        updateRotation(getWorld().getBlockState(getPos()));
        // nbt is parsed in network thread so we have update() to do the actual work
        multiblockInvalid = true;
    }



    // yes we have to override them... -.-
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setInteger("ocd:casecolor", getColor());
        nbt.setInteger("ocd:yaw", yaw().ordinal());
        nbt.setInteger("ocd:pitch", pitch().ordinal());

        nbt.setTag("ocd:screenData", getData().writeToNBT(new NBTTagCompound()));

        buffer().save(nbt);

        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        setYaw(EnumFacing.values()[nbt.getInteger("ocd:yaw")]);
        setPitch(EnumFacing.values()[nbt.getInteger("ocd:pitch")]);

        if(nbt.hasKey("ocd:screenData"))
            getData().readFromNBT(nbt.getCompoundTag("ocd:screenData"));

        setColor(nbt.getInteger("ocd:casecolor"));

        buffer().load(nbt);


        System.out.println("node loaded: " + node().address());
    }

    @Override
    public Node node(){
        return buffer().node();
    }

    @Override
    public Node[] onAnalyze(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        Node[] nodes = new Node[getScreens().size()];
        int i=0;
        nodes[i++] = (origin().node());

        for(TileEntityFlatScreen screen : getScreens())
            if(!screen.equals(origin()))
                nodes[i++] = screen.node();

        return nodes;
    }

    public void writeToNBTForClient(NBTTagCompound nbt){
        nbt.setInteger("ocd:casecolor", getColor());
        nbt.setInteger("ocd:yaw", yaw.ordinal());
        nbt.setInteger("ocd:pitch", pitch.ordinal());

        if(isOrigin())
            nbt.setTag("ocd:screenData", getData().writeToNBT(new NBTTagCompound()));

        buffer().save(nbt);
    }

    public void readFromNBTForClient(NBTTagCompound nbt){
        setColor(nbt.getInteger("ocd:casecolor"));

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
    public void handleUpdateTag(@Nonnull NBTTagCompound nbt){
        readFromNBTForClient(nbt);
    }

    public int tier(){
        return BlockFlatScreen.tier;
    }

    public int getColor(){
        return color;
    }

    public void setColor(int newColor){
        if(color != newColor) {
            color = newColor;
            onColorChanged();
        }
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

    @Override
    public World world(){
        return MCMultiPart.getRealWorld(this);
    }

    @Override
    public double xPosition(){ return getPos().getX(); }
    @Override
    public double yPosition(){ return getPos().getY(); }
    @Override
    public double zPosition(){ return getPos().getZ(); }

    @Override
    public void markChanged(){
        markDirty();
    }

}
