package jp.mochisystems.mfw._mc.tileEntity;


import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core.blockcopier.BlocksScanner;
import jp.mochisystems.core.blockcopier.IBLockCopyHandler;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw._mc.item.ItemBlockFerrisCore;
import jp.mochisystems.mfw.blocksReplication.MFWBlocksScanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;

public class TileEntityElevatorConstructor extends TileEntityBlocksScannerBase {

    @Override
    public void Init(EnumFacing side)
    {
        super.Init(side);

        limitFrame.SetLimit(new Vec3i(0, 1, 0), new Vec3i(0, 2, 0));
        switch (side){
            case NORTH:
            case SOUTH:
                limitFrame.SetReset(new Vec3i(-3, 1, -2), new Vec3i(3, 4, 2));
                break;
            case EAST:
            case WEST:
                limitFrame.SetReset(new Vec3i(-2, 1, -3), new Vec3i(2, 4, 3));
                break;
        }
        limitFrame.Reset();
    }

    @Override
    protected BlocksScanner InstantiateBlocksCopier(IBLockCopyHandler handler){
        return new MFWBlocksScanner(handler);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }


    @Override
    protected boolean isExistCore()
    {
        if(stackSlot.isEmpty())return false;
        return stackSlot.getItem() instanceof ItemBlockFerrisCore;
    }

    @Override
    public ItemStack InstantiateModelItem()
    {
        return new ItemStack(MFW.ferrisElevator, 1);
    }

    @Override
    public void registerExternalParam(NBTTagCompound model, NBTTagCompound nbt)
    {
        super.registerExternalParam(model, nbt);

        // -> Elevator

        // => BlockReplicator

    }


}
