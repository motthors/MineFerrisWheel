package jp.mochisystems.mfw._mc._core;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.block.BlockSeatPositionMarker;
import jp.mochisystems.core._mc.renderer.renderTileEntityLimitFrame;
import jp.mochisystems.core._mc.tileentity.TileEntityBlockModelCutter;
import jp.mochisystems.core._mc.tileentity.TileEntityFileManager;
import jp.mochisystems.mfw._mc.block.*;
import jp.mochisystems.mfw._mc.gui.MFW_GUIHandler;
import jp.mochisystems.mfw._mc.item.*;
import jp.mochisystems.mfw._mc.message.MFW_PacketHandler;
import jp.mochisystems.mfw._mc.proxy.IProxy;
import jp.mochisystems.mfw._mc.tileEntity.*;
import jp.mochisystems.mfw.ferriswheel.FerrisGarland;
import jp.mochisystems.mfw.renderer.renderTileEntityFerrisConstructor;
import jp.mochisystems.mfw.renderer.renderTileEntityFerrisWheel;
import jp.mochisystems.mfw.sound.SoundLoader;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;


@Mod(
	modid = MFW.MODID,
	name = "Mine Ferris Wheel",
	version = MFW.VERSION,
	useMetadata = true,
	dependencies = "after:"+_Core.MODID
)
@TransformerExclusions
public class MFW {
	public static final String MODID = "mineferriswheel";
	public static final String VERSION = "3.0beta2";


	//proxy////////////////////////////////////////
	@SidedProxy(
			clientSide = "jp.mochisystems.mfw._mc.proxy.ClientProxy",
			serverSide = "jp.mochisystems.mfw._mc.proxy.ServerProxy")
	public static IProxy proxy;

	//Blocks/////////////////////////////////////////
	public static Block ferrisConstructor = new BlockFerrisConstructor();
	public static Block elevatorConstructor = new blockElevatorConstructor();
    public static Block ferrisCore = new blockFerrisCore.Wheel();
    public static Block ferrisElevator = new blockFerrisCore.Elevator();
	public static Block ferrisGarland = new blockFerrisCore.Garland();
	public static Block ferrisGarlandEnd = new blockFerrisCore.GarlandEnd();
	public static Block ferrisSupporter = new blockFerrisSupporter();
	public static Block ferrisConnector = new blockFerrisConnector();
	public static Block ferrisSeatToSit = new BlockSeatPositionMarker();
	public static Block ferrisSeatToSit_d = new BlockSeatPositionMarker();
//	public static Block ferrisSeatEx = new BlockSeatToSitEx();
//	public static Block ferrisChunkLoader = new blockChunkLoader();
	public static Block blockShabuNabe = new blockShabuNabe();

	//item/////////////////////////////////////////
	public static Item ItemFerrisCore = new ItemBlockFerrisCore(ferrisCore);
	public static Item ItemFerrisElevator = new ItemFerrisElevator(ferrisElevator);
    public static Item ItemFerrisGarland = new ItemFerrisGarland(ferrisGarland);
    public static Item ItemFerrisGarlandEnd = new ItemFerrisGarland(ferrisGarlandEnd);
//	public static Item ItemFerrisBasket = new ItemFerrisBasket();
	public static Item ItemFerrisSeed = new ItemFerrisSeed();
	public static Item ItemFerrisGarlandSeed = new ItemFerrisGarlandSeed();

	//shabushabu/////////////////////////////////////////
	public static Item ItemSliceBeef = SliceMeat.FactoryCreateBeaf();
	public static Item ItemSlicePork = SliceMeat.FactoryCreatePork();
	public static Item ItemSliceChicken = SliceMeat.FactoryCreateChicken();
	public static Item ItemShabuBeef = SliceMeat.FactoryCreateShabuBeaf();
	public static Item ItemShabuPork = SliceMeat.FactoryCreateShabuPork();
	public static Item ItemShabuChicken = SliceMeat.FactoryCreateShabuChicken();

	//GUI/////////////////////////////////////////
	@Mod.Instance(MFW.MODID)
    public static MFW INSTANCE;
    public static final int GUIID_FerrisConstructor = 1;
    public static final int GUIID_FerrisElevatorConstructor = 2;
    public static final int GUIID_FerrisCore = 3;
	public static final int GUIID_FerrisStoryBoard = 6;
	public static final int GUIID_FerrisConnector = 7;
	public static final int GUIID_FerrisGarland = 8;
	public static final int GUIID_GarlandInit = 9;

