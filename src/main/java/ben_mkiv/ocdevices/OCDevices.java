package ben_mkiv.ocdevices;

import ben_mkiv.ocdevices.client.EventHandler;
import ben_mkiv.ocdevices.client.renderer.*;
import ben_mkiv.ocdevices.common.GuiHandler;
import ben_mkiv.ocdevices.common.blocks.*;
import ben_mkiv.ocdevices.common.bridge.Bridge;
import ben_mkiv.ocdevices.common.nanoAnalyzer.capability.IanalyzeCapability;
import ben_mkiv.ocdevices.common.nanoAnalyzer.capability.analyzeProvider;
import ben_mkiv.ocdevices.common.nanoAnalyzer.capability.analyzeStorage;
import ben_mkiv.ocdevices.common.drivers.FlatScreenDriver;
import ben_mkiv.ocdevices.common.drivers.RedstoneDriver;
import ben_mkiv.ocdevices.common.integration.MCMultiPart.MCMultiPart;
import ben_mkiv.ocdevices.common.items.*;
import ben_mkiv.ocdevices.common.nanoAnalyzer.NanoAnalyzer;
import ben_mkiv.ocdevices.common.tileentity.*;
import ben_mkiv.ocdevices.config.Config;
import ben_mkiv.ocdevices.manual.Manual;
import ben_mkiv.ocdevices.network.OCDevicesNetwork;
import ben_mkiv.ocdevices.proxy.CommonProxy;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.EnvironmentProvider;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.logging.Logger;

@Mod(
        modid = OCDevices.MOD_ID,
        name = OCDevices.MOD_NAME,
        version = BuildInfo.versionNumber + "-" + BuildInfo.buildNumber,
        dependencies = "required-after:opencomputers;" +
                "required-after:commons0815@[1.3.2,);" +
                "required-after:rendertoolkit;" +
                "after:rtfm",
        guiFactory = OCDevices.GUIFACTORY
)
public class OCDevices {

    public static final String MOD_ID = BuildInfo.modID;
    public static final String MOD_NAME = BuildInfo.modName;

    public static final String GUIFACTORY = "ben_mkiv.ocdevices.config.ConfigGUI";

    public static final Logger logger = Logger.getLogger(MOD_NAME);
    static final boolean verbose = false;
    public static final boolean debug = false;

    public static boolean MCMultiPart = false;
    public static boolean Optifine = false;
    public static boolean Albedo = false;
    public static boolean Computronics = false;
    public static boolean Techguns = false;

    public static boolean experimental = true;

    @Mod.Instance(MOD_ID)
    public static OCDevices INSTANCE;

    @SidedProxy(clientSide = "ben_mkiv.ocdevices.proxy.ClientProxy", serverSide = "ben_mkiv.ocdevices.proxy.CommonProxy")
    public static CommonProxy proxy;

    static HashSet<ItemStack> modItems = new HashSet<>();
    static HashSet<Block> modBlocks = new HashSet<>();

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        Config.preInit();
        Manual.preInit();

        modBlocks.add(BlockCardDock.DEFAULTITEM = new BlockCardDock());
        modBlocks.add(BlockFlatScreen.DEFAULTITEM = new BlockFlatScreen());
        modBlocks.add(BlockKeyboard.DEFAULTITEM = new BlockKeyboard());
        modBlocks.add(BlockRecipeDictionary.DEFAULTITEM = new BlockRecipeDictionary());
        modBlocks.add(BlockRedstone.DEFAULTITEM = new BlockRedstone());

        modBlocks.add(BlockItemBench.DEFAULTITEM = new BlockItemBench());

        modBlocks.add(BlockCase_ibm_5150.DEFAULTITEM = new BlockCase_ibm_5150());
        modBlocks.add(BlockCase_next.DEFAULTITEM = new BlockCase_next());
        modBlocks.add(BlockCase_slim_oc.DEFAULTITEM = new BlockCase_slim_oc());
        modBlocks.add(BlockCase_workstation.DEFAULTITEM = new BlockCase_workstation());

        modBlocks.add(BlockRack.DEFAULTITEM = new BlockRack());

        if(experimental) {
            modBlocks.add(BlockMatrix.DEFAULTITEM = new BlockMatrix());
            modBlocks.add(BlockBridge.DEFAULTITEM = new BlockBridge());
            modBlocks.add(BlockDatabaseAdapter.DEFAULTITEM = new BlockDatabaseAdapter());
        }

        modItems.add(UpgradeBlastResistance.DEFAULT_STACK = new ItemStack(new UpgradeBlastResistance()));
        modItems.add(UpgradeTier2.DEFAULT_STACK = new ItemStack(new UpgradeTier2()));
        modItems.add(UpgradeTier3.DEFAULT_STACK = new ItemStack(new UpgradeTier3()));
        modItems.add(UpgradeTier4.DEFAULT_STACK = new ItemStack(new UpgradeTier4()));


        for(Item item : Manual.items)
            modItems.add(new ItemStack(item));

        proxy.preinit();

        Optifine = Loader.isModLoaded("optifine");
        Albedo = Loader.isModLoaded("albedo");
        Computronics = Loader.isModLoaded("computronics");
        Techguns = Loader.isModLoaded("techguns");

        if(Loader.isModLoaded("mcmultipart"))
            new MCMultiPart();


        CapabilityManager.INSTANCE.register(IanalyzeCapability.class, new analyzeStorage(), analyzeProvider::new);

