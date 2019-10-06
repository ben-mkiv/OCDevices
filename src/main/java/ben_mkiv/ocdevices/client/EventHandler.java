package ben_mkiv.ocdevices.client;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.client.renderer.RenderRack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.HashMap;

import static ben_mkiv.ocdevices.OCDevices.MOD_ID;

public class EventHandler {
    public static void onTextureStitch(TextureStitchEvent.Pre evt){
        TextureAtlasSprite textureServer = evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/opencomputers/rack_server"));

        // opencomputers servers (creative, tier1, tier2, tier3)
        addRackTexture("opencomputers:component", 12, textureServer);
        addRackTexture("opencomputers:component", 13, textureServer);
        addRackTexture("opencomputers:component", 14, textureServer);
        addRackTexture("opencomputers:component", 15, textureServer);

        // opencomputers terminal server
        addRackTexture("opencomputers:component", 19, evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/opencomputers/rack_terminal_server")));

        // opencomputers disk drive
        addRackTexture("opencomputers:component", 20, evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/opencomputers/rack_disk_drive")));

        if(OCDevices.Computronics){
            // computronics boom board
            addRackTexture("computronics:oc_parts", 11, evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/computronics/boomboard")));

            // computronics rack capacitor
            addRackTexture("computronics:oc_parts", 12, evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/computronics/rack_capacitor")));

            // computronics switch board
            addRackTexture("computronics:oc_parts", 13, evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/computronics/switchboard")));

            // computronics light board
            addRackTexture("computronics:oc_parts", 10, "default", evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/computronics/lightboard_1")));
            addRackTexture("computronics:oc_parts", 10, "mode2", evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/computronics/lightboard_2")));
            addRackTexture("computronics:oc_parts", 10, "mode3", evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/computronics/lightboard_3")));
            addRackTexture("computronics:oc_parts", 10, "mode4", evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/computronics/lightboard_4")));
            addRackTexture("computronics:oc_parts", 10, "mode5", evt.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/rack/computronics/lightboard_5")));
        }
    }

    private static void addRackTexture(String itemName, int itemMeta, TextureAtlasSprite sprite){
        addRackTexture(itemName, itemMeta, "default", sprite);
    }

    private static void addRackTexture(String itemName, int itemMeta, String state, TextureAtlasSprite sprite){
        String id = itemName + ":" + itemMeta;
        HashMap<String, TextureAtlasSprite> map = new HashMap<>();
        if(RenderRack.textures.containsKey(id))
            map.putAll(RenderRack.textures.get(id));

        map.put(state, sprite);
        RenderRack.textures.put(id, map);
    }
}
