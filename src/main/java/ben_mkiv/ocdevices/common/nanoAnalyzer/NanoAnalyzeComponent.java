package ben_mkiv.ocdevices.common.nanoAnalyzer;

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.nanoAnalyzer.capability.IanalyzeCapability;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.WirelessEndpoint;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import techguns.items.guns.GenericGun;

import java.util.*;

import static net.minecraftforge.energy.CapabilityEnergy.ENERGY;

public class NanoAnalyzeComponent implements WirelessEndpoint {
    private Entity hostEntity;
    private UUID uuid;
    private HashMap<Packet, WirelessEndpoint> queue = new HashMap<>();


    public NanoAnalyzeComponent(Entity entity, IanalyzeCapability cap, ItemStack stack){
        hostEntity = entity;
        uuid = cap.getUniqueId();
        connect();

        send(cap.getBoundToHost().length() > 0 ? cap.getBoundToHost() : null, cap.getNetworkPort(), 10, 20, new Object[]{ "NanoAnalyzer", "initialized", stack.getDisplayName() });
    }

    private void broadcast(int port, int range, int ttl, Object[] data){
        send(null, port, range, ttl, data);
    }

    private void send(String destination, int port, int range, int ttl, Object[] data){
        if(data == null)
            return;

        Network.sendWirelessPacket(this, range, new li.cil.oc.server.network.Network.Packet(uuid.toString(), destination, port, data, ttl));
    }

    public void connect(){
        Network.joinWirelessNetwork(this);
    }

    public void disconnect(IanalyzeCapability cap){
        send(cap.getBoundToHost().length() > 0 ? cap.getBoundToHost() : null, cap.getNetworkPort(), 10, 10, new Object[]{ "NanoAnalyzer", "shutdown" });
        Network.leaveWirelessNetwork(this);
    }

    public void update(Entity entity, IanalyzeCapability cap, ItemStack stack){
        hostEntity = entity;
        Network.updateWirelessNetwork(this);

        HashMap<Packet, WirelessEndpoint> processQueue = new HashMap<>(queue);

        for(Map.Entry<Packet, WirelessEndpoint> entry : processQueue.entrySet()){
            if(cap.getBoundToHost().length() != 0 && !cap.getBoundToHost().equals(entry.getKey().source())){
                // remove packet if we are bound to a another source address
                queue.remove(entry.getKey(), entry.getValue());
                continue;
            }

            for(Object data : entry.getKey().data()) {
                ArrayList<Object> reply = new ArrayList<>();

                String cmd = "";

                if(data instanceof String)
                    cmd = (String) data;
                else if(data instanceof byte[])
                    cmd = new String((byte[]) data);

                reply.add("[" + cmd + "]");

                boolean answer = true;

                switch(cmd){
                    case "ping":
                        reply.add(stack.getDisplayName());
                        break;

                    case "getDurability":
                        reply.add(stack.getMaxDamage()-stack.getItemDamage());
                        reply.add(stack.getMaxDamage());
                        break;

                    case "getName":
                        reply.add(stack.getDisplayName());
                        break;

                    case "getAmmo":
                        if(OCDevices.Techguns && stack.getItem() instanceof GenericGun){
                            reply.add(((GenericGun) stack.getItem()).getAmmoLeft(stack));
                            reply.add(((GenericGun) stack.getItem()).getClipsize());
                        }
                        break;

                    case "getEnergy":
                        if(stack.hasCapability(ENERGY, null)){
                            IEnergyStorage energyStorage = stack.getCapability(ENERGY, null);
                            reply.add(energyStorage.getEnergyStored());
                            reply.add(energyStorage.getMaxEnergyStored());
                        }
                        break;

                    default:
                        // some dirty fix for now to avoid one item sending stuff around with another item -.-
                        answer = false;
                }

                if(answer)
                    send(entry.getKey().source(), cap.getNetworkPort(), 10, 20, reply.toArray());
            }


            queue.remove(entry.getKey(), entry.getValue());
        }
    }

    public int x(){
        return hostEntity.getPosition().getX();
    }

    public int y(){
        return hostEntity.getPosition().getY();
    }

    public int z(){
        return hostEntity.getPosition().getZ();
    }

    public World world(){
        return hostEntity.getEntityWorld();
    }

    public void receivePacket(Packet var1, WirelessEndpoint var2){
        // ignore packets which aren't for us
        if(var1.destination() != null && !var1.destination().equals(uuid.toString()))
            return;

        // ignore packets which we sent
        if(var1.source() != null && var1.source().equals(uuid.toString()))
            return;

        queue.put(var1, var2);
    }

}
