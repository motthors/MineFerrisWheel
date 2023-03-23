package jp.mochisystems.mfw._mc.tileEntity;

import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;

import jp.mochisystems.mfw.ferriswheel.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class TileEntityFerrisCore extends TileEntity implements IModelController, ITickable {

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
//    public static class GarlandEnd extends TileEntityFerrisCore {
//        @Override
//        protected FerrisPartBase GetInstance(IModelController controller) {
//            return new FerrisGarland.End(controller);
//        }
//    }

    protected abstract FerrisPartBase GetInstance(IModelController controller);

    //// modules
    public final FerrisPartBase blockModel;

    protected EnumFacing side = EnumFacing.UP;

    public void SetSide(EnumFacing facing) {
        this.side = facing;
    }

    public TileEntityFerrisCore() {
        blockModel = GetInstance(this);
    }

//    public void SetBlockModel(IModel blockModel)
//    {
//        this.blockModel = blockModel;
//    }

    //////////// implements IBlockModelController

    @Override
    public double CorePosX() {
        return pos.getX() + 0.5;
    }

    @Override
    public double CorePosY() {
        return pos.getY() + 0.5;
    }

    @Override
    public double CorePosZ() {
        return pos.getZ() + 0.5;
    }

    @Override
    public EnumFacing CoreSide() {
        return side;
    }

    @Override
    public boolean IsInvalid() {
        return isInvalid();
    }

    @Override
    public boolean IsRemote() {
        return World().isRemote;
    }

    @Override
    public World World() {
        return world;
    }

    @Nonnull
    @Override
    public IModel GetModel()
    {
        return blockModel;
    }



    @Override
    public void markBlockForUpdate() {
        markDirty();
        IBlockState state = World().getBlockState(pos);
        World().notifyBlockUpdate(pos, state, state, 3);
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
    public void setWorld(World world) {
        super.setWorld(world);
        blockModel.SetWorld(world);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        blockModel.Invalidate();
    }

    @Override
    public void update() {
        blockModel.Update();
    }

    @Override
    public void onChunkUnload() {
        blockModel.Unload();
    }

    private final String FerrisWheelNBTKey = "FerrisWheel";

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        side = EnumFacing.getFront(nbt.getInteger("side"));
        blockModel.readFromNBT(nbt.getCompoundTag(FerrisWheelNBTKey));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("side", side.getIndex());
        NBTTagCompound ferrisTag = nbt.getCompoundTag(FerrisWheelNBTKey);
        blockModel.writeToNBT(ferrisTag);
        nbt.setTag(FerrisWheelNBTKey, ferrisTag);
        return nbt;
    }

    /*
     * 呼ばれるタイミングリストアップ
     * ・ログイン時(チャンク読み込み時？)、GUIボタン押したとき（GUIのパケット受信時に自分で同期処理してるから？）、チャンクの保存or再読み込みした時
     */
    @Override
    public SPacketUpdateTileEntity getUpdatePacket(){
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        this.writeToNBT(nbtTagCompound);
        return new SPacketUpdateTileEntity(pos, 1, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
        this.readFromNBT(pkt.getNbtCompound());
    }
    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

}
