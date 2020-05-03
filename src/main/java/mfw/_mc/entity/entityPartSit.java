package mfw._mc.entity;

import mfw.ferriswheel.FerrisPartAddress;
import mfw.ferriswheel.FerrisPartBase;
import mfw.ferriswheel.FerrisWheel;
import mfw._mc.tileEntity.TileEntityFerrisCore;
import mochisystems.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class entityPartSit extends Entity {

//	double ox,oy,oz;
//	int bax,bay,baz; //blockAccess org position
	AxisAlignedBB absoluteAABB;
    FerrisWheel parent;
	public boolean updateFlag = false;
	public boolean waitUpdateRiderFlag = false;

	private void constructorInit()
	{
		setSize(1.0f, 1.0f);
		absoluteAABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}

	public entityPartSit(World world)
	{
		super(world);
		constructorInit();
	}

	public entityPartSit(World world, FerrisWheel parent, int meta)
	{
		super(world);
		constructorInit();
		this.parent = parent;
		setSeatHeight((meta == 0? 0 : -0.5f));
		posX = parent.connectorFromParent.Current().x;
		posY = parent.connectorFromParent.Current().y;
		posZ = parent.connectorFromParent.Current().z;
	}

	@Override
	public double getMountedYOffset()
    {
//		@SuppressWarnings("unused")
//		float a = getSeatHeight();
        return (double)this.height + getSeatHeight();
    }
	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(21, new Integer(-1));	// seatIndex
		dataWatcher.addObject(22, new Float(0f));	// offsetX
		dataWatcher.addObject(23, new Float(0f));	// offsetY
		dataWatcher.addObject(24, new Float(0f));	// offsetZ
		dataWatcher.addObject(25, new Integer(0));	// offsetX
		dataWatcher.addObject(26, new Integer(0));	//
		dataWatcher.addObject(27, new Integer(0));	//
		dataWatcher.addObject(28, new Integer(0));	//
		dataWatcher.addObject(29, new Integer(0));	//
		dataWatcher.addObject(30, new Float(0f));	// seatheight
		dataWatcher.addObject(31, new Integer(0));  // loopHead basket  partseatindex
	}

	public void setPosOffset(float x, float y, float z)
	{
		setOffsetX(x+0.5f);
		setOffsetY(y);
		setOffsetZ(z+0.5f);  // coreSide�ŉ�]����ĂȂ��H
	}
	public void setMiscData(int bax, int bay, int baz, int currentidx, int wheelidx, int partseatidx)
	{
		setBasePos(bax, bay, baz);
		setSlotIdx(currentidx);
		setWheelIdx(wheelidx);
		setPartseatIndex(partseatidx);
	}

	public boolean attackEntityFrom(DamageSource ds, float p_70097_2_)
    {
    	return true;
    }

	public boolean canBeCollidedWith()
    {
        return worldObj.isRemote;
    }
	public AxisAlignedBB getBoundingBox()
    {
        return worldObj.isRemote?boundingBox:null;
    }

	public void setAbsoluteAABB(double minx, double miny, double minz, double maxx, double maxy, double maxz)
	{
		absoluteAABB.setBounds(minx, miny, minz, maxx, maxy, maxz);
	}

    public boolean interactFirst(EntityPlayer player)
    {
    	if(parent==null)return true;
    	//��������Ă�@�{�@�v���C���[�������Ă�@�{�@�E�N���b�N�����v���C���[�ƈႤ�v���C���[�������Ă�
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
        {
            return true;
        }
        //����������Ă�@�{�@�E�N���b�N�����v���C���[�ȊO�̉���������Ă�
        else if (this.riddenByEntity != null && this.riddenByEntity != player)
        {
        	//���낷
        	riddenByEntity.mountEntity((Entity)null);
        	riddenByEntity = null;
            return true;
        }
        //����������Ă�@������������Ȃ�
        else if (this.riddenByEntity != null)
        {
        	return true;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                player.mountEntity(this);
            }
            return true;
        }
    }

    protected void setPosition()
    {
    	prevPosX = posX;
    	prevPosY = posY;
    	prevPosZ = posZ;
    	if(parent==null)return;

    	Vec3d pos = parent.connectorFromParent.Current();
//    	loopHead.setPositionToRoot(loopHead.parentTile, originalPos);
    	this.posX = pos.x/**parentTile.wheelSize + parentTile.posX */+ getBaseX() + getOffsetX();
		this.posY = pos.y/**parentTile.wheelSize + parentTile.posY */+ getBaseY() + getOffsetY();
		this.posZ = pos.z/**parentTile.wheelSize + parentTile.posZ */+ getBaseZ() + getOffsetZ();

//        this.posX = loopHead.posX + getOffsetX();
//        this.posY = loopHead.posY + getOffsetY();
//        this.posZ = loopHead.posZ + getOffsetZ();
//        if(absoluteAABB==null)return;
        double wx = 0.5;//(absoluteAABB.minX+absoluteAABB.maxX)/2;
        double wz = 0.5;//(absoluteAABB.minZ+absoluteAABB.maxZ)/2;
        double hu = 1.0;//this.absoluteAABB.maxY;
        double hd = 0.0;//this.absoluteAABB.minY;
        this.boundingBox.setBounds(posX-wx, posY+hd, posZ-wz, posX+wx, posY+hu, posZ+wz);

//        if(worldObj.isRemote)MFW_Logger.debugInfo("seat setPosition y:"+posY);
//    	if(waitUpdateRiderFlag)super.updateRiderPosition();
//        if(riddenByEntity!=null)updateRiderPosition();
    }

    protected boolean CheckParent()
	{
        FerrisPartAddress address = new FerrisPartAddress();
        address.x = getBaseX();
        address.y = getBaseY();
        address.z = getBaseZ();
        address.TreeListIndex = getWheelIdx();
        TileEntityFerrisCore tile = (TileEntityFerrisCore) worldObj.getTileEntity(address.x, address.y, address.z);
		FerrisPartBase parent = address.GetInstance(tile);
		if(parent==null)return false;
		return false; // TODO this.loopHead.setPartsSit(getPartseatIndex(), this);
	}

    @Override
	public void onUpdate()
	{
    	if(parent == null)
    	{
    		if(!CheckParent())
			{
				setDead();
				return;
			}
    	}
    	if(parent.IsInvalid())
    	{
    		setDead();
    		return;
    	}
//    	super.onUpdate();
    	setPosition();
	}

    public void setDead()
    {
        super.setDead();
    }


    @Override
	public void updateRiderPosition()
	{
    	super.updateRiderPosition();
	}

