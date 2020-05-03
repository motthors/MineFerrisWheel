package mfw._mc.entity;

import mochisystems._core.Logger;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class EntityCollisionParts extends Entity {

	Vec3d offset;
	int bax,bay,baz; //blockaccess org position
	EntityFerrisCollider parent;

	public EntityCollisionParts(World world, EntityFerrisCollider parent, Vec3d offset) {
		super(world);
		this.parent = parent;
		this.offset = offset;
		setSize(1.0f, 1.0f);

		//	boundingBox = new OBB();
		try {
			Field f = this.getClass().getSuperclass().getDeclaredField("boundingBox");
			Field modifiersField = Field.class.getDeclaredField("modifiers");          // Fieldクラスはmodifiersでアクセス対象のフィールドのアクセス判定を行っているのでこれを更新する。
			modifiersField.setAccessible(true);                                        // modifiers自体もprivateなのでアクセス可能にする。
			modifiersField.setInt(f,f.getModifiers() & ~Modifier.PRIVATE & ~Modifier.FINAL);
			f.set(this, new movingAABB(this, 0,0,0,0,0,0));
		}catch (Exception e){
            Logger.debugInfo(e.getMessage());
        }
	}

	public void setParent(EntityFerrisCollider b)
	{
		parent = b;
	}

	public void SetPosition(Vec3d pos, Quaternion rot)
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		// left * offset.x + up * offset.y + dir * offset.z
		double ox = rot.left.x * offset.x + rot.up.x * offset.y + rot.dir.x * offset.z;
		double oy = rot.left.y * offset.x + rot.up.y * offset.y + rot.dir.y * offset.z;
		double oz = rot.left.z * offset.x + rot.up.z * offset.y + rot.dir.z * offset.z;
		setPosition(parent.posX + ox+ 0.5, parent.posY + oy, parent.posZ + oz + 0.5);
//		Logger.debugInfo(""+boundingBox);
	}

//	public void SetRotation(Quaternion q)
//	{
//		aabb.SetRotation(q);
//	}

	public boolean canBeCollidedWith()
    {
        return true;
    }

	public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }

//	public void setAbsoluteAABB(double minx, double miny, double minz, double maxx, double maxy, double maxz)
//	{
//		absoluteAABB.setBounds(minx, miny, minz, maxx, maxy, maxz);
//	}
//	public void setAbsoluteAABBTurnMeta(int meta, double minx, double miny, double minz, double maxx, double maxy, double maxz)
//	{
////		double X = minx, Z = minz; TODO
////		double XX = maxx, ZZ = maxz;
////		switch(coreSide)
////		{
////		case 2 : break;
////		case 3 : minx = -X
////		}
//		setAbsoluteAABB(minx, miny, minz, maxx, maxy, maxz);
//	}

//    public void setPosition(double x, double y, double z)
//    {
//    	prevPosX = posX;
//    	prevPosY = posY;
//    	prevPosZ = posZ;
//        this.posX = x + ox;
//        this.posY = y + oy;
//        this.posZ = z + oz;
//        if(absoluteAABB==null)return;
//        double wx = (absoluteAABB.minX+absoluteAABB.maxX)/2;
//        double wz = (absoluteAABB.minZ+absoluteAABB.maxZ)/2;
//        double hu = this.absoluteAABB.maxY;
//        double hd = this.absoluteAABB.minY;
//        this.boundingBox.setBounds(posX-wx, posY+hd, posZ-wz, posX+wx, posY+hu, posZ+wz);
//    }

	@Override
	public boolean interactFirst(EntityPlayer player)
	{
		if(parent==null)return true;
		if(!player.isSneaking())return true;
		parent.openRootCoreGUI(player);
		return true;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {}

	@Override
	protected void entityInit() {}

}
