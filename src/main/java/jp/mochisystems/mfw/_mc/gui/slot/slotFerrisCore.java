package jp.mochisystems.mfw._mc.gui.slot;

import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.mfw._mc._core.MFW_Logger;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/*
 * FerrisCore用
 */
public class slotFerrisCore extends Slot{
	
    public slotFerrisCore(IInventory iinventory, int index, int x, int y, EntityPlayer player)
    {
        super(iinventory, index, x, y);
    }
    
    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
    	if(itemStack==null)return false;
    	if(itemStack.getItem() instanceof IItemBlockModelHolder)
		{
    		return itemStack.hasTagCompound();
		}
    	return false;
    }

    @Override
    public int getSlotStackLimit()
    {
    	return 1;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
    public ItemStack decrStackSize(int decrCount)
    {
        if(decrCount != 1) MFW_Logger.error("slotFerrisCore.decrStackSize");

        FerrisPartBase part = (FerrisPartBase)inventory;
        return  part.takeChildPart(this.getSlotIndex());
    }

}