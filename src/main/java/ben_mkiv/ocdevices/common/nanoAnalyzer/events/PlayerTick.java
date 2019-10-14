package ben_mkiv.ocdevices.common.nanoAnalyzer.events;

import ben_mkiv.ocdevices.common.nanoAnalyzer.capability.IanalyzeCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.UUID;

import static ben_mkiv.ocdevices.common.nanoAnalyzer.capability.analyzeCapability.ANALYZE;

public class PlayerTick {
    private static HashMap<UUID, HashMap<UUID, ItemStack>> playerItems = new HashMap<>();
    private static int playerIndex = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent evt) {
        if(evt.player.world.isRemote)
            return;

        if(FMLCommonHandler.instance().getMinecraftServerInstance() == null)
            return;

        int i=0;
        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            if(i == playerIndex)
                updatePlayer(player);

            tickPlayer(player);
            i++;
        }

        playerIndex++;
        if (playerIndex >= FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().size())
            playerIndex = 0;
    }

    private static void tickPlayer(EntityPlayer player){
        if(!playerItems.containsKey(player.getUniqueID()))
            return;

        for(ItemStack stack : playerItems.get(player.getUniqueID()).values()){
            IanalyzeCapability cap = stack.getCapability(ANALYZE, null);
            cap.updateEvent(player, stack);
        }
    }

    private static void updatePlayer(EntityPlayer player){
        HashMap<UUID, ItemStack> items = new HashMap<>();

        HashMap<UUID, ItemStack> itemsOld = new HashMap<>();

        if(playerItems.containsKey(player.getUniqueID())){
            itemsOld = new HashMap<>(playerItems.get(player.getUniqueID()));
        }

        for(int slot=0; slot < player.inventory.getSizeInventory(); slot++) {
            ItemStack stack = player.inventory.getStackInSlot(slot);

            if(!stack.hasCapability(ANALYZE, null))
                continue;

            IanalyzeCapability cap = stack.getCapability(ANALYZE, null);

            if(cap.isAttached()) {
                items.put(cap.getUniqueId(), stack);
                cap.setActive(player, stack);
            }

            itemsOld.remove(cap.getUniqueId());
        }

        playerItems.remove(player.getUniqueID());

        for(ItemStack stack : itemsOld.values()){
            if(!stack.hasCapability(ANALYZE, null))
                continue;

            IanalyzeCapability cap = stack.getCapability(ANALYZE, null);
            cap.setInactive();
        }

        if(items.size() > 0)
            playerItems.put(player.getUniqueID(), items);
    }


}
