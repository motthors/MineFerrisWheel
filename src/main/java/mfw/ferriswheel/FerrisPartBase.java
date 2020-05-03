package mfw.ferriswheel;

import mfw._mc.gui.gui.GUIFerrisCoreBase;
import mochisystems.util.IModel;
import mochisystems.blockcopier.IItemBlockModelHolder;
import mochisystems.util.IModelController;
import mochisystems.util.InterpolationTick;
import mfw.storyboard.StoryBoardManager;
import mochisystems.handler.TickEventHandler;
import mochisystems.math.Math;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.*;

/**
 * construct loopHead-child connection
 * control updating order
 * control UI layer
 */
public abstract class FerrisPartBase implements IInventory, IModel {

    public abstract float GetSoundSourceValue();
    public abstract void toggleStopFlag();
    public abstract GUIFerrisCoreBase GetGUIInstance(int x, int y, int z, InventoryPlayer inventory, FerrisPartBase part);

    //// sub modules
    public final StoryBoardManager storyboardManager;
    public final FerrisSound soundManager;

    //// connection
    public final IModelController controller;
    private FerrisPartBase rootPart;
    protected FerrisPartBase parent;
    private FerrisPartBase[] children = new FerrisPartBase[0];
    public Connector connectorFromParent = new Connector(Vec3d.Zero, "_root_");
    public Connector[] connectors = new Connector[0];
    protected long lastUpdatingTick = 0;
//    protected boolean isForrowParentTransform;
    protected boolean isIndependentTransform;
    public FerrisPartBase GetRootPart(){ return rootPart; }
    public FerrisPartBase GetParent(){ return parent; }
    public FerrisPartBase[] GetChildren(){ return children; }

    //// sync
    public boolean isEnableSync = false;
    public boolean isSyncTargetSpeed = false;
    public final FerrisPartAddress syncTarget;
    protected FerrisPartBase syncParent;
    private List<FerrisPartBase> SyncedChildren = new ArrayList<>();
    private List<ILateUpdater> lateUpdaters = new ArrayList<>();
    public void toggleSyncFlag()
    {
        isEnableSync = !isEnableSync;
    }
    public void toggleSyncMode()
    {
        isSyncTargetSpeed = !isSyncTargetSpeed;
    }
    public void AddLateUpdater(ILateUpdater updater)
    {
        if(lateUpdaters.contains(updater)) return;
        lateUpdaters.add(updater);
    }
    public void RemoveLateUpdater(ILateUpdater updater)
    {
        lateUpdaters.remove(updater);
    }

    //// position attitude
//    protected final Mat4 attitude = new Mat4();
    protected final Quaternion baseAttitudeRot = new Quaternion();
    protected final Quaternion rotation = new Quaternion();
    final Quaternion prevRotation = new Quaternion();
    final Quaternion rotaterForRender = new Quaternion();
    public final Vec3d offset = new Vec3d();
    public InterpolationTick Scale = new InterpolationTick(1.0f);
    public InterpolationTick Scale(){return Scale;}
    public float localScale = 1;
    public float GetLocalScale(){return localScale;}
    public void SetLocalScale(float value){
        localScale = value;
    }
    public Quaternion getRotation(){return rotation;}
    public boolean GetIsForrowParentTransform()
    {
        return isIndependentTransform;
    }
    public void SetIsIndependentTransform(boolean flag)
    {
        isIndependentTransform = flag;
    }
    public void SetOffset(float x, float y, float z)
    {
        offset.SetFrom(x, y, z);
        connectorFromParent.SetOrigin(new Vec3d(x, y, z));

    }
    public void ToggleIsIndependentTransform()
    {
        isIndependentTransform = !isIndependentTransform;
    }

    //// misc
    private boolean isActive;
    private boolean isValidated;
    public boolean isEnableStoryBoard = false;
    public void toggleStoryBoardFlag()
    {
        isEnableStoryBoard = !isEnableStoryBoard;
    }
    public void SetActive(boolean active){ isActive = active; }


