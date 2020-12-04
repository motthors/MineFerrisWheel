package mfw._mc._1_7_10._core;

import java.io.File;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import mfw._mc._1_7_10.block.*;
import mfw._mc._1_7_10.entity.EntityFerrisCollider;
import mfw._mc._1_7_10.entity.EntityCollisionParts;
import mfw._mc._1_7_10.entity.entityPartSit;
import mfw._mc._1_7_10.entity.entityPartsTestBase;
import mfw._mc._1_7_10.gui.MFW_GUIHandler;
import mfw._mc._1_7_10.handler.handlerChunkLoading;
import mfw._mc._1_7_10.item.*;
import mfw._mc._1_7_10.message.MFW_PacketHandler;
import mfw._mc._1_7_10.proxy.IProxy;
import mfw.sound.SoundManager;
import mfw._mc._1_7_10.tileEntity.*;
import mfw._mc._1_7_10.tileEntity.TileEntityFerrisCore;
import mochisystems._mc._1_7_10._core._Core;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeChunkManager;

@Mod( 
	modid = MFW_Core.MODID,
	name = "Mine Ferris Wheel",
	version = MFW_Core.VERSION,
	dependencies = "required-after:Forge@[10.12.1.1090,);after:erc;"+"required-after:"+_Core.MODID+"@[1.0,)",
	useMetadata = true
)
@TransformerExclusions
public class MFW_Core {
	public static final String MODID = "mfw";
	public static final String VERSION = "3.0alpha1";

	
	//proxy////////////////////////////////////////
	@SidedProxy(clientSide = "mfw._mc._1_7_10.proxy.ClientProxy", serverSide = "mfw._mc._1_7_10.proxy.ServerProxy")
	public static IProxy proxy;
	
	//Blocks/////////////////////////////////////////
	public static Block ferrisConstrcutor = new blockFerrisConstructor();
	public static Block elevatorConstructor = new blockElevatorConstructor();
    public static Block ferrisCore = new blockFerrisCore.Wheel();
    public static Block ferrisElevator = new blockFerrisCore.Elevator();
	public static Block ferrisGarland = new blockFerrisCore.Garland();
	public static Block ferrisGarlandEnd = new blockFerrisCore.GarlandEnd();
	public static Block ferrisSupporter = new blockFerrisSupporter();
	public static Block ferrisConnector = new blockFerrisConnector();
	public static Block ferrisFileManager = new blockFileManager();
	public static Block ferrisSeatToSit = new blockSeatToSitDown();
	public static Block ferrisCutter = new blockFerrisCutter();
	public static Block ferrisSeatEx = new blockSeatToSitEx();
	public static Block ferrischunkLoader = new blockChunkLoader();
	public static Block blockSyabuNabe = new blockSyabuNabe();
	
	//render block ID
	public static int blockCutterRenderId;
	public static int blockCoreRenderId;
	public static int blockSeatExId;
	public static int blockSyabuNabeId;
	
	//item/////////////////////////////////////////
	public static ItemBlock ItemFerrisCore = new itemBlockFerrisCore(ferrisCore);
	public static ItemBlock ItemFerrisElevator = new itemFerrisElevator(ferrisCore);
    public static ItemBlock ItemFerrisGarland = new itemFerrisGarland(ferrisCore);
    public static ItemBlock ItemFerrisGarlandEnd = new itemFerrisGarland.End(ferrisCore);
	public static Item ItemFerrisBasket = new itemFerrisBasket();
	public static Item ItemFerrisSeed = new itemFerrisSeed();

	//shabushabu/////////////////////////////////////////
	public static Item ItemSliceBeef = SliceMeat.FactoryCreateBeaf();
	public static Item ItemSlicePork = SliceMeat.FactoryCreatePork();
	public static Item ItemSliceChicken = SliceMeat.FactoryCreateChicken();
	public static Item ItemSyabuBeef = SliceMeat.FactoryCreateSyabuBeaf();
	public static Item ItemSyabuPork = SliceMeat.FactoryCreateSyabuPork();
	public static Item ItemSyabuChicken = SliceMeat.FactoryCreateSyabuChicken();
	
