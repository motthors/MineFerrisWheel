package mfw._mc._1_7_10.tileEntity;

import mfw._mc._1_7_10.entity.EntityFerrisCollider;
import mfw.ferriswheel.*;

import mochisystems.util.IModel;
import mochisystems.blockcopier.IModelCollider;
import mochisystems._mc._1_7_10.world.MTYBlockAccess;
import mochisystems.util.IModelController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public abstract class TileEntityFerrisCore extends TileEntity implements IModelController {

    public static class Wheel extends TileEntityFerrisCore {
        @Override
        protected FerrisPartBase GetInstance(IModelController controller) {
            return new FerrisWheel(controller);
        }
   }

    public static class Elevator extends TileEntityFerrisCore {
        @Override
        protected FerrisPartBase GetInstance(IModelController controller) {
            return new FerrisElevator(controller);
        }
    }

    public static class Garland extends TileEntityFerrisCore {
        @Override
        protected FerrisPartBase GetInstance(IModelController controller) {
            return new FerrisGarland(controller);
        }
    }
    public static class GarlandEnd extends TileEntityFerrisCore {
        @Override
        protected FerrisPartBase GetInstance(IModelController controller) {
            return new FerrisGarland.End(controller);
        }
    }

    protected abstract FerrisPartBase GetInstance(IModelController controller);

    //// modules
    public final IModel blockModel;

    protected int side;

    public void SetSide(int side) {
        this.side = side;
    }

    public TileEntityFerrisCore() {
        blockModel = GetInstance(this);
    }

//    public void SetBlockModel(IModel blockModel)
//    {
//        this.blockModel = blockModel;
//    }

    //// wrap between different mc version
    private World GetWorld() {
        return worldObj;
    }


    //// implements IBlockModelController
    @Override
    public int CorePosX() {
        return xCoord;
    }

    @Override
    public int CorePosY() {
        return yCoord;
    }

    @Override
    public int CorePosZ() {
        return zCoord;
    }

    @Override
    public int CoreSide() {
        return side;
    }

    @Override
    public boolean IsInvalid() {
        return isInvalid();
    }

    @Override
    public boolean IsRemote() {
        return GetWorld().isRemote;
    }

    @Override
    public World World() {
        return GetWorld();
    }

    @Override
    public IModel GetBlockModel(int x, int y, int z)
    {
        TileEntityFerrisCore tile = (TileEntityFerrisCore) GetWorld().getTileEntity(x, y, z);
        if (tile == null) return null;
        return tile.blockModel;
    }

    @Override
    public void markBlockForUpdate() {
        GetWorld().markBlockForUpdate(xCoord, yCoord, zCoord);
    }


    //// extends TileEntity
    @Override
    public double getMaxRenderDistanceSquared() {
        return Double.MAX_VALUE;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void setWorldObj(World world) {
        super.setWorldObj(world);
        blockModel.SetWorld(world);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        blockModel.Invalidate();
    }

    @Override
    public void updateEntity() {
        blockModel.Update();
    }

    @Override
    public void onChunkUnload() {
        blockModel.Unload();
    }

    private String FerrisWheelNBTKey = "FerrisWheel";

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        side = nbt.getInteger("side");
        blockModel.readFromNBT(nbt.getCompoundTag(FerrisWheelNBTKey));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("side", side);
        NBTTagCompound ferrisTag = nbt.getCompoundTag(FerrisWheelNBTKey);
        blockModel.writeToNBT(ferrisTag);
        nbt.setTag(FerrisWheelNBTKey, ferrisTag);
    }

    /*
     * 呼ばれるタイミングリストアップ
     * ・ログイン時(チャンク読み込み時？)、GUIボタン押したとき（GUIのパケット受信時に自分で同期処理してるから？）、チャンクの保存or再読み込みした時
     */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        this.writeToNBT(nbtTagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
//		modeltag = pkt.func_148857_g();
//        if(modeltag!=null)constructFromTag(modeltag, modeltag.getInteger("wheelmeta"));
    }

    @Override
    public IModelCollider MakeAndSpawnCollider(IModel parent, MTYBlockAccess blockAccess)
    {
        EntityFerrisCollider collider = new EntityFerrisCollider(World());
        collider.setPosition(xCoord, yCoord, zCoord);
        collider.setParentAddress(new FerrisPartAddress().Init(xCoord, yCoord, zCoord, FerrisPartBase.getTreeIndexOf((FerrisPartBase) parent)));
        collider.makeColliders(blockAccess);
        World().spawnEntityInWorld(collider);
        return collider;
    }

}
