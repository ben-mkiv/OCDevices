package ben_mkiv.ocdevices;

import ben_mkiv.ocdevices.client.renderer.RenderFlatScreen;
import ben_mkiv.ocdevices.common.GuiHandler;
import ben_mkiv.ocdevices.common.blocks.*;
import ben_mkiv.ocdevices.common.drivers.FlatScreenDriver;
import ben_mkiv.ocdevices.common.tileentity.*;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.EnvironmentProvider;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.logging.Logger;

@Mod(
        modid = OCDevices.MOD_ID,
        name = OCDevices.MOD_NAME,
        version = OCDevices.VERSION,
        dependencies = "required-after:opencomputers"
)
public class OCDevices {

    public static final String MOD_ID = "ocdevices";
    public static final String MOD_NAME = "OCDevices";
    public static final String VERSION = "snapshot_20190115";

    public static final CreativeTab creativeTab = new CreativeTab(MOD_NAME);

    public static final Logger logger = Logger.getLogger(MOD_NAME);
    static final boolean verbose = false;

    @Mod.Instance(MOD_ID)
    public static OCDevices INSTANCE;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        BlockFlatScreen.DEFAULTITEM = new BlockFlatScreen();
        BlockCardDock.DEFAULTITEM = new BlockCardDock();
        BlockCase_slim_oc.DEFAULTITEM = new BlockCase_slim_oc();
        BlockCase_next.DEFAULTITEM = new BlockCase_next();
        BlockCase_ibm_5150.DEFAULTITEM = new BlockCase_ibm_5150();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());

        li.cil.oc.api.Driver.add((EnvironmentProvider) FlatScreenDriver.driver);
        li.cil.oc.api.Driver.add((DriverItem) FlatScreenDriver.driver);
    }

    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onRegisterModels(ModelRegistryEvent event) {
            if(verbose) logger.info("register models");
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlockFlatScreen.DEFAULTITEM), 0, new ModelResourceLocation(BlockFlatScreen.DEFAULTITEM.getRegistryName().toString()));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlockCardDock.DEFAULTITEM), 0, new ModelResourceLocation(BlockCardDock.DEFAULTITEM.getRegistryName().toString()));

            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlockCase_next.DEFAULTITEM), 0, new ModelResourceLocation(BlockCase_next.DEFAULTITEM.getRegistryName().toString()));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlockCase_slim_oc.DEFAULTITEM), 0, new ModelResourceLocation(BlockCase_slim_oc.DEFAULTITEM.getRegistryName().toString()));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlockCase_ibm_5150.DEFAULTITEM), 0, new ModelResourceLocation(BlockCase_ibm_5150.DEFAULTITEM.getRegistryName().toString()));

            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlatScreen.class, new RenderFlatScreen());
        }

        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            if(verbose) logger.info("register items");
            event.getRegistry().register(new ItemBlock(BlockFlatScreen.DEFAULTITEM).setRegistryName(BlockFlatScreen.DEFAULTITEM.getRegistryName()));
            event.getRegistry().register(new ItemBlock(BlockCardDock.DEFAULTITEM).setRegistryName(BlockCardDock.DEFAULTITEM.getRegistryName()));

            event.getRegistry().register(new ItemBlock(BlockCase_ibm_5150.DEFAULTITEM).setRegistryName(BlockCase_ibm_5150.DEFAULTITEM.getRegistryName()));
            event.getRegistry().register(new ItemBlock(BlockCase_slim_oc.DEFAULTITEM).setRegistryName(BlockCase_slim_oc.DEFAULTITEM.getRegistryName()));
            event.getRegistry().register(new ItemBlock(BlockCase_next.DEFAULTITEM).setRegistryName(BlockCase_next.DEFAULTITEM.getRegistryName()));
        }

        @SubscribeEvent
        public static void addBlocks(RegistryEvent.Register<Block> event) {
            if(verbose) logger.info("register blocks");
            event.getRegistry().register(BlockFlatScreen.DEFAULTITEM);
            event.getRegistry().register(BlockCardDock.DEFAULTITEM);

            event.getRegistry().register(BlockCase_ibm_5150.DEFAULTITEM);
            event.getRegistry().register(BlockCase_next.DEFAULTITEM);
            event.getRegistry().register(BlockCase_slim_oc.DEFAULTITEM);

            GameRegistry.registerTileEntity(TileEntityFlatScreen.class, new ResourceLocation(MOD_ID, BlockFlatScreen.NAME));
            GameRegistry.registerTileEntity(TileEntityCardDock.class, new ResourceLocation(MOD_ID, BlockCardDock.NAME));

            GameRegistry.registerTileEntity(TileEntityCase_next.class, new ResourceLocation(MOD_ID, BlockCase_next.NAME));
            GameRegistry.registerTileEntity(TileEntityCase_slim_oc.class, new ResourceLocation(MOD_ID, BlockCase_slim_oc.NAME));
            GameRegistry.registerTileEntity(TileEntityCase_ibm_5150.class, new ResourceLocation(MOD_ID, BlockCase_ibm_5150.NAME));
        }


        @SubscribeEvent
        public static void registerRecipes(RegistryEvent.Register<IRecipe> event){
            if(verbose) logger.info("register recipes");
            ItemStack piston = new ItemStack(Item.getItemFromBlock(net.minecraft.init.Blocks.PISTON));
            ItemStack iron = new ItemStack(Items.IRON_INGOT);
            ItemStack glasspane = new ItemStack(Blocks.GLASS_PANE);
            ItemStack quartz = new ItemStack(Items.QUARTZ);

            ItemStack screen3 = li.cil.oc.api.Items.get("screen3").createItemStack(1);
            ItemStack cardContainerTier3 = li.cil.oc.api.Items.get("cardcontainer3").createItemStack(1);
            ItemStack componentBus1 = li.cil.oc.api.Items.get("componentbus1").createItemStack(1);
            ItemStack t3microchip = li.cil.oc.api.Items.get("chip3").createItemStack(1);
            ItemStack t2microchip = li.cil.oc.api.Items.get("chip2").createItemStack(1);
            ItemStack pcb = li.cil.oc.api.Items.get("printedcircuitboard").createItemStack(1);
            ItemStack cable = li.cil.oc.api.Items.get("cable").createItemStack(1);
            ItemStack case3 = li.cil.oc.api.Items.get("case3").createItemStack(1);


            event.getRegistry().register(new ShapedOreRecipe(BlockCardDock.DEFAULTITEM.getRegistryName(), new ItemStack(BlockCardDock.DEFAULTITEM, 1),
                    "mCm",
                    "MPb",
                    "ici",
                    'b', componentBus1, 'c', cable, 'C', cardContainerTier3, 'P', pcb, 'i', iron, 'm', t2microchip, 'M', t3microchip).setRegistryName(MOD_ID, BlockCardDock.DEFAULTITEM.getUnlocalizedName()));


            event.getRegistry().register(new ShapedOreRecipe(BlockFlatScreen.DEFAULTITEM.getRegistryName(), new ItemStack(BlockFlatScreen.DEFAULTITEM, 1),
                    "IPI",
                    "PSP",
                    "IPI",
                    'P', piston, 'I', iron, 'S', screen3).setRegistryName(MOD_ID, BlockFlatScreen.DEFAULTITEM.getUnlocalizedName()));


            event.getRegistry().register(new ShapedOreRecipe(BlockCase_slim_oc.DEFAULTITEM.getRegistryName(), new ItemStack(BlockCase_slim_oc.DEFAULTITEM, 1),
                    "III",
                    "ICI",
                    "III",
                    'C', case3, 'I', iron).setRegistryName(MOD_ID, BlockCase_slim_oc.DEFAULTITEM.getUnlocalizedName()));

            event.getRegistry().register(new ShapedOreRecipe(BlockCase_next.DEFAULTITEM.getRegistryName(), new ItemStack(BlockCase_next.DEFAULTITEM, 1),
                    "QQQ",
                    "PCQ",
                    "QQQ",
                    'C', case3, 'P', glasspane, 'Q', quartz).setRegistryName(MOD_ID, BlockCase_next.DEFAULTITEM.getUnlocalizedName()));

            event.getRegistry().register(new ShapedOreRecipe(BlockCase_ibm_5150.DEFAULTITEM.getRegistryName(), new ItemStack(BlockCase_ibm_5150.DEFAULTITEM, 1),
                    "   ",
                    "III",
                    "ICI",
                    'C', case3, 'I', iron).setRegistryName(MOD_ID, BlockCase_ibm_5150.DEFAULTITEM.getUnlocalizedName()));
        }
    }



    public static class CreativeTab extends CreativeTabs {
        public CreativeTab(String unlocalizedName) {
            super(unlocalizedName);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(BlockFlatScreen.DEFAULTITEM));
        }
    }

}