    //// redstone
    private float rsPower;
    private float prevRsPower;
    private byte isToggleNow; // 0:non  1:off to ON  -1:on to OFF
    public byte rsFlag = 0;
    //rsflag enum
    private static final byte rsFlag_Non = 0; //効果なし
    private static final byte rsFlag_StopWhenOn = 1; //ON時停止
    private static final byte rsFlag_StopWhenOff = 2; //OFF時停止
    private static final byte rsFlag_RatioPositive = 3; //大きいほど高倍率
    private static final byte rsFlag_RatioNegative = 4; //小さいほど高倍率
    public static final byte rsFlag_End = 5;
    public void rotateRSFlag()
    {
        rsFlag = (byte) ((rsFlag+1) % rsFlag_End);
    }

    //// GUI
    private String partName;
    public String GetName(){ return partName; }
    private FerrisPartBase selectedPartForGUI;
    public FerrisPartBase GetSelectedPartInGUI() {
        return rootPart.selectedPartForGUI;
    }
    public void SetSelectedPartInGUI(FerrisPartBase part) {
        rootPart.selectedPartForGUI = part;
    }
    public void BackSelectedPart() {
        rootPart.selectedPartForGUI = rootPart.selectedPartForGUI.GetParent();
    }
    public void ResetSelectedPartIngGUI()
    {
       rootPart.selectedPartForGUI = rootPart;
    }

    // lock destruct block and pick up item
    private boolean isLock = false;
    public void toggleLock()
    {
        rootPart.isLock = !rootPart.isLock;
    }
    public boolean IsLock(){ return rootPart.isLock; }

    // storage
    NBTTagCompound partNbtOnConstruct;

    public FerrisPartBase(IModelController controller)
    {
        this.controller = controller;
        rootPart = this;
        syncTarget = new FerrisPartAddress();
        storyboardManager = new StoryBoardManager(this);
        soundManager = new FerrisSound(this);
        selectedPartForGUI = this;
    }

    @Override
    public void Reset()
    {
        isIndependentTransform = false;
        offset.CopyFrom(Vec3d.Zero);
    }

    @Override
    public void Unload()
    {
        soundManager.Invalid();
    }

    public void SetWorld(World world)
    {
        for (FerrisPartBase child : children)
        {
            if (child != null) child.SetWorld(world);
        }
    }

    @Override
    public boolean HasChild()
    {
        boolean ret = false;
        for (FerrisPartBase child : children) {
            ret |= child != null;
        }
        return ret;
    }

    public void setSize(float size)
    {
        localScale = Math.Clamp(size, 0, 100.0f);
    }

    public FerrisPartAddress GetMyAddress()
    {
        FerrisPartAddress address = new FerrisPartAddress();
        address.x = controller.CorePosX();
        address.y = controller.CorePosY();
        address.z = controller.CorePosZ();
        address.TreeListIndex = getTreeIndexOf(this);
        return address;
    }

    private int GetChildSlotCount()
    {
        return children.length;
    }

    private static void Connect(FerrisPartBase parent, FerrisPartBase child, int slotIndex)
    {
        child.rootPart = parent.rootPart;
        child.parent = parent;
        parent.children[slotIndex] = child;
        parent.ReConstructTreeArray();
        child.connectorFromParent = parent.connectors[slotIndex];
        child.SetWorld(parent.controller.World());
    }

    public void Validate()
    {
        isValidated = true;
        ReConstructTreeArray();
    }

    public final void readFromNBT(NBTTagCompound nbt)
    {
        readMineFromNBT((NBTTagCompound) nbt.getTag("model"));
        readChildFromNBT(nbt);
    }

    protected void ChangeCoreInstanceNum(int slotNum)
    {
        if(childSlots == null || childSlots.length != slotNum) childSlots = new ItemStack[slotNum];
        if(children == null || children.length != slotNum) children = new FerrisPartBase[slotNum];
        if(connectors == null || connectors.length != slotNum) connectors = new Connector[slotNum];
        for(int i = 0; i < slotNum; ++i)
        {
            Vec3d v = new Vec3d();
            if(connectors[i] == null)connectors[i] = new Connector(v, "");
            else connectors[i].SetOrigin(v);
        }
    }
    protected void ChangeConnectorData(int index, String name, Vec3d originalPos)
    {
        connectors[index].Reset(name, originalPos);
    }

