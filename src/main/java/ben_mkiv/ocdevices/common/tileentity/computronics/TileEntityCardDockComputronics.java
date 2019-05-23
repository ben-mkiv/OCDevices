package ben_mkiv.ocdevices.common.tileentity.computronics;

import ben_mkiv.ocdevices.common.tileentity.TileEntityCardDock;
import net.minecraft.util.EnumFacing;
import pl.asie.computronics.api.audio.IAudioSource;

public class TileEntityCardDockComputronics extends TileEntityCardDock implements IAudioSource {
    public TileEntityCardDockComputronics(){
        super();
    }

    @Override
    public int getSourceId(){
        if(components.get(0).node() instanceof IAudioSource)
            return ((IAudioSource) components.get(0).node().host()).getSourceId();

        return -1;
    }

    @Override
    public boolean connectsAudio(EnumFacing var1){
        //if(FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT))
        //    return components.get(0) instanceof pl.asie.computronics
        // do something about clients... (if the carddock gui gets opened they disconnect cables)
        return components.get(0).node() != null && components.get(0).node().host() instanceof IAudioSource;
    }
}
