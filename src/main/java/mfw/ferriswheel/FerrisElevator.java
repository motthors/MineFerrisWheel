package mfw.ferriswheel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._mc._1_7_10._core.MFW_Command;
import mfw._mc._1_7_10.gui.gui.GUIFerrisCoreBase;
import mfw._mc._1_7_10.gui.gui.GUIFerrisElevator;
import mfw.util.MFWBlockAccess;
import mochisystems._mc._1_7_10._core.Logger;
import mochisystems.blockcopier.BlocksRenderer;
import mochisystems.util.IModelController;
import mochisystems.util.InterpolationTick;
import mfw._mc._1_7_10.message.MessageFerrisMisc;
import mfw.renderer.ElevatorCoreRenderer;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class FerrisElevator extends FerrisPartBase implements IFerrisParamGetter{

    //// sub modules
    private final MFWBlockAccess blockAccess;
    private final BlocksRenderer renderer;


    private boolean shouldDrawCore;
    public boolean ShouldDrawCore(){return shouldDrawCore;}
    private ElevatorCoreRenderer coreRenderer = new ElevatorCoreRenderer();

    public FerrisElevator(IModelController controller)
    {
        super(controller);
        blockAccess = new MFWBlockAccess(null);
        renderer = new BlocksRenderer(blockAccess);
    }

    public InterpolationTick length = new InterpolationTick(1);
    public float speed = 0;
    public float accel = 0;
    public float resist = 0.1f;
    public boolean stopFlag = false;
    public int constructSide;
    public InterpolationTick syncAmplitude = new InterpolationTick(1f);
    public InterpolationTick syncPhase = new InterpolationTick(0);

    public void SetAccel(float accel)
    {
        this.accel = accel;
    }
    public void SetSpeed(float speed)
    {
        this.speed = speed;
    }
    public float GetAccel(){ return accel;}
    public float GetSpeed(){return speed;}
    public InterpolationTick Position(){return length;}
    public InterpolationTick Amp(){return syncAmplitude;}
    public InterpolationTick Phase(){return syncPhase;}
    public float GetRegist(){return resist;}
    public void SetResist(float value){resist= value;}
    public float GetYaw(){return yaw;}
    public void SetYaw(float value){yaw = value;}
    public float GetPitch(){return pitch;}
    public void SetPitch(float value){pitch = value;}
    public Vec3d GetOffset(){return offset;}
    public void SetOffset(Vec3d source){ offset.CopyFrom(source);}

    // variable rotAngle axis
    public float pitch = 0, yaw = 0;
    private float angleConstructToBase = 0;
    private float angleBaseToPlace = 0;
    private Vec3d axisConstructToBase = new Vec3d(0, 0, 0);
    private Vec3d axisBaseToPlace = new Vec3d(0, 0, 0);

    @Override
    public float GetSoundSourceValue()
    {
        return speed;
    }

    @Override
    public void SetWorld(World world)
    {
        super.SetWorld(world);
        blockAccess.setWorld(world);
        blockAccess.setWorldToTileEntities(world);
    }

    @Override
    public void Reset()
    {
        super.Reset();
        stopFlag = true;
        speed = 0;
        length.set(0);
        resetRot();
    }

    public void resetRot()
    {
        pitch = 0; yaw = 0;
    }

    private void setRotAxis()
    {
        axisConstructToBase.CopyFrom(Vec3d.Zero);
        switch(constructSide)
        {
            case 0 :
            case 1 :
            case 2 : axisConstructToBase.y = 1; angleConstructToBase = 0;  break;
            case 3 : axisConstructToBase.y = 1; angleConstructToBase = 180; break;
            case 4 : axisConstructToBase.y = 1; angleConstructToBase = -90; break;
            case 5 : axisConstructToBase.y = 1; angleConstructToBase = 90; break;
        }

        switch(controller.CoreSide())
        {
            case -1 : angleBaseToPlace = 0;
            case 0 : axisBaseToPlace.x = 1; angleBaseToPlace = -90; break;
            case 1 : axisBaseToPlace.x = 1; angleBaseToPlace = 90; break;
            case 2 : axisBaseToPlace.y = 1; angleBaseToPlace = 0;  break;
            case 3 : axisBaseToPlace.y = 1; angleBaseToPlace = 180; break;
            case 4 : axisBaseToPlace.y = 1; angleBaseToPlace = 90; break;
            case 5 : axisBaseToPlace.y = 1; angleBaseToPlace = -90; break;
        }
    }

    @Override
    public void Invalidate()
    {
        super.Invalidate();
        renderer.delete();
        coreRenderer.DeleteBuffer();
    }

    @Override
    public void Validate()
    {
        super.Validate();
        coreRenderer.SetDirty();
        blockAccess.constructFromTag(partNbtOnConstruct,
                controller.CorePosX(), controller.CorePosY(), controller.CorePosZ(),
                true, renderer::CompileRenderer);
        renderer.delete();
        setRotAxis();
        connectorFromParent.Init();
    }

    public void rotateRSFlag()
    {
        rsFlag = (byte) ((rsFlag+1) % rsFlag_End);
    }

    public void AddSpeed(float add)
    {
        accel += add;
        if(accel > 100f)accel = 100f;
        else if(accel < -100f)accel = -100f;
    }

    public void turnSpeed()
    {
        accel *= -1;
        Logger.debugInfo("elevator accel : "+accel);
    }

    public void toggleStopFlag()
    {
        stopFlag = !stopFlag;
    }

    public void toggleStoryBoardFlag()
    {
        super.toggleStoryBoardFlag();
        if(isEnableStoryBoard)
        {
            speed = 0;
            accel = 0;
        }
    }

    @Override
    public void ToggleVisibleCore()
    {
        shouldDrawCore = !shouldDrawCore;
    }


    public void setResist(float regist)
    {
        resist = regist;
        if(resist > 0.99f)resist = 0.99f;
        else if(resist < 0.001f)resist = 0.001f;
    }

    public void setRot(int flagVal, int flagGUIButton)
    {
        float rot = 0;
        switch(flagVal)
        {
            case 0 : rot = -10f; break;
            case 1 : rot = -1f; break;
            case 2 : rot = 1f; break;
            case 3 : rot = 10f; break;
        }
        switch(flagGUIButton)
        {
            case MessageFerrisMisc.GUICoreRot1: pitch += rot; break;
            case MessageFerrisMisc.GUICoreRot2: yaw += rot; break;
        }
        if(pitch > 180f) pitch = 180f;
        else if(pitch < -180f) pitch = -180f;
        if(yaw > 180f) yaw = 180f;
        else if(yaw < -180f) yaw = -180f;

    }

    @Override
    public void UpdateOwn()
    {
        super.UpdateOwn();

        length.update();
        Scale.update();
        syncAmplitude.update();
        syncPhase.update();

        Translate();
        UpdateAttitude();
    }

    private void Translate()
    {
        if(isEnableSync)
        {
            if(syncParent == null)return;
            IFerrisParamGetter parent = (IFerrisParamGetter) syncParent;
            if(isSyncTargetSpeed)
            {
                speed = (parent.GetSpeed() * syncAmplitude.get() * getSpeedRatioFromRSFlag());
                length.add(speed);
            }
            else
            {
                length.set(parent.Position().get()* syncAmplitude.get()*getSpeedRatioFromRSFlag() + syncPhase.get());
                length.setPrev(parent.Position().getPrev()* syncAmplitude.get()*getSpeedRatioFromRSFlag() + syncPhase.get());
            }
            return;
        }
        if(isEnableStoryBoard)
        {
            if(isUpdatedRsPowerNow() == 1)storyboardManager.OnRSEnable();
            storyboardManager.Update();
        }

        speed *= (1f - resist);
        if(!stopFlag)speed += accel * resist * getSpeedRatioFromRSFlag();
        length.add(speed);
    }

    private void UpdateAttitude()
    {
        // ここで指定する回転軸はCoreSideのNormalの逆
        rotation.Identity();
        prevRotation.Identity();

        if(parent != null && !isIndependentTransform)
        {
            rotation.mul(parent.rotation);
            prevRotation.mul(parent.prevRotation);
        }

        for(int i = 0; i < connectors.length; ++i)
        {
            Vec3d p = connectors[i].Current();
            p.CopyFrom(Vec3d.Up)
                .mul(length.get())
                .Rotate(rotation)
                .add(offset);
            if(parent != null) p.add(parent.connectorFromParent.Current());

            p = connectors[i].Prev();
            p.CopyFrom(Vec3d.Up)
                .mul(length.getPrev())
                .Rotate(prevRotation)
                .add(offset);
            if(parent != null) p.add(parent.connectorFromParent.Prev());
        }

        if(parent != null) Scale.set(localScale * parent.Scale.get());
        else Scale.set(localScale);
    }


    private Vec3d connectPosForRender = new Vec3d();
    private Quaternion rotaterForRender = new Quaternion();
    @Override
    public void RenderOwn(int pass, float partialtick) {

        Connector.Fix(connectPosForRender, connectorFromParent, partialtick);
        connectPosForRender.add(offset);
        GL11.glTranslated(connectPosForRender.x, connectPosForRender.y, connectPosForRender.z);
        GL11.glRotatef(pitch, 1, 0, 0); // Rot1
        GL11.glRotatef(yaw, 0, 0, 1); // Rot2
        float size = Scale.getFix(partialtick);
        GL11.glScalef(size, size, size); // Scale


        GL11.glRotated(angleBaseToPlace, axisBaseToPlace.x, axisBaseToPlace.y, axisBaseToPlace.z); // coreSide?

        GL11.glRotated(angleConstructToBase, axisConstructToBase.x, axisConstructToBase.y, axisConstructToBase.z); // coreSide?


        if(pass == 0)
        {
            renderer.render();
            if (shouldDrawCore)
            {
                coreRenderer.Render();
            }
        }
        else renderer.render2();
    }

    protected void readMineFromNBT(NBTTagCompound nbt)
    {
        super.readMineFromNBT(nbt);
        constructSide = nbt.getByte("constructorside");

        speed = nbt.getFloat("speed");
        if(MFW_Command.doSync)
        {
            length.set(nbt.getFloat("rot"));
        }
        accel = nbt.getFloat("accel");
        setResist(nbt.getFloat("resist"));
        syncAmplitude.set(nbt.getFloat("syncAmplitude"));
        syncPhase.set(nbt.getFloat("rotMiscfloat2"));
        isEnableStoryBoard = nbt.getBoolean("enablestoryboard");/* MFW_Logger.debugInfo("enablesb:"+isEnableStoryBoard+" ."+CorePosX+"."+CorePosY+"."+CorePosZ);*/
        rsFlag = nbt.getByte("rsflag");
        stopFlag = nbt.getBoolean("stopflag");
        pitch = nbt.getFloat("pitch");
        yaw = nbt.getFloat("yaw");
        shouldDrawCore = nbt.getBoolean("isdrawingcore");

        soundManager.SetSoundIndex(nbt.getInteger("soundindex"));
    }


    protected void writeMineToNBT(NBTTagCompound nbt)
    {
        super.writeMineToNBT(nbt);
        nbt.setFloat("rot", length.get());
        nbt.setFloat("speed", speed);
        nbt.setFloat("accel", accel);
        nbt.setFloat("resist", resist);
        nbt.setFloat("syncAmplitude", syncAmplitude.get());
        nbt.setFloat("syncPhase", syncPhase.get());
        nbt.setBoolean("enablestoryboard", isEnableStoryBoard);
        nbt.setByte("rsflag", rsFlag);
        nbt.setBoolean("stopflag", stopFlag);
        nbt.setFloat("pitch", pitch);
        nbt.setFloat("yaw", yaw);
        nbt.setBoolean("isdrawingcore", shouldDrawCore);

        // 同期用親の保存
        nbt.setInteger("soundindex", soundManager.GetSoundIndex());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIFerrisCoreBase GetGUIInstance(int x, int y, int z, InventoryPlayer inventory, FerrisPartBase part) {
        return new GUIFerrisElevator(x, y, z, inventory, (FerrisElevator) part);
    }

}
