//package jp.mochisystems.mfw._mc.tileEntity;
//
//import jp.mochisystems.mfw._mc._core.MFW_Core;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraftforge.common.ForgeChunkManager;
//
//public class TileEntityChunkLoader extends TileEntity {
//
//	private ForgeChunkManager.Ticket chunkTicket;
//
//	public void updateEntity()
//	{
//		super.updateEntity();
//		if ((!this.worldObj.isRemote) && (this.chunkTicket == null))
//		{
//			ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(MFW_Core.INSTANCE, this.worldObj, ForgeChunkManager.Type.NORMAL);
//			if (ticket != null)
//			{
//				forceChunkLoading(ticket);
//			}
//		}
//	}
//
//	public void invalidate()
//	{
//		super.invalidate();
//		stopChunkLoading();
//	}
//
//	public void setLoadDistance(int dist)
//	{
//		forceChunkLoading(this.chunkTicket);
//	}
//
//	public void forceChunkLoading(ForgeChunkManager.Ticket ticket)
//	{
//		stopChunkLoading();
//		this.chunkTicket = ticket;
//		ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(this.xCoord >> 4,this.zCoord >> 4));
//	}
//
//	public void unforceChunkLoading()
//	{
//		for (Object obj : this.chunkTicket.getChunkList())
//		{
//			ChunkCoordIntPair coord = (ChunkCoordIntPair) obj;
//			ForgeChunkManager.unforceChunk(this.chunkTicket, coord);
//		}
//	}
//
//	public void stopChunkLoading()
//	{
//		if (this.chunkTicket != null)
//		{
//			ForgeChunkManager.releaseTicket(this.chunkTicket);
//			this.chunkTicket = null;
//		}
//	}
//}
//=======
////package mfw._mc.tileEntity;
////
////import mfw._core.MFW_Core;
////import net.minecraft.tileentity.TileEntity;
////import net.minecraft.util.ITickable;
////import net.minecraft.world.ChunkCoordIntPair;
////import net.minecraftforge.common.ForgeChunkManager;
////
////public class TileEntityChunkLoader extends TileEntity implements ITickable {
////
////	private ForgeChunkManager.Ticket chunkTicket;
////
////	@Override
////	public void update()
////	{
////		if ((!this.world.isRemote) && (this.chunkTicket == null))
////		{
////			ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(MFW_Core.INSTANCE, this.world, ForgeChunkManager.Type.NORMAL);
////			if (ticket != null)
////			{
////				forceChunkLoading(ticket);
////			}
////		}
////	}
////
////	public void invalidate()
////	{
////		super.invalidate();
////		stopChunkLoading();
////	}
////
////	public void setLoadDistance(int dist)
////	{
////		forceChunkLoading(this.chunkTicket);
////	}
////
////	public void forceChunkLoading(ForgeChunkManager.Ticket ticket)
////	{
////		stopChunkLoading();
////		this.chunkTicket = ticket;
////		ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(this.xCoord >> 4,this.zCoord >> 4));
////	}
////
////	public void unforceChunkLoading()
////	{
////		for (Object obj : this.chunkTicket.getChunkList())
////		{
////			ChunkCoordIntPair coord = (ChunkCoordIntPair) obj;
////			ForgeChunkManager.unforceChunk(this.chunkTicket, coord);
////		}
////	}
////
////	public void stopChunkLoading()
////	{
////		if (this.chunkTicket != null)
////		{
////			ForgeChunkManager.releaseTicket(this.chunkTicket);
////			this.chunkTicket = null;
////		}
////	}
////}
//>>>>>>> Stashed changes:src/main/java/mfw/_mc/tileEntity/TileEntityChunkLoader.java
