package jp.mochisystems.mfw.ferriswheel;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.*;
import jp.mochisystems.mfw.renderer.FerrisCoreRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class FerrisWheel extends FerrisSelfMover {

    private int copyNum = 1;
    private float copyRotOffset = 0;
    private int copyMode = 0;

    public final Vec3d modelOffset = new Vec3d();

    public FerrisWheel(IModelController controller)
    {
        super(controller);
        position = new InterpolationTick.Rounded(0);
        coreRenderer = new FerrisCoreRenderer();
    }

    @Override
    public void Validate() {
        blockAccess.setCopyNum(copyNum, rotationAxisForOrg, 0);
        super.Validate();
    }

    @Override
    protected void UpdateAttitude()
    {
        if(parent != null) {
            ScaleX.set((float)localScale.x * parent.ScaleX.get());
            ScaleY.set((float)localScale.y * parent.ScaleY.get());
            ScaleZ.set((float)localScale.z * parent.ScaleZ.get());
        }
        else {
            ScaleX.set((float)localScale.x);
            ScaleY.set((float)localScale.y);
            ScaleZ.set((float)localScale.z);
        }
        prevRotation.CopyFrom(rotation);
        rotation.Make(rotationAxisForOrg, Math.toRadians(position.get()));
//        prevRotation.Make(rotationAxisForOrg, Math.toRadians(position.getPrev()));

        rotation.mulLeft(tilt);
//        prevRotation.mulLeft(tilt);

        if(parent != null && !isIndependentTransform)
        {
            rotation.mulLeft(parent.rotation);
//            prevRotation.mulLeft(parent.prevRotation);
        }
//        rotation.makeDirection();
        if(!rotation.IsSameDir(prevRotation))
            prevRotation.Wrap();

        for(Connector connector : connectors)
        {
            UpdateChildConnector(connector);
        }
    }

    @Override
    public void UpdateChildConnector(@Nonnull Connector connector)
    {
        connector.UpdatePrev();
        Vec3d p = connector.Current();
        p.CopyFrom(connector.Base());
        p.Rotate(rotation)
                .mul(ScaleX.get(), ScaleY.get(), ScaleZ.get())
                .add(offset)
                .add(connectorFromParent.Current());
    }





    private final Vec3d connectPosForRender = new Vec3d();
    private final Quaternion.MatBuffer buf = new Quaternion.MatBuffer();
    @Override
    public void RenderOwn(int pass, float partialTick)
    {
        _Core.BindBlocksTextureMap();

        Connector.Fix(connectPosForRender, connectorFromParent, partialTick);
        GL11.glTranslated(
                connectPosForRender.x + offset.x,
                connectPosForRender.y + offset.y,
                connectPosForRender.z + offset.z);



//        prevRotation.Wrap();
        rotatorForRender.CopyFrom(prevRotation).Slerp(rotation, partialTick);
        GL11.glMultMatrix(buf.Fix(rotatorForRender));
//        GL11.glRotated(rotConst_meta2, axisConstructToBase.x, axisConstructToBase.y, axisConstructToBase.z); // coreSide?

        if(pass==0 && shouldDrawCore)
        {
            GlStateManager.pushMatrix();
            coreRenderer.Render();
            GlStateManager.popMatrix();
        }

        if(!isActive) return;

        GL11.glScalef(
                ScaleX.getFix(partialTick),
                ScaleY.getFix(partialTick),
                ScaleZ.getFix(partialTick));

        for(int i=0; i<copyNum; ++i)
        {
            GL11.glPushMatrix();
            GL11.glRotated(copyRotOffset*i, rotationAxisForOrg.x, rotationAxisForOrg.y, rotationAxisForOrg.z);
            GL11.glTranslated(-modelOffset.x, -modelOffset.y, -modelOffset.z);
            if(pass==0) renderer.render();
            else renderer.render2();
            GL11.glPopMatrix();
        }
    }

    protected void ChangeCoreInstanceNum(int slotNum)
    {
        super.ChangeCoreInstanceNum(slotNum);
    }

    public void readMineFromNBT(NBTTagCompound nbt)
    {
        copyNum = nbt.hasKey("copyNum") ? nbt.getInteger("copyNum") : 1;
        copyMode = nbt.getInteger("copyMode");
        copyRotOffset = 360f / copyNum;
        modelOffset.ReadFromNBT("modelOffset", nbt);
        super.readMineFromNBT(nbt);
    }

    public void writeMineToNBT(NBTTagCompound nbt)
    {
        super.writeMineToNBT(nbt);
        nbt.setInteger("copyNum", copyNum);
        nbt.setInteger("copyMode", copyMode);
        modelOffset.WriteToNBT("modelOffset", nbt);
    }
}
