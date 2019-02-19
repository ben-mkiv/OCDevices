package ben_mkiv.ocdevices.common.flatscreen;

import ben_mkiv.ocdevices.common.integration.MCMultiPart.MultiPartHelper;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.utils.AABBHelper;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.Visibility;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.*;

// helper for checking the multiblock structure
public class FlatScreenMultiblock {

    private TileEntityFlatScreen origin;
    private FlatScreenHelper helper = new FlatScreenHelper();
    private int width = 0, height = 0;

    private HashSet<TileEntityFlatScreen> screens = new HashSet<>();

    private AxisAlignedBB boundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    private long timeCreated = 0;

    public FlatScreenMultiblock(TileEntityFlatScreen screen){
        origin = MultiPartHelper.getScreenFromTile(screen);
    }

    public boolean initialized(){
        return timeCreated != 0;
    }

    public void initialize(){
        mergeIntoMultiBlock(origin);
        searchAndMergeMultiBlocks();

        timeCreated = System.currentTimeMillis();
    }

    private void searchAndMergeMultiBlocks(){
        for(Map.Entry<FlatScreenMultiblock, EnumFacing.Axis> otherMultiblock : getAdjacentMultiBlocks(getMergeAxis(origin())).entrySet()) {
            if(tryMergeMultiblocks(otherMultiblock.getKey(), otherMultiblock.getValue())){
                searchAndMergeMultiBlocks();
                return;
            }
        }
    }

    private void mergeIntoMultiBlock(TileEntityFlatScreen screen){
        screens.add(screen);
        screen.flatScreenMultiblock = this;

        updateBoundingBox();


        setupOrigin(origin().pitch(), origin().yaw());
        setupMultiblockConnection(screen);

        updateSize();
        getHelper().refresh(origin);

        screen.boundingBoxes = FlatScreenAABB.updateScreenBB(screen);
    }

    private HashMap<FlatScreenMultiblock, EnumFacing.Axis> getAdjacentMultiBlocks(ArrayList<EnumFacing.Axis> axis){
        HashMap<FlatScreenMultiblock, EnumFacing.Axis> list = new HashMap<>();

        for(TileEntityFlatScreen screen : screens()){
            for(EnumFacing facing : EnumFacing.values()){
                if(!axis.contains(facing.getAxis()))
                    continue;

                TileEntityFlatScreen offsetScreen = MultiPartHelper.getScreenFromTile(screen.getWorld().getTileEntity(screen.getPos().offset(facing)));

                if(offsetScreen == null || this.equals(offsetScreen.getMultiblock()))
                    continue;

                list.put(offsetScreen.getMultiblock(), facing.getAxis());
            }
        }

        return list;
    }

    public void split(){
        if(screens().size() > 1)
            for(TileEntityFlatScreen screen : screens())
                screen.flatScreenMultiblock = new FlatScreenMultiblock(screen);
    }

    private boolean tryMergeMultiblocks(FlatScreenMultiblock otherMultiblock, EnumFacing.Axis mergeOnAxis){
        if(!canMergeWithMutliblock(otherMultiblock, mergeOnAxis))
            return false;

        mergeMultiblocks(otherMultiblock);

        return true;
    }

    private void mergeMultiblocks(FlatScreenMultiblock source){
        for(TileEntityFlatScreen screen : source.screens())
            mergeIntoMultiBlock(screen);
        //System.out.println("[merge] new size: "+width()+"x"+height()+"), remote: " + origin().world().isRemote);
    }

    private void updateSize(){
        switch (origin().pitch()){
            case UP:
            case DOWN:
                if(origin().yaw().getAxis().equals(EnumFacing.Axis.X)) {
                    width = (int) (boundingBox.maxZ - boundingBox.minZ);
                    height = (int) (boundingBox.maxX - boundingBox.minX);
                }
                else {
                    height = (int) (boundingBox.maxZ - boundingBox.minZ);
                    width = (int) (boundingBox.maxX - boundingBox.minX);
                }

                break;
            default:
            case NORTH:
                width = (int) Math.max(boundingBox.maxX - boundingBox.minX, boundingBox.maxZ - boundingBox.minZ);
                height = (int) (boundingBox.maxY - boundingBox.minY);
        }
    }

