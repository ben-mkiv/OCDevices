package ben_mkiv.ocdevices.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class UtilsCommon {
    public static EnumFacing getYawForPlacement(EntityLivingBase entity, BlockPos blockPos, EnumFacing faceHit){
        EnumFacing yaw = EnumFacing.getDirectionFromEntityLiving(blockPos, entity);

        if(!faceHit.getAxis().equals(EnumFacing.Axis.Y))
            yaw = faceHit;

        if(yaw.getAxis().equals(EnumFacing.Axis.Y))
            yaw = EnumFacing.fromAngle(entity.rotationYaw).getOpposite();

        return yaw;
    }

    public static EnumFacing getPitchForPlacement(EntityLivingBase entity, BlockPos blockPos, EnumFacing faceHit){
        EnumFacing pitch = EnumFacing.getDirectionFromEntityLiving(blockPos, entity);
        return pitch.getAxis().equals(EnumFacing.Axis.Y) ? pitch : EnumFacing.NORTH;
    }

}
