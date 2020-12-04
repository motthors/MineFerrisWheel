package mfw._mc._1_7_10.entity;

import mfw.ferriswheel.*;
import mfw._mc._1_7_10.tileEntity.TileEntityFerrisCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._mc._1_7_10._core.MFW_Core;
import mochisystems._mc._1_7_10.block.itemBlockRemoteController;
import mochisystems.blockcopier.IModelCollider;
import mochisystems._mc._1_7_10.world.MTYBlockAccess;
import mochisystems.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFerrisCollider extends Entity implements IModelCollider, ILateUpdater {

	private final FerrisPartAddress parentTarget = new FerrisPartAddress();
	private FerrisWheel parent;
	private float theta;
	private float angleOffset;
	private int connectOffsetX;
	private int connectOffsetY;
	private int connectOffsetZ;
	private float rotCorrectX;
	private float rotCorrectY;
	private float rotCorrectZ;
	private float rotOffsetX;
	private float rotOffsetY;
	private float rotOffsetZ;
//	private Connector connector = new Connector(Vec3d.Zero, "");
	private EntityCollisionParts[] partsArray;
	private entityPartSit[] partSitArray;
	private int decompressSize;
	private byte[] compressedModelData;
	private int spawnFlagSlotIdx = -1;
	private int constructSide;
	private int parentMeta = -1;
	private float rot2;
	private boolean updateFlag = false;


//	public class entitySelector implements IEntitySelector{
//		@Override
//		public boolean isEntityApplicable(Entity entity)
//		{
//			boolean flag = (entity instanceof EntityFerrisCollider)
//					|| (entity instanceof EntityCollisionParts)
//					|| (entity instanceof entityPartSit)
//					|| (entity instanceof EntityRollingSeat)
//					|| entity.isRiding();
//			return !flag;
//		}
//	}
//	private entitySelector selector = new entitySelector();

	public EntityFerrisCollider(World world) {
		super(world);
		setSize(2.0f, 1.0f);

        theta = 0;

        renderDistanceWeight = Double.MAX_VALUE;
        this.preventEntitySpawning = true;
        partsArray = new EntityCollisionParts[]{new EntityCollisionParts(world, this, new Vec3d().CopyFrom(Vec3d.Up).add(Vec3d.Back))};
        partSitArray = new entityPartSit[]{new entityPartSit(world)};
	}

	public void setParentAddress(FerrisPartAddress address)
	{
		parentTarget.CopyFrom(address);
		UpdateTargetAddress(address);
	}

	public void makeColliders(MTYBlockAccess blockAccess)
	{
		int x = blockAccess.getSize(0);
		int y = blockAccess.getSize(1);
		int z = blockAccess.getSize(2);
		blockAccess.getBlockOrgPos(x, y, z);
	}

	private void UpdateTargetAddress(FerrisPartAddress address)
	{
		dataWatcher.updateObject(19, address.x);
		dataWatcher.updateObject(20, address.y);
		dataWatcher.updateObject(21, address.z);
		dataWatcher.updateObject(22, address.TreeListIndex);
	}

	public void Delete()
	{
		if(parent != null) parent.RemoveLateUpdater(this);
		setDead();
	}


	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(19, 0); // parent core x
		dataWatcher.addObject(20, -1); // paren core y
		dataWatcher.addObject(21, 0); // parent core z
		dataWatcher.addObject(22, 0); // parent core tree index
		dataWatcher.addObject(23, new Integer(0));
		dataWatcher.addObject(24, new Float(0)); // entitywidth
		dataWatcher.addObject(25, new Float(0)); // entityheight
		dataWatcher.addObject(26, new Integer(-1)); // wheelnum
		dataWatcher.addObject(27, new Integer(-1)); // slotnum
	}

	private void SyncTargetAddress(FerrisPartAddress address)
	{
		address.x = dataWatcher.getWatchableObjectInt(19);
		address.y = dataWatcher.getWatchableObjectInt(20);
		address.z = dataWatcher.getWatchableObjectInt(21);
		address.TreeListIndex = dataWatcher.getWatchableObjectInt(22);
	}

	public Entity[] getParts()
	{
		return this.partsArray;
    }

	public entityPartSit[] getPartsSit()
	{
		return this.partSitArray;
    }
	public boolean setPartsSit(int idx, entityPartSit e)
	{
		if(partSitArray == null)return false;
		if(partSitArray.length <= 0 || partSitArray.length <= idx)return false;
		partSitArray[idx] = e;
		return true;
    }

    @Override
	public AxisAlignedBB getBoundingBox()
    {
//        if(worldObj.isRemote)
//        	return boundingBox;
//        else
        	return null;
    }

    @Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		return null;
	}

	@Override
    public boolean canBeCollidedWith()
    {
        return false;//!this.isDead;
    }

	@Override
	public float getBrightness(float p_70013_1_)
	{
		return 15 << 20 | 15 << 4;
	}
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float p_70070_1_)
    {
		return 15 << 20 | 15 << 4;
//        int i = MathHelper.floor_double(this.posX);
//        int j = MathHelper.floor_double(this.posZ);
//
//        if (this.worldObj.blockExists(i, 0, j))
//        {
//            double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
//            int k = MathHelper.floor_double(this.posY - (double)this.yOffset + d0);
//            return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);w
//        }
//        else
//        {
//            return 0;
//        }
    }


	@Override
    protected boolean canTriggerWalking() {return false;}

	public void onChunkLoad() {} //�����Ɏg���邩��

	public boolean attackEntityFrom(DamageSource ds, float p_70097_2_)
    {
//    	boolean flag = ds.getEntity() instanceof EntityPlayer;// && ((EntityPlayer)ds.getEntity()).capabilities.isCreativeMode;
//
//	    if (flag)
//	    {
//	        setDead();
//	    }

    	return true;
    }

	@Override
	public boolean interactFirst(EntityPlayer player)
	{
		if(player.getHeldItem()==null)return true;
		if(player.getHeldItem().getItem() instanceof itemBlockRemoteController)
		{
//			itemBlockRemoteController.RegisterCore(player.getHeldItem(), parent.GetRootPart());
		}
		return true;
	}

	private void setPositionToRoot(FerrisWheel tile, Vec3 posInOut)
	{
//		float size = tile.Scale.get();
//		posInOut.CorePosX *= size;
//		posInOut.CorePosY *= size;
//		posInOut.CorePosZ *= size;
//		MFW_Math.rotateAroundVector(posInOut,
//				tile.rotvecConst_meta2.CorePosX, tile.rotvecConst_meta2.y, tile.rotvecConst_meta2.z,
//				Math.toRadians(-tile.rotConst_meta2));
//
//		MFW_Math.rotateAroundVector(posInOut, 0, 0, 1, Math.toRadians(-tile.rotation.get()));
//
//		MFW_Math.rotateAroundVector(posInOut, 0, 1, 0, Math.toRadians(-tile.yaw));
//		MFW_Math.rotateAroundVector(posInOut, 1, 0, 0, Math.toRadians(-tile.pitch));
//
//		MFW_Math.rotateAroundVector(posInOut,
//				tile.rotvecMeta2_side.CorePosX, tile.rotvecMeta2_side.y, tile.rotvecMeta2_side.z,
//				Math.toRadians(-tile.rotMeta2_side));
//
//
//		TileEntityFerrisCore nextparent = tile.getParentTile();
//		if(nextparent != null)
//		{
//			posInOut.CorePosX += tile.connectorFromParent.CorePosX;// * nextparent.Scale;
//			posInOut.CorePosY += tile.connectorFromParent.y;// * nextparent.Scale;
//			posInOut.CorePosZ += tile.connectorFromParent.z;// * nextparent.Scale;
//			setPositionToRoot(nextparent, posInOut);
//		}
	}

	private void setPosition()
	{
//		setPosition(
//				getBaseX()+Math.cos(getAngle()+rotOffsetX)*getLength()*rotCorrectX,      //connectOffsetX+
//				getBaseY()+Math.cos(getAngle()+rotOffsetY)*getLength()*rotCorrectY,      //connectOffsetY+
//				getBaseZ()+Math.cos(getAngle()+rotOffsetZ)*getLength()*rotCorrectZ);     //connectOffsetZ+e

//		Vec3 pos = Vec3.createVectorHelper(connector.Pos().x, connector.Pos().y, connector.Pos().z);
//		setPositionToRoot(parent, pos);
//		MFW_Math.rotateAroundVector(originalPos,
//				parent.rotvecMeta2_side.CorePosX, parent.rotvecMeta2_side.y, parent.rotvecMeta2_side.z,
//				Math.toRadians(-parent.rotMeta2_side));
//		MFW_Math.rotateAroundVector(originalPos,
//				parent.vecAxisRot.CorePosX, parent.vecAxisRot.y, parent.vecAxisRot.z,
//				Math.toRadians(-parent.rotation));


		this.posX = parent.controller.CorePosX() + parent.connectorFromParent.Current().x;
		this.posY = parent.controller.CorePosY() + parent.connectorFromParent.Current().y;
		this.posZ = parent.controller.CorePosZ() + parent.connectorFromParent.Current().z;
//		double sx = posX - connectOffsetX ;
//		double sy = posY - connectOffsetY ;
//		double sz = posZ - connectOffsetZ ;
//		this.boundingBox.setBounds(sx, sy, sz, sx+blocklenX, sy+blocklenY + 1, sz+blocklenZ);
		double size = (parent.getMaxSize()-1) * 0.5 * 1.7320508;
		boundingBox.setBounds(
				posX-size+1, posY-size+1, posZ-size+1,
				posX+size, posY+size, posZ+size);

//		for(EntityCollisionParts part : partsArray)
//		{
//			part.setPosition(posX, posY, posZ);
//		}
//		for(entityPartSit part : partSitArray)if(part!=null)
//		{
//			part.setPosition();
//		}
	}

	private boolean CheckParent()
	{
////		if(worldObj.isRemote==false)return false;
//		int CorePosX = getBaseX();
//		int y = getBaseY();
//		int z = getBaseZ();
//		TileEntityFerrisCore tile = (TileEntityFerrisCore) worldObj.getTileEntity(CorePosX, y, z);
//		if(tile==null)return false;
//		TileEntityFerrisCore loopHead = tile.getTileFromTreeIndex(getWheelIdx());
//		if(loopHead==null)return false;
//		if(loopHead.ArrayEntityParts.length <= getSlotIdx())return false;
//		if(loopHead.ArrayEntityParts[getSlotIdx()]==null)loopHead.ArrayEntityParts[getSlotIdx()] = this;
//		parent = loopHead;
//		loopHead.ValidatePart_clientBasket(getSlotIdx(), this);
		return true;
	}

	@Override
	public void onUpdate()
	{
		if(parent == null)
		{
			SyncTargetAddress(parentTarget);
			if(!parentTarget.isSyncing()) setDead();
			TileEntity tile = worldObj.getTileEntity(parentTarget.x, parentTarget.y, parentTarget.z);
			if(!(tile instanceof TileEntityFerrisCore))
			{
				Delete();
				return;
			}
			FerrisPartBase part = parentTarget.GetInstance((TileEntityFerrisCore)tile);
			if(!(part instanceof FerrisWheel))
			{
				Delete();
				return;
			}
			parent = (FerrisWheel) part;
			parent.AddLateUpdater(this);
			parent.collider = this;
		}
		if(parent.IsInvalid())
		{
			Delete();
			return;
		}

		super.onUpdate();
//		theta += 0.05;
//		if(theta>Math.PI*2)theta -= Math.PI*2;
//		float rot = parent.rotAngle.get();
////		if(worldObj.isRemote)rot+=2f;
//		setAngle((float) Math.toRadians(rot)+angleOffset);
//		setPosition();
	}

	@Override
	public void LateUpdate()
	{
		setPosition();
		for(EntityCollisionParts part : partsArray)
		{
			part.SetPosition(parent.connectorFromParent.Current(), parent.getRotation());
//			part.SetRotation();
		}
	}

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float ry, float rp, int p_70056_9_) {}

	private void setTileRotParam(int side)
	{
		rot2 = 0f;
		switch(side){
		case 2 :
			switch(parentMeta){
			case 3 : rot2 = 180f; break;
			case 4 : rot2 = 90f; break;
			case 5 : rot2 = -90f;  break;
			}break;
		case 3 :
			switch(parentMeta){
			case 2 : rot2 = 180f; break;
			case 4 : rot2 = -90f; break;
			case 5 : rot2 = 90f;  break;
			}break;
		case 4 :
			switch(parentMeta){
			case 2 : rot2 = -90f; break;
			case 3 : rot2 = 90f;  break;
			case 5 : rot2 = 180f; break;
			}break;
		case 5 :
			switch(parentMeta){
			case 2 : rot2 = 90f; break;
			case 3 : rot2 = -90f;  break;
			case 4 : rot2 = 180f; break;
			}break;
		}
	}

	public void openRootCoreGUI(EntityPlayer player)
	{
		player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, worldObj, parent.controller.CorePosX(), parent.controller.CorePosY(), parent.controller.CorePosZ());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		setBasePos(nbt.getInteger("tilex"),nbt.getInteger("tiley"),nbt.getInteger("tilez"));
		parentTarget.readFromNBT(nbt, "target");
