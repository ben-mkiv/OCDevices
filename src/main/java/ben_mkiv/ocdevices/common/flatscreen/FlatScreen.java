package ben_mkiv.ocdevices.common.flatscreen;

import net.minecraft.block.BlockLog;
import net.minecraft.nbt.NBTTagCompound;

public class FlatScreen {
    public static final int maxScreenDepth = 32;
    public static final float precision = 1f/maxScreenDepth;
    public static final int maxPadding = 16;

    public int screenDepthTop = maxScreenDepth/8;
    public int screenDepthBottom = maxScreenDepth/8;
    public int screenDepthLeft = maxScreenDepth/8;
    public int screenDepthRight = maxScreenDepth/8;

    public int padding = 0;
    public int opacity = 100;

    public boolean frameless = false;

    public BlockLog.EnumAxis tiltAxis = BlockLog.EnumAxis.NONE;

    public FlatScreen(){}

    public FlatScreen(NBTTagCompound tag){
        readFromNBT(tag);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        tag.setInteger("top", screenDepthTop);
        tag.setInteger("bottom", screenDepthBottom);
        tag.setInteger("left", screenDepthLeft);
        tag.setInteger("right", screenDepthRight);
        tag.setInteger("tilt", tiltAxis.ordinal());
        tag.setInteger("padding", padding);
        tag.setInteger("opacity", opacity);
        tag.setBoolean("frameless", frameless);

        return tag;
    }

    public NBTTagCompound readFromNBT(NBTTagCompound tag){
        screenDepthTop = tag.getInteger("top");
        screenDepthBottom = tag.getInteger("bottom");
        screenDepthLeft = tag.getInteger("left");
        screenDepthRight = tag.getInteger("right");
        tiltAxis = BlockLog.EnumAxis.values()[tag.getInteger("tilt")];
        opacity = tag.getInteger("opacity");
        setPadding(tag.getInteger("padding"));
        setFrameless(tag.getBoolean("frameless"));

        return tag;
    }

    public Object[] setDepth(int depth, String side){
        depth = Math.max(0, Math.min(depth, maxScreenDepth));

        switch(side){
            case "top":
                screenDepthTop = depth;
                tiltAxis = screenDepthTop != screenDepthBottom ? BlockLog.EnumAxis.X : BlockLog.EnumAxis.NONE;
                screenDepthRight = screenDepthLeft = Math.min(screenDepthRight, screenDepthLeft);
                break;
            case "bottom":
                screenDepthBottom = depth;
                tiltAxis = screenDepthTop != screenDepthBottom ? BlockLog.EnumAxis.X : BlockLog.EnumAxis.NONE;
                screenDepthRight = screenDepthLeft = Math.min(screenDepthRight, screenDepthLeft);
                break;
            case "left":
                screenDepthLeft = depth;
                tiltAxis = screenDepthLeft != screenDepthRight ? BlockLog.EnumAxis.Y : BlockLog.EnumAxis.NONE;
                screenDepthBottom = screenDepthTop = Math.min(screenDepthBottom, screenDepthTop);
                break;
            case "right":
                screenDepthRight = depth;
                tiltAxis = screenDepthLeft != screenDepthRight ? BlockLog.EnumAxis.Y : BlockLog.EnumAxis.NONE;
                screenDepthBottom = screenDepthTop = Math.min(screenDepthBottom, screenDepthTop);
                break;
            case "all":
                tiltAxis = BlockLog.EnumAxis.NONE;
                screenDepthTop = screenDepthBottom = screenDepthLeft = screenDepthRight = depth;
                break;
            default:
                return new Object[]{ false, "invalid side" };
        }

        return new Object[]{ true, "depth set for side '" + side + "'" };
    }

    public void setPadding(int value){ padding = Math.max(0, Math.min(value, maxPadding)); }

    public void setFrameless(boolean state){
        frameless = state;
    }

    public void setOpaque(int value){
        opacity = value;
    }
}
