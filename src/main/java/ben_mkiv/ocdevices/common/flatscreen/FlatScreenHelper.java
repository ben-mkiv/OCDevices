package ben_mkiv.ocdevices.common.flatscreen;

import ben_mkiv.ocdevices.common.tileentity.IMultiblockScreen;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMultiblockDisplay;
import ben_mkiv.ocdevices.utils.Triangle;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class FlatScreenHelper {

    public int screenCountX, screenCountY;
    public float displayWidth = 0, displayHeight = 0;
    public EnumFacing facing, pitch;

    public Vec3d tiltRenderOffset = new Vec3d(0, 0, 0);
    public Vec3d tiltRotationVector;

    public float topLeft = 1, topRight = 1, bottomLeft = 1, bottomRight = 1;
    public int opacity = 100;
    public Color color = new Color(0x0);

    private float factorAC;

    public void refresh(TileEntityMultiblockDisplay tile){
        screenCountX = tile.width();
        screenCountY = tile.height();
        facing = tile.yaw();
        pitch = tile.pitch();

        if(tile instanceof TileEntityFlatScreen)
            color = new Color(((TileEntityFlatScreen) tile).getColor());

        displayWidth = screenCountX;
        displayHeight = screenCountY;
        opacity = tile.getData().opacity;
        tiltRotationVector = new Vec3d(0, 0, 0);
        interpretControllerData(tile.getData());
    }


    // get the depth for the screen BLOCK in the argument to calculate BB
    public float[] getDepthForBlock(TileEntityMultiblockDisplay screen){
        BlockPos offset = IMultiblockScreen.MultiBlockOffset(screen);

        switch(screen.getData().tiltAxis) {
            case X:
                float top, bottom;

                if(topLeft > bottomLeft) {
                    top = Math.min(topLeft, bottomLeft) + Triangle.SubB(offset.getY(), factorAC);
                    bottom = Math.min(topLeft, bottomLeft) + Triangle.SubB(offset.getY() + 1, factorAC);
                }
                else {
                    top = Math.min(topLeft, bottomLeft) + Triangle.SubB(screenCountY - offset.getY() - 1, factorAC);
                    bottom = Math.min(topLeft, bottomLeft) + Triangle.SubB(screenCountY - offset.getY(), factorAC);
                }
                return new float[]{ top, top, bottom, bottom };

            case Y:
                float left, right;
                if(topLeft < topRight) {
                    left = Math.min(topLeft, topRight) + Triangle.SubB(offset.getX(), factorAC);
                    right = Math.min(topLeft, topRight) + Triangle.SubB(offset.getX() + 1, factorAC);
                }
                else {
                    left = Math.min(topLeft, topRight) + Triangle.SubB(screenCountX - offset.getX() - 1, factorAC);
                    right = Math.min(topLeft, topRight) + Triangle.SubB(screenCountX - offset.getX(), factorAC);
                }

                return new float[]{ left, right, left, right };

            default:
            case NONE:
                return new float[]{ topLeft, topLeft, topLeft, topLeft };
        }
    }

    // calculate screen size for current panel tilt
    private void interpretControllerData(FlatScreen data){
        Triangle tri;

        // calculate display size, rotation and renderoffset based on the panels tilt
        switch(data.tiltAxis){
            case X:
                topLeft = topRight = FlatScreen.precision * data.screenDepthTop;
                bottomLeft = bottomRight = FlatScreen.precision * data.screenDepthBottom;

                tri = new Triangle(topRight - bottomRight, screenCountY);
                displayHeight = tri.c;
                tiltRotationVector = new Vec3d(topLeft < bottomLeft ? tri.alpha : -tri.alpha, 0, 0);

                factorAC = Math.abs(tri.c / (float) screenCountY);
                break;

            case Y:
                bottomLeft = topLeft = FlatScreen.precision * data.screenDepthLeft;
                bottomRight = topRight = FlatScreen.precision * data.screenDepthRight;

                tri = new Triangle(topRight - topLeft, screenCountX);
                displayWidth = tri.c;
                tiltRotationVector = new Vec3d(0, topRight < topLeft ? tri.alpha : -tri.alpha, 0);

                factorAC = Math.abs(tri.c / (float) screenCountX);
                break;

            default:
            case NONE:
                bottomLeft = bottomRight = topLeft = topRight = FlatScreen.precision * data.screenDepthTop;
        }

        tiltRenderOffset = new Vec3d(0, 0, -1 + topLeft);
    }


}