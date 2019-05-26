package ben_mkiv.ocdevices.common.tileentity;

import ben_mkiv.ocdevices.common.blocks.BlockFlatScreen;
import ben_mkiv.ocdevices.common.component.FlatScreenComponent;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import ben_mkiv.rendertoolkit.common.widgets.component.common.ocScreenWidget;
import ben_mkiv.rendertoolkit.common.widgets.component.world.ocScreen3D;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.network.*;
import li.cil.oc.common.tileentity.Keyboard;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;

//todo: implement packets for color, rotation, touchMode state, ... changes instead of syncing whole NBT
//todo: add redstone support?
//todo: add arrow or more generic hit by entity support?

public class TileEntityFlatScreen extends TileEntityMultiblockDisplay implements ocScreen3D.IScreenBlock, ColoredTile {
    private final FlatScreenComponent buffer;

    private int color = 0;

    private boolean isTouchModeInverted = false;
    private final HashMap<EntityLivingBase, Vec3i> walkMap = new HashMap<>();

    public ocScreenWidget widgetWorld = new ocScreen3D();

    public TileEntityFlatScreen() {
        super();
        buffer = new FlatScreenComponent(this);
    }

    @Override
    public void onLoad(){
        super.onLoad();
        widgetWorld.bind(this);
    }

    public FlatScreenComponent buffer(){
        return buffer;
    }

    private void walk(EntityLivingBase entity, Vec3i pos){
        if(walkMap.containsKey(entity) && walkMap.get(entity).equals(pos))
            return;

        walkMap.remove(entity);
        walkMap.put(entity, pos);
        node().sendToReachable("computer.signal", "walk", pos.getX(), pos.getY());
    }

    public void walk(Entity entity){
        if(isClient() || !(entity instanceof EntityLivingBase))
            return;

        BlockPos offset = IMultiblockScreen.MultiBlockOffset(this);
        origin().walk((EntityLivingBase) entity, new Vec3i(offset.getX() + 1, offset.getY() + 1, 0));
    }

    @Override
    public boolean canConnect(EnumFacing face){
        return super.canConnect(face) || getKeyboard(face) != null;
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

    public boolean touchEvent(EntityPlayer player, EnumFacing side, Vec3d hitVect){
        if(!side.equals(facing()))
            return false;

        hitVect = unmapHitVector(hitVect);

        if(hitVect == null)
            return false;

        double x = (double) origin().buffer().getViewportWidth() / getHelper().displayWidth;
        x*= hitVect.x;

        double y = (double) origin().buffer().getViewportHeight() / getHelper().displayHeight;
        y*= (getHelper().displayHeight - hitVect.y);

        origin().buffer().mouseDown(x, y, 0, player);

        return true;
    }

    @Override
    public TileEntityFlatScreen origin(){
        return (TileEntityFlatScreen) super.origin();
    }

    @Override
    public void update(){
        super.update();

        if(isOrigin()) {
            if (isClient() || isConnected())
                buffer().update();
        }
    }

    public boolean hasKeyboard(){
        return getKeyboards().size() > 0;
    }

    public HashSet<TileEntity> getKeyboards(){
        HashSet<TileEntity> keyboards = new HashSet<>();
        for(TileEntityMultiblockDisplay screen : getScreens()){
            // check for keyboard in same block/tile
            if(MultiPartHelper.getKeyboardFromTile(screen) != null)
                keyboards.add(MultiPartHelper.getKeyboardFromTile(screen));
            // check for adjacent keyboards
            for(EnumFacing side : EnumFacing.values())
                if(screen instanceof TileEntityFlatScreen && ((TileEntityFlatScreen) screen).getKeyboard(side) != null)
                    keyboards.add(((TileEntityFlatScreen) screen).getKeyboard(side));
        }

        return keyboards;
    }

    public TileEntity getKeyboard(EnumFacing side){
        if(side == null)
            return MultiPartHelper.getKeyboardFromTile(this);

        TileEntity tile = getWorld().getTileEntity(this.getPos().offset(side));
        return tile instanceof Keyboard ? tile : MultiPartHelper.getKeyboardFromTile(tile);
    }

    public void onColorChanged() {
        // nbt is parsed in network thread so we have update() to do the actual work
        multiblockInvalid = true;
        markDirty();
    }

    // yes we have to override them... -.-
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setInteger("ocd:casecolor", getColor());
        nbt.setBoolean("ocd:invertTouch", isTouchModeInverted());

        buffer().save(nbt);

        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        setTouchModeInverted(nbt.getBoolean("ocd:invertTouch"));
        setColor(nbt.getInteger("ocd:casecolor"));

        buffer().load(nbt);
    }

    @Override
    public Node node(){
        return buffer().node();
    }

    @Override
    public void markDirty(){
        if(isClient()) return;
        super.markDirty();
    }

    @Override
    public @Nonnull NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        return writeToNBT(nbt);
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

    @Override
    public boolean shouldRenderContent(){
        return super.shouldRenderContent() && buffer().isRenderingEnabled();
    }

    // checks if the specified screen has the same color and tier
    @Override
    public boolean canMerge(TileEntityMultiblockDisplay screen){
        return super.canMerge(screen)
                && screen instanceof TileEntityFlatScreen
                && ((TileEntityFlatScreen) screen).tier() == origin().tier()
                && ((TileEntityFlatScreen) screen).getColor() == origin().getColor();
    }

    public boolean isTouchModeInverted(){ return isTouchModeInverted; }

    public void setTouchModeInverted(boolean state){
        if(state == isTouchModeInverted)
            return;

        isTouchModeInverted = state;
        markDirty();
    }
}
