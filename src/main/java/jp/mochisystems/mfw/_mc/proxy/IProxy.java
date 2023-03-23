package jp.mochisystems.mfw._mc.proxy;

import jp.mochisystems.core._mc.renderer.BlocksRenderer;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public interface IProxy{
	void preInit();
	void init();
	void postInit();

	BlocksRenderer GetBlocksRenderer(MTYBlockAccess ba);
//	public rendererFerrisBasket getrendererFerrisBasket(MTYBlockAccess ba, int side, int coreSide, float ox, float oy, float oz);
	Side checkSide();
	
	EntityPlayer getClientPlayer();
	void PlayMFWSound(FerrisSelfMover part, int idx);
}