package ben_mkiv.ocdevices.common.matrix;

import ben_mkiv.ocdevices.common.tileentity.TileEntityMatrix;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

public class buttonLuaObject extends widgetLuaObject {
    public buttonLuaObject(){} //required by oc to load the object

    public buttonLuaObject(int widgetIndex, TileEntityMatrix tileEntityMatrix){
        super(widgetIndex, tileEntityMatrix);
    }

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


    @Callback(doc = "function(String:name):boolean sets the widget label", direct = true)
    public Object[] setLabel(Context context, Arguments args){
        if(!args.isString(0))
            return new Object[]{ false, "parameter has to be a valid string" };

        get().setLabel(args.checkString(0));
        markDirty();
        return new Object[]{ true };
    }

}
