package ben_mkiv.ocdevices.common.tileentity;

import li.cil.oc.api.API;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.TileEntityEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "opencomputers")
public class ocComponentTE extends TileEntityEnvironment implements ITickable, ManagedEnvironment {
    private boolean addedToNetwork;
    private final String name;
    private Visibility visibility;

    public ocComponentTE(String name, Visibility visibility) {
        this.name = name;
        this.visibility = visibility;
        setupNode();
    }

    @Override
    public void load(NBTTagCompound nbt) {
        if(nbt.hasKey("visibility"))
            this.visibility = Visibility.values()[nbt.getInteger("visibility")];

        if(nbt.hasKey("node"))
            node.load(nbt.getCompoundTag("node"));
    }

    @Override
    public void save(NBTTagCompound nbt) {
        setupNode();
        if(node == null)
            return;

        NBTTagCompound nodeTag = new NBTTagCompound();
        node.save(nodeTag);
        nbt.setTag("node", nodeTag);

        nbt.setInteger("visibility", visibility.ordinal());
    }

    private void setupNode(){
        if(this.node() == null || this.node().network() == null)
            this.node = API.network.newNode(this, visibility).withComponent(getComponentName()).withConnector().create();
            //this.node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).create();
    }

    @Override
    public boolean canUpdate(){
        return false;
    }

    public void sendComputerSignal(String eventType, String name){
        if(node == null) return;
        node.sendToReachable("computer.signal", eventType.toLowerCase(), name);
    }

    public String getComponentName() { return this.name; }

    @Override
    public void update() {
        if (!addedToNetwork) {
            addedToNetwork = true;
            Network.joinOrCreateNetwork(this);
        }
    }
}

