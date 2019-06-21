package ben_mkiv.ocdevices.common.blocks;

import com.google.common.base.Optional;
import li.cil.oc.common.Tier;
import net.minecraft.block.properties.IProperty;

import java.util.Arrays;
import java.util.Collection;

public class caseTierProperty implements IProperty<Integer> {
    private static Collection<Integer> allowedValues = Arrays.asList(new Integer[]{ Tier.One(), Tier.Two(), Tier.Three(), Tier.Four() });

    @Override
    public String getName() {
        return "tier";
    }

    @Override
    public Class<Integer> getValueClass(){
        return Integer.class;
    }

    @Override
    public Collection<Integer> getAllowedValues(){
        return allowedValues;
    }

    @Override
    public Optional<Integer> parseValue(String value){
        int val = Integer.parseInt(value);
        return allowedValues.contains(val) ? Optional.of(val) : Optional.absent();
    }

    @Override
    public String getName(Integer value){
        return String.valueOf(value);
    }
}
