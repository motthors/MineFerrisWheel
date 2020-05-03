package mfw._core.proxy;

import cpw.mods.fml.relauncher.Side;
import mochisystems.blockcopier.BlocksRenderer;
import mochisystems.blockcopier.MTYBlockAccess;
import net.minecraft.entity.player.EntityPlayer;

public interface IProxy{
	public int getNewRenderType();
	public void preInit();
	public void init();
	public void postInit();
	
	public BlocksRenderer getrendererFerrisWheel(MTYBlockAccess ba);
//	public rendererFerrisBasket getrendererFerrisBasket(MTYBlockAccess ba, int side, int coreSide, float ox, float oy, float oz);
	public Side checkSide();
	
	public EntityPlayer getClientPlayer();
}