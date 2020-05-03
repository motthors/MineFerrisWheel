package mfw._mc.item;

import mfw._core.MFW_Core;
import mfw._core.MFW_Logger;
import mfw.ferriswheel.*;
import mochisystems.util.IModel;
import mochisystems.blockcopier.IItemBlockModelHolder;
import mochisystems.util.IModelController;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

public class itemFerrisGarland extends ItemBlock implements IItemBlockModelHolder {

    @Override
    public IModel GetBlockModel(IModelController controller)
    {
        return new FerrisGarland(controller);
    }

    @Override
    public void OnSetInventory(IModel model, int slotidx, ItemStack itemStack, EntityPlayer player)
    {
        FerrisPartBase part = (FerrisPartBase)model;
        if(player == null)
        {
            MFW_Logger.error("yavai in itemFerisGarland.OnSetInventory");
            return;
        }
        NBTTagCompound nbt = (NBTTagCompound) itemStack.getTagCompound().copy();
        int x = part.controller.CorePosX();
        int y = part.controller.CorePosY();
        int z = part.controller.CorePosZ();
        int treeIdx = FerrisPartBase.getTreeIndexOf(part);
        ItemStack newStack = MakeEndItemAndSetAddress(nbt,x, y, z, treeIdx);
        player.inventory.addItemStackToInventory(newStack);
//        player.inventory.setItemStack(newStack); TODO 手持ちにEndも足せようとしても、このあとContainer.slotClick()でnullにされるので無意味
    }

    public itemFerrisGarland(Block block)
    {
        super(block);
        setContainerItem(this);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List itemList) {
        ItemStack itemStack = new ItemStack(this, 1, 0);
        NBTTagCompound base = new NBTTagCompound();
        NBTTagCompound nbt = new NBTTagCompound();
        base.setInteger("connectornum", 3);
        base.setString("connectorName0", "0");
        base.setString("connectorName1", "1");
        base.setString("connectorName2", "2");
        base.setBoolean("isdrawingcore", true);
        nbt.setTag("model", base);
        itemStack.setTagCompound(nbt);

        itemList.add(itemStack);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        boolean isPlaced = super.onItemUse(itemStack, player, world, x, y, z, side, hitX, hitY, hitZ);
        if(isPlaced)
        {
            switch(side)
            {
                case 0 : y--; break; case 1 : y++; break;
                case 2 : z--; break; case 3 : z++; break;
                case 4 : x--; break; case 5 : x++; break;
            }
            NBTTagCompound nbt = itemStack.getTagCompound();
            ItemStack newStack = MakeEndItemAndSetAddress(nbt, x, y, z, 0);
            player.inventory.addItemStackToInventory(newStack);
        }
        return isPlaced;
    }



    private ItemStack MakeEndItemAndSetAddress(NBTTagCompound nbt, int x, int y, int z, int treeIndex)
    {
        if (nbt == null) nbt = new NBTTagCompound();
        ItemStack newStack = new ItemStack(MFW_Core.ferrisGarlandEnd, 1);
        FerrisPartAddress address = new FerrisPartAddress();
        address.Init(x, y, z, treeIndex);
        address.writeToNBT((NBTTagCompound) nbt.getTag("model"),"garlandMain_");
        newStack.setTagCompound(nbt);
        return newStack;
    }

    public static class End extends ItemBlock implements IItemBlockModelHolder {

        public End(Block block) {
            super(block);
        }

        @Override
        public IModel GetBlockModel(IModelController controller)
        {
            return new FerrisGarland.End(controller);
        }

        @Override
        public void OnSetInventory(IModel part, int slotidx, ItemStack itemStack, EntityPlayer player)
        {
            FerrisGarland.End garland = (FerrisGarland.End) part;
            NBTTagCompound nbt = itemStack.getTagCompound();
            FerrisPartAddress address = new FerrisPartAddress();
            address.readFromNBT((NBTTagCompound) nbt.getTag("model"),"garlandMain_");
            garland.InitEnd(address);
        }
    }
}
