package ben_mkiv.ocdevices.common.flatscreen;

import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.utils.Triangle;
import li.cil.oc.common.tileentity.Screen;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Vector3f;
import scala.collection.Iterator;

import java.awt.*;
import java.util.HashSet;

public class FlatScreenHelper {
    public int screenCountX, screenCountY;

    public EnumFacing facing, pitch;

    public float displayWidth = 0, displayHeight = 0;

    public Vec3d tiltRenderOffset = new Vec3d(0, 0, 0);
    public Vector3f tiltRotationVector;

    public float topLeft = 1, topRight = 1, bottomLeft = 1, bottomRight = 1;
    public boolean renderOpaqueModel = true;

    public Color color;

    float factorAC;

    Triangle tri;

    TileEntityFlatScreen tile;

    public FlatScreenHelper(TileEntityFlatScreen te){
        screenCountX = te.width();
        screenCountY = te.height();
        facing = te.yaw();
        pitch = te.pitch();
        color = new Color(te.getColor());
        displayWidth = screenCountX;
        displayHeight = screenCountY;
        tiltRotationVector = new Vector3f(0, 0, 0);
        tile = te;
        interpretControllerData();
    }

    // get the depth for the screen BLOCK in the argument to calculate BB
    public float[] getDepthForBlock(TileEntityFlatScreen screen){
        BlockPos offset = MultiBlockOffset(screen);

        switch(tile.getData().tiltAxis) {
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

    public static BlockPos MultiBlockOffset(TileEntityFlatScreen screen){
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

    // calculate screen size for current panel tilt
    private void interpretControllerData(){
        FlatScreen data = tile.getData();
        renderOpaqueModel = data.opaque;

        // calculate display size, rotation and renderoffset based on the panels tilt
        switch(data.tiltAxis){
            case X:
                topLeft = topRight = FlatScreen.precision * data.screenDepthTop;
                bottomLeft = bottomRight = FlatScreen.precision * data.screenDepthBottom;

                tri = new Triangle(topRight - bottomRight, screenCountY);
                displayHeight = tri.c;
                tiltRotationVector = new Vector3f(topLeft < bottomLeft ? tri.alpha : -tri.alpha, 0, 0);

                factorAC = Math.abs(tri.c / (float) screenCountY);
                break;

            case Y:
                bottomLeft = topLeft = FlatScreen.precision * data.screenDepthLeft;
                bottomRight = topRight = FlatScreen.precision * data.screenDepthRight;

                tri = new Triangle(topRight - topLeft, screenCountX);
                displayWidth = tri.c;
                tiltRotationVector = new Vector3f(0, topRight < topLeft ? tri.alpha : -tri.alpha, 0);

                factorAC = Math.abs(tri.c / (float) screenCountX);
                break;

            default:
            case NONE:
                bottomLeft = bottomRight = topLeft = topRight = FlatScreen.precision * data.screenDepthTop;
        }

        tiltRenderOffset = new Vec3d(0, 0, -1 + topLeft);
    }

    public static HashSet<TileEntityFlatScreen> getScreens(TileEntityFlatScreen screen){

        HashSet<TileEntityFlatScreen> screens = new HashSet<>();
        Iterator<Screen> screenSet = screen.screens().iterator();
        while(screenSet.hasNext()){
            Screen s = screenSet.next();
            if(s instanceof TileEntityFlatScreen)
                screens.add((TileEntityFlatScreen) s);
        }

        return screens;
    }


}