package mfw._mc.tileEntity;

import mochisystems.blockcopier.LimitFrame;
import mochisystems.blockcopier.ILimitLine;
import mochisystems.blockcopier.BlockExcluder;
import mochisystems.math.Math;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityFerrisCutter extends TileEntity implements BlockExcluder, ILimitLine {

	private int LimitFrameX;
	private int LimitFrameY;
	private int LimitFrameZ;
	LimitFrame limitFrame = new LimitFrame();
	
	public TileEntityFerrisCutter() {
		super();
		resetFrameLength();
	}

	@Override
	public double getMaxRenderDistanceSquared() 
	{
		return 100000d;
	}
	@Override
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}



	///////////////// override BlockExcluder
    @Override
    public int getMinX()
    {
        return xCoord;
    }
    @Override
    public int getMinY()
    {
        return yCoord;
    }
    @Override
    public int getMinZ()
    {
        return zCoord;
    }

    @Override
	public int getMaxX() {
		return xCoord + LimitFrameX;
	}

	@Override
	public int getMaxY() {
		return yCoord + LimitFrameY;
	}

	@Override
	public int getMaxZ() {
		return zCoord + LimitFrameZ;
	}

	////////////////////////////////////////////


//	public void setFrame(int l, int FLAG)
//	{
//		int value = 0;
//		switch(l)
//		{
//		case 0 : value = -100;break;
//		case 1 : value = -10; break;
//		case 2 : value = -1;  break;
//		case 3 : value = 1;   break;
//		case 4 : value = 10;  break;
//		case 5 : value = 100; break;
//		}
//		FLAG -= MessageFerrisMisc.GUIFerrisCutterX;
//		switch(FLAG)
//		{
//		case MessageFerrisMisc.GUIFerrisCutterX : _setFrameX(value);break;
//		case MessageFerrisMisc.GUIFerrisCutterY : _setFrameY(value);break;
//		case MessageFerrisMisc.GUIFerrisCutterZ : _setFrameZ(value);break;
//		}
//	}

	@Override
	public void setFrameLength(int l) {
		LimitFrameZ += l;
		LimitFrameZ = Math.Clamp(LimitFrameZ, 1, 1000);
	}

	@Override
	public void setFrameHeight(int h) {
		LimitFrameY += h;
		LimitFrameY = Math.Clamp(LimitFrameY, 1, 1000);
	}

	@Override
	public void setFrameWidth(int w) {
		LimitFrameX += w;
		LimitFrameX = Math.Clamp(LimitFrameX, 1, 1000);
	}

	@Override
	public int getFrameLength(){return LimitFrameZ;}
	@Override
	public int getFrameHeight(){return LimitFrameY;}
	@Override
	public int getFrameWidth(){return LimitFrameX;}

	@Override
	public void resetFrameLength() {
		LimitFrameX = 2;
		LimitFrameY = 2;
		LimitFrameZ = 2;
	}

	
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);      
    	loadFromNBT(par1NBTTagCompound, "");
    }
    private void loadFromNBT(NBTTagCompound nbt, String tag)
    {
    	LimitFrameX = nbt.getInteger(tag+"frameX");
    	LimitFrameY = nbt.getInteger(tag+"frameY");
    	LimitFrameZ = nbt.getInteger(tag+"frameZ");
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
		super.writeToNBT(par1NBTTagCompound);
		saveToNBT(par1NBTTagCompound, "");
	}
    private void saveToNBT(NBTTagCompound nbt, String tag)
    {
		nbt.setInteger(tag + "frameX", LimitFrameX);
		nbt.setInteger(tag + "frameY", LimitFrameY);
		nbt.setInteger(tag + "frameZ", LimitFrameZ);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		this.writeToNBT(nbtTagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
		limitFrame.SetLengths(0, LimitFrameX-1,0, LimitFrameY-1,0, LimitFrameZ-1, false,true);
	}



	@Override
	public void render(Tessellator tess) 
	{
		limitFrame.render(tess);
	}

}