	public static MFW_CreateCreativeTab MFW_Tab = new MFW_CreateCreativeTab("MineFerrisWheel");

	////////////////////////////////////////////////////////////////

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		_Core.Instance.AddTab(MFW_Tab);
		MinecraftForge.EVENT_BUS.register(this);

		MFW_PacketHandler.init();
		proxy.preInit();

	}


	@Mod.EventHandler
	public void Init(FMLInitializationEvent e)
	{
		proxy.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MFW_GUIHandler());


	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit();
		FMLCommonHandler.instance().bus().register(new FerrisGarland.GarlandManager());
	}
	////////////////////////////////////////////////////////////////


	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		_Core.RegisterBlock(r, ferrisCore, "FerrisCore", MODID, "ferriscore", MFW_Tab);
		_Core.RegisterBlock(r, ferrisElevator, "Elevator", MODID, "ferriselevator", MFW_Tab);
		_Core.RegisterBlock(r, ferrisGarland, "Garland", MODID, "ferrisgarland", null);
		_Core.RegisterBlock(r, ferrisGarlandEnd, "GarlandEnd", MODID, "ferrisgarlandend", null);
		_Core.RegisterBlock(r, ferrisConstructor, "FerrisCore Constructor", MODID, "ferriscoreconstructor", MFW_Tab);
		_Core.RegisterBlock(r, elevatorConstructor, "Elevator Constructor", MODID, "ferriselevatorconstructor", MFW_Tab);
		_Core.RegisterBlock(r, ferrisConnector, "Connector", MODID, "ferrisconnector", MFW_Tab);
		_Core.RegisterBlock(r, ferrisSupporter, "Supporter", MODID, "ferrissupporter", MFW_Tab);
		_Core.RegisterBlock(r, ferrisSeatToSit, "Seat Marker", MODID, "ferrisseatmarker", MFW_Tab);
		_Core.RegisterBlock(r, ferrisSeatToSit_d, "Seat Marker", MODID, "ferrisseatmarker_d", MFW_Tab);
		_Core.RegisterBlock(r, blockShabuNabe, "ShabuShabu Pot", MODID, "shabushabupot", MFW_Tab);

		_Core.RegisterTileEntity(TileEntityFerrisConstructor.class, MODID, "MFW:TileEntityFerrisConstructor");
		_Core.RegisterTileEntity(TileEntityElevatorConstructor.class, MODID, "MFW:TileEntityElevatorConstructor");
		_Core.RegisterTileEntity(TileEntityFerrisCore.Wheel.class, MODID, "MFW:TileEntityFerrisWheel");
		_Core.RegisterTileEntity(TileEntityFerrisCore.Elevator.class, MODID, "MFW:TileEntityFerrisElevator");
		_Core.RegisterTileEntity(TileEntityFerrisCore.Garland.class, MODID, "MFW:TileEntityFerrisGarland");
//		_Core.RegisterTileEntity(TileEntityFerrisCore.GarlandEnd.class, MODID, "MFW:TileEntityFerrisGarlandEnd");
		_Core.RegisterTileEntity(TileEntityFileManager.class, MODID, "MFW:TileEntityFileManager");
		_Core.RegisterTileEntity(TileEntityBlockModelCutter.class, MODID, "MFW:TileEntityFerrisCutter");
//		_Core.RegisterTileEntity(TileEntityChunkLoader.class, MODID, "MFW:TileEntityChunkLoader");
		_Core.RegisterTileEntity(TileEntityConnector.class, MODID, "MFW:TileEntityConnector");

	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
				ItemFerrisCore.setRegistryName(MODID, "ferriscore").setCreativeTab(MFW_Tab),
				ItemFerrisElevator.setRegistryName(MODID, "ferriselevator"),
				ItemFerrisGarland.setRegistryName(MODID, "itemblockgarland"),
				ItemFerrisGarlandEnd.setRegistryName(MODID, "itemblockgarlandend"),
				new ItemBlockFerrisConstructor(ferrisConstructor).setRegistryName(MODID, "itemferriscoreconstructor"),
				new ItemBlock(elevatorConstructor).setRegistryName(MODID, "itemblockelevatorconstructor"),
				new ItemBlock(ferrisSupporter).setRegistryName(MODID, "itemblocksupporter"),
				new ItemBlock(ferrisConnector).setRegistryName(MODID, "itemblockconnector"),
				new ItemBlock(ferrisSeatToSit).setRegistryName(MODID, "itemblockseatmarker"),
				new ItemBlock(ferrisSeatToSit_d).setRegistryName(MODID, "itemblockseatmarker_d"),
				new ItemBlock(blockShabuNabe).setRegistryName(MODID, "itemblockshabunabe")
