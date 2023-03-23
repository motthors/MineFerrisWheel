package jp.mochisystems.mfw.ferriswheel;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.entity.EntityBlockModelCollider;
import jp.mochisystems.core._mc.renderer.BlocksRenderer;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.blockcopier.BlockModelColliderModule;
import jp.mochisystems.core.bufferedRenderer.CachedBufferBase;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.*;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw._mc._core.MFW_Command;
import jp.mochisystems.mfw.storyboard.StoryBoardManager;
import jp.mochisystems.mfw.util.MFWBlockAccess;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FerrisSelfMover extends FerrisPartBase implements IBlockModel {

    //// sub modules
    protected final MFWBlockAccess blockAccess;
    protected final BlocksRenderer renderer;
    public MTYBlockAccess GetBlockAccess(){ return blockAccess; }
    public final StoryBoardManager storyboardManager;
    public final FerrisSoundManager soundManager;

    // core
    protected boolean shouldDrawCore;
    public boolean ShouldDrawCore(){return shouldDrawCore;}
    protected CachedBufferBase coreRenderer;

    // collider
    public BlockModelColliderModule colliderModule;
    @Override
    public boolean IsEnableCollider(){return colliderModule.IsEnableCollider();}
    @Override
    public void ToggleEnableCollider()
    {
        colliderModule.ToggleActive();
    }

    // moving
    public InterpolationTick position;
    private float speed = 0;
    private float accel = 0;
    public ClampValue.Float resist = ClampValue.Float.of(0.05f, 0.01f, 0.99f);
    public float speedTemp;
    public boolean stopFlag = false;
    public InterpolationTick amplitude = new InterpolationTick(45);
    public InterpolationTick phase = new InterpolationTick(0);

    // attitude
    public final Quaternion tilt = new Quaternion();
    public final Quaternion prevTilt = new Quaternion();
    public int constructSide;
    protected final Vec3d rotationAxisForOrg = new Vec3d();
    private final Vec3d rotationAxisForFixed = new Vec3d();

    //// sync
    public boolean isEnableSync = false;
    public boolean isSyncTargetSpeed = false;
    public boolean isSyncModeAbsolute = false;
    public final CommonAddress syncTarget = new CommonAddress();
    protected FerrisSelfMover syncParent;
    private final List<FerrisSelfMover> SyncedChildren = new ArrayList<>();
//    protected List<ILateUpdater> lateUpdaters = new ArrayList<>();
    public void toggleSyncFlag()
    {
        isEnableSync = !isEnableSync;
    }
    public void toggleSyncMode()
    {
        isSyncTargetSpeed = !isSyncTargetSpeed;
    }
    public void toggleSyncCopyMode()
    {
        isSyncModeAbsolute = !isSyncModeAbsolute;
    }
//    public void AddLateUpdater(ILateUpdater updater)
//    {
//        if(lateUpdaters.contains(updater)) return;
//        lateUpdaters.add(updater);
//    }




    public FerrisSelfMover(IModelController controller)
    {
        super(controller);
        blockAccess = new MFWBlockAccess(connectors);
        renderer = MFW.proxy.GetBlocksRenderer(blockAccess);
        colliderModule = new BlockModelColliderModule(blockAccess, this, controller);

        storyboardManager = new StoryBoardManager(this);
        soundManager = new FerrisSoundManager(this);
    }



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
    public InterpolationTick Position(){return position;}
    public InterpolationTick Amp(){return amplitude;}
    public InterpolationTick Phase(){return phase;}
    public float GetResist(){return resist.Get();}
    public void SetResist(float value){
        resist.Set(value);
    }

    @Override
    public void SetRotation(Quaternion q)
    {
        prevTilt.CopyFrom(tilt);
        tilt.CopyFrom(q);
        if(!rotation.IsSameDir(prevRotation)) prevRotation.Wrap();
    }

    @Override
    public void SetLocalScale(Vec3d value){
        super.SetLocalScale(value);
        colliderModule.SetScale(localScale.x);//TODO 部分拡縮されたときは警告を出す？
    }

    public boolean isEnableStoryBoard = false;
    public void toggleStoryBoardFlag()
    {
        isEnableStoryBoard = !isEnableStoryBoard;
        if(isEnableStoryBoard)
        {
            speed = 0;
            accel = 0;
        }
    }
    public boolean GetEnableStoryBoard()
    {
        return isEnableStoryBoard;
    }

    public boolean GetEnableSinConvert()
    {
        return isEnableSinConvert;
    }
    public boolean isEnableSinConvert = false;

    /////////////////implements FerrisPartBase

    @Override
    public void SetWorld(World world)
    {
        super.SetWorld(world);
        blockAccess.setWorld(world);
        colliderModule.SetWorld(world);
    }

    @Override
    public void Reset()
    {
        super.Reset();
        stopFlag = true;
        accel = 10;
        speed = 0;
        position.set(0);
        resist.Set(0.05f);
        amplitude.set(45);
        phase.set(0);
        speedTemp = 0;
        localScale.SetFrom(1, 1, 1);
        ScaleX.Init(1);
        ScaleY.Init(1);
        ScaleZ.Init(1);
        ScaleX.set(1.0f);
        ScaleY.set(1.0f);
        ScaleZ.set(1.0f);
        InitRotAxis();
    }

    @Override
    public void Unload()
    {
        soundManager.Invalid();
    }

    @Override
    public void Invalidate()
    {
        super.Invalidate();
        ReleaseSyncChildren();
        if(renderer!=null) renderer.delete();
        coreRenderer.DeleteBuffer();
    }

    @Override
    public void Validate()
    {
        super.Validate();
        Logger.debugInfo(blockAccess+"");
        Logger.debugInfo(controller+"");
        Logger.debugInfo(renderer+"");
        Logger.debugInfo(coreRenderer+"");
        Logger.debugInfo(connectorFromParent+"");
        if(renderer!=null) blockAccess.constructFromTag(partNbtOnConstruct,
                Math.floor(controller.CorePosX()),
                Math.floor(controller.CorePosY()),
                Math.floor(controller.CorePosZ()),
                true, renderer::CompileRenderer);
        else blockAccess.constructFromTag(partNbtOnConstruct,
                Math.floor(controller.CorePosX()),
                Math.floor(controller.CorePosY()),
                Math.floor(controller.CorePosZ()),
                true, ()->{});
        if(renderer!=null) renderer.delete();
        coreRenderer.SetDirty();
//        InitRotAxis();
        connectorFromParent.Reset();
    }

    @Override
    public float GetSoundSourceValue()
    {
        return speed;
    }


    /////////////////////////////////////////////////




    public void toggleSinConvertFlag()
    {
        isEnableSinConvert = !isEnableSinConvert;
    }

    @Override
    public void ToggleVisibleCore()
    {
        shouldDrawCore = !shouldDrawCore;
    }



    public void SetConstructSide(int side)
    {
        constructSide = side;
        rotationAxisForOrg.SetFrom(0, 0, 0);
        switch(constructSide)
        {
            case 0 : rotationAxisForOrg.y = 1; break;
            case 1 : rotationAxisForOrg.y =-1; break;
            case 2 : rotationAxisForOrg.z = 1; break;
            case 3 : rotationAxisForOrg.z =-1; break;
            case 4 : rotationAxisForOrg.x = 1; break;
            case 5 : rotationAxisForOrg.x =-1; break;
        }
    }

    private void InitRotAxis()
    {
        if(controller == null) return;

        for (Connector connector : connectors)
            connector.ResetBase();

        tilt.Identity();

        if(parent == null) {
            rotationAxisForFixed.SetFrom(0, 0, 0);
            switch(controller.CoreSide())
            {
                case DOWN : rotationAxisForFixed.y = 1; break;
                case UP   : rotationAxisForFixed.y =-1; break;
                case NORTH: rotationAxisForFixed.z = 1; break;
                case SOUTH: rotationAxisForFixed.z =-1; break;
                case EAST : rotationAxisForFixed.x =-1; break;
                case WEST : rotationAxisForFixed.x = 1; break;
            }
            tilt.Make(rotationAxisForOrg, rotationAxisForFixed);
            prevTilt.CopyFrom(tilt);
        }
//        tilt.Make(Vec3d.Left, 0.6f);
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
        //TODO　定期的な回転の同期　いる? マルチでどうなるか
//        ArrayList<FerrisWheel>wheellist = createPartArray();
//        if(compressedModelData==null)return;
//        int i=0;
//        for(TileEntityFerrisCore tile : wheellist)tile.rotAngle.set(afloat[i++]);
    }

    @Override
    public void Update()
    {
        if(!isValidated)
        {
            Validate();
        }
        if(!isEnableSync){
            UpdateRsPower();
            UpdateOwn();
            UpdateChildren();
        }
        else if(syncTarget.isSyncing() && syncParent == null){
            CheckAndReconstructSync();
        }
    }

    @Override
    public void UpdateOwn()
    {
        super.UpdateOwn();

        position.update();
        ScaleX.update();
        ScaleY.update();
        ScaleZ.update();
        amplitude.update();
        phase.update();

        Transform();
        UpdateAttitude();

//        Logger.debugInfo(""+speed+" : "+position.get());

        colliderModule.Update(rotation);
        blockAccess.updateTileEntity();

        UpdateSyncChildren();
    }


    protected void Transform()
    {
        if(isEnableSync)
        {
            if(syncParent == null) return;

            if(isSyncTargetSpeed)
            {
                speed = (syncParent.GetSpeed() * amplitude.get() * getSpeedRatioFromRSFlag());
                position.add(speed);
            }
            else
            {
                position.setPrev(syncParent.Position().getPrev() * amplitude.get() * getSpeedRatioFromRSFlag() + phase.get());
                position.set(syncParent.Position().get()* amplitude.get()*getSpeedRatioFromRSFlag() + phase.get());
            }
            return;
        }

        if(isEnableStoryBoard)
        {
            if(isUpdatedRsPowerNow() == 1) storyboardManager.OnRSEnable();
            storyboardManager.Update();
            return;
        }

        speed *= (1f - resist.Get());
        if(!stopFlag) speed += accel * resist.Get() * getSpeedRatioFromRSFlag();


        if(isEnableSinConvert){
            speedTemp += speed;
            if(speedTemp > 180)speedTemp -= 360f;
            else if(speedTemp < -180)speedTemp += 360f;
            float sin = (float) java.lang.Math.sin(java.lang.Math.toRadians(speedTemp))
                    * amplitude.get()*getSpeedRatioFromRSFlag()
                    + phase.get();
            position.set(sin);
        }
        else{
            position.add(speed);
//            Logger.debugInfo(String.format("%06.2f : %06.2f", rotAngle.get(), rotAngle.getPrev()));
        }


    }

    protected void UpdateAttitude()
    {

    }

    @Override
    public void UpdateChildConnector(@Nonnull Connector connector)
    {

    }

    @Override
    public void RegisterColEntity(EntityBlockModelCollider entity) {
        colliderModule.AddEntity(entity);
    }






    private int syncIntervalCount = 95; //最初の起動時はちょっと早めにSync確認したい
    private void CheckAndReconstructSync()
    {
        if(syncIntervalCount++ < 100) return;
        syncIntervalCount = 0;

//        if(syncTarget.x != Math.floor(syncParent.controller.CorePosX())
//                || syncTarget.y != Math.floor(syncParent.controller.CorePosY())
//                || syncTarget.z != Math.floor(syncParent.controller.CorePosZ())) return;
        RegisterSyncParent();
    }
    private void SetSyncChild(FerrisSelfMover child)
    {
        child.syncParent = this;
        SyncedChildren.add(child);
    }
    private void UpdateSyncChildren()
    {
        for (FerrisSelfMover child : SyncedChildren) {
            if (child != null) {
                child.UpdateRsPower();
                child.UpdateOwn();
                child.UpdateChildren();
//                for (ILateUpdater u : child.lateUpdaters) u.LateUpdate();
            }
        }
    }
    public void SetNewSyncParent(CommonAddress target)
    {
        if(target.Equals(syncTarget))return;
        if(target.Equals(this.GetCommonAddress()))
        {
            target.Disconnect();
            return;
        }
        syncTarget.CopyFrom(target);
        RegisterSyncParent();
    }

    private void RegisterSyncParent()
    {
        if(!isSyncModeAbsolute){
            CommonAddress address = controller.GetCommonAddress();
            syncTarget.x = address.x;
            syncTarget.y = address.y;
            syncTarget.z = address.z;
        }
        IModel model = syncTarget.GetInstance(controller);
        if(model instanceof FerrisSelfMover) syncParent = (FerrisSelfMover) model;
        else syncParent = null;
        if(syncParent == this)
        {
            syncParent = null;
            syncTarget.Disconnect();
            return;
        }
        if(syncParent != null) syncParent.SetSyncChild(this);
        controller.markBlockForUpdate();
    }
    public void ReleaseSyncChildren()
    {
        for(FerrisSelfMover child : SyncedChildren)
        {
            child.ReleaseSyncParent();
        }
        SyncedChildren.clear();
    }
    public void ReleaseSyncParent()
    {
        syncParent = null;
        syncTarget.Disconnect();
    }








    public void readMineFromNBT(NBTTagCompound nbt)
    {
        super.readMineFromNBT(nbt);
        SetConstructSide(nbt.getByte("constructorside"));

        speed = nbt.getFloat("speed");
        if(MFW_Command.doSync)
        {
            position.set(nbt.getFloat("rot"));
            speedTemp = nbt.getFloat("speedtemp");
        }
        if(nbt.hasKey("accel"))
            accel = nbt.getFloat("accel");
        if(nbt.hasKey("resist"))
            resist.Set(nbt.getFloat("resist"));
        if(nbt.hasKey("amplitude"))
            amplitude.set(nbt.getFloat("amplitude"));
        else amplitude.Init(1);
        phase.set(nbt.getFloat("phase"));
        isEnableSinConvert = nbt.getBoolean("enablesinconvert");
        isEnableStoryBoard = nbt.getBoolean("enablestoryboard");
        rsFlag = nbt.getByte("rsflag");
        if(nbt.hasKey("stopflag"))
            stopFlag = nbt.getBoolean("stopflag");
        if(nbt.hasKey("tiltvx")) tilt.ReadFromNBT("tilt", nbt);
        else InitRotAxis();

        shouldDrawCore = nbt.getBoolean("isDrawCore");
        colliderModule.ReadFromNBT(nbt);

        soundManager.SetSoundIndex(nbt.getInteger("soundindex"));
        storyboardManager.createFromSerialCode(nbt.getString("storyboard"));

        syncTarget.readFromNBT(nbt,"syncTarget");
        isEnableSync = nbt.getBoolean("enablesyncrot");
        isSyncTargetSpeed = nbt.getBoolean("synctargetspeed");
        isSyncModeAbsolute = nbt.getBoolean("synccopymode");
    }

    public void writeMineToNBT(NBTTagCompound nbt)
    {
        super.writeMineToNBT(nbt);
        nbt.setByte("constructorside", (byte) constructSide);
        nbt.setFloat("speed", speed);
        nbt.setFloat("rot", position.get());
        nbt.setFloat("accel", accel);
        nbt.setFloat("resist", resist.Get());
        nbt.setFloat("speedtemp", speedTemp);
        nbt.setFloat("amplitude", amplitude.get());
        nbt.setFloat("phase", phase.get());
        nbt.setBoolean("enablesinconvert", isEnableSinConvert);
        nbt.setBoolean("enablestoryboard", isEnableStoryBoard);
        nbt.setByte("rsflag", rsFlag);
        nbt.setBoolean("stopflag", stopFlag);
        tilt.WriteToNBT("tilt", nbt);
        prevTilt.CopyFrom(tilt);
        nbt.setBoolean("isDrawCore", shouldDrawCore);
        nbt.setInteger("soundindex", soundManager.GetSoundIndex());
        nbt.setString("storyboard", storyboardManager.ToSerialCode());
        colliderModule.WriteToNBT(nbt);

        syncTarget.writeToNBT(nbt, "syncTarget");
        nbt.setBoolean("synctargetspeed", isSyncTargetSpeed);
        nbt.setBoolean("enablesyncrot", isEnableSync);
        nbt.setBoolean("synccopymode", isSyncModeAbsolute);
    }


    public int getMaxSize()
    {
        int x = blockAccess.getSizeX();
        int y = blockAccess.getSizeY();
        int z = blockAccess.getSizeZ();
        return java.lang.Math.max(java.lang.Math.max(x, y), z);
    }

}
