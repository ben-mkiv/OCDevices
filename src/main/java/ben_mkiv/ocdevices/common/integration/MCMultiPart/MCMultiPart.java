package ben_mkiv.ocdevices.common.integration.MCMultiPart;

/* thanks to mekanism authors, grabbed most of the MCMP stuff from their sources */

import ben_mkiv.ocdevices.OCDevices;
import ben_mkiv.ocdevices.common.blocks.BlockKeyboard;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.blocks.KeyboardMultipart;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.tileentity.TileEntityKeyboardMultipart;
import ben_mkiv.ocdevices.common.tileentity.TileEntityKeyboard;
import mcmultipart.api.addon.IMCMPAddon;
import mcmultipart.api.addon.MCMPAddon;
import mcmultipart.api.event.DrawMultipartHighlightEvent;
import mcmultipart.api.multipart.IMultipartRegistry;
import mcmultipart.api.ref.MCMPCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


@MCMPAddon
public class MCMultiPart implements IMCMPAddon {
    public static KeyboardMultipart keyboardMultipart;

    @Override
    public void registerParts(IMultipartRegistry registry) {
        MinecraftForge.EVENT_BUS.register(this);
        registry.registerPartWrapper(BlockKeyboard.DEFAULTITEM, keyboardMultipart = new KeyboardMultipart());
        registry.registerStackWrapper(BlockKeyboard.DEFAULTITEM);
    }

    @SubscribeEvent
    public void onAttachTile(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof TileEntityKeyboard)
            register(event, "keyboard");
    }

    private void register(AttachCapabilitiesEvent<TileEntity> e, String id) {
        e.addCapability(new ResourceLocation(OCDevices.MOD_ID, id), new ICapabilityProvider() {
            private TileEntityKeyboardMultipart tile;

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == MCMPCapabilities.MULTIPART_TILE;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                if (capability == MCMPCapabilities.MULTIPART_TILE) {
                    if (tile == null && e.getObject() instanceof TileEntityKeyboard)
                        tile = new TileEntityKeyboardMultipart((TileEntityKeyboard) e.getObject());

                    return MCMPCapabilities.MULTIPART_TILE.cast(tile);
                }

                return null;
            }
        });
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void drawBlockHighlightEvent(DrawMultipartHighlightEvent ev) {
        IBlockState state = ev.getPartInfo().getState();
        if (!state.getBlock().equals(BlockKeyboard.DEFAULTITEM))
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
}
