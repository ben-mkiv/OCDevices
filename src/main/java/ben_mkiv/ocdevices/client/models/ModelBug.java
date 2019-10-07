package ben_mkiv.ocdevices.client.models;
/*
import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.entity.BugEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ModelBug extends ModelOBJBVH {
    private static ResourceLocation obj = new ResourceLocation(OCDevices.MOD_ID, "models/entity/bug.obj");
    private static ResourceLocation bvh = new ResourceLocation(OCDevices.MOD_ID, "models/entity/bug.bvh");

    public ModelBug(){
        super(obj, bvh);
    }


    public void render(BugEntity entity, float yaw, float partialTicks){
        GlStateManager.pushMatrix();
        yaw = entity.rotationYaw + entity.rotationYawHead;
        GlStateManager.rotate(-entity.rotationYaw, 0, 1, 0);

        renderPart(parts.get("Body"));

        int legs = 6;
        for(int leg=1; leg <= legs; leg++) {

            BonedPart part;

            float f = MathHelper.cos(entity.limbSwing * 0.666F * 2.0F) * entity.limbSwingAmount;

            part = parts.get("LeftLeg" + leg);
            part.rotation.setX(1f/5f * (leg % 2 == 0 ? 1 : -1)* f);
            renderPart(part);

            part = parts.get("RightLeg" + leg);
            part.rotation.setX(1f/5f * (leg % 2 != 0 ? 1 : -1) * f);
            renderPart(part);
        }

        GlStateManager.popMatrix();
    }


}
*/