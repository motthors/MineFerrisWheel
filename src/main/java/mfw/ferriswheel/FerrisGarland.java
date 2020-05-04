package mfw.ferriswheel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._mc.gui.gui.GUIFerrisCoreBase;
import mfw._mc.gui.gui.GUIFerrisGarland;
import mfw.renderer.FerrisCoreRenderer;
import mochisystems._core.Logger;
import mochisystems.math.Math;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;
import mochisystems.util.IModelController;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

public class FerrisGarland extends FerrisPartBase {

    protected FerrisGarland pair;
    public FerrisPartAddress pairAddress = new FerrisPartAddress();
    private int coreNum = -1;

    private boolean shouldDrawCore;
    public boolean ShouldDrawCore(){return shouldDrawCore;}
    private FerrisCoreRenderer coreRenderer = new FerrisCoreRenderer();

    public FerrisGarland(IModelController controller)
    {
        super(controller);
    }

    public void SetAccel(float accel)
    {
    }
    public void SetSpeed(float speed)
    {
    }
    public void SetLength(float length)
    {
    }
    public float GetAccel(){ return 0;}
    public float GetSpeed(){return 0;}
    public float GetLength(){return 0;}

    public boolean isEnableSync = false;
    public boolean isSyncTargetSpeed = false;

    public static class End extends FerrisGarland {
        public End(IModelController controller)
        {
            super(controller);
        }
        public void InitEnd(FerrisPartAddress address) {
            this.pairAddress.CopyFrom(address);
            pair = (FerrisGarland) pairAddress.GetInstance(controller);
            pair.pair = this;
            pair.pairAddress.Init(controller.CorePosX(), controller.CorePosY(), controller.CorePosZ(), getTreeIndexOf(this));
        }
        @Override
        protected void UpdateAttitude()
        {
            pair.UpdateAttitude();
        }
    }

    public int GetCoreNum()
    {
        return coreNum;
    }
    public void ChangeCore(int num)
    {
        if(num == coreNum) return;

        if(num < 3) num = 3;
        else if(num > 100) num = 100;
        coreNum = num;
        ChangeCoreInstanceNum(coreNum);

        for(int i = 0; i < coreNum; ++i)
        {
            ChangeConnectorData(i, Integer.toString(i), Vec3d.Zero);
        }
    }


    // variable rotAngle axis

    @Override
    public float GetSoundSourceValue()
    {
        return 1;
    }

    @Override
    public void toggleStopFlag() {}

    @Override
    public void Reset()
    {
        super.Reset();
    }


    @Override
    public void Invalidate()
    {
        super.Invalidate();
        coreRenderer.DeleteBuffer();
    }

    @Override
    public void Validate()
    {
        super.Validate();
        coreRenderer.SetDirty();
    }

    @Override
    public void ToggleVisibleCore()
    {
        shouldDrawCore = !shouldDrawCore;
    }


    @Override
    public void UpdateOwn()
    {
        super.UpdateOwn();

        Scale.update();

        if(pair == null && pairAddress.isSyncing())
        {
            pair = (FerrisGarland) pairAddress.GetInstance(controller);
            if(pair == null)
            {
                pairAddress.Disconnect();
                return;
            }
            pair.pair = this;
            pair.pairAddress.Init(controller.CorePosX(), controller.CorePosY(), controller.CorePosZ(), getTreeIndexOf(this));
        }
        else if(pair != null && pair.IsInvalid())
        {
            pair = null;
        }

        if(pair != null && pair.lastUpdatingTick == this.lastUpdatingTick)
        {
            UpdateAttitude();
        }
    }

    private Vec3d end = new Vec3d();
    private Vec3d end_n = new Vec3d();
    protected void UpdateAttitude()
    {
        prevRotation.CopyFrom(rotation);
//        Math.MakeQuaternionFromDirUp(prevRotation, end_n, parent.prevRotation.up);

        end.CopyFrom(pair.connectorFromParent.Current());
        end.sub(this.connectorFromParent.Current());
        end.x += pair.controller.CorePosX() - controller.CorePosX();
        end.y += pair.controller.CorePosY() - controller.CorePosY();
        end.z += pair.controller.CorePosZ() - controller.CorePosZ();

        end_n.CopyFrom(end).normalize();
//        prevRotation.CopyFrom(rotation);
//        Logger.debugInfo(end_n.toString());
        Math.MakeQuaternionFromDirUp(rotation, end_n, parent.rotation.up);
//        rotation.Identity();

//        if(parent != null && isForrowParentTransform)
//        {
//            rotation.mul(parent.rotation);
//            prevRotation.mul(parent.prevRotation);
//        }

        for(int i = 0; i < connectors.length; ++i)
        {
            Vec3d p = connectors[i].Current();
            p.CopyFrom(connectors[i].Base());
            Vec3d.Lerp(p, (float)i/(float)(coreNum-1), p, end);
            p.add(connectorFromParent.Current());
        }

        if(parent != null) Scale.set(localScale * parent.Scale.get());
        else Scale.set(localScale);
    }

    private Vec3d connectPosForRender = new Vec3d();
    private Quaternion rotaterForRender = new Quaternion();
    @Override
    public void RenderOwn(int pass, float partialtick) {
        if(pass != 0) return;

        Connector.Fix(connectPosForRender, connectorFromParent, partialtick);
        GL11.glTranslated(connectPosForRender.x, connectPosForRender.y, connectPosForRender.z);
        float size = Scale.getFix(partialtick);
        GL11.glScalef(size, size, size); // Scale

        if (shouldDrawCore)
        {
            TextureManager texturemanager = TileEntityRendererDispatcher.instance.field_147553_e;
            texturemanager.bindTexture(TextureMap.locationBlocksTexture);
            coreRenderer.Render();
        }
    }

    @Override
    protected void ChangeConnectorData(int index, String name, Vec3d originalPos)
    {
        connectors[index].Reset(Integer.toString(index), originalPos);
    }

    @Override
    protected void readMineFromNBT(NBTTagCompound nbt)
    {
        coreNum = nbt.getInteger("connectornum");
        super.readMineFromNBT(nbt);
        shouldDrawCore = nbt.getBoolean("isdrawingcore");
        pairAddress.readFromNBT(nbt, "garlandMain_");
        ChangeCore(coreNum);
    }

    @Override
    protected void writeMineToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("connectornum", coreNum);
        super.writeMineToNBT(nbt);
        nbt.setBoolean("isdrawingcore", shouldDrawCore);
        pairAddress.writeToNBT(nbt, "garlandMain_");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIFerrisCoreBase GetGUIInstance(int x, int y, int z, InventoryPlayer inventory, FerrisPartBase part) {
        return new GUIFerrisGarland(x, y, z, inventory, (FerrisGarland) part);
    }


}
