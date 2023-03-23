package jp.mochisystems.mfw._mc.proxy;

import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.renderer.BlocksRenderer;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.util.gui.GuiDragController;
import jp.mochisystems.core._mc.eventhandler.KeyHandlerClient;
import jp.mochisystems.mfw._mc.gui.container.ContainerFerrisCore;
import jp.mochisystems.mfw._mc.gui.gui.GUIFerrisElevator;
import jp.mochisystems.mfw._mc.gui.gui.GUIFerrisGarland;
import jp.mochisystems.mfw._mc.gui.gui.GUIFerrisSelfMover;
import jp.mochisystems.mfw.ferriswheel.FerrisElevator;
import jp.mochisystems.mfw.ferriswheel.FerrisGarland;
import jp.mochisystems.mfw.ferriswheel.FerrisWheel;
import jp.mochisystems.mfw.sound.SoundLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy implements IProxy{
	
	@Override
	public void preInit()
	{	
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
//		MinecraftForge.EVENT_BUS.register(new renderEventCompileWheel());
//		MinecraftForge.EVENT_BUS.register(new FerrisArmorRenderer());
	}

	@Override
	public void init()
	{
		KeyHandlerClient.init();
		FMLCommonHandler.instance().bus().register(new KeyHandlerClient());
//		FMLCommonHandler.instance().bus().register(new handlerClientConnected());

		GUIHandler.RegisterBlockModelGui(FerrisWheel.class, this, GUIFerrisSelfMover.class, ContainerFerrisCore.class);
		GUIHandler.RegisterBlockModelGui(FerrisElevator.class, this, GUIFerrisElevator.class, ContainerFerrisCore.class);
		GUIHandler.RegisterBlockModelGui(FerrisGarland.class, this, GUIFerrisGarland.class, ContainerFerrisCore.class);

		SoundLoader.Instance.Load();
		ForgeRegistries.SOUND_EVENTS.registerAll(SoundLoader.Instance.events.toArray(new SoundEvent[0]));
	}

	@Override
	public void postInit() 
	{
		GuiDragController.ResetSpecialCamera();
	}
	
	public BlocksRenderer GetBlocksRenderer(MTYBlockAccess ba)
	{
		return new BlocksRenderer(ba);
	}

	public Side checkSide()
	{
		return Side.CLIENT;
	}
	
	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().player;
	}

}