    public final void writeToNBT(NBTTagCompound nbt)
    {
        if(partNbtOnConstruct == null) partNbtOnConstruct = new NBTTagCompound();
        nbt.setTag("model", partNbtOnConstruct);
        writeMineToNBT(partNbtOnConstruct);
        writeChildToNBT(nbt);
    }

    protected void readMineFromNBT(NBTTagCompound nbt)
    {
        partNbtOnConstruct = (NBTTagCompound) nbt.copy();
        partName = nbt.getString("ModelName");
        if(nbt.hasKey("scale")) localScale = nbt.getFloat("scale");
        int slotNum = nbt.getInteger("connectornum");
        ChangeCoreInstanceNum(slotNum);
        for(int i = 0; i < slotNum; ++i)
        {
            Vec3d v = new Vec3d(); v.ReadFromNBT("connector"+i, nbt);
            String name = nbt.getString("connectorName"+i);
            ChangeConnectorData(i, name, v);
        }
        isLock = nbt.getBoolean("islock");
        isEnableStoryBoard = nbt.getBoolean("enablestoryboard");
        offset.ReadFromNBT("offset", nbt);
        isIndependentTransform = nbt.getBoolean("isIndependentTransform");
        syncTarget.readFromNBT(nbt,"syncTarget");
        isEnableSync = nbt.getBoolean("enablesyncrot");
        isSyncTargetSpeed = nbt.getBoolean("synctargetspeed");
    }

    // 設置後変化のあるもののみでよい。基本データは保存しておいたものをWriteToNBT()で入れてる
    protected void writeMineToNBT(NBTTagCompound nbt)
    {
//        if(partName==null || "".equals(partName)) return;
        nbt.setBoolean("islock", isLock);
        nbt.setFloat("scale", localScale);
        offset.WriteToNBT("offset", nbt);
        nbt.setBoolean("isIndependentTransform", isIndependentTransform);
        syncTarget.writeToNBT(nbt, "syncTarget");
        nbt.setBoolean("synctargetspeed", isSyncTargetSpeed);
        nbt.setBoolean("enablesyncrot", isEnableSync);
    }

    private void readChildFromNBT(NBTTagCompound nbt)
    {
        for (int slotIndex = 0; slotIndex < this.children.length; ++slotIndex) {
            NBTTagCompound childNbt = (NBTTagCompound) nbt.getTag("PartSlot" + slotIndex);
            if (childNbt == null) continue;

            boolean isNew = (children[slotIndex] == null);
            if (isNew) {
                if(childSlots[slotIndex]==null) childSlots[slotIndex] = ItemStack.loadItemStackFromNBT(childNbt);
                IModel child = ((IItemBlockModelHolder) childSlots[slotIndex].getItem()).GetBlockModel(controller);
                Connect(this, (FerrisPartBase) child, slotIndex);
                child.Reset();
                child.readFromNBT(childNbt);
            } else {
                children[slotIndex].readFromNBT(childNbt);
            }
        }
    }

    private void writeChildToNBT(NBTTagCompound nbt) {
        for (int i = 0; i < this.children.length; ++i) {
            FerrisPartBase child = this.children[i];
            if (child == null) continue;

            NBTTagCompound childNBT = new NBTTagCompound();
            child.writeToNBT(childNBT);

            this.childSlots[i].writeToNBT(childNBT);
            nbt.setTag("PartSlot"+i, childNBT);
        }
    }


    public boolean IsInvalid()
    {
        return controller.IsInvalid();
    }

    private void InvalidatePart(int slotIndex)
    {
        children[slotIndex].Invalidate();
        children[slotIndex] = null;
        ReConstructTreeArray();
    }

    public void Invalidate()
    {
        for(int i = 0; i < children.length; ++i)
        {
            if(children[i] == null) continue;
            children[i].Invalidate();
            children[i] = null;
        }
    }

    public final void Update()
    {
        if(!isValidated)
        {
            Validate();
        }
        if(isEnableSync && syncTarget.isSyncing())
        {
            CheckAndReconstructSync();
            return;
        }
        UpdateRsPower();
        UpdateOwn();
        UpdateSyncChildren();
        UpdateChildren();
        for(ILateUpdater u : lateUpdaters) u.LateUpdate();
    }

