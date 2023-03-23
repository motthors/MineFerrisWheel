package jp.mochisystems.mfw.ferriswheel;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.Connector;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import jp.mochisystems.mfw.renderer.FerrisCoreRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class FerrisGarland extends FerrisPartBase {

    public static final String garlandIdKey = "garlandId";


    protected FerrisGarland pair;
//    public CommonAddress pairAddress = new CommonAddress();
    private int coreNum = -1;

    private boolean isDrawCore;
    public boolean ShouldDrawCore(){return isDrawCore;}
    private FerrisCoreRenderer coreRenderer = new FerrisCoreRenderer();

    private boolean isLead;
    private String garlandId = "";
    public FerrisGarland GetLead(){ return isLead ? this : pair; }

    public FerrisGarland(IModelController controller)
    {
        super(controller);
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
        connectors.clear();
        for(int i = 0; i < coreNum; ++i)
        {
            Connector c = new Connector(Integer.toString(i));
            connectors.add(c);
        }
    }


    // variable rotAngle axis

    @Override
    public float GetSoundSourceValue()
    {
        return 1;
    }

    @Override
    public void Reset()
    {
        super.Reset();
    }

    @Override
    public void Unload() {

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
        isDrawCore = !isDrawCore;
    }


    @Override
    public void UpdateOwn()
    {
        super.UpdateOwn();
        ScaleX.update();
        ScaleY.update();
        ScaleZ.update();

        if(pair == null)
        {
            GarlandManager.OnSearch(garlandId, this);
//            pair = (FerrisGarland) pairAddress.GetInstance(controller);
//            if(pair == null)
//            {
//                pairAddress.Disconnect();
//                return;
//            }
//            pair.pair = this;
//            pair.pairAddress.CopyFrom(controller.GetCommonAddress());
        }
        else {
            if(pair.IsInvalid())
            {
                pair = null;
                return;
            }
            if(pair.lastUpdatingTick == this.lastUpdatingTick)
            {
                GetLead().UpdateAttitude();
            }
        }

    }

    private final Vec3d end = new Vec3d();
    private final Vec3d end_n = new Vec3d();
    private final Quaternion.V3Mat qMat = new Quaternion.V3Mat();
    protected void UpdateAttitude()
    {
        prevRotation.CopyFrom(rotation);
//        Math.MakeQuaternionFromDirUp(prevRotation, end_n, parent.prevRotation.up);

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

        end.CopyFrom(pair.connectorFromParent.Current());
//        end.sub(this.connectorFromParent.Current());
        end.x += pair.controller.CorePosX() - controller.CorePosX();
        end.y += pair.controller.CorePosY() - controller.CorePosY();
        end.z += pair.controller.CorePosZ() - controller.CorePosZ();

        end_n.CopyFrom(end).sub(this.connectorFromParent.Current()).normalize();

        qMat.Fix(prevRotation);
        Quaternion.MakeQuaternionFromDirUp(rotation, end_n, qMat.up);
//        rotation.makeDirection();

        int i = 0;
        float d = 1f / (coreNum-1);
        for(Connector connector : connectors)
        {
            connector.UpdatePrev();
            Vec3d p = connector.Current();
            p.CopyFrom(connector.Base()).add(offset);
            p.add(connectorFromParent.Current());
            Vec3d.Lerp(p, i*d, p, end);
            i++;
        }
    }

    private final Vec3d connectPosForRender = new Vec3d();
//    private final Quaternion rotaterForRender = new Quaternion();
    @Override
    public void RenderOwn(int pass, float partialTick) {
        if(pass != 0) return;

        _Core.BindBlocksTextureMap();

        Connector.Fix(connectPosForRender, connectorFromParent, partialTick);
        GL11.glTranslated(connectPosForRender.x, connectPosForRender.y, connectPosForRender.z);
        GL11.glScalef(
                ScaleX.getFix(partialTick),
                ScaleY.getFix(partialTick),
                ScaleZ.getFix(partialTick));

        if (isDrawCore)
        {
            TextureManager texturemanager = TileEntityRendererDispatcher.instance.renderEngine;
            texturemanager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            coreRenderer.Render();
        }
    }

//    @Override
//    protected void ChangeConnectorData(int index, String name, Vec3d originalPos)
//    {
//        connectors.get(index).Reset(Integer.toString(index), originalPos);
//    }

    @Override
    public void readMineFromNBT(NBTTagCompound nbt)
    {
        coreNum = nbt.getInteger("connectornum");
        super.readMineFromNBT(nbt);
        isDrawCore = nbt.getBoolean("isDrawCore");
//        pairAddress.readFromNBT(nbt, "garlandMain_");
        garlandId = nbt.getString(garlandIdKey);
        isLead = nbt.getBoolean("lead");
        ChangeCore(coreNum);

//        OnSpawn(garlandId, this);
    }

    @Override
    public void writeMineToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("connectornum", coreNum);
        super.writeMineToNBT(nbt);
        nbt.setBoolean("isDrawCore", isDrawCore);
        nbt.setBoolean("lead", isLead);

//        pairAddress.writeToNBT(nbt, "garlandMain_");
    }




    public static class GarlandManager {
        public static HashMap<String, FerrisGarland> ExistedGarland_c = new HashMap<>();
        public static HashMap<String, FerrisGarland> ExistedGarland_s = new HashMap<>();

        public static void OnSearch(String id, FerrisGarland garland) {
            HashMap<String, FerrisGarland> ExistedGarland = garland.controller.IsRemote() ? ExistedGarland_s : ExistedGarland_c;
            if (ExistedGarland.containsValue(garland)) return;
            if (ExistedGarland.containsKey(id)) {
//            Logger.debugInfo("found");
                FerrisGarland first = ExistedGarland.remove(id);
                first.pair = garland;
                garland.pair = first;
//            first.isLead = true;
//            garland.isLead = false;
            } else {
//            Logger.debugInfo("put");
                ExistedGarland.put(id, garland);
            }
        }
//    public static FerrisGarland OnRemove(String id, FerrisGarland garland)
//    {
//        return ExistedGarland.remove(id);
//    }

        @SubscribeEvent
        public void OnRemove(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
            ExistedGarland_c.clear();
            ExistedGarland_s.clear();
        }
    }
}
