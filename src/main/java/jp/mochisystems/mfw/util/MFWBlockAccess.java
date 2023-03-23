package jp.mochisystems.mfw.util;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.block.BlockSeatPositionMarker;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityConnector;
import jp.mochisystems.core.util.Connector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MFWBlockAccess extends MTYBlockAccess {

	int copyNum = 1;
	int copyMode = 0;
	Vec3d vecForCopyRotAxis;
	public final List<Connector> connectors;


	public MFWBlockAccess(List<Connector> connectors)
	{
//		vecForCopyRotAxis = new Vec3d(0, 0, 0);
		this.connectors = connectors;
	}

	public void setCopyNum(int num, Vec3d rotSrc, int mode)
	{
		copyNum = num;
		copyMode = mode;
		vecForCopyRotAxis = rotSrc;
//		switch(constructormeta)
//		{
//			case 0 : vecForCopyRotAxis.y = 1; break;
//			case 1 : vecForCopyRotAxis.y =-1; break;
//			case 2 : vecForCopyRotAxis.z = 1; break;
//			case 3 : vecForCopyRotAxis.z =-1; break;
//			case 4 : vecForCopyRotAxis.x = 1; break;
//			case 5 : vecForCopyRotAxis.x =-1; break;
//		}
	}

	@Override
	public void postInit()
	{
		super.postInit();
		double rotOffset = Math.PI*2 / copyNum;
		List<Connector> src = new ArrayList<>(connectors);
		List<Connector> seatSrc = new ArrayList<>(listSeat);
		if(copyMode==0)
		{
			for(int i=1; i < copyNum; ++i)
			{
//				for(Connector c : src)
//				{
//					Connector cNew = new Connector(c.GetName()+":"+i);
//					cNew.copyFrom(c);
//					cNew.SetOrigin(cNew.Current().Rotate(vecForCopyRotAxis, i*rotOffset));
////					Vec3d p = c.originalPos;
////					Vec3d a = vecForCopyRotAxis;
////					Math.rotateAroundVector(p, p, a, Math.toRadians(-i*rotOffset));
////					cNew.originalPos.CopyFrom(p);
//					connectors.add(cNew);
//				}
				int seatIdx = 0;
				for(Connector c : seatSrc)
				{
					Connector cNew = new Connector(c.GetName()+":"+i);
					cNew.copyFrom(c);
					cNew.SetOrigin(cNew.Current().Rotate(vecForCopyRotAxis, i*rotOffset));
					listSeat.add(cNew);
					listSeatRot.add(new Quaternion().CopyFrom(listSeatRot.get(seatIdx)).mul(new Quaternion().Make(vecForCopyRotAxis, -i*rotOffset)));
//					Vec3d p = new Vec3d(e.getOffsetX(), e.getOffsetY()+1, e.getOffsetZ());
//					Vec3d a = vecForCopyRotAxis;
//					Math.rotateAroundVector(p, p, a, Math.toRadians(-i*rotOffset));
//					setSeatEx((float)p.CorePosX, (float)p.y, (float)p.z, (int)MFW_Math.wrap(e.getSeatAngle()-i*rotOffset));
					seatIdx++;
				}
			}
		}
	}

	@Override
	public void setBlock(IBlockState state, int worldX, int worldY, int worldZ)
	{
		if(state.getBlock() == MFW.ferrisConnector)
		{
			return;
		}
		super.setBlock(state, worldX, worldY, worldZ);
	}



//	public void setTileEntity(TileEntity tile, int worldX, int worldY, int worldZ)
//	{
//		if(getBlockState(new BlockPos(worldX, worldY, worldZ)).getBlock()
//				== MFW.ferrisConnector)
//		{
//			TileEntityConnector TILE = (TileEntityConnector) tile;
//			Connector c = new Connector(TILE.GetName());
//			int x = LocalFromWorldX(worldX);
//			int y = LocalFromWorldY(worldY);
//			int z = LocalFromWorldZ(worldZ);
//			c.SetOrigin(new Vec3d(x, y, z));
//			connectors.add(c);
//			return;
//		}
//		super.setTileEntity(tile, worldX, worldY, worldZ);
//	}

//	public void setSeatEx(float x, float y, float z)
//	{
////		if(MFW_Core.proxy.checkSide().isClient())return;
////		if(getWorld()==null)return;
////		if(getWorld().isRemote)return;
////		entityPartSitEx e = new entityPartSitEx(getWorld(), loopHead, -1000, CorePosX, y, z, angle);
//		Connector c = new Connector("");
//		c.SetOrigin(new Vec3d(x, y, z));
//		listEntitySeatEx.add(c);
//	}

//	public int getConnectorNum()
//	{
//		return listConnectPos.size();
//	}

//	public void updateFirstAfterConstruct()
//	{
//		if(getWorld().isRemote)return;
////		for(Entity e : listEntitySeatEx)
////		{
////			getWorld().spawnEntityInWorld(e);
////		}
//	}

}
