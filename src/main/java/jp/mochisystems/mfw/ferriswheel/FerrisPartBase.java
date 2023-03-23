package jp.mochisystems.mfw.ferriswheel;

import jp.mochisystems.core._mc.eventhandler.TickEventHandler;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.math.*;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.*;

public abstract class FerrisPartBase implements IInventory, IModel {

    public abstract float GetSoundSourceValue();


    //// connection
    public final IModelController controller;
    private FerrisPartBase rootPart;
    protected FerrisPartBase parent;
    private FerrisPartBase[] children = new FerrisPartBase[0];
    public Connector connectorFromParent = new Connector("_root_");
    public final List<Connector> connectors = new ArrayList<>();
    public int layer = 0;
    protected long lastUpdatingTick = 0;
    protected boolean isIndependentTransform;
    public FerrisPartBase GetRootPart(){ return rootPart; }
    public FerrisPartBase GetParent(){ return parent; }
    public FerrisPartBase[] GetChildren(){ return children; }


    //// position attitude
    protected final Quaternion rotation = new Quaternion();
    final Quaternion prevRotation = new Quaternion();
    final Quaternion rotatorForRender = new Quaternion();
    public final Vec3d offset = new Vec3d();
    public final InterpolationTick ScaleX = new InterpolationTick(1.0f);
    public final InterpolationTick ScaleY = new InterpolationTick(1.0f);
    public final InterpolationTick ScaleZ = new InterpolationTick(1.0f);
    public Vec3d localScale = Vec3d.One.New();
    public Vec3d GetLocalScale(){return localScale;}
    public void SetLocalScale(Vec3d v){
        v.x = Math.Clamp(v.x, 0.001f, 1000.0f);
        v.y = Math.Clamp(v.y, 0.001f, 1000.0f);
        v.z = Math.Clamp(v.z, 0.001f, 1000.0f);
        localScale.CopyFrom(v);
    }
    public Quaternion getRotation(){return rotation;}
    public boolean GetIsFollowParentTransform()
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
    public void SetRotation(Quaternion q)
    {

    }
    @Override
    public CommonAddress GetCommonAddress(){
        CommonAddress address = controller.GetCommonAddress();
        address.TreeListIndex = getTreeIndexOf(this);
        return address;
    }


    @Override
    public double ModelPosX() {
        return controller.CorePosX() + connectorFromParent.Current().x;
    }
    @Override
    public double ModelPosY() {
        return controller.CorePosY() + connectorFromParent.Current().y;
    }
    @Override
    public double ModelPosZ() {
        return controller.CorePosZ() + connectorFromParent.Current().z;
    }

    public void ToggleIsIndependentTransform()
    {
        isIndependentTransform = !isIndependentTransform;
    }

    //// misc
    protected boolean isActive;
    protected boolean isValidated;
    public void SetVisible(boolean active){ isActive = active; }


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
    private String partName = "";
    public String GetName(){ return partName; }


    // lock destruct block and pick up item
    private boolean isLock = false;
    public void toggleLock()
    {
        rootPart.isLock = !rootPart.isLock;
    }
    public boolean IsLock(){ return rootPart.isLock; }
    public void SetLock(boolean lock){ rootPart.isLock = lock; }

    // storage
    NBTTagCompound partNbtOnConstruct;

    public FerrisPartBase(IModelController controller)
    {
        this.controller = controller;
        rootPart = this;
    }

    @Override
    public void Reset()
    {
        isIndependentTransform = false;
        offset.CopyFrom(Vec3d.Zero);
    }

    @Override
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




    private int GetChildSlotCount()
    {
        return children.length;
    }

    private static void Connect(FerrisPartBase parent, FerrisPartBase child, int slotIndex)
    {
        child.layer = parent.layer + 1;
        child.rootPart = parent.rootPart;
        child.parent = parent;
        parent.children[slotIndex] = child;
        parent.ReConstructTreeArray();
        child.connectorFromParent = parent.connectors.get(slotIndex);
        child.SetWorld(parent.controller.World());
    }

    public void Validate()
    {
        isValidated = true;
        ReConstructTreeArray();
    }

    public final void readFromNBT(NBTTagCompound nbt)
    {
        partNbtOnConstruct = nbt.getCompoundTag("model").copy();
        readMineFromNBT(partNbtOnConstruct);
        readChildFromNBT(nbt);
    }

    protected void ChangeCoreInstanceNum(int slotNum)
    {
        if(childSlots == null || childSlots.size() != slotNum) childSlots = NonNullList.withSize(slotNum, ItemStack.EMPTY);
        if(children == null || children.length != slotNum) children = new FerrisPartBase[slotNum];
    }


    public final void writeToNBT(NBTTagCompound nbt)
    {
        if(partNbtOnConstruct == null) partNbtOnConstruct = new NBTTagCompound();
        nbt.setTag("model", partNbtOnConstruct);
        writeMineToNBT(partNbtOnConstruct);
        writeChildToNBT(nbt);
    }

