package mfw._core.proxy;

import cpw.mods.fml.relauncher.Side;
import mochisystems.blockcopier.BlocksRenderer;
import mochisystems.blockcopier.MTYBlockAccess;
import net.minecraft.entity.player.EntityPlayer;

public class ServerProxy implements IProxy{
	
	@Override
	public int getNewRenderType()
	{
		return -1;
	}

	@Override
	public void preInit()
	{

//		ERC_Core.tickEventHandler = new TickEventHandler();
//		FMLCommonHandler.instance().bus().register(ERC_Core.tickEventHandler);
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}
	
	
	public BlocksRenderer getrendererFerrisWheel(MTYBlockAccess ba)
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
	
	
}