	//GUI/////////////////////////////////////////
	@Mod.Instance(MFW_Core.MODID)
    public static MFW_Core INSTANCE;
//    public static Item sampleGuiItem;
//    public static final int GUIID_RailBase = 0;
    public static final int GUIID_FerrisConstructor = 1;
    public static final int GUIID_FerrisElevatorConstructor = 2;
    public static final int GUIID_FerrisCore = 3;
    public static final int GUIID_FerrisFileManager = 4;
    public static final int GUIID_FerrisCutter = 5;
	public static final int GUIID_FerrisStoryBoard = 6;
	public static final int GUIID_FerrisConnector = 7;
	public static final int GUIID_FerrisGarland = 8;

	////////////////////////////////////////////////////////////////
	// �Ǝ��N���G�C�e�B�u�^�u�쐬
	public static MFW_CreateCreativeTab MFW_Tab = new MFW_CreateCreativeTab("MineFerrisWheel", ItemFerrisCore);
	
	////////////////////////////////////////////////////////////////
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) throws Exception
	{
		MFW_Logger.info("Start preInit");

		MFW_checker.check();
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())SoundManager.JsonUpdate();
//		if(!MFW_checker.is())return;

		GameRegistry.registerTileEntity(TileEntityFerrisConstructor.class, "MFW:TileEntityFerrisConstructor");
		GameRegistry.registerTileEntity(TileEntityElevatorConstructor.class, "MFW:TileEntityElevatorConstructor");
		GameRegistry.registerTileEntity(TileEntityFerrisCore.Wheel.class, "MFW:TileEntityFerrisWheel");
        GameRegistry.registerTileEntity(TileEntityFerrisCore.Elevator.class, "MFW:TileEntityFerrisElevator");
        GameRegistry.registerTileEntity(TileEntityFerrisCore.Garland.class, "MFW:TileEntityFerrisGarland");
        GameRegistry.registerTileEntity(TileEntityFerrisCore.GarlandEnd.class, "MFW:TileEntityFerrisGarlandEnd");
		GameRegistry.registerTileEntity(TileEntityFileManager.class, "MFW:TileEntityFileManager");
		GameRegistry.registerTileEntity(TileEntityFerrisCutter.class, "MFW:TileEntityFerrisCutter");
		GameRegistry.registerTileEntity(TileEntityChunkLoader.class, "MFW:TileEntityChunkLoader");
        GameRegistry.registerTileEntity(TileEntityConnector.class, "MFW:TileEntityConnector");

		blockCutterRenderId = proxy.getNewRenderType();
		blockCoreRenderId = proxy.getNewRenderType();
		blockSeatExId = proxy.getNewRenderType();
		blockSyabuNabeId = proxy.getNewRenderType();
		
		MFW_PacketHandler.init();
		proxy.preInit();
		
		new File("./MFWFiles/WheelFrame/").mkdirs();
		new File("./MFWFiles/Basket/").mkdirs();

		MFW_Logger.info("End preInit");
	}

	
	@EventHandler
	public void Init(FMLInitializationEvent e)
	{
		MFW_Logger.info("Start Init");

		proxy.init();
//		if(!MFW_checker.is())return;
		
		//�G���e�B�e�B�̓o�^�B
		int eid=100;
		EntityRegistry.registerModEntity(EntityFerrisCollider.class, "mfw:basket", eid++, this, 200, 10, true);
		
		EntityRegistry.registerModEntity(entityPartsTestBase.class, "mfw:test", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(EntityCollisionParts.class, "mfw:testp", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(entityPartSit.class, "mfw:partsit", eid++, this, 200, 100, true);
//		EntityRegistry.registerModEntity(entityPartSitEx.class, "mfw:partsitex", eid++, this, 200, 100, true);
		// �A�C�e���̓o�^
		InitItem_Ferris();
		InitBlock_Ferris();
		//����V�[�g�̐ݒ�
		InitExSeat();
		
		// ���V�s�̓o�^
		InitItemRecipe();
		
		//�`�����N���[�_�[�p
		ForgeChunkManager.setForcedChunkLoadingCallback(this, new handlerChunkLoading());
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MFW_GUIHandler());
		
		MFW_Logger.info("End Init");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
//		if(!MFW_checker.is())return;
		proxy.postInit();
	}
	////////////////////////////////////////////////////////////////

	private void InitBlock_Ferris()
	{
		ferrisConstrcutor
			.setBlockName("TileEntityFerrisConstructor")
			.setBlockTextureName(MFW_Core.MODID+":ferrisConstructor")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisConstrcutor, "MFW.TileEntityFerrisConstructor");

		elevatorConstructor
				.setBlockName("TileEntityElevatorConstructor")
				.setBlockTextureName(MFW_Core.MODID+":elevatorConstructor")
				.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(elevatorConstructor, "MFW.TileEntityElevatorConstructor");

//		ferrisBasketConstrcutor
//			.setBlockName("FerrisBasketConstructor")
//			.setBlockTextureName(MFW_Core.MODID+":ferrisBasketConstructor")
//			.setCreativeTab(Tab);
//		GameRegistry.registerBlock(ferrisBasketConstrcutor, "MFW.FerrisBasketConstructor");
	
		ferrisConnector
		.setBlockName("FerrisConnector")
		.setBlockTextureName(MFW_Core.MODID+":ferrisConnector")
		.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisConnector, "MFW.FerrisConnector");
		
		ferrisSupporter
			.setBlockName("FerrisSupporter")
			.setBlockTextureName(MFW_Core.MODID+":supporter")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisSupporter, "MFW.FerrisSupporter");
	

		ferrisFileManager
			.setBlockName("FerrisFileManager")
			.setBlockTextureName(MFW_Core.MODID+":ferrisFileManager")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisFileManager, "MFW.FileManager");
		
		ferrisCutter
			.setBlockName("TileEntityFerrisCutter")
			.setBlockTextureName(MFW_Core.MODID+":ferrisCutter")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisCutter, "MFW.Cutter");
		
		ferrischunkLoader
		.setBlockName("FerrisChunkLoader")
		.setBlockTextureName(MFW_Core.MODID+":chunkLaoder")
		.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrischunkLoader, "MFW.chunkLoader");
		
		ferrisSeatToSit
			.setBlockName("FerrisSeatToSit")
			.setBlockTextureName(MFW_Core.MODID+":SeatToSit")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisSeatToSit, itemBlockSeatToSitDown.class, "MFW.SeatToSit");
		
		blockSyabuNabe
			.setBlockName("blockSyabuNabe")
			.setBlockTextureName("anvil_base")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(blockSyabuNabe, itemBlockSeatToSitDown.class, "MFW.BlockSyabuNabe");
	}
	
	private void InitItem_Ferris()
	{
		ItemFerrisSeed.setCreativeTab(MFW_Tab)
			.setUnlocalizedName("ItemFerrisSeed")
			.setTextureName(MODID+":ferrisSeed")
			.setMaxStackSize(1);
		GameRegistry.registerItem(ItemFerrisSeed, "FerrisSeed");
		
		ferrisCore
			.setBlockName("FerrisCore")
			.setBlockTextureName(MODID+":ferrisWheel")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisCore, itemBlockFerrisCore.class, "MFW.FerrisCore");

        ferrisElevator
                .setBlockName("FerrisElevator")
                .setBlockTextureName(MODID+":ferrisElevator")
                .setCreativeTab(MFW_Tab);
        GameRegistry.registerBlock(ferrisElevator, itemFerrisElevator.class, "MFW.FerrisElevator");

        ferrisGarland
                .setBlockName("FerrisGarland")
                .setBlockTextureName(MODID+":ferrisGarland")
                .setCreativeTab(MFW_Tab);
        GameRegistry.registerBlock(ferrisGarland, itemFerrisGarland.class, "MFW.FerrisGarland");

		ferrisGarlandEnd
				.setBlockName("FerrisGarlandEnd")
				.setBlockTextureName(MODID+":ferrisGarlandEnd");
		GameRegistry.registerBlock(ferrisGarlandEnd, itemFerrisGarland.End.class, "MFW.FerrisGarlandEnd");

		ItemFerrisBasket.setCreativeTab(MFW_Tab);
		ItemFerrisBasket.setUnlocalizedName("ItemFerrisBasket");
		ItemFerrisBasket.setTextureName(MODID+":ferrisBasket");
		ItemFerrisBasket.setMaxStackSize(10);
		GameRegistry.registerItem(ItemFerrisBasket, "FerrisBasket");
		
		//��
		ItemSliceBeef.setCreativeTab(MFW_Tab)
			.setUnlocalizedName("ItemSliceBeef")
			.setTextureName(MODID+":slicebeef");
		GameRegistry.registerItem(ItemSliceBeef, "ItemSliceBeef");
	
		ItemSlicePork.setCreativeTab(MFW_Tab)
			.setUnlocalizedName("ItemSlicePork")
			.setTextureName(MODID+":slicepork");
		GameRegistry.registerItem(ItemSlicePork, "ItemSlicePork");

		ItemSliceChicken.setCreativeTab(MFW_Tab)
			.setUnlocalizedName("ItemSliceChicken")
			.setTextureName(MODID+":slicechicken");
		GameRegistry.registerItem(ItemSliceChicken, "ItemSliceChicken");
		
		ItemSyabuBeef.setCreativeTab(MFW_Tab)
			.setUnlocalizedName("ItemSyabuBeef")
			.setTextureName(MODID+":syabubeef");
		GameRegistry.registerItem(ItemSyabuBeef, "ItemSyabuBeef");
		
		ItemSyabuPork.setCreativeTab(MFW_Tab)
			.setUnlocalizedName("ItemSyabuPork")
			.setTextureName(MODID+":syabupork");
		GameRegistry.registerItem(ItemSyabuPork, "ItemSyabuPork");
	
		ItemSyabuChicken.setCreativeTab(MFW_Tab)
			.setUnlocalizedName("ItemSyabuChicken")
			.setTextureName(MODID+":syabuchicken");
		GameRegistry.registerItem(ItemSyabuChicken, "ItemSyabuChicken");
	}
	
	private void InitExSeat()
	{
//		if(!Loader.isModLoaded(ERC_Core.MODID))return;
//		ferrisSeatEx
//			.setBlockName("FerrisSeatEx")
//			.setBlockTextureName(MFW_Core.MODID+":SeatEx")
//			.setCreativeTab(Tab);
//		GameRegistry.registerBlock(ferrisSeatEx, "MFW.SeatToSitEx");
	}
	
	private void InitItemRecipe()
	{

		// �^�l
		GameRegistry.addRecipe(new ItemStack(ItemFerrisSeed),
				"DE",
				'D',Items.diamond,
				'E',Items.emerald
		);
		
		// �t���[��
		GameRegistry.addRecipe(new ItemStack(ferrisCore),
				" I ",
				"ISI",
				" I ",
				'S',ItemFerrisSeed,
				'I',Items.iron_ingot
		);

		//TODO add Elevator
		
		// �o�X�P�b�g
		GameRegistry.addRecipe(new ItemStack(ItemFerrisBasket),
				" S ",
				" I ",
				"III",
				'S',ItemFerrisSeed,
				'I',Items.iron_ingot
		);
		
		// �t���[���H���
		GameRegistry.addRecipe(new ItemStack(ferrisConstrcutor),
				"IWI",
				"ISI",
				"ITI",
				'S',ItemFerrisSeed,
				'W',ferrisCore,
				'T',Blocks.crafting_table,
				'I',Blocks.stone
		);

		GameRegistry.addRecipe(new ItemStack(elevatorConstructor),
				"IWI",
				"ISI",
				"ITI",
				'S',ItemFerrisSeed,
				'W',ferrisElevator,
				'T',Blocks.crafting_table,
				'I',Blocks.stone
		);

		// �o�X�P�b�g�H���
//		GameRegistry.addRecipe(new ItemStack(ferrisBasketConstrcutor),
//				"IBI",
//				"ISI",
//				"ITI",
//				'S',ItemFerrisSeed,
//				'B',ItemFerrisBasket,
//				'T',Blocks.crafting_table,
//				'I',Blocks.stone
//		);
		
		// �ڑ��u���b�N
		GameRegistry.addRecipe(new ItemStack(ferrisConnector,10,0),
				" I ",
				"BSB",
				" I ",
				'S',ItemFerrisSeed,
				'B',Items.stick,
				'I',Items.iron_ingot
		);
		
		// �T�|�[�^�u���b�N
		GameRegistry.addRecipe(new ItemStack(ferrisSupporter,10,0),
				" G ",
				"GSG",
				" G ",
				'S',ItemFerrisSeed,
				'G',Blocks.glass
		);
		
		// �����R��

		
		// �t�@�C���ǂݏ���
		GameRegistry.addRecipe(new ItemStack(ferrisFileManager),
				"OOO",
				"OSO",
				"OEO",
				'S',ItemFerrisSeed,
				'O',Blocks.stone,
				'E',Items.ender_pearl
		);
		
		// �J�b�^�[
		GameRegistry.addRecipe(new ItemStack(ferrisCutter),
				"OOO",
				"OSO",
				"OCO",
				'S',ItemFerrisSeed,
				'O',Blocks.stone,
				'C',Items.shears
		);
		
		// �V�[�g0
		GameRegistry.addRecipe(new ItemStack(ferrisSeatToSit,1,0),
				"   ",
				"WSW",
				"   ",
				'S',ItemFerrisSeed,
				'W',Blocks.carpet
		);
		// �V�[�g0.5
		GameRegistry.addRecipe(new ItemStack(ferrisSeatToSit,1,1),
				"   ",
				"   ",
				"WSW",
				'S',ItemFerrisSeed,
				'W',Blocks.carpet
		);
		
		//���[�_�[
		GameRegistry.addRecipe(new ItemStack(ferrischunkLoader),
				"RLR",
				"GSG",
				"RLR",
				'S',ItemFerrisSeed,
				'L',new ItemStack(Items.dye, 1, 4), //���s�X
				'G',Items.glowstone_dust,
				'R',Blocks.stone
		);
		
//		if(Loader.isModLoaded(ERC_Core.MODID))
//		{
//			// �V�[�gEx
//			GameRegistry.addRecipe(new ItemStack(ferrisSeatEx),
//					"   ",
//					"WSW",
//					" P ",
//					'S',ItemFerrisSeed,
//					'W',Blocks.carpet,
//					'P',ERC_Core.itemBasePipe
//					);
//		}
		
		//��
		GameRegistry.addRecipe(new ItemStack(ItemSliceBeef, 3),
				"S",
				'S',Items.beef
		);
		//��
		GameRegistry.addRecipe(new ItemStack(ItemSlicePork, 3),
				"S",
				'S',Items.porkchop
		);
		//��
		GameRegistry.addRecipe(new ItemStack(ItemSliceChicken, 3),
				"S",
				'S',Items.chicken
		);
		//��
		GameRegistry.addRecipe(new ItemStack(ItemSliceChicken, 3),
				"IDI",
				" I ",
				'I',Items.iron_ingot,
				'D',Blocks.dirt
		);
	}
	
	@EventHandler
	public void handleServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new MFW_Command());
	}
}