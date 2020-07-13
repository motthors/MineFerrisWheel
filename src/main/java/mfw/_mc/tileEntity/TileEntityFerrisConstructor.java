package mfw._mc.tileEntity;


import mfw._core.MFW_Core;
import mfw._mc.block.blockFerrisConstructor;
import mfw.blocksReplication.MFWBlocksScanner;
import mfw._mc.item.itemBlockFerrisCore;
import mochisystems._mc.tileentity.TileEntityBlocksScannerBase;
import mochisystems.blockcopier.BlocksScanner;
import mochisystems.math.Math;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityFerrisConstructor extends TileEntityBlocksScannerBase {


    @Override
    protected BlocksScanner InstantiateBlocksCopier(){
        return new MFWBlocksScanner();
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

	@Override
	public boolean isExceptBlock(Block b)
	{
		return b instanceof blockFerrisConstructor;
	}

	@Override
	public void setFrameLength(int l)
	{
		LimitFrameLength = l;
        LimitFrameLength = Math.Clamp(LimitFrameLength, 3, 32767);
        createVertex();
    }

	@Override
	public void setFrameHeight(int h)
	{
        setFrameWidth(h);
	}

	@Override
	public void setFrameWidth(int w)
	{
		LimitFrameWidth = w;
        LimitFrameWidth = Math.Clamp(LimitFrameWidth, 1, 32767);
        createVertex();
    }

    @Override
    public int getFrameLength(){return LimitFrameLength;}
    @Override
    public int getFrameHeight(){return LimitFrameWidth;}
    @Override
    public int getFrameWidth(){return LimitFrameWidth;}

    @Override
    public void resetFrameLength()
    {
        LimitFrameLength = 3;
        LimitFrameWidth = 1;
        createVertex();
    }

    @Override
	protected void createVertex()
    {
        int Length1 = (LimitFrameLength) / 2;
        int Length2 = (LimitFrameWidth) / 2;

        int x = Length1;
        int y = Length1;
        int z = Length1;
        switch(side)
        {
            case 0 : case 1 : y = Length2; break;
            case 2 : case 3 : z = Length2; break;
            case 4 : case 5 : x = Length2; break;
        }
        limitFrame.SetLengths( -x, x, -y, y, -z, z, isOdd, true);
    }


	

    @Override
    protected boolean isExistCore()
    {
        if(stackSlot==null)return false;
        return stackSlot.getItem() instanceof itemBlockFerrisCore;
    }

    @Override
    public ItemStack InstantiateModelItem()
    {
        return new ItemStack(MFW_Core.ferrisCore, 1);
    }

    @Override
    public void registerExternalParam(NBTTagCompound nbt)
    {
        super.registerExternalParam(nbt);

    	// -> FerrisWheel
        nbt.setFloat("wsize", 1f);

        // => BlockReplicator
    	nbt.setInteger("copiedPosX", xCoord); // CTM
		nbt.setInteger("copiedPosY", yCoord);
		nbt.setInteger("copiedPosZ", zCoord);
		nbt.setInteger("originlocalx", (limitFrame.lenX()+1)/2);
		nbt.setInteger("originlocaly", (limitFrame.lenY()+1)/2);
		nbt.setInteger("originlocalz", (limitFrame.lenZ()+1)/2);
		nbt.setInteger("copynum", copyNum);
		nbt.setInteger("copyMode", copyMode);
    }

    @Override
    protected void RecieveExtBlockData(NBTTagCompound nbt)
    {
    }



}
