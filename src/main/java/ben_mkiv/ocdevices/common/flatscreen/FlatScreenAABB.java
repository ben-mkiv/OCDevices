package ben_mkiv.ocdevices.common.flatscreen;

import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMultiblockDisplay;
import ben_mkiv.ocdevices.utils.AABBHelper;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;

import static ben_mkiv.ocdevices.common.flatscreen.FlatScreen.precision;

public class FlatScreenAABB {
    public static ArrayList<AxisAlignedBB> updateScreenBB(TileEntityMultiblockDisplay screen){
        ArrayList<AxisAlignedBB> boundingBoxes = new ArrayList<>();

        float[] l = screen.getHelper().getDepthForBlock(screen); // 0 = sTopLeft, 1 = sTopRight, 2 = sBottomLeft, 3 = sBottomRight
        float sliceDepth;
        final int sliceCount = 8;
        final float sliceResolution = 1f/sliceCount;

        int start = 0;
        if(screen.getHelper().topRight > screen.getHelper().topLeft || screen.getHelper().topLeft < screen.getHelper().bottomLeft)
            start = sliceCount-1;

        switch(screen.getData().tiltAxis){
            case X:
                for(int c = 0, slice = start; c < sliceCount; c++, slice = Math.abs(start-c)) {
                    sliceDepth = Math.min(l[0], l[2]) + c * sliceResolution * (Math.max(l[0], l[2]) - Math.min(l[0], l[2]));

                    AxisAlignedBB bb;

                    bb = new AxisAlignedBB(0d, slice * sliceResolution, 1d-sliceDepth, 1d, (slice+1) * sliceResolution, 1d);

                    bb = AABBHelper.rotateVertical(bb, screen.pitch());
                    bb = AABBHelper.rotateHorizontal(bb, screen.yaw());

                    boundingBoxes.add(bb);
                }
                break;

            case Y:
                for(int c = 0, slice = start; c < sliceCount; c++, slice = Math.abs(c - start)) {
                    sliceDepth = Math.min(l[0], l[1]) + c * sliceResolution * (Math.max(l[0], l[1]) - Math.min(l[0], l[1]));

                    AxisAlignedBB bb;

                    bb = new AxisAlignedBB(slice * sliceResolution, 0d, 1d-sliceDepth, (slice+1) * sliceResolution, 1d, 1d);
                    bb = AABBHelper.rotateVertical(bb, screen.pitch());
                    bb = AABBHelper.rotateHorizontal(bb, screen.yaw());

                    boundingBoxes.add(bb);
                }
                break;

            default:
            case NONE:
                AxisAlignedBB bb = new AxisAlignedBB(0, 0, 1d-precision * screen.getData().screenDepthTop, 1, 1, 1d);
                bb = AABBHelper.rotateVertical(bb, screen.pitch());
                bb = AABBHelper.rotateHorizontal(bb, screen.yaw());
                boundingBoxes.add(bb);
        }

        return boundingBoxes;
    }
}
