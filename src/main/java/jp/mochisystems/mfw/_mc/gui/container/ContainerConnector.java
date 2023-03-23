package jp.mochisystems.mfw._mc.gui.container;

import jp.mochisystems.core._mc.gui.slotCanInsertOnlyItem;
import jp.mochisystems.core._mc.item.itemBlockRemoteController;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityConnector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerConnector extends Container
{
    protected int width, height;
    private TileEntityConnector tile;

    public ContainerConnector(InventoryPlayer invPlayer, TileEntityConnector tile)
    {
        this.tile = tile;
        int idx;
        int x = 2;
        int y = 140;
        int dy = 18;

        for (int i = 0; i < 9; ++i)
        {
            idx = i;
            this.addSlotToContainer(new Slot(invPlayer, idx, x + i * 18, y + 3*dy + 5));
        }

        for (int i = 0; i < 3; ++i)
        {
        	for (int j = 0; j < 9; ++j)
        	{
        	    idx = 9+j+i*9;
        		this.addSlotToContainer(new Slot(invPlayer, idx, x + j * 18, y+i*dy));
        	}
        }

        this.addSlotToContainer(new slotCanInsertOnlyItem(stack -> stack.getItem() instanceof itemBlockRemoteController,tile, 36,  x + 170, y - 80));

    }

    public void SetWindowSize(int x, int y)
    {
        width = x;
        height = y;
        slideSlotPos();
    }
    protected void slideSlotPos()
    {
        for (Slot slot : inventorySlots)
        {
            int idx = slot.getSlotIndex();
            if (0 <= idx && idx < 9) {
                slot.xPos = (idx % 9) * 18 + width/2 - 81;
                slot.yPos = (idx / 9) * 18 + height/2 + 64;
            } else if (9 <= idx && idx < 9 * 4) {
                idx -= 9;
                slot.xPos = (idx % 9) * 18 + width/2 - 81;
                slot.yPos = (idx / 9) * 18 + height/2 + 4;
            }
        }
        Slot s = inventorySlots.get(36);
        s.xPos = width/2 + 90;
        s.yPos = 65;
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

//    private boolean isItemCanSetSlot(ItemStack is)
//    {
//    	Item item = is.getItem();
//    	if(item==null)return false;
//    	if(item instanceof itemFerrisCore || item instanceof itemFerrisBasket)return true;
//    	return false;
//    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < tile.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, tile.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, tile.getSizeInventory(), false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}