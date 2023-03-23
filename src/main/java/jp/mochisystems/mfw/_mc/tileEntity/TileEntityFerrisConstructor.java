package jp.mochisystems.mfw._mc.tileEntity;


import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core.blockcopier.BlocksScanner;
import jp.mochisystems.core.blockcopier.IBLockCopyHandler;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw.blocksReplication.MFWBlocksScanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

public class TileEntityFerrisConstructor extends TileEntityBlocksScannerBase {

    protected int copyMode = 0;
    public Vec3d modelOffset = new Vec3d();

    public float angleForRenderBody = 0;
    public final Vec3d AxisForRenderBody = new Vec3d(0, 0, 0);
    public final Vec3d faceDir = new Vec3d(0, 0, 0);
    public int BodyGuide = 0; // 1:head, 2:left arm, 3, right arm, 4:body, 5:left reg, 6:right leg


    private void setRotAxis()
    {
        faceDir.CopyFrom(Vec3d.Zero);
        AxisForRenderBody.CopyFrom(Vec3d.Zero);
        switch(side)
        {
            case DOWN : faceDir.y = 1; AxisForRenderBody.x = 1; angleForRenderBody = -90; break;
            case UP : faceDir.y = 1; AxisForRenderBody.x = 1; angleForRenderBody = 90; break;
            case NORTH : faceDir.z = 1; AxisForRenderBody.y = 1; angleForRenderBody = 0;  break;
            case SOUTH : faceDir.z = 1; AxisForRenderBody.y = 1; angleForRenderBody = 180; break;
            case EAST : faceDir.x = 1; AxisForRenderBody.y = 1; angleForRenderBody = 90; break;
            case WEST : faceDir.x = 1; AxisForRenderBody.y = 1; angleForRenderBody = -90; break;
        }
    }

    @Override
    public void Init(EnumFacing side)
    {
        super.Init(side);
        switch (side){
            case NORTH:
            case SOUTH:
                limitFrame.SetLimit(new Vec3i(-1, -1, 0), new Vec3i(1, 1, 0));
                limitFrame.SetReset(new Vec3i(-2, -2, 0), new Vec3i(2, 2, 0));
                break;
            case UP:
            case DOWN:
                limitFrame.SetLimit(new Vec3i(-1, 0, -1), new Vec3i(1, 0, 1));
                limitFrame.SetReset(new Vec3i(-2, -0, -2), new Vec3i(2, 0, 2));
                break;
            case EAST:
            case WEST:
                limitFrame.SetLimit(new Vec3i(0, -1, -1), new Vec3i(0, 1, 1));
                limitFrame.SetReset(new Vec3i(0, -2, -2), new Vec3i(0, 2, 2));
                break;
        }
        limitFrame.Reset();
        setRotAxis();
    }

    private void setBodyGuide(int idx)
    {
        BodyGuide = idx;
//        scale.CopyFrom(Vec3d.One).mul(idx==0 ? 1 : 0.25f);
//        switch(idx)
//        {
//            case 2:
//            case 5: modelName = "L"; break;
//            case 3:
//            case 6: modelName = "R"; break;
//            case 1:
//            case 4: modelName = "Body"; break;
//        }
    }


    @Override
    protected BlocksScanner InstantiateBlocksCopier(IBLockCopyHandler handler){
        return new MFWBlocksScanner(handler);
    }


    @Override
    public ItemStack InstantiateModelItem()
    {
        return new ItemStack(MFW.ferrisCore, 1);
    }

    @Override
    public void registerExternalParam(NBTTagCompound model, NBTTagCompound nbt)
    {
        super.registerExternalParam(model, nbt);

    	// -> FerrisWheel
        model.setInteger("copyMode", copyMode);
        // 回転軸の方向のOffsetの影響を無くしたい
        modelOffset.x *= faceDir.x==0 ? 1 : 0;
        modelOffset.y *= faceDir.y==0 ? 1 : 0;
        modelOffset.z *= faceDir.z==0 ? 1 : 0;
        modelOffset.WriteToNBT("modelOffset", model);
    }


    @Override
    public void ReadParamFromNBT(NBTTagCompound nbt)
    {
        super.ReadParamFromNBT(nbt);
        copyMode = nbt.getInteger("copyMode");
        setBodyGuide(nbt.getInteger("BodyGuide"));
        modelOffset.ReadFromNBT("modelOffset", nbt);

        setRotAxis();
    }

    @Nonnull
    @Override
    public NBTTagCompound WriteParamToNBT(NBTTagCompound nbt)
    {
        super.WriteParamToNBT(nbt);
        nbt.setInteger("copyMode", copyMode);
        nbt.setInteger("BodyGuide", BodyGuide);
        modelOffset.WriteToNBT("modelOffset", nbt);
        return nbt;
    }
}
