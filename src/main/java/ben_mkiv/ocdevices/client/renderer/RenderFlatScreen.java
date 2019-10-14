package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.common.tileentity.TileEntityMultiblockDisplay;
import ben_mkiv.rendertoolkit.client.OptifineHelper;
import ben_mkiv.rendertoolkit.common.widgets.IRenderableWidget;
import ben_mkiv.rendertoolkit.renderToolkit;
import net.minecraft.client.Minecraft;

import static ben_mkiv.rendertoolkit.surface.ClientSurface.vec3d000;

public class RenderFlatScreen extends RenderMultiblockDisplay {

    @Override
    public void render(TileEntityMultiblockDisplay screen, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        if(!shouldRender(screen) || !(screen instanceof TileEntityFlatScreen))
            return;

        preRender(x, y, z, screen.getHelper());

        if(screen.shouldRenderContent()){
            renderContent(screen, ((TileEntityFlatScreen) screen).buffer().renderWidth(), ((TileEntityFlatScreen) screen).buffer().renderHeight(), partialTicks);
        }


        postRender();
    }

    @Override
    public void renderScreenContent(TileEntityMultiblockDisplay screen, float partialTicks){
        boolean optifineSpecialCase = renderToolkit.Optifine && OptifineHelper.isShaderActive();

        if(optifineSpecialCase)
            OptifineHelper.releaseShaderProgram();

        IRenderableWidget widget = ((TileEntityFlatScreen) screen).widgetWorld.getRenderable();
        widget.render(Minecraft.getMinecraft().player, vec3d000, ~0L);

        if(optifineSpecialCase)
            OptifineHelper.rebindShaderProgram();
    }



}
