//package jp.mochisystems.mfw._mc.gui.slot;
//
//import jp.mochisystems.core._mc.tileentity.TileEntityFileManager;
//import jp.mochisystems.mfw._mc.item.ItemBlockFerrisCore;
//import jp.mochisystems.mfw._mc.item.ItemFerrisBasket;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.ItemStack;
//
//public class slotFileManager extends Slot{
//	public slotFileManager(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
//		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
//	}
//	public boolean isItemValid(ItemStack is)
//	{
//		if(is==null)return false;
//		if(is.getItem() instanceof ItemBlockFerrisCore || is.getItem() instanceof ItemFerrisBasket)return true;
//		return false;
//	}
//
//	public int getSlotStackLimit()
//	{
//		return 64;
//	}
//
//	public ItemStack decrStackSize(int p_75209_1_)
//    {
//    	if(!(inventory instanceof TileEntityFileManager))return null;
//    	TileEntityFileManager tile = (TileEntityFileManager)inventory;
//		tile.setFlag(0);
//        return super.decrStackSize(p_75209_1_);
//    }
//}
//
