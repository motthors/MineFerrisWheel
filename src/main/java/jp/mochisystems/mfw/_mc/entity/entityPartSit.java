//package jp.mochisystems.mfw._mc.entity;
//
//import jp.mochisystems.core.math.Vec3d;
//import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisCore;
//import jp.mochisystems.core.util.CommonAddress;
//import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
//import jp.mochisystems.mfw.ferriswheel.FerrisWheel;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.network.datasync.DataParameter;
//import net.minecraft.network.datasync.DataSerializers;
//import net.minecraft.network.datasync.EntityDataManager;
//import net.minecraft.util.DamageSource;
//import net.minecraft.util.EnumHand;
//import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//public class entityPartSit extends Entity {
//
////	AxisAlignedBB absoluteAABB;
//    FerrisWheel parent = null;
//	public boolean updateFlag = false;
//	public boolean waitUpdateRiderFlag = false;
//
//	public entityPartSit(World world)
//	{
//		super(world);
//		setSize(1.0f, 1.0f);
//	}
//
//	public entityPartSit(World world, FerrisWheel parent, int meta)
//	{
//		super(world);
//		this.parent = parent;
////		setSeatHeight((meta == 0? 0 : -0.5f));
//		posX = parent.connectorFromParent.Current().x;
//		posY = parent.connectorFromParent.Current().y;
//		posZ = parent.connectorFromParent.Current().z;
//	}
//
//	@Override
//	public double getMountedYOffset()
//    {
////		@SuppressWarnings("unused")
////		float a = getSeatHeight();
//        return (double)this.height;// + getSeatHeight();
//    }
//
//
//	public void setEntityBoundingBox(AxisAlignedBB bb)
//	{
//		super.setEntityBoundingBox(bb);
////		this.boundingBox = bb;
//	}
//
//
//	public void setPosOffset(float x, float y, float z)
//	{
//		setOffsetX(x+0.5f);
//		setOffsetY(y);
//		setOffsetZ(z+0.5f);  // coreSide�ŉ�]����ĂȂ��H
//	}
//	public void setMiscData(int bax, int bay, int baz, int currentidx, int wheelidx, int partseatidx)
//	{
//		setBasePos(bax, bay, baz);
//		setSlotIdx(currentidx);
//		setWheelIdx(wheelidx);
////		setPartseatIndex(partseatidx);
//	}
//
//	public boolean attackEntityFrom(DamageSource ds, float p_70097_2_)
//    {
//    	return true;
//    }
//
//	public boolean canBeCollidedWith()
//    {
//        return world.isRemote;
//    }
//
//	public AxisAlignedBB getEntityBoundingBox()
//    {
//        return world.isRemote?getEntityBoundingBox():null;
//    }
//
////	public void setAbsoluteAABB(double minx, double miny, double minz, double maxx, double maxy, double maxz)
////	{
////		absoluteAABB.setBounds(minx, miny, minz, maxx, maxy, maxz);
////	}
//
//	@Override
//	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
//	{
//		if(parent==null)return true;
//		if (player.isSneaking()) return false;
//
//		if (!this.world.isRemote)
//		{
//			player.startRiding(this);
//		}
//		return true;
//	}
//
//	@Override
//	protected boolean canFitPassenger(Entity passenger)
//	{
//		return this.getPassengers().size() < 1;
//	}
//
//    protected void setPosition()
//    {
//    	prevPosX = posX;
//    	prevPosY = posY;
//    	prevPosZ = posZ;
//    	if(parent==null)return;
//
//    	Vec3d pos = parent.connectorFromParent.Current();
////    	loopHead.setPositionToRoot(loopHead.parentTile, originalPos);
//    	this.posX = pos.x/**parentTile.wheelSize + parentTile.posX */+ getBaseX() + getOffsetX();
//		this.posY = pos.y/**parentTile.wheelSize + parentTile.posY */+ getBaseY() + getOffsetY();
//		this.posZ = pos.z/**parentTile.wheelSize + parentTile.posZ */+ getBaseZ() + getOffsetZ();
//
////        this.posX = loopHead.posX + getOffsetX();
////        this.posY = loopHead.posY + getOffsetY();
////        this.posZ = loopHead.posZ + getOffsetZ();
////        if(absoluteAABB==null)return;
//        double wx = 0.5;//(absoluteAABB.minX+absoluteAABB.maxX)/2;
//        double wz = 0.5;//(absoluteAABB.minZ+absoluteAABB.maxZ)/2;
//        double hu = 1.0;//this.absoluteAABB.maxY;
//        double hd = 0.0;//this.absoluteAABB.minY;
//        this.setEntityBoundingBox(new AxisAlignedBB(posX-wx, posY+hd, posZ-wz, posX+wx, posY+hu, posZ+wz));
//
////        if(worldObj.isRemote)MFW_Logger.debugInfo("seat setPosition y:"+posY);
////    	if(waitUpdateRiderFlag)super.updateRiderPosition();
////        if(riddenByEntity!=null)updateRiderPosition();
//    }
//
//    protected boolean CheckParent()
//	{
////        CommonAddress address = new CommonAddress();
////        address.x = getBaseX();
////        address.y = getBaseY();
////        address.z = getBaseZ();
////        address.TreeListIndex = getWheelIdx();
////        TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(new BlockPos(address.x, address.y, address.z));
////		FerrisPartBase parent = address.GetInstance(parent.controller);
////		if(parent==null)return false;
//		return false;
//	}
//
//    @Override
//	public void onUpdate()
//	{
//    	if(parent == null)
//    	{
//    		if(!CheckParent())
//			{
//				setDead();
//				return;
//			}
//    	}
//    	if(parent.IsInvalid())
//    	{
//    		setDead();
//    		return;
//    	}
////    	super.onUpdate();
//    	setPosition();
//	}
//
//    public void setDead()
//    {
//        super.setDead();
//    }
//
//
//
////    public void updateRiderPosition2()
////    {
//////    	MFW_Logger.debugInfo("updateRiderPosition2 > super");
////    	super.updateRiderPosition();
////    }
//
//	@Override
//	protected void readEntityFromNBT(NBTTagCompound nbt)
//	{
////		setSeatIndex(nbt.getInteger("seatindex"));
//		setOffsetX(nbt.getFloat("seatoffsetx"));
//		setOffsetY(nbt.getFloat("seatoffsety"));
//		setOffsetZ(nbt.getFloat("seatoffsetz"));
////		setBasePos(nbt.getInteger("tilex"),nbt.getInteger("tiley"),nbt.getInteger("tilez"));
//		setWheelIdx(nbt.getInteger("wheelidx"));
//		setSlotIdx(nbt.getInteger("slotidx"));
////		setSeatHeight(nbt.getFloat("seatheight"));
//	}
//
//	@Override
//	protected void writeEntityToNBT(NBTTagCompound nbt)
//	{
////		nbt.setInteger("seatindex", getSeatIndex());
//		nbt.setFloat("seatoffsetx", getOffsetX());
//		nbt.setFloat("seatoffsety", getOffsetY());
//		nbt.setFloat("seatoffsetz", getOffsetZ());
////		nbt.setInteger("tilex", getBaseX());
////		nbt.setInteger("tiley", getBaseY());
////		nbt.setInteger("tilez", getBaseZ());
//		nbt.setInteger("wheelidx", getWheelIdx());
//		nbt.setInteger("slotidx", getSlotIdx());
////		nbt.setFloat("seatheight", getSeatHeight());
//	}
//
//
//	@Override
//	protected void entityInit()
//	{
////		dataManager.register(21, new Integer(-1));	// seatIndex
//		dataManager.register(OFFSET_X, 0f);	// offsetX
//		dataManager.register(OFFSET_Y, 0f);	// offsetY
//		dataManager.register(OFFSET_Z, 0f);	// offsetZ
//		dataManager.register(BASE_X, 0);	// offsetX
//		dataManager.register(BASE_Y, 0);	//
//		dataManager.register(BASE_Z, 0);	//
//		dataManager.register(WHEEL_IDX, 0);	//
//		dataManager.register(SLOT_IDX, 0);	//
////		dataManager.register(30, new Float(0f));	// seatheight
////		dataManager.register(31, new Integer(0));  // loopHead basket  partseatindex
//	}
//
//	private static final DataParameter<Float> OFFSET_X = EntityDataManager.createKey(entityPartSit.class, DataSerializers.FLOAT);
//	public float getOffsetX()
//	{
//		return dataManager.get(OFFSET_X);
//	}
//	public void setOffsetX(float offsetx)
//	{
//		dataManager.set(OFFSET_X, offsetx);
//	}
//
//	private static final DataParameter<Float> OFFSET_Y = EntityDataManager.createKey(entityPartSit.class, DataSerializers.FLOAT);
//	public float getOffsetY()
//	{
//		return dataManager.get(OFFSET_Y);
//	}
//	public void setOffsetY(float offsety)
//	{
//		dataManager.set(OFFSET_Y, offsety);
//	}
//
//	private static final DataParameter<Float> OFFSET_Z = EntityDataManager.createKey(entityPartSit.class, DataSerializers.FLOAT);
//	public float getOffsetZ()
//	{
//		return dataManager.get(OFFSET_Z);
//	}
//	public void setOffsetZ(float offsetz)
//	{
//		dataManager.set(OFFSET_Z, offsetz);
//	}
//
//	private static final DataParameter<Integer> BASE_X = EntityDataManager.createKey(entityPartSit.class, DataSerializers.VARINT);
//	private static final DataParameter<Integer> BASE_Y = EntityDataManager.createKey(entityPartSit.class, DataSerializers.VARINT);
//	private static final DataParameter<Integer> BASE_Z = EntityDataManager.createKey(entityPartSit.class, DataSerializers.VARINT);
//	protected void setBasePos(int x, int y, int z)
//	{
////		dataManager.set(BASE_X, Integer.valueOf(x));
////		dataManager.set(BASE_Y, Integer.valueOf(y));
////		dataManager.set(BASE_Z, Integer.valueOf(z));
//	}
//	protected int getBaseX(){return dataManager.get(BASE_X);}
//	protected int getBaseY(){return dataManager.get(BASE_Y);}
//	protected int getBaseZ(){return dataManager.get(BASE_Z);}
//
//	private static final DataParameter<Integer> WHEEL_IDX = EntityDataManager.createKey(entityPartSit.class, DataSerializers.VARINT);
//	public void setWheelIdx(int idx){dataManager.set(WHEEL_IDX, idx);}
//	protected int getWheelIdx(){return dataManager.get(WHEEL_IDX);}
//
//	private static final DataParameter<Integer> SLOT_IDX = EntityDataManager.createKey(entityPartSit.class, DataSerializers.VARINT);
//	public void setSlotIdx(int idx){dataManager.set(SLOT_IDX, idx);}
//	protected int getSlotIdx(){return dataManager.get(SLOT_IDX);}
//
////	public void setSeatHeight(float h){dataManager.updateObject(30, Float.valueOf(h));}
////	public float getSeatHeight(){return dataManager.getWatchableObjectFloat(30);}
//
////	public void setPartseatIndex(int idx){dataManager.updateObject(31, Integer.valueOf(idx));}
////	public int getPartseatIndex(){return dataManager.getWatchableObjectInt(31);}
//}
