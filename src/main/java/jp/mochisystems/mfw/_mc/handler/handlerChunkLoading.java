//package jp.mochisystems.mfw._mc.handler;
//
//import java.util.List;
//
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.world.World;
//import net.minecraftforge.common.ForgeChunkManager;
//
//public class handlerChunkLoading implements ForgeChunkManager.LoadingCallback{
//
//	  public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
//	  {
//	    for (ForgeChunkManager.Ticket ticket : tickets)
//	    {
//	      int x = ticket.getModData().getInteger("CorePosX");
//	      int y = ticket.getModData().getInteger("CorePosY");
//	      int z = ticket.getModData().getInteger("CorePosZ");
//	      TileEntity te = world.getTileEntity(x, y, z);
//	      if ((te instanceof TileEntityChunkLoader)) {
//	        ((TileEntityChunkLoader)te).forceChunkLoading(ticket);
//	      }
//	    }
//	  }
//	}