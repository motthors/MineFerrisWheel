package mfw._core.proxy;

import cpw.mods.fml.relauncher.Side;
import mochisystems.blockcopier.BlocksRenderer;
import mochisystems.blockcopier.MTYBlockAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IProxy{
	int getNewRenderType();
	void preInit();
	void init();
	void postInit();
	
	BlocksRenderer getrendererFerrisWheel(MTYBlockAccess ba);
//	public rendererFerrisBasket getrendererFerrisBasket(MTYBlockAccess ba, int side, int coreSide, float ox, float oy, float oz);
	Side checkSide();
	
	EntityPlayer getClientPlayer();
	boolean CanPlaceBlock(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack);
}