    public void readMineFromNBT(NBTTagCompound nbt)
    {
        partName = nbt.getString("ModelName");
        Vec3d offset = new Vec3d(); offset.ReadFromNBT("modelOffset", nbt);
        if(nbt.hasKey("scalevx")) {
//            SetLocalScale(nbt.getFloat("scale"));
            localScale.ReadFromNBT("scale", nbt);
        }
        int slotNum = nbt.getInteger("connectornum");
        ChangeCoreInstanceNum(slotNum);
        if(connectors.size() != slotNum) {
            connectors.clear();
            for (int i = 0; i < slotNum; ++i) {
                Vec3d v = new Vec3d();
                v.ReadFromNBT("connector" + i, nbt);
                String name = nbt.getString("connectorName" + i);
                Connector c = new Connector(name);
                v.sub(offset);
                c.SetOrigin(v);
                connectors.add(c);
            }
        }
        this.offset.ReadFromNBT("offset", nbt);
        isLock = nbt.getBoolean("islock");
        isActive = nbt.getBoolean("isActive");
        isIndependentTransform = nbt.getBoolean("isIndependentTransform");

    }

    // 設置後変化のあるもののみでよい。基本データは保存しておいたものをWriteToNBT()で入れてる
    public void writeMineToNBT(NBTTagCompound nbt)
    {
//        if(partName==null || "".equals(partName)) return;
        nbt.setString("ModelName", partName);
        nbt.setBoolean("islock", isLock);
        nbt.setBoolean("isActive", isActive);
        nbt.setInteger("connectornum", children.length);
        localScale.WriteToNBT("scale", nbt);
        offset.WriteToNBT("offset", nbt);
        nbt.setBoolean("isIndependentTransform", isIndependentTransform);

    }

    private void readChildFromNBT(NBTTagCompound nbt)
    {
        for (int slotIndex = 0; slotIndex < this.children.length; ++slotIndex) {
            NBTTagCompound childNbt = (NBTTagCompound) nbt.getTag("PartSlot" + slotIndex);
            if (childNbt == null) continue;

            boolean isNew = (children[slotIndex] == null);
            if (isNew) {
                childSlots.set(slotIndex, new ItemStack(childNbt));
                IModel child = ((IItemBlockModelHolder) childSlots.get(slotIndex).getItem()).GetBlockModel(controller);
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

            this.childSlots.get(i).writeToNBT(childNBT);
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
        children = new FerrisPartBase[0];
    }

    public void Update()
    {
        if(!isValidated)
        {
            Validate();
        }
        UpdateRsPower();
        UpdateOwn();
        UpdateChildren();
//        for(ILateUpdater u : lateUpdaters) u.LateUpdate();
    }



    protected void UpdateOwn()
    {
        lastUpdatingTick = TickEventHandler.getTickCounter();
    }

    protected void UpdateChildren()
    {
        for(FerrisPartBase child : children)
        {
            if(child != null) child.Update();
        }
    }







    // return ; enabled part in slot
    public boolean canOpenChildGUI(int num)
    {
//        if(children.length==0)return false;
        return (0 <= num && num < children.length && !childSlots.get(num).isEmpty());
    }

    public boolean isRoot()
    {
        return rootPart == this;
    }

    public boolean hasParent()
    {
        return parent != null;
    }

    public void ToggleVisibleCore() {}

    @Override
    public final void RenderModel(int pass, float partialTick)
    {
        Render(pass, partialTick);
    }


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
        ItemStack tookItem = childSlots.get(slotIndex);
        if(tookItem.isEmpty()) return null;

        childSlots.set(slotIndex, ItemStack.EMPTY);
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

    protected void UpdateRsPower()
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
        while (deque.size() != 0) {
            FerrisPartBase branch = deque.remove();
            for (FerrisPartBase part : branch.children) {
                if (part == null) continue;
                deque.add(part);
                rootPart.BreadthFirstPartTreeArray.add(part);
            }
        }
    }

    public static int getTreeIndexOf(FerrisPartBase target)
    {
        return target.rootPart.BreadthFirstPartTreeArray.indexOf(target);
    }

    @Override
    public IModel GetModelFromTreeIndex(int partIndex)
    {
        if(partIndex < 0)return null;
        if(partIndex >= rootPart.BreadthFirstPartTreeArray.size()) return null;
        return rootPart.BreadthFirstPartTreeArray.get(partIndex);
    }


    //////////extends IInventry

    private NonNullList<ItemStack> childSlots = NonNullList.withSize(0, ItemStack.EMPTY);
    public static EntityPlayer ChangeUser;

    @Override
    public int getSizeInventory(){return GetChildSlotCount();}

    @Override
    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.childSlots)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int idx)
    {
        if(childSlots == null) return ItemStack.EMPTY;
        if(childSlots.size() <= idx) return ItemStack.EMPTY;
        return childSlots.get(idx);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(this.childSlots, index, count);
    }

    @Override
    public void setInventorySlotContents(int slotidx, ItemStack itemstack)
    {
        if(itemstack.isEmpty()) return;
        childSlots.set(slotidx, itemstack);

        IItemBlockModelHolder ferrisItem = (IItemBlockModelHolder) itemstack.getItem();
        if(!controller.IsRemote()) {
            FerrisPartBase child = (FerrisPartBase) ferrisItem.GetBlockModel(controller);
            Connect(this, child, slotidx);
            NBTTagCompound nbt = itemstack.getTagCompound();
            child.Reset();
            child.readFromNBT(nbt);
            //nbtでスロット情報同期
            controller.markBlockForUpdate();
            // Itemstack内の情報(NBT)は空にしておく。情報は展開されて子の情報は子が全部持ってる　はず
            itemstack.setTagCompound(new NBTTagCompound());
        }
    }

    @Override
    public String getName(){return "container.mfw.core";}

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {return 1;}

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
    public boolean isItemValidForSlot(int index, ItemStack stack){
        return false;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void markDirty() {
        controller.markBlockForUpdate();
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

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
}
