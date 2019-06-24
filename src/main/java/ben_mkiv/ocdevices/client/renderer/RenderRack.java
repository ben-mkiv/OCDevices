package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.client.models.ModelRack;
import ben_mkiv.ocdevices.common.blocks.BlockRack;
import ben_mkiv.ocdevices.common.tileentity.TileEntityRack;
import li.cil.oc.api.component.RackMountable;
import li.cil.oc.api.event.RackMountableRenderEvent;
import li.cil.oc.client.renderer.block.ServerRackModel;
import li.cil.oc.common.component.TerminalServer;
import li.cil.oc.common.tileentity.Rack;
import li.cil.oc.server.component.DiskDriveMountable;
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
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLSync;

import java.awt.*;

public class RenderRack extends TileEntitySpecialRenderer<TileEntityRack> {
    private static ModelRack modelRack = new ModelRack();
    private static ServerRackModel ocModel;
    private static TextureAtlasSprite[] rackTextures;

    public static TextureAtlasSprite serverTex, terminalServerTex, diskDriveTex, ocServerTex;

    public static void init(){
        if(ocModel != null)
            return;

        ocModel = new ServerRackModel(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(BlockRack.DEFAULTITEM.getDefaultState()));
        rackTextures = ocModel.serverTexture();
        ocServerTex = rackTextures[2];
    }

    @Override
    public void render(TileEntityRack rack,  double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        init();

        //remove this line when done with modeling
        //modelRack = new ModelRack();

        GlStateManager.pushMatrix();

        GlStateManager.pushAttrib();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        switch(rack.yaw()) {
            case EAST: GlStateManager.rotate(-90, 0, 1, 0); break;
            case SOUTH: GlStateManager.rotate(180, 0, 1, 0); break;
            case WEST: GlStateManager.rotate(90, 0, 1, 0); break;
        }
        GlStateManager.translate(-0.5, -0.5, -0.5);



        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.scale(-1, 1, -0.95);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.translate(0, 0, 0.05);
        for(int rackSlot=0; rackSlot < rack.getSizeInventory(); rackSlot++) {
            renderSlot(rack, rackSlot, null);
        }
        GlStateManager.popMatrix();

        // render rack case in world
        modelRack.render(rack, 0.0625F, null);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private void renderSlot(TileEntityRack rack, int rackSlot, BufferBuilder buffer) {
        if(rack.getStackInSlot(rackSlot).isEmpty())
            return;

        GlStateManager.pushMatrix();

        renderServerBox(rack, rackSlot, buffer);
        renderStatusLED(rack, rackSlot);

        GlStateManager.popMatrix();
    }

    private void renderServerBox(TileEntityRack rack, int rackSlot, BufferBuilder bufferBuilder){

        int colorValue = rack.getServerColor(rackSlot);
        Color color = new Color(colorValue);

        Tessellator tess = Tessellator.getInstance();
        bufferBuilder = tess.getBuffer();

        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        if(colorValue != -14869215){ // != black
            rackTextures[2] = getMountableTexture(rack.getMountable(rackSlot));

            GlStateManager.color(1f/255 * color.getRed(), 1f/255 * color.getGreen(), 1f/255 * color.getBlue(), 1);
        }
        else { // use original oc server textures if the mountable is black colored
            RackMountableRenderEvent.Block event = new RackMountableRenderEvent.Block(rack, rackSlot, rack.lastData()[rackSlot], EnumFacing.DOWN);
            MinecraftForge.EVENT_BUS.post(event);

            if(event.getFrontTextureOverride() != null)
                rackTextures[2] = event.getFrontTextureOverride();
            else
                rackTextures[2] = ocServerTex;
        }

        // finally put the data to the buffer
        for(BakedQuad quad : ocModel.bakeQuads(ocModel.Servers()[rackSlot], rackTextures, colorValue)) {
            LightUtil.renderQuadColor(bufferBuilder, quad, colorValue);
        }

        tess.draw();
        GlStateManager.color(1, 1, 1, 1);
    }

    private TextureAtlasSprite getMountableTexture(RackMountable mountable){
        if(mountable instanceof TerminalServer)
            return terminalServerTex;
        else if(mountable instanceof DiskDriveMountable) {
            //((DiskDriveMountable) mountable).getStackInSlot(0).isEmpty();
            return diskDriveTex;
        }
        else
            return serverTex;
    }

    private void renderStatusLED(TileEntityRack rack, int rackSlot){
        float v0 = 0.125F + (float)rackSlot * 1F/16 * 3;
        float v1 = v0 + 0.1875F;

        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.translate(-1, -1, 0.031);
        GlStateManager.scale(1, 1, -0.2);

        MinecraftForge.EVENT_BUS.post(new RackMountableRenderEvent.TileEntity(rack, rackSlot, rack.getMountableData(rackSlot), v0, v1));
    }

}
