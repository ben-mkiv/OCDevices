package ben_mkiv.ocdevices.client.models;

import ben_mkiv.ocdevices.OCDevices;
import li.cil.oc.client.renderer.block.ServerRackModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

public class ModelServerRack extends ServerRackModel {
    static ArrayList<ResourceLocation> texturesServer = new ArrayList<>();
    static ArrayList<ResourceLocation> texturesRack = new ArrayList<>();
    static TextureAtlasSprite[] spritesServer = new TextureAtlasSprite[6];
    static TextureAtlasSprite[] spritesRack = new TextureAtlasSprite[6];



    static {
        texturesServer.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/top.png"));
        texturesServer.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/top.png"));
        texturesServer.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/side.png"));
        texturesServer.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/side.png"));
        texturesServer.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/side.png"));
        texturesServer.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/side.png"));

        texturesRack.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/top.png"));
        texturesRack.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/top.png"));
        texturesRack.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/front.png"));
        texturesRack.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/front.png"));
        texturesRack.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/front.png"));
        texturesRack.add(new ResourceLocation(OCDevices.MOD_ID, "textures/blocks/rack/front.png"));
    }

    public ModelServerRack(IBakedModel parent){
        super(parent);

        int i=0;
        for(ResourceLocation rl : texturesServer)
            spritesServer[i++] = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(rl.toString());

        i=0;
        for(ResourceLocation rl : texturesRack)
            spritesRack[i++] = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(rl.toString());
    }

    @Override
    public TextureAtlasSprite[] serverRackTexture() {
        return spritesRack;
    }

    @Override
    public TextureAtlasSprite[] serverTexture() {
        return spritesServer;
    }

}
