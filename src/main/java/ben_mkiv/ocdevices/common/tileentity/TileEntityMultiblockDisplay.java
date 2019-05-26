package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreen;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenAABB;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenHelper;
import ben_mkiv.ocdevices.common.flatscreen.FlatScreenMultiblock;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import li.cil.oc.api.API;
import li.cil.oc.api.network.*;
import li.cil.oc.api.prefab.TileEntityEnvironment;
import li.cil.oc.common.block.property.PropertyRotatable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
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

public class TileEntityMultiblockDisplay extends TileEntityEnvironment implements IMultiblockScreen, EnvironmentHost, SidedEnvironment, ITickable, Analyzable {

    public ArrayList<AxisAlignedBB> boundingBoxes = new ArrayList<>(Arrays.asList(FULL_BLOCK_AABB));
    public FlatScreenMultiblock flatScreenMultiblock = new FlatScreenMultiblock(this);

    private EnumFacing yaw, pitch;

    private final boolean isClient;
    private final FlatScreen data = new FlatScreen();

    private boolean loaded = false;
    boolean multiblockInvalid = false;

    boolean joinedNetwork = false;

    boolean isLoaded(){
        return loaded;
    }

    public TileEntityMultiblockDisplay(){
        super();
        isClient = FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT);
    }

    public TileEntityMultiblockDisplay origin() {
        if(getMultiblock() != null && getMultiblock().origin() != null)
            return getMultiblock().origin();
        else
            return MultiPartHelper.getScreenFromTile(this);
    }

    public FlatScreenMultiblock getMultiblock(){
        return flatScreenMultiblock;
    }

    public FlatScreen getData(){
        return isOrigin() || origin() == null || origin().isInvalid() || !origin().isLoaded() ? data : origin().getData();
    }

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

        if(!isClient() && !joinedNetwork)
            joinNetwork();
    }

    public void updateNeighbours(){
        getHelper().refresh(this);

        for(TileEntityMultiblockDisplay screen : getScreens()){
            screen.boundingBoxes = FlatScreenAABB.updateScreenBB(screen);
            screen.markDirty();
        }
    }

    HashSet<TileEntityMultiblockDisplay> getScreens() {
        return getMultiblock().screens();
    }

    public FlatScreenHelper getHelper(){
        return getMultiblock().getHelper();
    }


    @Override
    public @Nonnull
    AxisAlignedBB getRenderBoundingBox(){
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


    // checks if the specified screen has the same color and facing
    public boolean canMerge(TileEntityMultiblockDisplay screen){
        if(screen == null || screen.isInvalid())
            return false;

        if(!screen.yaw().equals(origin().yaw()) || !screen.pitch().equals(origin().pitch()))
            return false;

        return true;
    }

    @Override
    public void onLoad(){
        super.onLoad();
        IBlockState state = getWorld().getBlockState(getPos());
        pitch = state.getValue(PropertyRotatable.Pitch());
        yaw = state.getValue(PropertyRotatable.Yaw());
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
        if(pitchIn.equals(pitch))
            return;

        pitch = pitchIn;
        onRotationChanged();
    }

    public void setYaw(EnumFacing yawIn){
        if(yawIn.equals(yaw))
            return;

        yaw = yawIn;
        onRotationChanged();
    }

    public EnumFacing yaw(){
        return yaw != null ? yaw : EnumFacing.NORTH;
    }

    public EnumFacing pitch(){
        return pitch != null ? pitch : EnumFacing.NORTH;
    }

    boolean isClient(){
        return isClient;
    }

    public void onRotationChanged(){
        // nbt is parsed in network thread so we have update() to do the actual work
        multiblockInvalid = true;
        markDirty();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (node() != null)
            node().remove();
    }

    boolean isConnected(){
        return joinedNetwork || (node() != null && node().network() != null);
    }

    void joinNetwork(){
        if(!isClient() && !isConnected()){
            API.network.joinOrCreateNetwork(this);
            joinedNetwork = true;
        }
    }

    public boolean shouldRenderContent(){
        return true;
    }

    // Analyzeable Interface
    @Override
    public Node[] onAnalyze(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!OCDevices.debug)
            return new Node[]{ origin().node() };

        Node[] nodes = new Node[getScreens().size()];
        int i=0;
        nodes[i++] = (origin().node());

        for(TileEntityMultiblockDisplay screen : getScreens())
            if(!screen.equals(origin()))
                nodes[i++] = screen.node();

        return nodes;
    }

    // Environment Host Interface
    @Override
    public World world(){ return MultiPartHelper.getRealWorld(this); }
    @Override
    public double xPosition(){ return getPos().getX(); }
    @Override
    public double yPosition(){ return getPos().getY(); }
    @Override
    public double zPosition(){ return getPos().getZ(); }
    @Override
    public void markChanged(){ markDirty(); }


    // NBT
    @Override
    public void readFromNBT(NBTTagCompound nbt){
        readFacingFromNBT(nbt);
        if(nbt.hasKey("ocd:screenData"))
            getData().readFromNBT(nbt.getCompoundTag("ocd:screenData"));

        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt = writeFacingToNBT(nbt);
        nbt.setTag("ocd:screenData", getData().writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(nbt);
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
        super.handleUpdateTag(nbt);

        if(nbt.hasKey("ocd:screenData")) {
            getData().readFromNBT(nbt.getCompoundTag("ocd:screenData"));
            getHelper().refresh(this);
            for(TileEntityMultiblockDisplay screen : getScreens())
                screen.boundingBoxes = FlatScreenAABB.updateScreenBB(screen);
        }
    }


    @Override
    public void markDirty(){
        if(getWorld() == null) return;
        IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        super.markDirty();
    }

    @Override
    public @Nonnull NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        return writeToNBT(nbt);
    }


    @Override
    public Node sidedNode(EnumFacing face){
        return canConnect(face) ? node() : null;
    }

    @Override
    public boolean canConnect(EnumFacing face){
        return !face.equals(facing());
    }




}
