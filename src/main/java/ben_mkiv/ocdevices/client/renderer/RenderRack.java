package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.client.models.ModelServerRack;
import ben_mkiv.ocdevices.common.blocks.BlockRack;
import ben_mkiv.ocdevices.common.tileentity.TileEntityRack;
import li.cil.oc.api.event.RackMountableRenderEvent;
import li.cil.oc.client.renderer.block.ServerRackModel;
import li.cil.oc.common.tileentity.Rack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class RenderRack extends TileEntitySpecialRenderer<TileEntityRack> {
    static ServerRackModel model;

    @Override
    public void render(TileEntityRack rack, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState state = BlockRack.DEFAULTITEM.getDefaultState();

        if(model == null)
            model = new ModelServerRack(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(BlockRack.DEFAULTITEM.getDefaultState()));

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);

        GlStateManager.rotate(rack.yaw().getHorizontalAngle(), 0, 1, 0);

        GlStateManager.scale(1, -1, -1);
        GlStateManager.translate(-0.5D, -0.5D, -0.5D);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        /*
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        for(EnumFacing face : EnumFacing.values()) {
            if (face.equals(EnumFacing.NORTH))
                GlStateManager.translate(0, 0, 0.4);

            for (BakedQuad quad : model.parent().getQuads(state, face, 0))
                LightUtil.renderQuadColor(vertexbuffer, quad, 0);

            if (face.equals(EnumFacing.NORTH))
                GlStateManager.translate(0, 0, -0.4);
        }
        tessellator.draw();
        */

        BlockModelRenderer renderer = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();
        renderer.renderModel(getWorld(), model, state, rack.getPos(), vertexbuffer, false, 0);

        for(int slot=0; slot < rack.getSizeInventory(); slot++)
            renderSlot(rack, slot);

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color) {
        for (int j = quads.size(), i = 0; i < j; ++i)
            LightUtil.renderQuadColor(renderer, quads.get(i), color);
    }

    private void renderSlot(Rack rack, int rackSlot) {
        if(rack.getStackInSlot(rackSlot).isEmpty())
            return;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);

        for(BakedQuad quad : model.bakeQuads(model.Servers()[rackSlot], model.serverTexture(), 0))
            LightUtil.renderQuadColor(vertexbuffer, quad, 0);

        tessellator.draw();

        float v0 = 0.125F + (float)rackSlot * 0.1875F;
        float v1 = v0 + 0.1875F;

        MinecraftForge.EVENT_BUS.post(new RackMountableRenderEvent.TileEntity(rack, rackSlot, rack.lastData()[rackSlot], v0, v1));
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

}
