package ben_mkiv.ocdevices.common.matrix;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class MatrixWidget {

    public static int matrixResolution = 16;

    public int x = 0, y = 0, width = 0, height = 0, backgroundColor = 0x0, foregroundColor = 0xFFFFFF, fontSize = 12;
    public double depth;

    public enum textAlignments { LEFT, CENTER, RIGHT }

    public textAlignments textAlignment = textAlignments.LEFT;

    private String name, label = "";

    public MatrixWidget(String name){
        this.name = name;
    }

    public MatrixWidget(NBTTagCompound nbt){
        this("");
        readFromNBT(nbt);
    }

    public boolean hovered(Vec3d hitVec){
        if(hitVec.x < (double) x/matrixResolution || hitVec.x > (double) (x + width)/matrixResolution)
            return false;

        if(1 - hitVec.y < (double) y/matrixResolution || 1 - hitVec.y > (double) (y + height)/matrixResolution)
            return false;

        return true;
    }

    public String getName(){
        return name;
    }

    public String getLabel(){
        return label;
    }

    public void setLabel(String newLabel){
        label = newLabel;
    }

    public void setName(String newName){
        name = newName;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("w", width);
        tag.setInteger("h", height);
        tag.setDouble("depth", depth);
        tag.setString("name", name);
        tag.setString("label", label);

        tag.setInteger("bg", backgroundColor);
        tag.setInteger("fg", foregroundColor);
        tag.setInteger("fontSize", fontSize);

        tag.setInteger("textAlign", textAlignment.ordinal());

        return tag;
    }

    public void readFromNBT(NBTTagCompound tag){
        x = tag.getInteger("x");
        y = tag.getInteger("y");
        width = tag.getInteger("w");
        height = tag.getInteger("h");
        depth = tag.getDouble("depth");
        name = tag.getString("name");
        label = tag.getString("label");

        backgroundColor = tag.getInteger("bg");
        foregroundColor = tag.getInteger("fg");
        fontSize = tag.getInteger("fontSize");

        textAlignment = textAlignments.values()[tag.getInteger("textAlign")];
    }
}

