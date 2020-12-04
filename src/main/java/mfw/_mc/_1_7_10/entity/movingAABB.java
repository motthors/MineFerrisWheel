package mfw._mc._1_7_10.entity;

import mochisystems._mc._1_7_10._core.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class movingAABB extends AxisAlignedBB {

    Entity parent;
    private static final double offset = 0.01;

    public movingAABB(Entity parent, double mx, double my, double mz, double xx, double xy, double xz) {
        super(mx, my, mz, xx, xy, xz);
        this.parent = parent;
        Logger.debugInfo("spawn aabb : "+this);
    }

    @Override
    public AxisAlignedBB setBounds( double mx, double my, double mz, double xx, double xy, double xz)
    {
        return super.setBounds(mx, my, mz, xx, xy, xz);
    }

    @Override
    public boolean intersectsWith(AxisAlignedBB that)
    {
        return that.maxX > this.minX && that.minX < this.maxX ?
                (that.maxY > this.minY && that.minY < (this.maxY+offset) ?
                        that.maxZ > this.minZ && that.minZ < this.maxZ
                        : false)
                : false;
    }

    @Override
    public double calculateYOffset(AxisAlignedBB that, double speedY)
    {
        return super.calculateYOffset(that, speedY) + (parent.posY - parent.prevPosY) * (((parent.posY - parent.prevPosY)>=0)?1.2:0.9) + 0.002;
//        if (that.maxX > this.minX && that.minX < this.maxX)
//        {
//            if (that.maxZ > this.minZ && that.minZ < this.maxZ)
//            {
//                double d1;
//
//                if (speedY > 0.0D && that.maxY <= this.minY)
//                {
//                    d1 = this.minY - that.maxY;
//                    if (d1 < speedY)
//                        speedY = d1;
//                }
//
//                if (speedY < 0.0D && that.minY >= (this.maxY-offset))
//                {
//                    d1 = this.maxY - that.minY - offset;
//                    if (d1 > speedY)
//                        speedY = d1;
//                }
//
//                return speedY
//                        + (parent.posY - parent.prevPosY);
//            }
//            else return speedY
//                    + (parent.posY - parent.prevPosY);
//        }
//        else return speedY
//                + (parent.posY - parent.prevPosY);
    }

    @Override
    public double calculateXOffset(AxisAlignedBB that, double speedX)
    {
        return super.calculateXOffset(that, speedX) + (parent.posX - parent.prevPosX);
    }

    @Override
    public double calculateZOffset(AxisAlignedBB that, double speedZ)
    {
        return super.calculateZOffset(that, speedZ) + (parent.posZ - parent.prevPosZ);
    }
}