    private boolean canMergeWithMutliblock(FlatScreenMultiblock otherMultiblock, EnumFacing.Axis mergeOnAxis){
        //if(!isInOtherMultiblock(otherMultiblock.origin()) && !otherMultiblock.isInOtherMultiblock(origin()))
        //    return false;
        if(this.equals(otherMultiblock))
            return false;

        //if(otherMultiblock.screens().size() < 1)
        //    return false;

        // check facing, pitch, color and tier
        if(!origin().canMerge(otherMultiblock.origin()))
            return false;

        // this looks weird but it makes sense
        ArrayList<EnumFacing.Axis> axisList = new ArrayList<>(getMergeAxis(origin()));
        axisList.remove(mergeOnAxis);
        switch(axisList.get(0)){
            case X:
                if(otherMultiblock.boundingBox.minX != boundingBox.minX || otherMultiblock.boundingBox.maxX != boundingBox.maxX)
                    return false;
                break;
            case Y:
                if(otherMultiblock.boundingBox.minY != boundingBox.minY || otherMultiblock.boundingBox.maxY != boundingBox.maxY)
                    return false;
                break;
            case Z:
                if(otherMultiblock.boundingBox.minZ != boundingBox.minZ || otherMultiblock.boundingBox.maxZ != boundingBox.maxZ)
                    return false;
                break;
        }

        return true;
    }

    private void setupMultiblockConnection(TileEntityFlatScreen screen) {
        if (origin.getWorld().isRemote)
            return;

        if (screen.equals(origin))
            ((Component) screen.buffer().node()).setVisibility(Visibility.Network);
        else
            ((Component) screen.buffer().node()).setVisibility(Visibility.None);
    }

    private void updateBoundingBox() {
        boundingBox = null;
        for (TileEntityFlatScreen screen : screens()){
            if (screen == null || screen.isInvalid())
                continue;

            if (boundingBox == null)
                boundingBox = new AxisAlignedBB(screen.getPos(), screen.getPos().add(1, 1, 1));
            else
                boundingBox = boundingBox.union(new AxisAlignedBB(screen.getPos(), screen.getPos().add(1, 1, 1)));
        }
    }

    private void setupOrigin(EnumFacing pitch, EnumFacing yaw){
        int x, z, y = (int) boundingBox.minY;

        x = (int) boundingBox.maxX - 1;
        z = (int) boundingBox.maxZ - 1;

        switch(yaw){
            case SOUTH: x = (int) boundingBox.minX; break;
            case WEST: z = (int) boundingBox.minZ; break;
        }

        if(pitch.equals(EnumFacing.UP))
            switch(yaw){
                case NORTH: z = (int) boundingBox.minZ; break;
                case EAST: case WEST: x = (int) boundingBox.minX; break;
            }

        if(pitch.equals(EnumFacing.DOWN)) {
            System.out.println("yaw: " + yaw.getName() + ", east ordinal: " + EnumFacing.EAST.ordinal());
            switch (yaw) {
                case EAST:
                    System.out.println("east case");
                    x = (int) boundingBox.minX;
                    break;
                case SOUTH:
                    z = (int) boundingBox.minZ;
                    break;
            }
        }



        setNewOrigin(MultiPartHelper.getScreenFromTile(origin().getWorld().getTileEntity(new BlockPos(x, y, z))));
    }

    void setNewOrigin(TileEntityFlatScreen newOrigin){
        if(newOrigin != null)
            origin = newOrigin;
    }

    // returns the axis the screen cant merge along
    private ArrayList<EnumFacing.Axis> getMergeAxis(TileEntityFlatScreen screen){
        ArrayList<EnumFacing.Axis> axis = new ArrayList<>();
        // dont change that order or multiblock merging will break
        axis.add(EnumFacing.Axis.Y);
        axis.add(EnumFacing.Axis.X);
        axis.add(EnumFacing.Axis.Z);

        // remove y-Axis if screen is facing up/down, else remove the axis the screen is placed on
        if(screen.pitch().getAxis().equals(EnumFacing.Axis.Y))
            axis.remove(EnumFacing.Axis.Y);
        else
            axis.remove(screen.yaw().getAxis());

        return axis;
    }

    public HashSet<TileEntityFlatScreen> screens(){
        return screens;
    }

    public FlatScreenHelper getHelper(){
        return helper;
    }

    public int width(){
        return width;
    }

    public int height(){
        return height;
    }

    public AxisAlignedBB getBoundingBox(){
        return boundingBox;
    }

    public TileEntityFlatScreen origin(){
        return origin;
    }

}
