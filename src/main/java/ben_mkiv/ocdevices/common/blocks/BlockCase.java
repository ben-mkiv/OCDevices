package ben_mkiv.ocdevices.common.blocks;

import ben_mkiv.ocdevices.OCDevices;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Case;
import net.minecraft.block.state.IBlockState;

public class BlockCase extends Case {
    public static final int tier = Tier.Three();

    public BlockCase(String caseName){
        super(tier);
        setRegistryName(OCDevices.MOD_ID, caseName);
        setUnlocalizedName(caseName);
        setCreativeTab(OCDevices.creativeTab);
    }

    @Deprecated
    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Deprecated
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @Deprecated
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }


    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

}
