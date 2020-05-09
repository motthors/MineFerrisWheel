package mfw.ferriswheel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Command;
import mfw._mc.gui.gui.GUIFerrisCoreBase;
import mfw._mc.gui.gui.GUIFerrisWheel;
import mochisystems._core.Logger;
import mochisystems.blockcopier.IModelCollider;
import mochisystems.math.Math;
import mochisystems.util.IModelController;
import mochisystems.util.InterpolationTick;
import mfw.message.MessageFerrisMisc;
import mfw.renderer.FerrisCoreRenderer;
import mfw.util.MFWBlockAccess;
import mochisystems.blockcopier.BlocksRenderer;
import mochisystems.math.*;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class FerrisWheel extends FerrisPartBase implements IFerrisParamGetter {

    //// sub modules
    private final MFWBlockAccess blockAccess;
    private final BlocksRenderer renderer;

    // core
    private boolean shouldDrawCore;
    public boolean ShouldDrawCore(){return shouldDrawCore;}
    private FerrisCoreRenderer coreRenderer = new FerrisCoreRenderer();

    // collider
    public IModelCollider collider;
    private boolean isEnableCollider;
    public boolean IsEnableCollider(){return isEnableCollider;}
    public void toggleEnableCollider()
    {
        isEnableCollider = !isEnableCollider;
        Logger.debugInfo("toggle");
    }
    private void CheckColliderStatus()
    {
        if(isEnableCollider && collider == null)
        {
            if(!controller.IsRemote())
                collider = controller.MakeAndSpawnCollider(this, blockAccess);
        }
        else if(!isEnableCollider && collider != null)
        {
            collider.Delete();
            collider = null;
        }
    }

    public FerrisWheel(IModelController controller)
    {
        super(controller);
        blockAccess = new MFWBlockAccess(null);
        renderer = new BlocksRenderer(blockAccess);
    }
    public InterpolationTick rotAngle = new InterpolationTick(0);
    private float rotSpeed = 0;
    private float rotAccel = 0;
    public float rotResist = 0.1f;
    public float speedTemp;
    public InterpolationTick syncAmplitude = new InterpolationTick(1f);
    public InterpolationTick syncPhase = new InterpolationTick(0);
    public boolean stopFlag = false;
    public int constructSide;
    private Vec3d rotationAxis = new Vec3d();

    public boolean isEnableSinConvert = false;
    //同期モードは上2つのフラグは無効になる

    public void SetAccel(float accel)
    {
        rotAccel = accel;
    }
    public void SetSpeed(float speed)
    {
        rotSpeed = speed;
    }
    public float GetAccel(){ return rotAccel;}
    public float GetSpeed(){return rotSpeed;}
    public InterpolationTick Position(){return rotAngle;}
    public InterpolationTick Amp(){return syncAmplitude;}
    public InterpolationTick Phase(){return syncPhase;}
    public float GetRegist(){return rotResist;}
    public void SetResist(float value){
        rotResist = value;
        if(rotResist > 0.99f)rotResist = 0.99f;
        else if(rotResist < 0.001f)rotResist = 0.001f;
    }
    public float GetYaw(){return yaw;}
    public void SetYaw(float value) {
        yaw = value;
        if(yaw > 180f) yaw = 180f;
        else if(yaw < -180f) yaw = -180f;
        setRotAxis();
    }
    public float GetPitch(){return pitch;}
    public void SetPitch(float value){
        pitch = value;
        if(pitch > 180f) pitch = 180f;
        else if(pitch < -180f) pitch = -180f;
        setRotAxis();
    }

    public Vec3d GetOffset(){return offset;}
    public void SetOffset(Vec3d source){
        offset.CopyFrom(source);}

    final ArrayList<Connector> ChairConnector = new ArrayList<>();


    // コピー時の向きと設置時の向きを考慮するための回転軸と角度
    private float rotConst_meta2 = 0, rotMeta2_side = 0;
    private Vec3d axisConstructToBase = new Vec3d(0, 0, 0);
    private Vec3d rotvecMeta2_side = new Vec3d(0, 0, 0);
    private Vec3d vecCloneAxis = new Vec3d(0, 0, 0);

    private int copyNum = 1;
    private float copyRotOffset = 0;
    private int copyMode = 0;
    // variable rotAngle axis
    public float pitch = 0, yaw = 0;


    public int sizeForGUI;

    /////////////////implements FerrisPartBase

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
        rotAccel = 10;
        rotSpeed = 0;
        rotAngle.set(0);
        rotResist = 0.05f;
        syncAmplitude.set(1);
        syncPhase.set(0);
        speedTemp = 0;
        localScale = 1;
        Scale.Init(1);

        resetRot();
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
        blockAccess.constructFromTag(partNbtOnConstruct,
                controller.CorePosX(), controller.CorePosY(), controller.CorePosZ(),
                true, renderer::CompileRenderer);
        renderer.delete();
        coreRenderer.SetDirty();
        setRotAxis();
        connectorFromParent.Init();
    }

    @Override
    public float GetSoundSourceValue()
    {
        return rotSpeed;
    }


    /////////////////////////////////////////////////


    public void AddSpeed(float add)
    {
        rotAccel += add;
        if(rotAccel > 100f)rotAccel = 100f;
        else if(rotAccel < -100f)rotAccel = -100f;
    }

    public void turnSpeed()
    {
        rotAccel *= -1;
    }

