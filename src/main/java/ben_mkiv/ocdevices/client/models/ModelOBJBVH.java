package ben_mkiv.ocdevices.client.models;

import ben_mkiv.commons0815.chickenbones.Matrix4;
import ben_mkiv.ocdevices.common.entity.BugEntity;
import ben_mkiv.ocdevices.libs.bvhParser.Node;
import ben_mkiv.ocdevices.libs.bvhParser.Skeleton;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.BufferBuilder;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import javax.vecmath.Vector4f;
import java.util.HashMap;
import java.util.function.Function;

abstract public class ModelOBJBVH {

    private OBJModel model;

    public HashMap<String, BonedPart> parts = new HashMap<>();
    public HashMap<String, IBakedModel> modelParts = new HashMap<>();
    public HashMap<String, OBJModel.OBJState> IModelParts = new HashMap<>();


    abstract void render(BugEntity entity, float yaw, float partialTicks);

    public static class BonedPart {
        Node bone;
        OBJModel.OBJState iModel;
        IBakedModel model;
        public Quaternion rotation = new Quaternion();
        public Vec3d position;

        public BonedPart(IBakedModel iBakedModel, OBJModel.OBJState iModelState, Node boneNode){
            this.bone = boneNode;
            this.iModel = iModelState;
            this.model = iBakedModel;
            this.position = new Vec3d(boneNode.getOffset().x, boneNode.getOffset().y, boneNode.getOffset().z);
        }

        public Quaternion getRotation(){
            return rotation;
        }

        public Vec3d getPosition(){
            return position;
        }

        public Node getBone(){
            return bone;
        }

        public IBakedModel getModel(){
            return model;
        }
    }

    public ModelOBJBVH(ResourceLocation objFile, ResourceLocation bvhFile){
        Skeleton skeleton;
        try {
            System.out.println("loading object model from "+objFile.toString());
            model = (OBJModel) OBJLoader.INSTANCE.loadModel(objFile);
        } catch(Exception ex){
            System.out.println("couldnt load object model from "+objFile.toString());
            return;
        }

        try {
            System.out.println("loading bvh skeleton nodes from "+bvhFile.toString());
            skeleton = new Skeleton(bvhFile);
        } catch(Exception ex){
            System.out.println("couldnt load bvh data from "+bvhFile.toString());
            return;
        }

        if(skeleton.getRootNode() == null){
            System.out.println("missing bvh root");
            return;
        }

        // apply bone offset to their groups
        for(OBJModel.Group group : model.getMatLib().getGroups().values()){
            String name = group.getName();

            Node bone = null;
            for(Node node : skeleton.getRootNode().getChildrens()) {
                if (node.getName().equals(name)) {
                    bone = node;
                    break;
                }
            }

            if(bone == null)
                continue;

            Vector4f offset = new Vector4f(bone.getOffset());

            for(OBJModel.Face face : group.getFaces()){
                for(OBJModel.Vertex v : face.getVertices()){
                    Vector4f tmpV = v.getPos();
                    tmpV.sub(offset);
                    v.setPos(tmpV);
                }
            }
        }

        // load and bake
        loadModelGroups();

        // cache data
        for (Node node : skeleton.getRootNode().getChildrens()) {
            String name = node.getName();
            if(modelParts.containsKey(name)){
                parts.put(name, new BonedPart(modelParts.get(name), IModelParts.get(name), node));
                System.out.println("added boned part: "+node.getName());
            }
        }


    }

    static public void renderPart(BonedPart part){
        GlStateManager.pushMatrix();
        GlStateManager.translate(part.getPosition().x, part.getPosition().y, part.getPosition().z);
        GlStateManager.rotate(part.getRotation());


        renderPart(part.getModel());
        GlStateManager.popMatrix();
    }

    static public void renderPart(IBakedModel part){
        if(part == null)
            return;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buff = tessellator.getBuffer();
        buff.begin(GL11.GL_QUADS, Attributes.DEFAULT_BAKED_FORMAT);

        for (BakedQuad bakedQuad : part.getQuads(null, null, 0))
            LightUtil.renderQuadColor(buff, bakedQuad, -1);

        tessellator.draw();
    }


    public void loadModelGroups() {
        for (String groupName : model.getMatLib().getGroups().keySet()) {
            IModelParts.put(groupName, getPartIModelState(groupName));
            modelParts.put(groupName, getPartBakedModel(groupName));
        }
    }

    public IBakedModel getPartBakedModel(String groupName){
        if(!IModelParts.containsKey(groupName))
            IModelParts.put(groupName, getPartIModelState(groupName));

        return model.bake(IModelParts.get(groupName), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
    }

    public OBJModel.OBJState getPartIModelState(String groupName){
        return new OBJModel.OBJState(ImmutableList.of(groupName), false, model.getDefaultState());
    }

    private static Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
        public TextureAtlasSprite apply(ResourceLocation location) {
            return DummyAtlasTexture.instance;
        }
    };

    private static class DummyAtlasTexture extends TextureAtlasSprite {
        public static DummyAtlasTexture instance = new DummyAtlasTexture();

        DummyAtlasTexture() {
            super("dummy");
        }
    }
}