    private int syncIntervalCount = 85; //最初の起動時はちょっと早めにSync確認したい
    private void CheckAndReconstructSync()
    {
        syncIntervalCount++;
        if(syncIntervalCount < 100) return;

        syncIntervalCount = 0;
        if(syncTarget.Equals(syncParent)) return;
        RegisterSyncParent();
    }

    protected void UpdateOwn()
    {
        lastUpdatingTick = TickEventHandler.getTickCounter();
        for(int i = 0; i < connectors.length; ++i)
        {
            connectors[i].SetPrev();
        }
    }

    private void UpdateChildren()
    {
        for(FerrisPartBase child : children)
        {
            if(child != null) child.Update();
        }
    }

    private void UpdateSyncChildren()
    {
        for(FerrisPartBase child : SyncedChildren)
        {
            if(child != null)
            {
                child.UpdateRsPower();
                child.UpdateOwn();
                child.UpdateSyncChildren();
                child.UpdateChildren();
                for(ILateUpdater u : child.lateUpdaters) u.LateUpdate();
            }
        }
    }

    private void SetSyncChild(FerrisPartBase child)
    {
        child.syncParent = this;
        SyncedChildren.add(child);
    }

    public void SetNewSyncParent(FerrisPartAddress target)
    {
        if(target.Equals(syncTarget))return;
        if(target.equals(this))
        {
            target.Disconnect();
            return;
        }
        syncTarget.CopyFrom(target);
        RegisterSyncParent();
    }

    private void RegisterSyncParent()
    {
        syncParent = syncTarget.GetInstance(controller);
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
        for(FerrisPartBase child : SyncedChildren)
        {
            child.ReleaseSyncParent();
        }
        SyncedChildren.clear();
    }

    public void ReleaseSyncParent()
    {
        if(syncParent != null) syncParent.SyncedChildren.remove(this);
        syncParent = null;
        syncTarget.Disconnect();
    }

    // return ; enabled part in slot
    public boolean canOpenChildGUI(int num)
    {
//        if(children.length==0)return false;
        return (0 <= num && num < children.length && childSlots[num] != null);
    }

    public boolean isRoot()
    {
        return rootPart == this;
    }

    public boolean hasParent()
    {
        if(parent == null)return false;
        return true;
    }

    public void ToggleVisibleCore() {}

    @Override
    public final void RenderModel(int pass, float partialTick)
    {
        Render(pass, partialTick);
    }

//    public final void RenderRoot(int pass, float partialTick)
//    {
//        //TODO
////        GL11.glRotated(rotMeta2_side, rotvecMeta2_side.CorePosX, rotvecMeta2_side.y, rotvecMeta2_side.z); // coreSide
//        Render(pass, partialTick);
//    }

    private void Render(int pass, float partialTick)
    {
        GL11.glPushMatrix();
        RenderOwn(pass, partialTick);
        GL11.glPopMatrix();
        RenderChildren(pass, partialTick);
    }

    protected void RenderOwn(int pass, float partialTick){}

    private void RenderChildren(int pass, float partialTick)
    {
        for (FerrisPartBase child : children)
        {
            if(child == null) continue;
            child.Render(pass, partialTick);
        }
    }

    public ItemStack takeChildPart(int slotIndex)
    {
        ItemStack tookItem = childSlots[slotIndex];
        if(tookItem == null) return null;

        childSlots[slotIndex] = null;
        NBTTagCompound nbt = new NBTTagCompound();
        children[slotIndex].writeToNBT(nbt);
        tookItem.setTagCompound(nbt);
        InvalidatePart(slotIndex);
        return tookItem;
    }

    ////////// control red stone input

    public void setRSPower(int p)
    {
        rsPower = p/(float)15;
    }

    private void UpdateRsPower()
    {
        isToggleNow = (byte) ((prevRsPower == rsPower) ? 0 : rsPower == 0 ? -1 : 1 );
        prevRsPower = rsPower;
    }

    public float getRSPower()
    {
        return rootPart.rsPower;
    }
    public float isUpdatedRsPowerNow()
    {
        return rootPart.isToggleNow;
    }

