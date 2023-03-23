package jp.mochisystems.mfw._mc.proxy;

import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.renderer.BlocksRenderer;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.mfw._mc.gui.container.ContainerFerrisCore;
import jp.mochisystems.mfw.ferriswheel.FerrisElevator;
import jp.mochisystems.mfw.ferriswheel.FerrisGarland;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;
import jp.mochisystems.mfw.ferriswheel.FerrisWheel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy implements IProxy{
	

	@Override
	public void preInit()
	{

//		ERC_Core.tickEventHandler = new TickEventHandler();
//		FMLCommonHandler.instance().bus().register(ERC_Core.tickEventHandler);
	}

	@Override
	public void init() {
		GUIHandler.RegisterBlockModelGui(FerrisWheel.class, this, null, ContainerFerrisCore.class);
		GUIHandler.RegisterBlockModelGui(FerrisElevator.class, this, null, ContainerFerrisCore.class);
		GUIHandler.RegisterBlockModelGui(FerrisGarland.class, this, null, ContainerFerrisCore.class);
	}

	@Override
	public void postInit() {}
	
	
	public BlocksRenderer GetBlocksRenderer(MTYBlockAccess ba)
	{
		return null;
	}
//	public rendererFerrisBasket getrendererFerrisBasket(MTYBlockAccess ba, int side, int meta, float ox, float oy, float oz)
//	{
//		return null;
//	}
	
	public Side checkSide()
	{
		return Side.SERVER;
	}
	
	public EntityPlayer getClientPlayer()
	{
		return null;
	}

	public void PlayMFWSound(FerrisSelfMover part, int idx)
	{

	}
}
