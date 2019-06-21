package ben_mkiv.ocdevices.common.matrix;

import ben_mkiv.ocdevices.common.tileentity.TileEntityMatrix;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

public class itemLuaObject extends buttonLuaObject {
    public itemLuaObject(){} //required by oc to load the object

    public itemLuaObject(int widgetIndex, TileEntityMatrix tileEntityMatrix){
        super(widgetIndex, tileEntityMatrix);
    }

    @Callback(doc = "function(String:name):boolean sets the widget item", direct = true)
    public Object[] setItem(Context context, Arguments args){
        if(!args.isString(0))
            return new Object[]{ false, "parameter has to be a valid string" };

        boolean succeed = ((ItemWidget) get()).setItem(args.checkString(0), args.optInteger(1, 0));
        markDirty();
        return new Object[]{ succeed };
    }

}