//		setWheelIdx(nbt.getInteger("wheelidx"));
//		setSlotIdx(nbt.getInteger("slotidx"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		parentTarget.writeToNBT(nbt, "target");
	}

	private void setAngle(float angle)
	{
		dataWatcher.updateObject(19, Float.valueOf(angle));
	}

//	private float getAngle()
//	{
//		return dataWatcher.getWatchableObjectFloat(19);
//	}

	private void setLength(float len)
	{
		dataWatcher.updateObject(20, Float.valueOf(len));
	}

//	private float getLength()
//	{
//		return dataWatcher.getWatchableObjectFloat(20);
//	}

	private void setBasePos(int x, int y, int z)
	{
		dataWatcher.updateObject(21, Integer.valueOf(x));
		dataWatcher.updateObject(22, Integer.valueOf(y));
		dataWatcher.updateObject(23, Integer.valueOf(z));
	}

//	private int getBaseX(){return dataWatcher.getWatchableObjectInt(21);}
//	private int getBaseY(){return dataWatcher.getWatchableObjectInt(22);}
//	private int getBaseZ(){return dataWatcher.getWatchableObjectInt(23);}

//	public void setWheelIdx(int idx){dataWatcher.updateObject(26, Integer.valueOf(idx));}
//	private int getWheelIdx(){return dataWatcher.getWatchableObjectInt(26);}
//	public void setSlotIdx(int idx){dataWatcher.updateObject(27, Integer.valueOf(idx));}
//	private int getSlotIdx(){return dataWatcher.getWatchableObjectInt(27);}

	// renderer renderer
