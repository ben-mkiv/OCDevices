package ben_mkiv.ocdevices.common.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface IMultiblockScreen extends IOrientable {
    default Vec3d unmapHitVector(Vec3d hitVect){
        switch(yaw()) {
            case NORTH:
                hitVect = new Vec3d(1 - hitVect.x, hitVect.y, -hitVect.z); break;
            case EAST:
                hitVect = new Vec3d(1 - hitVect.z, hitVect.y, hitVect.x); break;
            case WEST:
                hitVect = new Vec3d(hitVect.z, hitVect.y, -hitVect.x); break;
        }

        switch(pitch()){
            case DOWN:
                switch(yaw()){
                    case NORTH:
                    case WEST: hitVect = new Vec3d(hitVect.x, 1+hitVect.z, hitVect.y); break;
                    case SOUTH:
                    case EAST: hitVect = new Vec3d(hitVect.x, hitVect.z, hitVect.y); break;
                }
                break;
            case UP:
                switch(yaw()){
                    case SOUTH:
                    case EAST: hitVect = new Vec3d(hitVect.x, 1-hitVect.z, hitVect.y); break;
                    case NORTH:
                    case WEST: hitVect = new Vec3d(hitVect.x, -hitVect.z, hitVect.y); break;
                }
                break;
        }

        BlockPos offset = MultiBlockOffset(this); //unprojected offset in the multiblock
        hitVect = hitVect.add(new Vec3d(offset.getX(), offset.getY(), 0));

        return hitVect;
    }

    BlockPos getPos();

    IMultiblockScreen origin();

    static BlockPos MultiBlockOffset(IMultiblockScreen screen){
        BlockPos worldOffset = screen.origin().getPos().subtract(screen.getPos());

        int offsetX, offsetY;

        if(!screen.pitch().getAxis().equals(EnumFacing.Axis.Y)) {
            offsetX = screen.yaw().getAxis().equals(EnumFacing.Axis.Z) ? worldOffset.getX() : worldOffset.getZ();
            offsetY = worldOffset.getY();
        } else {
            offsetX = screen.yaw().getAxis().equals(EnumFacing.Axis.Z) ? worldOffset.getX() : worldOffset.getZ();
            offsetY = screen.yaw().getAxis().equals(EnumFacing.Axis.Z) ? worldOffset.getZ() : worldOffset.getX();
        }

        return new BlockPos(Math.abs(offsetX), Math.abs(offsetY), 0);
    }

}
