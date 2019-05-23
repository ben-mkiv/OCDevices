package ben_mkiv.ocdevices.common.matrix;

import ben_mkiv.ocdevices.common.tileentity.TileEntityMatrix;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Value;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class widgetLuaObject implements Value {
    private BlockPos position;
    private int dim, index = -1;
    private TileEntityMatrix matrix;

    public widgetLuaObject(int widgetIndex, TileEntityMatrix tileEntityMatrix){
        index = widgetIndex;
        position = tileEntityMatrix.getPos();
        dim = tileEntityMatrix.getWorld().provider.getDimension();
        matrix = tileEntityMatrix;
    }

    public widgetLuaObject(){} //required by oc to load the object

    @Callback(doc = "function(Integer:fontSize):boolean sets the fontsize", direct = true)
    public Object[] setFontSize(Context context, Arguments args){
        if(!args.isInteger(0))
            return new Object[]{ false, "integer font size required as first parameter" };

        get().fontSize = args.checkInteger(0);
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(Integer:color):boolean sets the background color", direct = true)
    public Object[] setBackground(Context context, Arguments args){
        if(!args.isInteger(0))
            return new Object[]{ false, "integer color required as first parameter" };

        get().backgroundColor = args.checkInteger(0);
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(String:position):boolean sets the text alignment to left, center or right)", direct = true)
    public Object[] setTextalignment(Context context, Arguments args){
        if(!args.isString(0))
            return new Object[]{ false, "alignment name required as first parameter" };

        get().textAlignment = MatrixWidget.textAlignments.valueOf(args.checkString(0).toUpperCase());
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(Integer:color):boolean sets the foreground color", direct = true)
    public Object[] setForeground(Context context, Arguments args){
        if(!args.isInteger(0))
            return new Object[]{ false, "integer color required as first parameter" };

        get().foregroundColor = args.checkInteger(0);
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(Integer:x, Integer:y):boolean sets the widget x/y position", direct = true)
    public Object[] setPosition(Context context, Arguments args){
        if(!args.isInteger(0))
            return new Object[]{ false, "x position has to be a valid integer value" };
        if(!args.isInteger(1))
            return new Object[]{ false, "y position has to be a valid integer value" };

        get().x = args.checkInteger(0);
        get().y = args.checkInteger(1);
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(Integer:x):boolean sets the widget x position", direct = true)
    public Object[] setX(Context context, Arguments args){
        if(!args.isInteger(0))
            return new Object[]{ false, "x position has to be a valid integer value" };

        get().x = args.checkInteger(0);
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(Integer:y):boolean sets the widget y position", direct = true)
    public Object[] setY(Context context, Arguments args){
        if(!args.isInteger(0))
            return new Object[]{ false, "y position has to be a valid integer value" };

        get().y = args.checkInteger(0);
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(Integer:width, Integer:height):boolean sets the widget width/height", direct = true)
    public Object[] setSize(Context context, Arguments args){
        if(!args.isInteger(0))
            return new Object[]{ false, "width has to be a valid integer value" };
        if(!args.isInteger(1))
            return new Object[]{ false, "height has to be a valid integer value" };

        get().width = args.checkInteger(0);
        get().height = args.checkInteger(1);
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(Integer:width):boolean sets the widget width", direct = true)
    public Object[] setWidth(Context context, Arguments args){
        if(!args.isInteger(0))
            return new Object[]{ false, "width has to be a valid integer value" };

        get().width = args.checkInteger(0);
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(Integer:height):boolean sets the widget height", direct = true)
    public Object[] setHeight(Context context, Arguments args){
        if(!args.isInteger(0))
            return new Object[]{ false, "height has to be a valid integer value" };

        get().height = args.checkInteger(0);
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(Double:depth):boolean sets the widget depth", direct = true)
    public Object[] setDepth(Context context, Arguments args){
        if(!args.isDouble(0))
            return new Object[]{ false, "depth has to be a valid integer/double value" };

        get().depth = args.checkDouble(0);
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(String:name):boolean sets the widget name", direct = true)
    public Object[] setName(Context context, Arguments args){
        if(!args.isString(0))
            return new Object[]{ false, "parameter has to be a valid string" };

        get().setName(args.checkString(0));
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function(String:name):boolean sets the widget label", direct = true)
    public Object[] setLabel(Context context, Arguments args){
        if(!args.isString(0))
            return new Object[]{ false, "parameter has to be a valid string" };

        get().setLabel(args.checkString(0));
        markDirty();
        return new Object[]{ true };
    }

    @Callback(doc = "function():boolean removes the widget", direct = true)
    public Object[] remove(Context context, Arguments args){
        getTile().widgets.remove(index);
        markDirty();
        return new Object[]{ !getTile().widgets.containsKey(index) };
    }

    public void markDirty(){
        matrix.markDirty();
    }

    public TileEntityMatrix getTile(){
        if(matrix == null) try {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dim);
            TileEntity tile = world.getTileEntity(position);
            if (tile instanceof TileEntityMatrix)
                matrix = (TileEntityMatrix) tile;
        } catch(Exception e){ e.printStackTrace(); }

        return matrix;
    }

    public MatrixWidget get(){
        return matrix.widgets.get(index);
    }

    public Object apply(Context var1, Arguments var2){
        return null;
    }

    public void unapply(Context var1, Arguments var2){}

    public Object[] call(Context var1, Arguments var2){
        return new Object[]{};
    }

    public void dispose(Context var1){}

    public void load(NBTTagCompound nbt){
        index = nbt.getInteger("index");
        position = NBTUtil.getPosFromTag(nbt.getCompoundTag("position"));
        dim = nbt.getInteger("dim");
    }

    public void save(NBTTagCompound nbt){
        nbt.setInteger("index", index);
        nbt.setTag("position", NBTUtil.createPosTag(position));
        nbt.setInteger("dim", dim);
    }
}