    protected float getSpeedRatioFromRSFlag()
    {
        switch(rsFlag)
        {
            case rsFlag_Non : return 1;
            case rsFlag_StopWhenOn : return (getRSPower() > 0.01) ? 0 : 1;
            case rsFlag_StopWhenOff : return (getRSPower() < 0.01) ? 0 : 1;
            case rsFlag_RatioPositive : return getRSPower();
            case rsFlag_RatioNegative : return 1f-getRSPower();
        }
        return -1000;
    }

    //////// Ferris Part Tree Indexing Service

    public final ArrayList<FerrisPartBase> BreadthFirstPartTreeArray = new ArrayList<>();

    private void ReConstructTreeArray()
    {
        rootPart.BreadthFirstPartTreeArray.clear();
        ArrayDeque<FerrisPartBase> deque = new ArrayDeque<>();
        deque.add(rootPart);
        rootPart.BreadthFirstPartTreeArray.add(rootPart);

        //breadth-first search
        for(;;)
        {
            if(deque.size() == 0) break;
            FerrisPartBase branch = deque.remove();
            for(FerrisPartBase part : branch.children)
            {
                if(part==null)continue;
                deque.add(part);
                rootPart.BreadthFirstPartTreeArray.add(part);
            }
        }
    }

    public static int getTreeIndexOf(FerrisPartBase target)
    {
        return target.rootPart.BreadthFirstPartTreeArray.indexOf(target);
    }

    public FerrisPartBase getPartFromTreeIndex(int partIndex)
    {
        if(partIndex < 0)return null;
        if(partIndex >= rootPart.BreadthFirstPartTreeArray.size()) return null;
        return rootPart.BreadthFirstPartTreeArray.get(partIndex);
    }


    //////////extends IInventry

    private ItemStack[] childSlots = new ItemStack[0];
    public static EntityPlayer ChangeUser;

    @Override
    public int getSizeInventory(){return GetChildSlotCount();}

    @Override
    public void setInventorySlotContents(int slotidx, ItemStack itemstack)
    {
        if(itemstack == null) return;
        childSlots[slotidx] = itemstack;

        IItemBlockModelHolder ferrisItem = (IItemBlockModelHolder) itemstack.getItem();
        if(!controller.IsRemote()) {
            FerrisPartBase child = (FerrisPartBase) ferrisItem.GetBlockModel(controller);
            Connect(this, child, slotidx);
            ferrisItem.OnSetInventory(child, slotidx, itemstack, ChangeUser);
            NBTTagCompound nbt = itemstack.getTagCompound();
            child.Reset();
            child.readFromNBT(nbt);
            //nbtでスロット情報同期
            controller.markBlockForUpdate();
            // Itemstack内の情報(NBT)は空にしておく。情報は展開されてFerrisWheelが全部持ってる　はず
            itemstack.setTagCompound(new NBTTagCompound());
        }
    }

    @Override
    public ItemStack getStackInSlot(int idx)
    {
        if(childSlots == null)return null;
        if(idx >= childSlots.length)return null;
        return childSlots[idx];
    }

    @Override
    public ItemStack decrStackSize(int slotIndex, int decrCount)
    {
        if (this.childSlots[slotIndex] == null) return null;
        if (this.isLock) return null;

        if (decrCount >= this.childSlots[slotIndex].stackSize)
        {
            ItemStack itemstack = this.childSlots[slotIndex];
            this.childSlots[slotIndex] = null;
            return itemstack;
        }
        else
        {
            ItemStack tookItemStack = this.childSlots[slotIndex].splitStack(decrCount);

            if (this.childSlots[slotIndex].stackSize == 0)
            {
                this.childSlots[slotIndex] = null;
            }

            return tookItemStack;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotIndex)
    {
        if (this.childSlots[slotIndex] == null) return null;

        ItemStack itemstack = this.childSlots[slotIndex];
        this.childSlots[slotIndex] = null;
        return itemstack;
    }

    @Override
    public String getInventoryName(){return "container.mfw.ferriccore";}

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {return 1;}

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
        //this.worldObj.getTileEntity(this.CorePosX, this.CorePosY, this.CorePosZ) != this ? false
        //        : player.getDistanceSq((double)this.CorePosX + 0.5D, (double)this.CorePosY + 0.5D, (double)this.CorePosZ + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_){return false;}

    @Override
    public void markDirty(){} //TODO なんか実装する

}
