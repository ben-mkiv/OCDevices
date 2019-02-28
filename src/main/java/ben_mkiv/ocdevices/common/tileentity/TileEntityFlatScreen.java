package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.OCDevices;
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
import li.cil.oc.common.tileentity.Keyboard;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;


//todo: implement packets for color, rotation, touchMode state, ... changes instead of syncing whole NBT
//todo: add redstone support?
//todo: add arrow or more generic hit by entity support?

public class TileEntityFlatScreen extends TileEntityEnvironment implements SidedEnvironment, EnvironmentHost, ITickable, Analyzable {
    private final FlatScreenComponent buffer;
    private final boolean isClient;

    public ArrayList<AxisAlignedBB> boundingBoxes = new ArrayList<>(Arrays.asList(FULL_BLOCK_AABB));

    private final FlatScreen data = new FlatScreen();
    private EnumFacing yaw, pitch;
    private int color = 0;
    private boolean isTouchModeInverted = false;
    private final HashMap<EntityLivingBase, Vec3i> walkMap = new HashMap<>();

    public FlatScreenMultiblock flatScreenMultiblock = new FlatScreenMultiblock(this);

    private boolean loaded = false, multiblockInvalid = false;

    private boolean isLoaded(){
        return loaded;
    }

    public TileEntityFlatScreen() {
        super();
        isClient = FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT);
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

    private void walk(EntityLivingBase entity, Vec3i pos){
        if(walkMap.containsKey(entity) && walkMap.get(entity).equals(pos))
            return;

        walkMap.remove(entity);
        walkMap.put(entity, pos);
        node().sendToReachable("computer.signal", "walk", pos.getX(), pos.getY());
    }

    public void walk(Entity entity){
        if(isClient || !(entity instanceof EntityLivingBase))
            return;

        BlockPos offset = FlatScreenHelper.MultiBlockOffset(this);
        origin().walk((EntityLivingBase) entity, new Vec3i(offset.getX() + 1, offset.getY() + 1, 0));
    }

    @Override
    public Node sidedNode(EnumFacing face){
        return canConnect(face) ? node() : null;
    }