//    public void setRotMisc(float add, int idx)
//    {
//        switch(idx)
//        {
//            case 1 :
//                syncAmplitude.add(add);
//                syncAmplitude.clamp(-400, 400);
//                break;
//            case 2 :
//                syncPhase.add(add);
//                syncPhase.clamp(-400, 400);
//                break;
//        }
//    }


    @Override
    public void toggleStopFlag()
    {
        stopFlag = !stopFlag;
    }

    public void toggleSinConvertFlag()
    {
        isEnableSinConvert = !isEnableSinConvert;
    }

    public void toggleStoryBoardFlag()
    {
        super.toggleStoryBoardFlag();
        if(isEnableStoryBoard)
        {
            rotSpeed = 0;
            rotAccel = 0;
        }
    }

    public void setResist(float regist)
    {
        rotResist = regist;
        if(rotResist > 0.99f)rotResist = 0.99f;
        else if(rotResist < 0.001f)rotResist = 0.001f;
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

    public void resetRot()
    {
        pitch = 0; yaw = 0;
        Scale.set(1.0f);
    }


    private void setRotAxis()
    {
        if(controller == null) return;

        vecCloneAxis.CopyFrom(Vec3d.Zero);
        switch(constructSide)
        {
            case 0 : vecCloneAxis.y = 1; axisConstructToBase.x = 1; rotConst_meta2 = 90; break;
            case 1 : vecCloneAxis.y =-1; axisConstructToBase.x = 1; rotConst_meta2 = -90; break;
            case 2 : vecCloneAxis.z = 1; axisConstructToBase.y = 1; rotConst_meta2 = 0;  break;
            case 3 : vecCloneAxis.z =-1; axisConstructToBase.y = 1; rotConst_meta2 = 180; break;
            case 4 : vecCloneAxis.x = 1; axisConstructToBase.y = 1; rotConst_meta2 = -90; break;
            case 5 : vecCloneAxis.x =-1; axisConstructToBase.y = 1; rotConst_meta2 = 90; break;
        }
        switch(controller.CoreSide())
        {
            case -1 : rotMeta2_side = 0;
            case 0 : /*rotationAxis.y = 1;*/ rotvecMeta2_side.x = 1; rotMeta2_side = -90; break;
            case 1 : /*rotationAxis.y =-1;*/ rotvecMeta2_side.x = 1; rotMeta2_side = 90; break;
            case 2 : /*rotationAxis.z = 1;*/ rotvecMeta2_side.y = 1; rotMeta2_side = 0;  break;
            case 3 : /*rotationAxis.z =-1;*/ rotvecMeta2_side.y = 1; rotMeta2_side = 180; break;
            case 4 : /*rotationAxis.x = 1;*/ rotvecMeta2_side.y = 1; rotMeta2_side = 90; break;
            case 5 : /*rotationAxis.x =-1;*/ rotvecMeta2_side.y = 1; rotMeta2_side = -90; break;
        }
        MakeBaseRot();
    }

    private void MakeBaseRot()
    {
        baseAttitudeRot.Identity();
//        baseAttitudeRot.Make(axisConstructToBase, Math.toRadians(rotConst_meta2));
        Quaternion q2 = new Quaternion(); q2.Make(rotvecMeta2_side, Math.toRadians(rotMeta2_side));
        Quaternion qp = new Quaternion();
        Quaternion qy = new Quaternion();
        qp.Make(Vec3d.Left, Math.toRadians(pitch));
        qy.Make(Vec3d.Up, Math.toRadians(yaw));
        baseAttitudeRot.mulLeft(qp).mulLeft(qy);
        if(parent == null) baseAttitudeRot.mulLeft(q2);
        baseAttitudeRot.makeMatrixBuffer();
        RotateConnectorByAttitude();

        rotationAxis.CopyFrom(Vec3d.Front);
//        rotationAxis.Rotate(qy).Rotate(qp);
//        Logger.debugInfo("!! : " + rotationAxis.toString());
    }

    public void RotateConnectorByAttitude()
    {
        for (Connector connector : connectors)
        {
            connector.ResetBase();
            connector.Base()
                    .Rotate(axisConstructToBase, Math.toRadians(rotConst_meta2));
//                    .Rotate(baseAttitudeRot)
//                    .Rotate(rotvecMeta2_side, Math.toRadians(rotMeta2_side));
        }
    }

//    private int syncCounter = 0;
//    public void syncRotFromServerToClient()
//    {
//        if(MFW_Core.proxy.checkSide().isClient())return;
//        if(loopHead != null)return;
//        if(--syncCounter < 0)
//        {
//            syncCounter = 200;
//            if(root == this)worldObj.markBlockForUpdate(CorePosX, CorePosY, CorePosZ);
//        }
//    }
//
    public void syncRot_recieve(float[] afloat)
    {
        //TODO
//        ArrayList<FerrisWheel>wheellist = createPartArray();
//        if(compressedModelData==null)return;
//        int i=0;
//        for(TileEntityFerrisCore tile : wheellist)tile.rotAngle.set(afloat[i++]);
    }

    @Override
    public void UpdateOwn()
    {
        super.UpdateOwn();
        CheckColliderStatus();

        rotAngle.update();
        Scale.update();
        syncAmplitude.update();
        syncPhase.update();

        Rotate();

        blockAccess.updateTileEntity();

        UpdateAttitude();


//        BlocksRep.update();
    }

    private void Rotate()
    {
        if(isEnableSync)
        {
            if(syncParent == null)
            {
                return;
            }

            IFerrisParamGetter parent = (IFerrisParamGetter) syncParent;

            if(isSyncTargetSpeed)
            {
                rotSpeed = (parent.GetSpeed() * syncAmplitude.get() * getSpeedRatioFromRSFlag());
                rotAngle.add(rotSpeed);
                rotAngle.round();
            }
            else
            {
                rotAngle.set(parent.Position().get()* syncAmplitude.get()*getSpeedRatioFromRSFlag() + syncPhase.get());
                rotAngle.setPrev(parent.Position().getPrev()* syncAmplitude.get()*getSpeedRatioFromRSFlag() + syncPhase.get());
                rotAngle.round();
            }
            return;
        }

        if(isEnableStoryBoard)
        {
            if(isUpdatedRsPowerNow() == 1)storyboardManager.OnRSEnable();
            storyboardManager.Update();
        }

        rotSpeed *= (1f - rotResist);
        if(!stopFlag)rotSpeed += rotAccel*rotResist*getSpeedRatioFromRSFlag();


        if(isEnableSinConvert){
            speedTemp += rotSpeed;
            if(speedTemp > 180)speedTemp -= 360f;
            else if(speedTemp < -180)speedTemp += 360f;
            float sin = (float) java.lang.Math.sin(java.lang.Math.toRadians(speedTemp))
                    * syncAmplitude.get()*getSpeedRatioFromRSFlag()
                    + syncPhase.get();
            rotAngle.set(sin);
        }
        else{
            rotAngle.add(rotSpeed);
            rotAngle.round();
        }
    }

    private void UpdateAttitude()
    {
        // ここで指定する回転軸はCoreSideのNormalの逆
        rotation.Make(rotationAxis, Math.toRadians(rotAngle.get()));
        prevRotation.Make(rotationAxis, Math.toRadians(rotAngle.getPrev()));

        if(parent != null)
        {
            Scale.set(localScale * parent.Scale.get());
        }
        else{
            Scale.set(localScale);
        }
//        Logger.debugInfo(lastUpdatingTick + offset.toString());


        rotation.mulLeft(baseAttitudeRot);
        prevRotation.mulLeft(baseAttitudeRot);

        if(parent != null && !isIndependentTransform)
        {
            rotation.mulLeft(parent.rotation);
            prevRotation.mulLeft(parent.prevRotation);
        }
        rotation.makeDirection();

        connectorFromParent.UpdatePrev();
        Vec3d p = connectorFromParent.Current();
        p.CopyFrom(connectorFromParent.Base())
                .add(offset);
        if(parent != null) {
           p.Rotate(parent.rotation)
            .mul(parent.Scale.get())
            .add(parent.connectorFromParent.Current());
        }

    }

    @Override
    public void ToggleVisibleCore()
    {
        shouldDrawCore = !shouldDrawCore;
    }

    private Vec3d connectPosForRender = new Vec3d();
    @Override
    public void RenderOwn(int pass, float partialtick)
    {
        rotaterForRender.CopyFrom(prevRotation).Lerp(rotation, partialtick);
        Connector.Fix(connectPosForRender, connectorFromParent, partialtick);
        float size = Scale.getFix(partialtick);


        GL11.glTranslated(connectPosForRender.x, connectPosForRender.y, connectPosForRender.z);

        GL11.glScalef(size, size, size); // Scale

//        GL11.glRotated(rotMeta2_side, rotvecMeta2_side.x, rotvecMeta2_side.y, rotvecMeta2_side.z); // coreSide?
//        GL11.glMultMatrix(baseAttitudeRot.GetBuffer());
        GL11.glMultMatrix(rotaterForRender.makeMatrixBuffer());
        GL11.glRotated(rotConst_meta2, axisConstructToBase.x, axisConstructToBase.y, axisConstructToBase.z); // coreSide?

        if(pass==0 && shouldDrawCore)
        {
            coreRenderer.Render();
        }
        for(int i=0; i<copyNum; ++i)
        {
            GL11.glPushMatrix();
            GL11.glRotated(copyRotOffset*i, vecCloneAxis.x, vecCloneAxis.y, vecCloneAxis.z);
            if(pass==0) renderer.render();
            else renderer.render2();
//            BlocksRep.renderEntities(partialtick);
            GL11.glPopMatrix();
        }
    }

    protected void readMineFromNBT(NBTTagCompound nbt)
    {
        super.readMineFromNBT(nbt);
        constructSide = nbt.getByte("constructorside");

        rotSpeed = nbt.getFloat("speed");
        if(MFW_Command.doSync)
        {
            rotAngle.set(nbt.getFloat("rot"));
            speedTemp = nbt.getFloat("speedtemp");
        }
        if(nbt.hasKey("accel"))
            rotAccel = nbt.getFloat("accel");
        if(nbt.hasKey("accel"))
            rotResist = nbt.getFloat("resist");
        if(nbt.hasKey("syncAmplitude"))
            syncAmplitude.set(nbt.getFloat("syncAmplitude"));
        else syncAmplitude.Init(1);
        syncPhase.set(nbt.getFloat("syncPhase"));
        isEnableSinConvert = nbt.getBoolean("enablesinconvert");
        rsFlag = nbt.getByte("rsflag");
        if(nbt.hasKey("stopflag"))
            stopFlag = nbt.getBoolean("stopflag");
        pitch = nbt.getFloat("pitch");
        yaw = nbt.getFloat("yaw");
        copyNum = nbt.getInteger("copynum");
        copyMode = nbt.getInteger("copyMode");
        shouldDrawCore = nbt.getBoolean("isdrawingcore");
        isEnableCollider = nbt.getBoolean("isenablecollider");

        storyboardManager.createFromSerialCode(nbt.getString("storyboard"));
        soundManager.SetSoundIndex(nbt.getInteger("soundindex"));

        setRotAxis();
        copyRotOffset = 360f / copyNum;
    }

    protected void writeMineToNBT(NBTTagCompound nbt)
    {
        super.writeMineToNBT(nbt);
        nbt.setByte("constructormetaflag", (byte) constructSide);
        nbt.setFloat("speed", rotSpeed);
        nbt.setFloat("rot", rotAngle.get());
        nbt.setFloat("accel", rotAccel);
        nbt.setFloat("resist", rotResist);
        nbt.setFloat("speedtemp", speedTemp);
        nbt.setFloat("syncAmplitude", syncAmplitude.get());
        nbt.setFloat("syncPhase", syncPhase.get());
        nbt.setBoolean("enablesinconvert", isEnableSinConvert);
        nbt.setBoolean("enablestoryboard", isEnableStoryBoard);
        nbt.setByte("rsflag", rsFlag);
        nbt.setBoolean("stopflag", stopFlag);
        nbt.setFloat("pitch", pitch);
        nbt.setFloat("yaw", yaw);
        nbt.setInteger("copynum", copyNum);
        nbt.setInteger("copyMode", copyMode);
        nbt.setBoolean("isdrawingcore", shouldDrawCore);
        nbt.setBoolean("isenablecollider", isEnableCollider);

        // 同期用親の保存
        nbt.setString("storyboard", storyboardManager.ToSerialCode());
        nbt.setInteger("soundindex", soundManager.GetSoundIndex());

    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIFerrisCoreBase GetGUIInstance(int x, int y, int z, InventoryPlayer inventory, FerrisPartBase part) {
        return new GUIFerrisWheel(x, y, z, inventory, (FerrisWheel) part);
    }

    public int getMaxSize()
    {
        int x = blockAccess.getSize(0);
        int y = blockAccess.getSize(1);
        int z = blockAccess.getSize(2);
        return java.lang.Math.max(java.lang.Math.max(x, y), z);
    }
}
