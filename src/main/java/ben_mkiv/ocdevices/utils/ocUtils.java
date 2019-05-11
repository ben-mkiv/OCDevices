package ben_mkiv.ocdevices.utils;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ocUtils {
    public static Map<String, Object> analyze(World world, BlockPos pos){
        IBlockState state = world.getBlockState(pos);

        Map<String, Object> result = new HashMap<>();

        Block block = state.getBlock();

        result.put("color", state.getMapColor(world, pos).colorValue);
        result.put("hardness", state.getBlockHardness(world, pos));
        result.put("harvestLevel", block.getHarvestLevel(state));
        result.put("metadata", state.getBlock().getMetaFromState(state));
        result.put("name", block.getRegistryName().toString());

        HashMap<String, Object> properties = new HashMap<>();

        for(Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()){
            properties.put(entry.getKey().getName(), entry.getValue());
        }

        result.put("properties", properties);

        return result;
    }
}