    @Override
    public boolean canConnect(EnumFacing face){
        return !face.equals(facing()) || getKeyboard(face) != null;
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

    @Override
    public void invalidate() {
        super.invalidate();
        if (node() != null)
            node().remove();
    }

    boolean isConnected(){
        return node() != null && node().network() != null;
    }

    void joinNetwork(){
        if(!isClient() && !isConnected()) API.network.joinOrCreateNetwork(this);
    }

    boolean isClient(){
        return isClient;
    }

    //todo: rewrite the rotate methods to easy math like in pitch subcase
    public boolean touchEvent(EntityPlayer player, EnumFacing side, Vec3d hitVect){
        if(!side.equals(facing()))
            return false;

        //System.out.println("(+) " + hitVect.toString());

        switch(yaw()) {
            case NORTH:
                hitVect = hitVect.rotateYaw((float) Math.toRadians(-180)).addVector(1, 0, 0); break;
            case EAST:
                hitVect = hitVect.rotateYaw((float) Math.toRadians(-90)).addVector(1, 0, 0); break;
            case WEST:
                hitVect = hitVect.rotateYaw((float) Math.toRadians(90)); break;
        }

        switch(pitch()){
            case DOWN:
                hitVect = hitVect.rotatePitch((float) Math.toRadians(-90));
                switch(yaw()){
                    case NORTH:
                    case WEST: hitVect = new Vec3d(hitVect.x, 1-hitVect.y, 0); break;
                    case SOUTH:
                    case EAST: hitVect = new Vec3d(hitVect.x, -hitVect.y, 0); break;
                }
                break;
            case UP:
                hitVect = hitVect.rotatePitch((float) Math.toRadians(-90));
                switch(yaw()){
                    case EAST:
                    case SOUTH: hitVect = hitVect.addVector(0, 1, 0); break;
                }
                break;
        }

        //System.out.println("(Q)  " + hitVect.toString());

        BlockPos offset = FlatScreenHelper.MultiBlockOffset(this); //unprojected offset in the multiblock
        hitVect = hitVect.add(new Vec3d(offset.getX(), offset.getY(), 0));

        //System.out.println("(F) " + hitVect.toString());

        double x = (double) origin().buffer().getViewportWidth() / getHelper().displayWidth;
        x*= hitVect.x;

        double y = (double) origin().buffer().getViewportHeight() / getHelper().displayHeight;
        y*= (getHelper().displayHeight - hitVect.y);

        origin().buffer().mouseDown(x, y, 0, player);

        return true;
    }

    @Override
    public void update(){
        if(!isLoaded()) //for some reason this fails for multiparts on clientside sometimes, so we have to check it -.-
            onLoad();

        joinNetwork();

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
        return getKeyboards().size() > 0;
    }

    public HashSet<TileEntity> getKeyboards(){
        HashSet<TileEntity> keyboards = new HashSet<>();
        for(TileEntityFlatScreen screen : getScreens()){
            // check for keyboard in same block/tile
            if(MultiPartHelper.getKeyboardFromTile(screen) != null)
                keyboards.add(MultiPartHelper.getKeyboardFromTile(screen));
            // check for adjacent keyboards
            for(EnumFacing side : EnumFacing.values())
                if(screen.getKeyboard(side) != null)
                    keyboards.add(screen.getKeyboard(side));
        }

        return keyboards;
    }

    public TileEntity getKeyboard(EnumFacing side){
        if(side == null)
            return MultiPartHelper.getKeyboardFromTile(this);

        TileEntity tile = getWorld().getTileEntity(this.getPos().offset(side));
        return tile instanceof Keyboard ? tile : MultiPartHelper.getKeyboardFromTile(tile);
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
    public @Nonnull AxisAlignedBB getRenderBoundingBox(){
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

    public void onColorChanged() {
        // nbt is parsed in network thread so we have update() to do the actual work
        multiblockInvalid = true;
        markDirty();
    }

    public void onRotationChanged(){
        // nbt is parsed in network thread so we have update() to do the actual work
        multiblockInvalid = true;
        markDirty();
    }

    // yes we have to override them... -.-
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setInteger("ocd:casecolor", getColor());
        nbt.setInteger("ocd:yaw", yaw().ordinal());
        nbt.setInteger("ocd:pitch", pitch().ordinal());
        nbt.setBoolean("ocd:invertTouch", isTouchModeInverted());

        nbt.setTag("ocd:screenData", getData().writeToNBT(new NBTTagCompound()));

        buffer().save(nbt);

        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        setYaw(EnumFacing.values()[nbt.getInteger("ocd:yaw")]);
        setPitch(EnumFacing.values()[nbt.getInteger("ocd:pitch")]);
        setTouchModeInverted(nbt.getBoolean("ocd:invertTouch"));

        if(nbt.hasKey("ocd:screenData"))
            getData().readFromNBT(nbt.getCompoundTag("ocd:screenData"));

        setColor(nbt.getInteger("ocd:casecolor"));

        buffer().load(nbt);
    }

    @Override
    public Node node(){
        return buffer().node();
    }

    @Override
    public Node[] onAnalyze(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!OCDevices.debug)
            return new Node[]{ origin().node() };

        Node[] nodes = new Node[getScreens().size()];
        int i=0;
        nodes[i++] = (origin().node());

        for(TileEntityFlatScreen screen : getScreens())
            if(!screen.equals(origin()))
                nodes[i++] = screen.node();

        return nodes;
    }

    @Override
    public void markDirty(){
        if(isClient() || getWorld() == null) return;
        IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        super.markDirty();
    }

    @Override
    public @Nonnull NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        return writeToNBT(nbt);
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
        readFromNBT(nbt);

        if(nbt.hasKey("ocd:screenData")) {
            getData().readFromNBT(nbt.getCompoundTag("ocd:screenData"));
            getHelper().refresh(this);
            for(TileEntityFlatScreen screen : getScreens())
                screen.boundingBoxes = FlatScreenAABB.updateScreenBB(screen);
        }
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

        if(!screen.yaw().equals(origin().yaw()) || !screen.pitch().equals(origin().pitch()))
            return false;

        return screen.tier() == origin().tier() && screen.getColor() == origin().getColor();
    }


    public boolean isTouchModeInverted(){ return isTouchModeInverted; }

    public void setTouchModeInverted(boolean state){
        if(state == isTouchModeInverted)
            return;

        isTouchModeInverted = state;
        markDirty();
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

}
