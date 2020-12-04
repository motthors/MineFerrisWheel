package mfw._mc._1_7_10.tileEntity;

import mfw.ferriswheel.FerrisElevator;
import mfw.ferriswheel.FerrisPartBase;
import mfw.ferriswheel.FerrisWheel;
import mochisystems._mc._1_7_10.block.BlockRemoteController;
import mochisystems._mc._1_7_10.tileentity.TileEntityBlocksScannerBase;
import mochisystems.blockcopier.IModelCollider;
import mochisystems._mc._1_7_10.world.MTYBlockAccess;
import mochisystems.util.IModel;
import mochisystems.util.IModelController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityConnector extends TileEntity implements IInventory, IModelController {

//    public final FerrisWheel AutobuildSettings = new FerrisWheel(null);

    private String name = "";
    public void SetName(String name)
    {
        this.name = name;
    }
    public String GetName()
    {
        return name;
    }
    private ItemStack stackSlot;

    public int slotMode;

    public FerrisWheel wheel = new FerrisWheel(null);
    public FerrisElevator elevator = new FerrisElevator(null);

    public boolean isInserted(){return stackSlot != null; }

    public FerrisPartBase GetCurrentPart()
    {
        switch(slotMode){
            case 1 : return wheel;
            case 2 : return elevator;
            default : return null;
        }
    }

    public TileEntityBlocksScannerBase GetRegisteredConstructor()
    {
        if(!isInserted()) return null;
        NBTTagCompound nbt = stackSlot.getTagCompound();
        int x = nbt.getInteger("mfwcontrollerX");
        int y = nbt.getInteger("mfwcontrollerY");
        int z = nbt.getInteger("mfwcontrollerZ");
        TileEntity tile = worldObj.getTileEntity(x, y, z);
        if(tile instanceof TileEntityBlocksScannerBase) return (TileEntityBlocksScannerBase) tile;
        else return null;
    }

    private void checkSlotStatus()
    {
        if(stackSlot == null) slotMode = 0;
        else
        {
            TileEntity target = BlockRemoteController.TargetTileEntity(World(), stackSlot.getTagCompound());
            if (target == null)
            {
                slotMode = 0;
                return;
            }
            boolean nowHasWheel = target instanceof TileEntityFerrisConstructor;
            boolean nowHasElevator = target instanceof TileEntityElevatorConstructor;
            if (nowHasWheel) slotMode = 1;
            else if (nowHasElevator) slotMode = 2;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        name = nbt.getString("ferrisConnectorName");
        stackSlot = ItemStack.loadItemStackFromNBT((NBTTagCompound) nbt.getTag("itemstack"));
        wheel.readFromNBT((NBTTagCompound) nbt.getTag("wheelnbt"));
        elevator.readFromNBT((NBTTagCompound) nbt.getTag("elevatornbt"));
        slotMode = nbt.getInteger("slotMode");
    }
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setString("ferrisConnectorName", name);
        NBTTagCompound stacknbt = new NBTTagCompound();
        NBTTagCompound wheelNbt = new NBTTagCompound();
        NBTTagCompound elevatorNbt = new NBTTagCompound();
        if(stackSlot != null) stackSlot.writeToNBT(stacknbt);
        nbt.setTag("itemstack", stacknbt);
        wheel.writeToNBT(wheelNbt);
        elevator.writeToNBT(elevatorNbt);
        nbt.setTag("wheelnbt", wheelNbt);
        nbt.setTag("elevatornbt", elevatorNbt);
        nbt.setInteger("slotMode", slotMode);
    }
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbt);
    }
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int idx) {
        return stackSlot;
    }

    @Override
    public ItemStack decrStackSize(int slotIdx, int decrAmount) {
        if (this.stackSlot != null)
        {
            ItemStack itemstack;

            if (this.stackSlot.stackSize <= decrAmount)
            {
                itemstack = this.stackSlot;
                this.stackSlot = null;
                checkSlotStatus();
                return itemstack;
            }
            else
            {
                itemstack = this.stackSlot.splitStack(decrAmount);
                if (this.stackSlot.stackSize == 0)
                {
                    this.stackSlot = null;
                    checkSlotStatus();
                }
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slotIdx, ItemStack itemstack) {
        this.stackSlot = itemstack;
        checkSlotStatus();
    }

    @Override
    public String getInventoryName() {
        return "container.mfw.Connector";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return false;
    }


    public int CorePosX(){return xCoord;}
    public int CorePosY(){return yCoord;}
    public int CorePosZ(){return zCoord;}
    public int CoreSide(){return 0;}
    public boolean IsInvalid(){return isInvalid();}
    public boolean IsRemote(){return World().isRemote;}
    public void markBlockForUpdate(){ World().markBlockForUpdate(xCoord, yCoord, zCoord);}
    public World World(){return worldObj;}
    public IModel GetBlockModel(int x, int y, int z){return wheel;}
    public IModelCollider MakeAndSpawnCollider(IModel parent, MTYBlockAccess blockAccess) { return null; }
}
