package mfw._mc._1_7_10.gui.slot;

import mfw._mc._1_7_10.item.itemFerrisBasket;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class slotCanInsertOnlyItemFerrisBasket extends Slot{
		public slotCanInsertOnlyItemFerrisBasket(IInventory iinventory, int idx, int x, int y) {
			super(iinventory, idx, x, y);
		}
		public boolean isItemValid(ItemStack p_75214_1_)
		{
			return p_75214_1_ !=null && p_75214_1_.getItem() instanceof itemFerrisBasket;
		}
//		public int getSlotStackLimit(){return 10;}
	}