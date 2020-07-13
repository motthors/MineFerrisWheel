package mfw._mc.tileEntity;


import mfw._core.MFW_Core;
import mfw._mc.block.blockElevatorConstructor;
import mfw.blocksReplication.MFWBlocksScanner;
import mfw._mc.item.itemBlockFerrisCore;
import mochisystems._mc.tileentity.TileEntityBlocksScannerBase;
import mochisystems.blockcopier.BlocksScanner;
import mochisystems.math.Math;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityElevatorConstructor extends TileEntityBlocksScannerBase {

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
        return b instanceof blockElevatorConstructor;
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
        LimitFrameHeight = h;
        LimitFrameHeight = Math.Clamp(LimitFrameHeight, 1, 32767);
        createVertex();
    }

    @Override
    public void setFrameWidth(int w)
    {
        LimitFrameWidth = w ;
        LimitFrameWidth = Math.Clamp(LimitFrameWidth, 1, 32767);
        createVertex();
    }

    @Override
    public int getFrameLength(){return LimitFrameLength;}
    @Override
    public int getFrameHeight(){return LimitFrameHeight;}
    @Override
    public int getFrameWidth(){return LimitFrameWidth;}

    @Override
    public void resetFrameLength()
    {
        LimitFrameLength = 2;
        LimitFrameWidth = 3;
        LimitFrameHeight = 3;
        createVertex();
    }

    @Override
    public void createVertex()
    {
        int x = LimitFrameWidth / 2;
        int z = LimitFrameWidth / 2;
        int y = LimitFrameHeight;
        switch(side) {
            case 2:
            case 3:
                z = LimitFrameLength / 2;
                break;
            case 4:
            case 5:
                x = LimitFrameLength / 2;
                break;
        }
        limitFrame.SetLengths(-x, x, 1, y+1, -z, z, isOdd, true);
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
        return new ItemStack(MFW_Core.ferrisElevator, 1);
    }

    @Override
    public void registerExternalParam(NBTTagCompound nbt)
    {
        super.registerExternalParam(nbt);

        // -> FerrisWheel
        nbt.setFloat("wsize", 1f);

        // => BlockReplicator
        nbt.setByte("constructorside", (byte)side);
        nbt.setInteger("copiedPosX", xCoord); // CTM
        nbt.setInteger("copiedPosY", yCoord);
        nbt.setInteger("copiedPosZ", zCoord);
        nbt.setInteger("originlocalx", (limitFrame.lenX()+1)/2);
        nbt.setInteger("originlocaly", -1);
        nbt.setInteger("originlocalz", (limitFrame.lenZ()+1)/2);
        nbt.setInteger("copynum", 1);
        nbt.setInteger("copyMode", 0);
    }

    @Override
    protected void RecieveExtBlockData(NBTTagCompound nbt)
    {
    }



}
