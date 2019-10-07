package ben_mkiv.ocdevices.client.renderer;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.client.models.ModelBug;
import ben_mkiv.ocdevices.common.entity.BugEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.client.registry.IRenderFactory;


@SideOnly(Side.CLIENT)
public class RenderBug extends Render<BugEntity> {
    public static Factory FACTORY = new Factory();

   // private static ModelBug model;

    private static final ResourceLocation textureLoc = new ResourceLocation(OCDevices.MOD_ID + ":textures/model/bug.png");

    public RenderBug(RenderManager manager){
        super(manager);
        //if(model == null)
        //    model = new ModelBug();
    }

    @Override
    public void doRender(BugEntity entity, double x, double y, double z, float entityYaw, float partialTicks){
        GlStateManager.disableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        //GlStateManager.rotate(entity.prevRotationYaw + ((entityYaw-entity.prevRotationYaw) * partialTicks), 0, 1, 0);

        //model.render(entity, entityYaw, partialTicks);

        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
    }

    @Override
    protected ResourceLocation getEntityTexture(BugEntity par1EntityLiving) {
        return textureLoc;
    }

    public static class Factory implements IRenderFactory<BugEntity> {
        @Override
        public Render<? super BugEntity> createRenderFor(RenderManager manager) {
            return new RenderBug(manager);
        }
    }




}
