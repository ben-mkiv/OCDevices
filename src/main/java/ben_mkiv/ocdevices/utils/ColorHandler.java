package ben_mkiv.ocdevices.utils;

import ben_mkiv.ocdevices.common.tileentity.ColoredTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ColorHandler implements IBlockColor {
    public ColorHandler() {}

    @Override
    public int colorMultiplier(@Nonnull IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex){
        if(pos == null)
            return 0;

        TileEntity te = worldIn.getTileEntity(pos);

        int color = te instanceof ColoredTile ? ((ColoredTile) te).getColor() : 0;

        if(color >= 0 && color <= 15)
            color = EnumDyeColor.values()[color].getColorValue();

        return color;
    }
}