//    public void updateRiderPosition2()
//    {
////    	MFW_Logger.debugInfo("updateRiderPosition2 > super");
//    	super.updateRiderPosition();
//    }

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
//		setSeatIndex(nbt.getInteger("seatindex"));
		setOffsetX(nbt.getFloat("seatoffsetx"));
		setOffsetY(nbt.getFloat("seatoffsety"));
		setOffsetZ(nbt.getFloat("seatoffsetz"));
//		setBasePos(nbt.getInteger("tilex"),nbt.getInteger("tiley"),nbt.getInteger("tilez"));
		setWheelIdx(nbt.getInteger("wheelidx"));
		setSlotIdx(nbt.getInteger("slotidx"));
		setSeatHeight(nbt.getFloat("seatheight"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
//		nbt.setInteger("seatindex", getSeatIndex());
		nbt.setFloat("seatoffsetx", getOffsetX());
		nbt.setFloat("seatoffsety", getOffsetY());
		nbt.setFloat("seatoffsetz", getOffsetZ());
//		nbt.setInteger("tilex", getBaseX());
//		nbt.setInteger("tiley", getBaseY());
//		nbt.setInteger("tilez", getBaseZ());
		nbt.setInteger("wheelidx", getWheelIdx());
		nbt.setInteger("slotidx", getSlotIdx());
		nbt.setFloat("seatheight", getSeatHeight());
	}

	public float getOffsetX()
	{
		return dataWatcher.getWatchableObjectFloat(22);
	}
	public void setOffsetX(float offsetx)
	{
		dataWatcher.updateObject(22, Float.valueOf(offsetx));
	}

	public float getOffsetY()
	{
		return dataWatcher.getWatchableObjectFloat(23);
	}
	public void setOffsetY(float offsety)
	{
		dataWatcher.updateObject(23, Float.valueOf(offsety));
	}

	public float getOffsetZ()
	{
		return dataWatcher.getWatchableObjectFloat(24);
	}
	public void setOffsetZ(float offsetz)
	{
		dataWatcher.updateObject(24, Float.valueOf(offsetz));
	}

	protected void setBasePos(int x, int y, int z)
	{
		dataWatcher.updateObject(25, Integer.valueOf(x));
		dataWatcher.updateObject(26, Integer.valueOf(y));
		dataWatcher.updateObject(27, Integer.valueOf(z));
	}

	protected int getBaseX(){return dataWatcher.getWatchableObjectInt(25);}
	protected int getBaseY(){return dataWatcher.getWatchableObjectInt(26);}
	protected int getBaseZ(){return dataWatcher.getWatchableObjectInt(27);}

	public void setWheelIdx(int idx){dataWatcher.updateObject(28, Integer.valueOf(idx));}
	protected int getWheelIdx(){return dataWatcher.getWatchableObjectInt(28);}
	public void setSlotIdx(int idx){dataWatcher.updateObject(29, Integer.valueOf(idx));}
	protected int getSlotIdx(){return dataWatcher.getWatchableObjectInt(29);}

	public void setSeatHeight(float h){dataWatcher.updateObject(30, Float.valueOf(h));}
	public float getSeatHeight(){return dataWatcher.getWatchableObjectFloat(30);}

	public void setPartseatIndex(int idx){dataWatcher.updateObject(31, Integer.valueOf(idx));}
	public int getPartseatIndex(){return dataWatcher.getWatchableObjectInt(31);}
}