//	public rendererFerrisBasket BlocksVertex = MFW_Core.proxy.getrendererFerrisBasket(blockAccess,0,0,0f,0f,0f);
//	BlocksReplicator BlocksRep;

	private int blocklenX,blocklenY,blocklenZ;
	public void constructFromTag(NBTTagCompound nbt, int parentcside, int parentmeta, int xCoord, int yCoord, int zCoord, boolean isMultiThread)
	{
		if(worldObj==null)return;
//		if(isConstructed)return;
		constructSide = nbt.getByte("constructormetaflag") & 7;
		this.parentMeta = parentmeta;
//		BlocksRep = new BlocksReplicator(worldObj, blockAccess);
		connectOffsetX = nbt.getInteger("connectoffsetx");
		connectOffsetY = nbt.getInteger("connectoffsety");
		connectOffsetZ = nbt.getInteger("connectoffsetz");

		blocklenX = nbt.getInteger("mtybr:sizex");
		blocklenY = nbt.getInteger("mtybr:sizey");
		blocklenZ = nbt.getInteger("mtybr:sizez");
	}

//	public void completeConstruct(NBTTagCompound nbt)
//	{
//		setTileRotParam(parent.constructSide);
//        ArrayList<EntityCollisionParts> entitypartList = new ArrayList<EntityCollisionParts>();
//        ArrayList<entityPartSit> listPartsSit = new ArrayList<entityPartSit>();
//
//
//        //partsArray�p�����蔻��ݒ�
//        for(int x=0;x<blocklenX;++x){
//			for(int y=0;y<blocklenY;++y){
//				for(int z=0;z<blocklenZ;++z){
//					Block b = blockAccess.getBlockOrgPos(x, y, z);
//					//�t�̂Ȃ瓖���蔻��Ȃ�
//					if(b.getMaterial().isLiquid())continue;
//					//��C�u���b�N�͓����蔻��Ȃ�
//					if(Block.getIdFromBlock(b)==0)continue;
//					//�V�[�g�̓����蔻��͏���
//					if(b.canCollideCheck(blockAccess.getBLockMetadata_AbsolutePos(x, y, z), false)==false)continue;
//					//��ɃV�[�g����������ʘg��Entity�o�^����̂�Part�Ƃ��Ă̓����蔻��͏���
//					if(blockAccess.getBlockOrgPos(x, y+1, z) instanceof blockSeatToSitDown)
//					{
////						MFW_Logger.debugInfo("b:"+blockAccess.getBlockOrgPos(CorePosX, y+1, z)+"coreSide:"+blockAccess.getBLockMetadata_AbsolutePos(CorePosX, y+1, z));
//						//entityPartSit e = new entityPartSit(worldObj, this, blockAccess.getBLockMetadata_AbsolutePos(CorePosX, y+1, z));
////						e.setPosOffset(CorePosX-connectOffsetX, y-connectOffsetY, z-connectOffsetZ, parentMeta);
////						listPartsSit.add(e);
//						//addEntityPart(listPartsSit, e, CorePosX-connectOffsetX, y-connectOffsetY, z-connectOffsetZ, constructSide, parentMeta, true);
//						continue;
//					}
//					addEntityPart(entitypartList, new EntityCollisionParts(worldObj), x-connectOffsetX, y-connectOffsetY, z-connectOffsetZ, constructSide, parentMeta, false);
//				}
//			}
//	    }
//        // entitypart set
//        partsArray = new EntityCollisionParts[entitypartList.size()];
//        int i=0;
//        for(EntityCollisionParts e : entitypartList)
//        {
//        	// TODO 以下のコメントアウト全部一時的なアレ
////        	Block b = blockAccess.getBlock(e.bax+BlocksRep.getCTMX(), e.bay+BlocksRep.getCTMY(), e.baz+BlocksRep.getCTMZ());
////        	b.setBlockBoundsBasedOnState(blockAccess, e.bax, e.bay, e.baz);
////        	ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
////        	AxisAlignedBB aabb;
////        	try{
////        		b.addCollisionBoxesToList(worldObj, 0, 0, 0, TileEntity.INFINITE_EXTENT_AABB, list, this);
////        		aabb = b.getCollisionBoundingBoxFromPool(worldObj, 0, 0, 0);
////        	}catch(Exception exception){aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);}
////        	if(aabb!=null)e.setAbsoluteAABB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
////        	e.setParent(this);
////        	partsArray[i++] = e;
//        }
//        entitypartList.clear();
//
//        //entitypartSit set
//        partSitArray = new entityPartSit[listPartsSit.size()];
//        i=0;
//        if(worldObj.isRemote == false)
//        {
//	        for(entityPartSit e : listPartsSit)
//	        {
//	        	e.setMiscData(getBaseX(), getBaseY(), getBaseZ(), getSlotIdx(), getWheelIdx(), i);
//	        	partSitArray[i++] = e;
//	        	 worldObj.spawnEntityInWorld(e);
//	        }
//        }
//        fixEntityLength(parentMeta);
//	}


	public void fixEntityLength(int parentmeta)
	{
		if(parentmeta <= 1)return;
		int X, Z;
		int rotnum = fixrotforconnect(parent.constructSide) - fixrotforconnect(parentmeta);
		if(rotnum<0)rotnum+=4;
		for(int i=0; i<rotnum; ++i)
		{
			X=connectOffsetX; Z=connectOffsetZ;
			connectOffsetX = Z;
			connectOffsetZ = blocklenX-X-1;
			X=blocklenX; Z=blocklenZ;
			blocklenX = Z; blocklenZ = X;
		}
	}
	private int fixrotforconnect(int i){
		switch(i){case 2: return 0; case 4: return 3; case 3: return 2; case 5: return 1; default: return 0;}
	}

	//////////////////////////////////////////// wrap_ferrispart

