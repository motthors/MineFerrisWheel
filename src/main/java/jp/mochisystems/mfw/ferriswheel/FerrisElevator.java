package jp.mochisystems.mfw.ferriswheel;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.Connector;
import jp.mochisystems.core.util.IModelController;
import jp.mochisystems.core.util.InterpolationTick;
import jp.mochisystems.mfw.renderer.ElevatorCoreRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.nio.DoubleBuffer;

public class FerrisElevator extends FerrisSelfMover {

    public FerrisElevator(IModelController controller)
    {
        super(controller);
        coreRenderer = new ElevatorCoreRenderer();

        position = new InterpolationTick(0);
    }

    // moving
    public Vec3d currentLocalPosition = new Vec3d();
    public Vec3d prevLocalPosition = new Vec3d();




    @Override
    public void Reset()
    {
        super.Reset();
        SetAccel(1f);
    }




    @Override
    protected void UpdateAttitude()
    {
        prevRotation.CopyFrom(rotation);
        rotation.Identity();

        rotation.mulLeft(tilt);

        if(parent != null && !isIndependentTransform)
        {
            rotation.CopyFrom(parent.rotation);
        }

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

        prevLocalPosition.CopyFrom(currentLocalPosition);
        currentLocalPosition
                .CopyFrom(Vec3d.Up)
                .mul(position.get() * ScaleX.get(),
                        position.get()*ScaleY.get(),
                        position.get()*ScaleZ.get())
                .add(offset)
                .Rotate(rotation);


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
        p.CopyFrom(connector.Base())
                .Rotate(rotation)
                .add(currentLocalPosition)
                .mul(ScaleX.get(), ScaleY.get(), ScaleZ.get())
                .add(offset)
                .add(connectorFromParent.Current());
    }




    private final Vec3d connectPosForRender = new Vec3d();
    private final Quaternion.MatBuffer buf = new Quaternion.MatBuffer();
    @Override
    public void RenderOwn(int pass, float partialTick) {

        _Core.BindBlocksTextureMap();

        rotatorForRender.CopyFrom(prevRotation).Slerp(rotation, partialTick);

        Connector.Fix(connectPosForRender, connectorFromParent, partialTick);
        connectPosForRender.add(offset);
        GL11.glTranslated(connectPosForRender.x, connectPosForRender.y, connectPosForRender.z);

        GL11.glScalef(
                ScaleX.getFix(partialTick),
                ScaleY.getFix(partialTick),
                ScaleZ.getFix(partialTick));

        GL11.glMultMatrix(buf.Fix(rotatorForRender));

//        GL11.glRotated(angleBaseToPlace, axisBaseToPlace.x, axisBaseToPlace.y, axisBaseToPlace.z); // coreSide?
//        GL11.glRotated(angleConstructToBase, axisConstructToBase.x, axisConstructToBase.y, axisConstructToBase.z); // coreSide?

        if (shouldDrawCore && pass == 0)
        {
            GlStateManager.pushMatrix();
            coreRenderer.Render();
            GlStateManager.popMatrix();
        }

        if(!isActive) return;

        GL11.glTranslated(0, position.getFix(partialTick), 0);

        if(pass == 0)
        {
            renderer.render();
        }
        else renderer.render2();
    }

}
