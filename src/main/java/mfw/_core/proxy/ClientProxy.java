package mfw._core.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mfw._core.MFW_Core;
import mfw._mc.entity.EntityFerrisCollider;
import mfw._mc.tileEntity.*;
import mochisystems.blockcopier.BlocksRenderer;
import mochisystems.blockcopier.MTYBlockAccess;
import mfw._mc.entity.entityPartSit;
import mfw._mc.entity.entityPartsTestBase;
import mfw.handler.KeyHandlerClient;
import mfw.handler.handlerClientConnected;
import mfw.handler.renderEventCompileWheel;
import mfw.renderer.renderBlockCutter;
import mfw.renderer.renderBlockFerrisCore;
import mfw.renderer.renderBlockSeatEx;
import mfw.renderer.renderBlockSyabuNabe;
import mfw.renderer.renderEntityFerrisCollider;
import mfw.renderer.renderEntityNon;
import mfw.renderer.renderEntityPartsTest;
import mfw.renderer.renderTileEntityFerrisWheel;
import mochisystems._mc.renderer.renderTileEntityLimitFrame;
import mfw._mc.tileEntity.TileEntityFerrisConstructor;
import mfw._mc.tileEntity.TileEntityFerrisCutter;
import mochisystems.util.gui.GuiDragController;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy{
	
	@Override
	public int getNewRenderType()
	{
		return RenderingRegistry.getNextAvailableRenderId();
	}
	

	@Override
	public void preInit()
	{	
		renderTileEntityLimitFrame rendererFrame = new renderTileEntityLimitFrame();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisConstructor.class, rendererFrame);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorConstructor.class, rendererFrame);

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisCore.Wheel.class, new renderTileEntityFerrisWheel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisCore.Elevator.class, new renderTileEntityFerrisWheel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisCore.Garland.class, new renderTileEntityFerrisWheel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisCutter.class, rendererFrame);

		// Entity
		RenderingRegistry.registerEntityRenderingHandler(EntityFerrisCollider.class, new renderEntityFerrisCollider());
		RenderingRegistry.registerEntityRenderingHandler(entityPartsTestBase.class, new renderEntityPartsTest());
		renderEntityNon renderNon = new renderEntityNon();
		RenderingRegistry.registerEntityRenderingHandler(entityPartSit.class, renderNon);
//		RenderingRegistry.registerEntityRenderingHandler(entityPartSitEx.class, renderNon);
		
		//
		RenderingRegistry.registerBlockHandler(new renderBlockCutter());
		RenderingRegistry.registerBlockHandler(new renderBlockFerrisCore());
		RenderingRegistry.registerBlockHandler(new renderBlockSeatEx());
		RenderingRegistry.registerBlockHandler(new renderBlockSyabuNabe());
		
		KeyHandlerClient.init();
//		Minecraft mc = Minecraft.getMinecraft();
//		ERC_Core.tickEventHandler = new ERC_ClientTickEventHandler(mc);
//		FMLCommonHandler.instance().bus().register(new TickEventHandler());
//		FMLCommonHandler.instance().bus().register(ERC_Core.tickEventHandler);
//		MinecraftForge.EVENT_BUS.register(new ERC_InputEventHandler(mc));
		
//		if(Loader.isModLoaded(ERC_Core.MODID))
//		{
//			MinecraftForge.EVENT_BUS.register(new RenderLivingEventHandler());
//			FMLCommonHandler.instance().bus().register(new handerClientRenderTick(Minecraft.getMinecraft()));
//		}
		MinecraftForge.EVENT_BUS.register( new renderEventCompileWheel());
		MinecraftForge.EVENT_BUS.register(MFW_Core.ItemFerrisCore);
	}

	@Override
	public void init()
	{
		FMLCommonHandler.instance().bus().register(new KeyHandlerClient());
		FMLCommonHandler.instance().bus().register(new handlerClientConnected());
	}

	@Override
	public void postInit() 
	{
		GuiDragController.ResetSpecialCamera();
	}
	
	public BlocksRenderer getrendererFerrisWheel(MTYBlockAccess ba)
	{
		return new BlocksRenderer(ba);
	}
//	public rendererFerrisBasket getrendererFerrisBasket(MTYBlockAccess ba, int constructSide, int coreSide, float ox, float oy, float oz)
//	{
//		return new rendererFerrisBasket(ba,constructSide,coreSide,ox,oy,oz);
//	}
	
	public Side checkSide()
	{
		return Side.CLIENT;
	}
	
	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().thePlayer;
	}


}