//				new ItemBlock(ferrisSeatEx).setRegistryName(MODID, "itemblockseatex"),
//				new ItemBlock(ferrisChunkLoader).setRegistryName(MODID, "itemblockcunk"),

		);

		event.getRegistry().registerAll(
			ItemFerrisSeed
//					.setCreativeTab(MFW_Tab)
				.setUnlocalizedName("ItemFerrisSeed")
				.setRegistryName("FerrisSeed")
				.setMaxStackSize(1),
//			ItemFerrisBasket
//				.setCreativeTab(MFW_Tab)
//				.setUnlocalizedName("ItemFerrisBasket")
//				.setRegistryName("FerrisBasket")
//				.setMaxStackSize(10),
			ItemFerrisGarlandSeed.setCreativeTab(MFW_Tab)
				.setUnlocalizedName("itemFerrisGarlandSeed")
				.setRegistryName("itemFerrisGarlandSeed"),
			ItemSliceBeef.setCreativeTab(MFW_Tab)
				.setUnlocalizedName("ItemSliceBeef")
				.setRegistryName("ItemSliceBeef"),
			ItemSlicePork.setCreativeTab(MFW_Tab)
				.setUnlocalizedName("ItemSlicePork")
				.setRegistryName("ItemSlicePork"),
			ItemSliceChicken.setCreativeTab(MFW_Tab)
				.setUnlocalizedName("ItemSliceChicken")
				.setRegistryName("ItemSliceChicken"),
			ItemShabuBeef.setCreativeTab(MFW_Tab)
				.setUnlocalizedName("ItemShabuBeef")
				.setRegistryName("ItemShabuBeef"),
			ItemShabuPork.setCreativeTab(MFW_Tab)
				.setUnlocalizedName("ItemShabuPork")
				.setRegistryName("ItemShabuPork"),
			ItemShabuChicken.setCreativeTab(MFW_Tab)
				.setUnlocalizedName("ItemShabuChicken")
				.setRegistryName("ItemShabuChicken")
		);
	}


	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event)
	{
//		EntityEntry FerrisCollider = new EntityEntry(EntityBlockModelCollider.class, MODID + ":blockCollider");
//		FerrisCollider.setRegistryName(MODID, "blockCollider");
//		event.getRegistry().register(FerrisCollider);

//		EntityRegistry.registerModEntity(EntityFerrisCollider.class, "mfw:basket", eid++, this, 200, 10, true);

//		EntityRegistry.registerModEntity(entityPartsTestBase.class, "mfw:test", eid++, this, 200, 10, true);
//		EntityRegistry.registerModEntity(EntityCollisionParts.class, "mfw:testp", eid++, this, 200, 10, true);
//		EntityRegistry.registerModEntity(entityPartSit.class, "mfw:partsit", eid++, this, 200, 100, true);
//		EntityRegistry.registerModEntity(entityPartSitEx.class, "mfw:partsitex", eid++, this, 200, 100, true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) throws Exception {

//		IStateMapper mapper = new StateMap.Builder().ignore(BlockRail.FACING).build();
//		ModelLoader.setCustomStateMapper(railNormal, mapper);

		ModelLoader.setCustomStateMapper(ferrisConstructor,  new StateMap.Builder().build());
		ModelLoader.setCustomStateMapper(elevatorConstructor,  new StateMap.Builder().build());
		ModelLoader.setCustomStateMapper(ferrisSupporter,  new StateMap.Builder().build());
		ModelLoader.setCustomStateMapper(ferrisConnector,  new StateMap.Builder().build());
		ModelLoader.setCustomStateMapper(ferrisCore,  new StateMap.Builder().ignore(blockFerrisCore.FACING).build());
		ModelLoader.setCustomStateMapper(ferrisElevator,  new StateMap.Builder().ignore(blockFerrisCore.FACING).build());
		ModelLoader.setCustomStateMapper(ferrisGarland,  new StateMap.Builder().ignore(blockFerrisCore.FACING).build());
		ModelLoader.setCustomStateMapper(ferrisGarlandEnd,  new StateMap.Builder().ignore(blockFerrisCore.FACING).build());
		ModelLoader.setCustomStateMapper(ferrisSeatToSit,  new StateMap.Builder().build());
		ModelLoader.setCustomStateMapper(ferrisSeatToSit_d,  new StateMap.Builder().build());
		ModelLoader.setCustomStateMapper(blockShabuNabe,  new StateMap.Builder().build());

		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ferrisConstructor), 0, new ModelResourceLocation(ferrisConstructor.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(elevatorConstructor), 0, new ModelResourceLocation(elevatorConstructor.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ferrisSupporter), 0, new ModelResourceLocation(ferrisSupporter.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ferrisConnector), 0, new ModelResourceLocation(ferrisConnector.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ferrisSeatToSit), 0, new ModelResourceLocation(ferrisSeatToSit.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ferrisSeatToSit_d), 0, new ModelResourceLocation(ferrisSeatToSit_d.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockShabuNabe), 0, new ModelResourceLocation(blockShabuNabe.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemFerrisCore, 0, new ModelResourceLocation(ItemFerrisCore.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemFerrisElevator, 0, new ModelResourceLocation(ItemFerrisElevator.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemFerrisGarland, 0, new ModelResourceLocation(ItemFerrisGarland.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemFerrisGarlandEnd, 0, new ModelResourceLocation(ItemFerrisGarlandEnd.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemFerrisSeed, 0, new ModelResourceLocation(ItemFerrisSeed.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemFerrisGarlandSeed, 0, new ModelResourceLocation(ItemFerrisGarlandSeed.getRegistryName(), "inventory"));

		ModelLoader.setCustomModelResourceLocation(ItemSliceBeef, 0, new ModelResourceLocation(ItemSliceBeef.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemSliceChicken, 0, new ModelResourceLocation(ItemSliceChicken.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemSlicePork, 0, new ModelResourceLocation(ItemSlicePork.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemShabuBeef, 0, new ModelResourceLocation(ItemShabuBeef.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemShabuChicken, 0, new ModelResourceLocation(ItemShabuChicken.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemShabuPork, 0, new ModelResourceLocation(ItemShabuPork.getRegistryName(), "inventory"));


//		RenderTileEntityRailBase tileRenderer = new rendercore;
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisCore.class, tileRenderer);

		renderTileEntityLimitFrame rendererFrame = new renderTileEntityLimitFrame();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisConstructor.class, new renderTileEntityFerrisConstructor());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorConstructor.class, rendererFrame);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisCore.Wheel.class, new renderTileEntityFerrisWheel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisCore.Elevator.class, new renderTileEntityFerrisWheel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisCore.Garland.class, new renderTileEntityFerrisWheel());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisCore.GarlandEnd.class, new renderTileEntityFerrisWheel());


//		RenderingRegistry.registerEntityRenderingHandler(EntityBlockModelCollider.class, renderEntityFerrisCollider::new);
//		RenderingRegistry.registerEntityRenderingHandler(entityPartsTestBase.class, renderEntityPartsTest::new);
//		RenderingRegistry.registerEntityRenderingHandler(entityPartSit.class, renderEntityNon::new);
//		RenderingRegistry.registerEntityRenderingHandler(entityPartSitEx.class, renderNon);



	}

//	@SubscribeEvent
//	public void registerBlockColors(final ColorHandlerEvent.Block event) {
//		event.getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> jp.mochisystems.mfw._mc.block.blockShabuNabe.colorMultiplier(tintIndex), blockShabuNabe);
//	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerSound(SoundLoadEvent soundLoadEvent) {
//		SoundLoader.Instance.Load();
//		ForgeRegistries.SOUND_EVENTS.registerAll(SoundLoader.Instance.events.toArray(new SoundEvent[0]));
	}
//	@SubscribeEvent
//	public static void registerSounds(RegistryEvent.Register<SoundEvent> event){
//		SoundLoader.Instance.Load();
//		event.getRegistry().registerAll(SoundLoader.Instance.events.toArray(new SoundEvent[0]));
//	}

	@EventHandler
	public void handleServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new MFW_Command());
	}

	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(MODID))
		{
			ConfigManager.sync(MODID, Type.INSTANCE);
		}
	}

}