//	public void init(TileEntityFerrisCore loopHead, Connector cp, int currentidx)
//	{
//		parent = loopHead;
//		setSlotIdx(currentidx);
//		setWheelIdx(loopHead.getTreeIndexFromTile(loopHead));
//		angleOffset = cp.angle;
//		cp.copy(connectorFromParent);
////		fixConnectorWithParentMeta(loopHead.coreSide);
////		MFW_Logger.debugInfo("debug connectpos : CorePosX:"+cp.CorePosX+", y:"+cp.y+", z:"+cp.z);
//		setLength(cp.len);
//		setBasePos(loopHead.CorePosX,loopHead.CorePosY,loopHead.CorePosZ);
//		setPosition();
//		switch(loopHead.coreSide)
//		{
//		case 0: rotCorrectX = 1; rotCorrectY = 0; rotCorrectZ = 1; break;
//		case 1: rotCorrectX = 1; rotCorrectY = 0; rotCorrectZ =-1; break;
//		case 2: rotCorrectX = 1; rotCorrectY = 1; rotCorrectZ = 0; break;
//		case 3: rotCorrectX =-1; rotCorrectY = 1; rotCorrectZ = 0; break;
//		case 4: rotCorrectX = 0; rotCorrectY =-1; rotCorrectZ =-1; break;
//		case 5: rotCorrectX = 0; rotCorrectY = 1; rotCorrectZ = 1; break;
//		}
//		switch(loopHead.coreSide)
//		{
//		case 0: rotOffsetX = 0; rotOffsetY = 0; rotOffsetZ =(float)(-Math.PI/2d); break;
//		case 1: rotOffsetX = 0; rotOffsetY = 0; rotOffsetZ = (float)(Math.PI/2d); break;
//		case 2: rotOffsetX = 0; rotOffsetY =(float)(-Math.PI/2d); rotOffsetZ = 0; break;
//		case 3: rotOffsetX = 0; rotOffsetY = (float)(Math.PI/2d); rotOffsetZ = 0; break;
//		case 4: rotOffsetX = 0; rotOffsetY = 0; rotOffsetZ =(float)(-Math.PI/2d); break;
//		case 5: rotOffsetX = 0; rotOffsetY = 0; rotOffsetZ = (float)(Math.PI/2d); break;
//		}
//	}
//	public boolean isTile()
//	{
//		return false;
//	}
//	public void updateChildren()
//	{
//		return;
//	}
//	public void renderThis(double CorePosX, double y, double z, float f)
//	{
//		return;
//	}
//
//	@Override
//	public void renderThisPostPass(double CorePosX, double y, double z, float f)
//	{
//		return;
//	}
//	public void dead()
//	{
//		setDead();
//		if(worldObj.isRemote)BlocksRep.invalidate();
//	}
//
//	@Override
//	public void writeChildToNBT(NBTTagCompound nbt, int layer) {}
//
//	@Override
//	public void readChildFromNBT(NBTTagCompound nbt, int layer) {}
//
//	@Override
//	public void readMineFromNBT(NBTTagCompound nbt, int snum)
//	{
//		parentMeta = nbt.getInteger("coreSide");
//		compressedModelData = nbt.getByteArray("compressedbytearray");
//	}
//
//	@Override
//	public void writeMineToNBT(NBTTagCompound nbt, int slotidx)
//	{
//		nbt.setInteger("coreSide", parentMeta);
//		nbt.setInteger("decompresssize", decompressSize);
//		if(compressedModelData!=null)nbt.setByteArray("compressedbytearray", compressedModelData);
//	}

}
