package mfw._mc.gui.container;

import mfw._mc.gui.slot.slotCanInsertOnlyItemFerrisRemoteController;
import mfw._mc.tileEntity.TileEntityConnector;
import mochisystems._mc.block.itemBlockRemoteController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerConnector extends Container
{
    private TileEntityConnector tile;

    public ContainerConnector(InventoryPlayer invPlayer, TileEntityConnector tile)
    {
        this.tile = tile;
        int idx;
        int x = 130;
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

        this.addSlotToContainer(new slotCanInsertOnlyItemFerrisRemoteController(tile, 36,  x + 162, y - 50));

    }

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
    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(!(itemstack1.getItem() instanceof itemBlockRemoteController))return null;
            
            if (p_82846_2_ < 1)
            {
                if (!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 1, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
            slot.onPickupFromSlot(p_82846_1_, itemstack1);
        }
        return itemstack;
    }
}