//package jp.mochisystems.mfw._mc.handler;
//
//
//import net.minecraft.client.Minecraft;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.common.gameevent.TickEvent;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//@SideOnly(Side.CLIENT)
//public class handerClientRenderTick{
//
//	Minecraft mc;
//
//	public handerClientRenderTick(Minecraft minecraft)
//	{
//	    mc = minecraft;
//	}
//
//	public void onRenderTickPre(float partialTicks)
//	{
////		if (Minecraft.getMinecraft().isGamePaused()) {
////			return;
////	    }
////	    EntityPlayer player = this.mc.thePlayer;
////	    if (player == null) {
////	    	return;
////	    }
////	    entityPartSitEx seat = null;
////	    if (player.ridingEntity instanceof entityPartSitEx)
////	    {
////	    	seat = (entityPartSitEx)player.ridingEntity;
////	    }
////
////	    if ((seat != null))
////	    {
////			if (this.mc.inGameHasFocus && Display.isActive())
////	        {
////	            this.mc.mouseHelper.mouseXYChange();
////	            float f1 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
////	            float f2 = f1 * f1 * f1 * 8.0F;
////	            float f3 = (float)this.mc.mouseHelper.deltaX * f2;
////	            float f4 = (float)this.mc.mouseHelper.deltaY * f2;
////	            byte b0 = 1;
////
////	            if (this.mc.gameSettings.invertMouse)
////	            {
////	                b0 = -1;
////	            }
////
////	            {
////	            }
////	        }
////	    }
//	}
//
//
//	public void onTickPost()
//	{
////	    float r = 0;
////	    float pr = 0;
////		EntityPlayer player = this.mc.thePlayer;
////	    if (player != null) {
////	    	entityPartSitEx seat = null;
////	    	if (player.ridingEntity instanceof entityPartSitEx)
////	    	{
////	    		seat = (entityPartSitEx)player.ridingEntity;
////	    		r = seat.rotationRoll;
////	    		pr = seat.prevRotationRoll;
////	    	}
////	    }
//	}
//
//
////	@SubscribeEvent
////	public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
////	{
////		if (event.phase == TickEvent.Phase.START) {
//////			ERC_Logger.info("Player tick start");
////			onPlayerTickPre(event.player);
////		}
////		if (event.phase == TickEvent.Phase.END) {
////			onPlayerTickPost(event.player);
//////			ERC_Logger.info("Player tick end");
////		}
////	}
//
//	@SubscribeEvent
//	public void onClientTickEvent(TickEvent.ClientTickEvent event)
//	{
////		if (event.phase == TickEvent.Phase.START) {
//////			ERC_Logger.info("Client tick start");
////			onTickPre();
////			setTickcounter(getTickCounter() + 1);
////		}
//		if (event.phase == TickEvent.Phase.END) {
//			onTickPost();
////			ERC_Logger.info("Client tick end");
//		}
//	}
//
//	@SubscribeEvent
//	public void onRenderTickEvent(TickEvent.RenderTickEvent event)
//	{
//		if (event.phase == TickEvent.Phase.START) {
////			ERC_Logger.info("Render tick start");
//			onRenderTickPre(event.renderTickTime);
//		}
////		if (event.phase == TickEvent.Phase.END) {
////			onRenderTickPost(event.renderTickTime);
//////			ERC_Logger.info("Render tick end");
////		}
//	}
//
//}
