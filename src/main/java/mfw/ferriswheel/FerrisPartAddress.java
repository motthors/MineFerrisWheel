package mfw.ferriswheel;

import mochisystems.util.IModel;
import mochisystems.util.IModelController;
import net.minecraft.nbt.NBTTagCompound;

public class FerrisPartAddress {
    public int x;
    public int y = -1;
    public int z;
    public int TreeListIndex;

    public FerrisPartAddress Init(int x, int y, int z, int TreeListIndex)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.TreeListIndex = TreeListIndex;
        return this;
    }

    public void Disconnect()
    {
        x = -1;
        y = -1;
        z = -1;
        TreeListIndex = -1;
    }

    public boolean isSyncing()
    {
        return y >= 0;
    }

    public FerrisPartBase GetInstance(IModelController controller)
    {
        IModel blockModel = controller.GetBlockModel(x, y, z);
        if(blockModel instanceof FerrisPartBase) return ((FerrisPartBase)blockModel).getPartFromTreeIndex(TreeListIndex);
        else return null;
    }

    public void readFromNBT(NBTTagCompound nbt, String key)
    {
        TreeListIndex = nbt.getInteger("parentSyncTileIdx");
        if(TreeListIndex != -1)
        {
            x = nbt.getInteger(key+"parentsyncx");
            if(nbt.hasKey(key+"parentsyncy")) y = nbt.getInteger(key+"parentsyncy");
            z = nbt.getInteger(key+"parentsyncz");
        }
    }

    public void writeToNBT(NBTTagCompound nbt, String key)
    {
        nbt.setInteger(key+"parentsyncx", x);
        nbt.setInteger(key+"parentsyncy", y);
        nbt.setInteger(key+"parentsyncz", z);
        nbt.setInteger("parentSyncTileIdx", TreeListIndex);
    }

    public boolean Equals(FerrisPartAddress other)
    {
        return this.x == other.x && this.y == other.y && this.z == other.z
                && this.TreeListIndex == other.TreeListIndex;
    }

    public boolean Equals(FerrisPartBase part)
    {
        if(part == null) return false;
        return this.x == part.controller.CorePosX() && this.y == part.controller.CorePosY() && this.z == part.controller.CorePosZ()
                && this.TreeListIndex == part.getTreeIndexOf(part);
    }

    public void CopyFrom(FerrisPartAddress other)
    {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.TreeListIndex = other.TreeListIndex;
    }
}
