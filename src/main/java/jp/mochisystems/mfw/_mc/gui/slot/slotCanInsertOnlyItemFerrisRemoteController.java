//package jp.mochisystems.mfw._mc.gui.slot;
//
//import net.minecraft.inventory.IInventory;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.ItemStack;
//
//public class slotCanInsertOnlyItemFerrisRemoteController extends Slot{
//	public slotCanInsertOnlyItemFerrisRemoteController(IInventory inv, int idx, int x, int y) {
//		super(inv, idx, x, y);
//	}
//	public boolean isItemValid(ItemStack is)
//	{
//		if(is==null)return false;
//		return(is.getItem() instanceof itemBlockRemoteController);
//	}
//
//	public int getSlotStackLimit()
//	{
//		return 1;
//	}
//
//	public ItemStack decrStackSize(int p_75209_1_)
//    {
////    	if(!(inventory instanceof TileEntityFileManager))return null;
////    	TileEntityFileManager tile = (TileEntityFileManager)inventory;
////		tile.setFlag(0);
//        return super.decrStackSize(p_75209_1_);
//    }
//}
//
