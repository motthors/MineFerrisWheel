package jp.mochisystems.mfw._mc.tileEntity;

import jp.mochisystems.core._mc.block.BlockRemoteController;
import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.blockcopier.IBLockCopyHandler;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.blockcopier.IModelCollider;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import jp.mochisystems.mfw.ferriswheel.FerrisElevator;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import jp.mochisystems.mfw.ferriswheel.FerrisWheel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileEntityConnector extends TileEntity implements IInventory {

    private String connectorName = "";
    public String GetName()
    {
        return connectorName;
    }

    private ItemStack stackSlot = ItemStack.EMPTY;

    public enum Mode {none, wheel, elevator}
    public Mode mode = Mode.none;

    public NBTTagCompound modelNbt = new NBTTagCompound();

//    public final FerrisWheel wheel;
//    public final FerrisElevator elevator;

    public TileEntityConnector()
    {
//        wheel = new FerrisWheel(null);
//        elevator = new FerrisElevator(null);
//        wheel.Reset();
//        elevator.Reset();
    }


    public TileEntityBlocksScannerBase GetRegisteredConstructor()
    {
        if(isEmpty()) return null;
        NBTTagCompound nbt = stackSlot.getTagCompound();
        int x = nbt.getInteger(BlockRemoteController.KeyRemotePosX);
        int y = nbt.getInteger(BlockRemoteController.KeyRemotePosY);
        int z = nbt.getInteger(BlockRemoteController.KeyRemotePosZ);
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if(tile instanceof TileEntityBlocksScannerBase) return (TileEntityBlocksScannerBase) tile;
        else return null;
    }

    private void checkSlotStatus()
    {
        if(stackSlot.isEmpty()) {
            mode = Mode.none;
            return;
        }
        TileEntity target = BlockRemoteController.TargetTileEntity(world, stackSlot.getTagCompound());
        if (target == null)
        {
            mode = Mode.none;
        }
        else if (target instanceof TileEntityFerrisConstructor)
        {
            mode = Mode.wheel;
        }
        else if(target instanceof TileEntityElevatorConstructor)
        {
            mode = Mode.elevator;
        }
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        connectorName = nbt.getString("connectorName");
        modelNbt = nbt.getCompoundTag("model");
        if(nbt.hasKey("itemstack"))
            stackSlot = new ItemStack(nbt.getCompoundTag("itemstack"));
        if(world != null) checkSlotStatus();
    }
    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setString("connectorName", connectorName);
        NBTTagCompound stackNbt = new NBTTagCompound();
        stackSlot.writeToNBT(stackNbt);
        nbt.setTag("itemstack", stackNbt);
        nbt.setTag("model", modelNbt);
        return nbt;
    }
    @Override
    public SPacketUpdateTileEntity getUpdatePacket(){
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new SPacketUpdateTileEntity(pos, 1, nbt);
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




    /////////////////// IInventory

    @Override
    public int getSizeInventory() {
        return 1;
    }
    @Override
    public boolean isEmpty(){
        return stackSlot.isEmpty();
    }
    @Override
    public ItemStack getStackInSlot(int idx) {
        return stackSlot;
    }
    @Override
    public ItemStack decrStackSize(int index, int amount) {
        return !stackSlot.isEmpty() && amount > 0 ? stackSlot.splitStack(amount) : ItemStack.EMPTY;
    }
    @Override
    public ItemStack removeStackFromSlot(int index){
        if(index != 0) return ItemStack.EMPTY;
        return stackSlot = ItemStack.EMPTY;
    }
    @Override
    public void setInventorySlotContents(int slotIdx, ItemStack itemstack) {
        this.stackSlot = itemstack;
        checkSlotStatus();
    }
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }
    @Override
    public void openInventory(EntityPlayer player) {

    }
    @Override
    public void closeInventory(EntityPlayer player) {

    }
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }
    @Override
    public int getField(int id) {
        return 0;
    }
    @Override
    public void setField(int id, int value) {

    }
    @Override
    public int getFieldCount() {
        return 0;
    }
    @Override
    public void clear() {

    }
    @Nonnull
    @Override
    public String getName() {
        return "mfw.connector.container";
    }
    @Override
    public boolean hasCustomName() {
        return false;
    }


    /////////////////// imodelcontroller


//    public double CorePosX(){return pos.getX();}
//    public double CorePosY(){return pos.getY();}
//    public double CorePosZ(){return pos.getZ();}
//    public EnumFacing CoreSide(){return EnumFacing.UP;}
//    public boolean IsInvalid(){return isInvalid();}
//    public boolean IsRemote(){return World().isRemote;}
//    public void markBlockForUpdate(){
//        markDirty();
//        IBlockState state = World().getBlockState(pos);
//        World().notifyBlockUpdate(pos, state, state, 3);
//    }
//    public World World(){return world;}
//    public IModelCollider MakeAndSpawnCollider(IModel parent, MTYBlockAccess blockAccess) { return null; }

}
