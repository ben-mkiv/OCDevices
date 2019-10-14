package ben_mkiv.ocdevices.network;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.network.messages.GuiButtonClick;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class OCDevicesNetwork {

    public static SimpleNetworkWrapper channel;

    public static void init(){
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(OCDevices.MOD_ID.toLowerCase());
        int id=-1;

        channel.registerMessage(GuiButtonClick.Handler.class, GuiButtonClick.class, id++, Side.SERVER);
    }


    public static void sendToNearPlayers(IMessage msg, TileEntity entity){
        sendToNearPlayers(msg, entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getX(), entity.getWorld());
    }

    public static void sendToNearPlayers(IMessage msg, double x, double y, double z, World world){
        if(msg == null)
            return;

        if(world == null)
            return;

        if(channel == null)
            return;

        int dimension = world.provider.getDimension();

        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        int viewDistance = players.getViewDistance()*16;

        for(EntityPlayerMP player : players.getPlayers())
            if(player != null && dimension == player.world.provider.getDimension())
               if(player.getDistance(x, y, z) <= viewDistance)
                    channel.sendTo(msg, player);
    }

    public static void sendTo(IMessage msg, EntityPlayer player){
        channel.sendTo(msg, (EntityPlayerMP) player);
    }

    public static void sendToNearPlayers(IMessage msg, Entity entity){
        sendToNearPlayers(msg, entity.getPosition().getX(), entity.getPosition().getY(), entity.getPosition().getZ(), entity.world);
    }


}