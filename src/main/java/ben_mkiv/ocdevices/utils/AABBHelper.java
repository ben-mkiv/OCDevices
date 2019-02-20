package ben_mkiv.ocdevices.utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class AABBHelper {
    public static AxisAlignedBB rotateVertical(AxisAlignedBB bb, EnumFacing newFacing){
        //allways assume bb facing NORTH as input
        switch(newFacing){
            case UP: return new AxisAlignedBB(bb.minX, 1d-bb.minZ, bb.minY, bb.maxX, 1d-bb.maxZ, bb.maxY); // 90
            case DOWN: return new AxisAlignedBB(bb.minX, bb.minZ, 1d-bb.minY, bb.maxX, bb.maxZ, 1d-bb.maxY); // 270
            default: return bb;
        }
    }

    public static AxisAlignedBB rotateHorizontal(AxisAlignedBB bb, EnumFacing newFacing){
        //allways assume bb facing NORTH as input
        switch(newFacing){
            case WEST: return new AxisAlignedBB(bb.minZ, bb.minY, 1d - bb.minX, bb.maxZ, bb.maxY, 1d - bb.maxX);
            case EAST: return new AxisAlignedBB(1d - bb.maxZ, bb.minY, bb.minX, 1d - bb.minZ, bb.maxY, bb.maxX);
            case SOUTH: return new AxisAlignedBB(1d - bb.maxX, bb.minY, 1d - bb.maxZ, 1d - bb.minX, bb.maxY, 1d - bb.minZ);
            case NORTH: default: return bb;
        }
    }

    public static NBTTagCompound writeToNBT(AxisAlignedBB bb, NBTTagCompound nbt){
        nbt.setDouble("x1", bb.minX);
        nbt.setDouble("y1", bb.minY);
        nbt.setDouble("z1", bb.minZ);
        nbt.setDouble("x2", bb.maxX);
        nbt.setDouble("y2", bb.maxY);
        nbt.setDouble("z2", bb.maxZ);
        return nbt;
    }

    public static AxisAlignedBB readFromNBT(NBTTagCompound nbt){
        return new AxisAlignedBB(
                nbt.getDouble("x1"), nbt.getDouble("y1"), nbt.getDouble("z1"),
                nbt.getDouble("x2"), nbt.getDouble("y2"), nbt.getDouble("z2"));
    }

}
