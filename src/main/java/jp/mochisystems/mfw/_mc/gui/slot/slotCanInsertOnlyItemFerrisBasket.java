//package jp.mochisystems.mfw._mc.gui.slot;
//
//import jp.mochisystems.mfw._mc.item.ItemFerrisBasket;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.ItemStack;
//
//public class slotCanInsertOnlyItemFerrisBasket extends Slot{
//		public slotCanInsertOnlyItemFerrisBasket(IInventory iinventory, int idx, int x, int y) {
//			super(iinventory, idx, x, y);
//		}
//		public boolean isItemValid(ItemStack p_75214_1_)
//		{
//			return p_75214_1_ !=null && p_75214_1_.getItem() instanceof ItemFerrisBasket;
//		}
////		public int getSlotStackLimit(){return 10;}
//	}