        NanoAnalyzer.registerEvents();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());

        li.cil.oc.api.Driver.add((EnvironmentProvider) FlatScreenDriver.driver);
        li.cil.oc.api.Driver.add((DriverItem) FlatScreenDriver.driver);

        li.cil.oc.api.Driver.add((EnvironmentProvider) RedstoneDriver.driver);
        li.cil.oc.api.Driver.add((DriverItem) RedstoneDriver.driver);

        OCDevicesNetwork.init();

        proxy.registerBlockColorHandlers();
    }

    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onRegisterModels(ModelRegistryEvent event) {
            if(verbose) logger.info("register models");

            OBJLoader.INSTANCE.addDomain(MOD_ID);

            for(ItemStack itemStack : modItems)
                ModelLoader.setCustomModelResourceLocation(itemStack.getItem(), 0, new ModelResourceLocation(itemStack.getItem().getRegistryName().toString()));

            for(Block block : modBlocks)
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName().toString()));
            
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlatScreen.class, new RenderFlatScreen());

            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRack.class, new RenderRack());

            if(experimental){
                ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMatrix.class, new RenderMatrix());
            }

            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCase_next.class, new RenderCase(TileEntityCase_next.getPowerLED(), TileEntityCase_next.getStatusLED()));
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCase_slim_oc.class, new RenderCase(TileEntityCase_slim_oc.getPowerLED(), TileEntityCase_slim_oc.getStatusLED()));
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCase_ibm_5150.class, new RenderCase(TileEntityCase_ibm_5150.getPowerLED(), TileEntityCase_ibm_5150.getStatusLED()));
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCase_workstation.class, new RenderCase(TileEntityCase_workstation.getPowerLED(), TileEntityCase_workstation.getStatusLED()));

            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemBench.class, new RenderItemBench());

            ModelLoader.setCustomStateMapper(BlockCase_next.DEFAULTITEM, new StateMap.Builder().ignore(BlockCase_next.caseTier).build());
            ModelLoader.setCustomStateMapper(BlockCase_slim_oc.DEFAULTITEM, new StateMap.Builder().ignore(BlockCase_slim_oc.caseTier).build());
            ModelLoader.setCustomStateMapper(BlockCase_ibm_5150.DEFAULTITEM, new StateMap.Builder().ignore(BlockCase_ibm_5150.caseTier).build());
            ModelLoader.setCustomStateMapper(BlockCase_workstation.DEFAULTITEM, new StateMap.Builder().ignore(BlockCase_workstation.caseTier).build());
        }

        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            if(verbose) logger.info("register items");

            for(ItemStack itemStack : modItems)
                event.getRegistry().register(itemStack.getItem());

            for(Block block : modBlocks)
                event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }

        @SubscribeEvent
        public static void addBlocks(RegistryEvent.Register<Block> event) {
            if(verbose) logger.info("register blocks");

            for(Block block : modBlocks)
                event.getRegistry().register(block);


            GameRegistry.registerTileEntity(ben_mkiv.ocdevices.common.tileentity.TileEntityCardDock.class, new ResourceLocation(MOD_ID, BlockCardDock.NAME));

            GameRegistry.registerTileEntity(TileEntityFlatScreen.class, new ResourceLocation(MOD_ID, BlockFlatScreen.NAME));
            GameRegistry.registerTileEntity(TileEntityKeyboard.class, new ResourceLocation(MOD_ID, BlockKeyboard.NAME));
            GameRegistry.registerTileEntity(TileEntityRecipeDictionary.class, new ResourceLocation(MOD_ID, BlockRecipeDictionary.NAME));
            GameRegistry.registerTileEntity(TileEntityRedstone.class, new ResourceLocation(MOD_ID, BlockRedstone.NAME));
            GameRegistry.registerTileEntity(TileEntityItemBench.class, new ResourceLocation(MOD_ID, BlockItemBench.NAME));

            GameRegistry.registerTileEntity(TileEntityCase_ibm_5150.class, new ResourceLocation(MOD_ID, BlockCase_ibm_5150.NAME));
            GameRegistry.registerTileEntity(TileEntityCase_next.class, new ResourceLocation(MOD_ID, BlockCase_next.NAME));
            GameRegistry.registerTileEntity(TileEntityCase_slim_oc.class, new ResourceLocation(MOD_ID, BlockCase_slim_oc.NAME));
            GameRegistry.registerTileEntity(TileEntityCase_workstation.class, new ResourceLocation(MOD_ID, BlockCase_workstation.NAME));

            GameRegistry.registerTileEntity(TileEntityRack.class, new ResourceLocation(MOD_ID, BlockRack.NAME));


            if(experimental) {
                GameRegistry.registerTileEntity(TileEntityBridge.class, new ResourceLocation(MOD_ID, BlockBridge.NAME));
                GameRegistry.registerTileEntity(TileEntityDatabaseAdapter.class, new ResourceLocation(MOD_ID, BlockDatabaseAdapter.NAME));
                GameRegistry.registerTileEntity(TileEntityMatrix.class, new ResourceLocation(MOD_ID, BlockMatrix.NAME));
            }
        }


        @SubscribeEvent
        public static void registerEntities(RegistryEvent.Register<EntityEntry> event){
            //EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, BugEntity.NAME), BugEntity.class, BugEntity.NAME, 0, INSTANCE, 80, 3, true, 0x000000, 0x00FFFF);
        }



        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onTextureStitch(TextureStitchEvent.Pre evt){
            EventHandler.onTextureStitch(evt);
        }

        @Mod.EventHandler
        public static void onServerStopped(FMLServerStoppedEvent event){
            // clear static server side caches
            Bridge.onServerStopped();
        }
    }


    public static final CreativeTabs creativeTab = new CreativeTabs(MOD_NAME) {
        @Override
        @SideOnly(Side.CLIENT)
        public @Nonnull ItemStack createIcon() {
            return new ItemStack(Item.getItemFromBlock(BlockFlatScreen.DEFAULTITEM));
        }
    };

}
