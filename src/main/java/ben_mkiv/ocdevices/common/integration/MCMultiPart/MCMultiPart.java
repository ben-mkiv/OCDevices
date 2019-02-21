package ben_mkiv.ocdevices.common.integration.MCMultiPart;

/* thanks to mekanism authors, grabbed most of the MCMP stuff from their sources */

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.blocks.BlockCase_ibm_5150;
import ben_mkiv.ocdevices.common.blocks.BlockFlatScreen;
import ben_mkiv.ocdevices.common.blocks.BlockKeyboard;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.blocks.CaseMultipart_ibm_5150;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.blocks.FlatScreenMultipart;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.blocks.KeyboardMultipart;
import ben_mkiv.ocdevices.common.tileentity.TileEntityCase_ibm_5150;
import ben_mkiv.ocdevices.common.tileentity.TileEntityFlatScreen;
import ben_mkiv.ocdevices.common.tileentity.TileEntityKeyboard;
import mcmultipart.api.addon.IMCMPAddon;
import mcmultipart.api.addon.MCMPAddon;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.event.DrawMultipartHighlightEvent;
import mcmultipart.api.multipart.IMultipartRegistry;
import mcmultipart.api.ref.MCMPCapabilities;
import mcmultipart.api.slot.IPartSlot;
import mcmultipart.api.world.IMultipartBlockAccess;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@MCMPAddon
public class MCMultiPart implements IMCMPAddon {
    HashSet<BlockMultipart> multipartBlocks = new HashSet<>();

    @Override
    public void registerParts(IMultipartRegistry registry) {
        MinecraftForge.EVENT_BUS.register(this);

        multipartBlocks.add(new KeyboardMultipart());
        multipartBlocks.add(new FlatScreenMultipart());
        //multipartBlocks.add(new CaseMultipart_ibm_5150()); //disabled, todo: figure out powering/ticking

        for(BlockMultipart block : multipartBlocks){
            registry.registerPartWrapper(block.getBlock(), block);
            registry.registerStackWrapper(block.getBlock());
        }
    }

    @SubscribeEvent
    public void onAttachTile(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof TileEntityKeyboard)
            register(event, BlockKeyboard.NAME);
        else if (event.getObject() instanceof TileEntityFlatScreen)
            register(event, BlockFlatScreen.NAME);
        else if (event.getObject() instanceof TileEntityCase_ibm_5150)
            register(event, BlockCase_ibm_5150.NAME);

    }

    private void register(AttachCapabilitiesEvent<TileEntity> e, String id) {
        e.addCapability(new ResourceLocation(OCDevices.MOD_ID, id), new ICapabilityProvider() {
            private TileEntityMultipart tile = null;

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == MCMPCapabilities.MULTIPART_TILE;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                if (capability == MCMPCapabilities.MULTIPART_TILE) {
                    if (tile == null) {
                        if (e.getObject() instanceof TileEntityKeyboard)
                            tile = new TileEntityMultipart(e.getObject());
                        else if (e.getObject() instanceof TileEntityFlatScreen)
                            tile = new TileEntityMultipartTicking(e.getObject());
                        else if (e.getObject() instanceof TileEntityCase_ibm_5150)
                            tile = new TileEntityMultipartTicking(e.getObject());
                    }

                    return MCMPCapabilities.MULTIPART_TILE.cast(tile);
                }

                return null;
            }
        });
    }

    private boolean isMultipartBlock(Block blockIn){
        for(BlockMultipart blockMultipart : multipartBlocks)
            if (blockMultipart.getBlock().equals(blockIn))
                return true;

        return false;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void drawBlockHighlightEvent(DrawMultipartHighlightEvent ev) {
        IBlockState state = ev.getPartInfo().getState();

        if(!isMultipartBlock(state.getBlock()))
            return;

        EntityPlayer player = ev.getPlayer();
        @SuppressWarnings("deprecation")
        AxisAlignedBB bb = state.getBlock().getSelectedBoundingBox(state, ev.getPartInfo().getPartWorld(), ev.getPartInfo().getPartPos());
        //NB rendering code copied from MCMultipart
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * ev.getPartialTicks();
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * ev.getPartialTicks();
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * ev.getPartialTicks();
        RenderGlobal.drawSelectionBoundingBox(bb.grow(0.002).offset(-x, -y, -z), 0.0F, 0.0F, 0.0F, 0.4F);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        ev.setCanceled(true);
    }

    private static IMultipartContainer getMultipartContainer(TileEntity tile){
        if(tile == null || tile.isInvalid())
            return null;

        if (tile instanceof IMultipartContainer)
            return (IMultipartContainer) tile;

        if(tile.getWorld() instanceof IMultipartBlockAccess) {
            TileEntity worldTile = getRealWorldAccess(tile).getTileEntity(tile.getPos());
            if(!tile.equals(worldTile))
                return getMultipartContainer(worldTile);
        }

        return null;
    }

    static HashMap<IPartSlot, TileEntity> getMCMPTiles(TileEntity mcmpTile) {
        HashMap<IPartSlot, TileEntity> list = new HashMap<>();

        IMultipartContainer container = getMultipartContainer(mcmpTile);

        if(container == null)
            return list;

        for(Map.Entry<IPartSlot, ? extends IPartInfo> part : container.getParts().entrySet()){
            if(part.getValue() == null)
                continue;

            TileEntity te = part.getValue().getTile().getTileEntity();

            if(te.equals(mcmpTile))
                continue; //dont add the main tile to its own content list

            list.put(part.getKey(), te);
        }

        return list;
    }

    private static IBlockAccess getRealWorldAccess(TileEntity mcmpTile){
        return ((IMultipartBlockAccess) mcmpTile.getWorld()).getActualWorld();
    }

    public static World getRealWorld(TileEntity mcmpTile){
        return getRealWorldAccess(mcmpTile).getTileEntity(mcmpTile.getPos()).getWorld();
    }
}
