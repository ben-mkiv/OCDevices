package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.common.tileentity.TileEntityCase;
import li.cil.oc.client.renderer.tileentity.RenderUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderCase extends TileEntitySpecialRenderer<TileEntityCase> {

    public static class statusLED{
        final Vec3d ledLocation;
        final float ledWidth, ledHeight;
        final EnumFacing ledSide;

        public statusLED(Vec3d pos, float width, float height, EnumFacing side){
            ledLocation = pos;
            ledWidth = width;
            ledHeight = height;
            ledSide = side;
        }

        public void render(Color color){
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buff = tess.getBuffer();

            float r = 1f/255*color.getRed();
            float g = 1f/255*color.getGreen();
            float b = 1f/255*color.getBlue();

            GlStateManager.pushMatrix();
            GlStateManager.translate(ledLocation.x, ledLocation.y, ledLocation.z);

            switch(ledSide){
                case UP: GlStateManager.rotate(-90, 1, 0, 0); break;
                case DOWN: GlStateManager.rotate(90, 1, 0, 0); break;
                case SOUTH: GlStateManager.rotate(180, 0, 1, 0); break;
                case EAST: GlStateManager.rotate(-90, 0, 1, 0); break;
                case WEST: GlStateManager.rotate(90, 0, 1, 0); break;
            }

            buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buff.pos(ledWidth, ledHeight, 0).color(r, g, b, 0.9f).endVertex();
            buff.pos(0, ledHeight, 0).color(r, g, b, 0.9f).endVertex();
            buff.pos(0, 0, 0).color(r, g, b, 0.9f).endVertex();
            buff.pos(ledWidth, 0, 0).color(r, g, b, 0.9f).endVertex();

            tess.draw();
            GlStateManager.popMatrix();
        }
    }

    private final statusLED powerLED, hddLED;

    public RenderCase(statusLED power, statusLED hdd){
        powerLED = power;
        hddLED = hdd;
    }

    @Override
    public void render(TileEntityCase computer, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushAttrib();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        switch(computer.yaw()) {
            case EAST: GlStateManager.rotate(90, 0, 1, 0); break;
            case NORTH: GlStateManager.rotate(180, 0, 1, 0); break;
            case WEST: GlStateManager.rotate(-90, 0, 1, 0); break;
        }

        setLightmapDisabled(true);

        if (computer.isRunning()) {
            powerLED.render(new Color(0, 255, 0));
            if (System.currentTimeMillis() - computer.lastFileSystemAccess() < 400 && computer.getWorld().rand.nextDouble() > 0.1) {
                hddLED.render(new Color(0, 255, 0));
            }
        }
        else if (computer.hasErrored() && RenderUtil.shouldShowErrorLight(computer.hashCode())) {
            powerLED.render(new Color(255, 0, 0));
        }

        setLightmapDisabled(false);

        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.popAttrib();
    }

}
