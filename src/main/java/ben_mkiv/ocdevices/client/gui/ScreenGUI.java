package ben_mkiv.ocdevices.client.gui;

import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import li.cil.oc.client.gui.Screen;
import net.minecraft.tileentity.TileEntity;
import scala.Function0;
import scala.runtime.AbstractFunction0;

public class ScreenGUI extends Screen {
    public static TileEntity screen;

    public ScreenGUI(){
        super(((TileEntityFlatScreen) screen).buffer(), true, ScreenCallbacks.hasKeyboard, ScreenCallbacks.hasPower);
    }

    static class ScreenCallbacks {
        static Function0<Object> hasPower = new AbstractFunction0<Object>() {
            @Override
            public Object apply() {
                return screen instanceof TileEntityFlatScreen && ((TileEntityFlatScreen) screen).powered();
            }
        };

        static Function0<Object> hasKeyboard = new AbstractFunction0<Object>() {
            @Override
            public Object apply() {
                return screen instanceof TileEntityFlatScreen && ((TileEntityFlatScreen) screen).hasKeyboard();
            }
        };
    }
}
