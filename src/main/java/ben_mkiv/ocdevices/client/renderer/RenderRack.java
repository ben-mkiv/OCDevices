package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.client.models.ModelRack;
import ben_mkiv.ocdevices.common.blocks.BlockRack;
import ben_mkiv.ocdevices.common.tileentity.TileEntityRack;
import li.cil.oc.api.event.RackMountableRenderEvent;
import li.cil.oc.client.renderer.block.ServerRackModel;
import li.cil.oc.common.tileentity.Rack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.nio.Buffer;

public class RenderRack extends TileEntitySpecialRenderer<TileEntityRack> {
    private static ModelRack modelRack = new ModelRack();
    private static ServerRackModel ocModel;

    @Override
    public void render(TileEntityRack rack,  double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        if(ocModel == null)
            ocModel = new ServerRackModel(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(BlockRack.DEFAULTITEM.getDefaultState()));

        //todo: remove this line when done with modeling
        modelRack = new ModelRack();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableBlend();

        // render rack case in world
        modelRack.render(rack, 0.0625F, null);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.scale(-1, 1, -0.95);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.translate(0, 0, 0.05);
        for(int rackSlot=0; rackSlot < rack.getSizeInventory(); rackSlot++) {
            renderSlot(rack, rackSlot, null);
        }

        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }

    private void renderSlot(Rack rack, int rackSlot, BufferBuilder buffer) {
        if(rack.getStackInSlot(rackSlot).isEmpty())
            return;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        renderServerBox(rack, rackSlot, buffer);
        renderStatusLED(rack, rackSlot);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private void renderServerBox(Rack rack, int rackSlot, BufferBuilder bufferBuilder){
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RackMountableRenderEvent.Block event = new RackMountableRenderEvent.Block(rack, rackSlot, rack.lastData()[rackSlot], EnumFacing.DOWN);
        MinecraftForge.EVENT_BUS.post(event);

        Tessellator tess = Tessellator.getInstance();
        bufferBuilder = tess.getBuffer();

        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        TextureAtlasSprite[] rackTextures = ocModel.serverTexture();

        // override the default server texture if the event holds reference to a custom texture
        if(event.getFrontTextureOverride() != null)
            rackTextures[2] = event.getFrontTextureOverride();

        // finally put the data to the buffer
        for(BakedQuad quad : ocModel.bakeQuads(ocModel.Servers()[rackSlot], rackTextures, 0)) {
            LightUtil.renderQuadColor(bufferBuilder, quad, 0);
        }

        tess.draw();

    }

    private void renderStatusLED(Rack rack, int rackSlot){
        float v0 = 0.125F + (float)rackSlot * 1F/16 * 3;
        float v1 = v0 + 0.1875F;

        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.translate(-1, -1, 0);

        MinecraftForge.EVENT_BUS.post(new RackMountableRenderEvent.TileEntity(rack, rackSlot, rack.lastData()[rackSlot], v0, v1));
    }

}
