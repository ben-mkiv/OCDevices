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
    private int dim;
    private TileEntityMatrix matrix;

    int index = -1;

    public widgetLuaObject(int widgetIndex, TileEntityMatrix tileEntityMatrix){
        index = widgetIndex;
        position = tileEntityMatrix.getPos();
        dim = tileEntityMatrix.getWorld().provider.getDimension();
        matrix = tileEntityMatrix;
    }


    @Callback(doc = "function():boolean removes the widget", direct = true)
    public Object[] remove(Context context, Arguments args){
        getTile().widgets.remove(index);
        markDirty();
        return new Object[]{ !getTile().widgets.containsKey(index) };
    }

    @Callback(doc = "function(String:name):boolean sets the widget name", direct = true)
    public Object[] setName(Context context, Arguments args){
        if(!args.isString(0))
            return new Object[]{ false, "parameter has to be a valid string" };

        get().setName(args.checkString(0));
        markDirty();
        return new Object[]{ true };
    }

    public widgetLuaObject(){} //required by oc to load the object


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
