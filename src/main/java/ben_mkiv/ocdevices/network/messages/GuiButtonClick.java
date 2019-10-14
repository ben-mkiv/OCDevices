package ben_mkiv.ocdevices.network.messages;

import ben_mkiv.guitoolkit.network.buttonClick;
import ben_mkiv.ocdevices.common.tileentity.IButtonCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiButtonClick extends buttonClick {
    BlockPos tilePosition;
    int dimension;

    public GuiButtonClick(){}

    @SideOnly(Side.CLIENT)
    public GuiButtonClick(GuiButton button, TileEntity tile){
        super(button);
        tilePosition = tile.getPos();
        dimension = tile.getWorld().provider.getDimension();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        tilePosition = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        dimension = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(tilePosition.getX());
        buf.writeInt(tilePosition.getY());
        buf.writeInt(tilePosition.getZ());
        buf.writeInt(dimension);
    }

    public static class Handler implements IMessageHandler<GuiButtonClick, IMessage> {
        @Override
        public IMessage onMessage(GuiButtonClick message, MessageContext ctx) {
            if(message.dimId == Integer.MIN_VALUE)
                return null;
            World world = DimensionManager.getWorld(message.dimId);
            if (world.isRemote)
                return null;

            TileEntity tileEntity = world.getTileEntity(message.tilePosition);

            if(tileEntity == null)
                return null;

            if(tileEntity instanceof IButtonCallback)
                ((IButtonCallback) tileEntity).buttonCallback(message.nbt);

            return null;
        }

    }
}