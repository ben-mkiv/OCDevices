package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMultiblockDisplay;
import net.minecraft.client.renderer.*;

public class RenderFlatScreen extends RenderMultiblockDisplay {

    @Override
    public void render(TileEntityMultiblockDisplay screen, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        if(!shouldRender(screen) || !(screen instanceof TileEntityFlatScreen))
            return;

        preRender(x, y, z, screen.getHelper());

        if(screen.shouldRenderContent())
            renderContent(screen, ((TileEntityFlatScreen) screen).buffer().renderWidth(), ((TileEntityFlatScreen) screen).buffer().renderHeight());

        postRender();
    }

    @Override
    public void renderScreenContent(TileEntityMultiblockDisplay screen){
        renderFlatScreenContent((TileEntityFlatScreen) screen);
    }

    private void renderFlatScreenContent(TileEntityFlatScreen screen){
        screen.buffer().renderText();

        if(screen.getHelper().opacity != 100){
            // render the whole text again on the backside as the oc method disables the depth mask
            GlStateManager.translate(0, 0, .0002);
            GlStateManager.disableCull();
            screen.buffer().renderText();
            GlStateManager.enableCull();
        }
    }




}
