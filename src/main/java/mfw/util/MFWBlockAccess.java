package mfw.util;

import mfw._mc._1_7_10._core.MFW_Core;
import mochisystems.math.Vec3d;
import mochisystems._mc._1_7_10.world.MTYBlockAccess;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class MFWBlockAccess extends MTYBlockAccess{

	int copyNum = 1;
	int copyMode = 0;
	Vec3d vecForCopyRotAxis;
//	public List<Connector> listConnectPos = new ArrayList<>();
//	public class sortConnectPos implements java.util.Comparator<Connector>{
//		public int compare(Connector s, Connector t) {
//			float sub = s.angle - t.angle;
//			if ( 0.02 > java.lang.Math.abs(sub))return (int) java.lang.Math.ceil(s.len - t.len);
//			return (sub>0)?1:-1;
//		}
//	}

//	public List<entityPartSitEx> listEntitySeatEx = new ArrayList<entityPartSitEx>();

	public MFWBlockAccess(World world)
	{
		super(world);
		vecForCopyRotAxis = new Vec3d(0, 0, 0);
	}

	public void setCopyNum(int num, int constructormeta, int mode)
	{
		copyNum = num;
		copyMode = mode;
		switch(constructormeta)
		{
			case 0 : vecForCopyRotAxis.y = -1; break;
			case 1 : vecForCopyRotAxis.y = 1; break;
			case 2 : vecForCopyRotAxis.z = 1; break;
			case 3 : vecForCopyRotAxis.z = -1; break;
			case 4 : vecForCopyRotAxis.x = 1; break;
			case 5 : vecForCopyRotAxis.x = -1; break;
		}
	}

	@Override
	public void postInit()
	{
		super.postInit();
//		Collections.sort(listConnectPos, new sortConnectPos());
		float rotoffset = 360f / ((float)copyNum);
//		Connector list[] = new Connector[listConnectPos.size()];
//		entityPartSitEx seatlist[] = new entityPartSitEx[listEntitySeatEx.size()];
//		listConnectPos.toArray(list);
//		listEntitySeatEx.toArray(seatlist);
//		if(copyMode==0)
//		{
//			for(int i=1; i < copyNum; ++i)
//			{
//				for(Connector cOrg : list)
//				{
//					Connector cNew = new Connector();
//					cNew.len = cOrg.len;
//					cNew.angle = cOrg.len + i*rotoffset;
//					Vec3d p = cOrg.originalPos;
//					Vec3d a = vecForCopyRotAxis;
//					Math.rotateAroundVector(p, p, a, Math.toRadians(-i*rotoffset));
//					cNew.originalPos.CopyFrom(p);
//					listConnectPos.add(cNew);
//				}
//				for(entityPartSitEx e : seatlist)
//				{
//					Vec3d p = new Vec3d(e.getOffsetX(), e.getOffsetY()+1, e.getOffsetZ());
//					Vec3d a = vecForCopyRotAxis;
//					Math.rotateAroundVector(p, p, a, Math.toRadians(-i*rotoffset));
//					setSeatEx((float)p.CorePosX, (float)p.y, (float)p.z, (int)MFW_Math.wrap(e.getSeatAngle()-i*rotoffset));
//				}
//			}
//		}
	}

	@Override
	public void setBlock(Block block, int meta, int worldX, int worldY, int worldZ)
	{
		if(block ==MFW_Core.ferrisSeatEx)
		{
			setSeatEx(worldX, worldY, worldZ, meta*90);
			return;
		}
		super.setBlock(block, meta, worldX, worldY, worldZ);
	}

//	public void setConnectPos(int CorePosX, int y, int z)
//	{
//		Connector c = new Connector();
//		c.originalPos.SetFrom(CorePosX, y, z);
//		c.len = (float) java.lang.Math.sqrt(CorePosX*CorePosX+y*y+z*z);
//		c.angle = (float) java.lang.Math.atan2(-CorePosX, y);
//		listConnectPos.add(c);
//	}

	public void setSeatEx(float x, float y, float z, int angle)
	{
//		if(MFW_Core.proxy.checkSide().isClient())return;
		if(getWorld()==null)return;
		if(getWorld().isRemote)return;
//		entityPartSitEx e = new entityPartSitEx(getWorld(), loopHead, -1000, CorePosX, y, z, angle);
//		listEntitySeatEx.add(e);
	}

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

	public void invalidate() // TODO
	{
//		if(MFW_Core.proxy.checkSide().isClient())return;
		if(getWorld().isRemote)return;
//		for(Entity e : listEntitySeatEx)
//		{
//			e.setDead();
//		}
//		listEntitySeatEx.clear();
	}
}
