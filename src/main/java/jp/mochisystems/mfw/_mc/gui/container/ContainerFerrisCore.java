package jp.mochisystems.mfw._mc.gui.container;

import jp.mochisystems.core._mc.gui.container.ContainerBlockModelerBase;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.mfw._mc.gui.slot.slotFerrisCore;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFerrisCore extends ContainerBlockModelerBase {
	
    private final FerrisPartBase ferrisPart;
    private int PageNum = 0;
	public int getPageNum()
	{
		return PageNum;
	}

	public ContainerFerrisCore(InventoryPlayer playerInventory, IModel part)
	{
		super(playerInventory);
		this.ferrisPart = (FerrisPartBase) part;//((FerrisPartBase) part).GetSelectedPartInGUI();
		setContainerForPage();
	}

	private void setContainerForPage()
	{
        // ferris part inventory
        int num = ferrisPart.getSizeInventory();
        for (int index = 0; index < num; ++index)
        {
            this.addSlotToContainer(new slotFerrisCore(ferrisPart, index, 0,0, playerInventory.player));
        }
	}

	public void changePage(int add)
	{
		PageNum += add;
		if(PageNum<0)PageNum=0;
		if(PageNum> ferrisPart.getSizeInventory()/(4*6))PageNum = ferrisPart.getSizeInventory()/25;
//		MFW_Logger.debugInfo("changepage  pagenum:"+PageNum);
		slideSlotPos();
	}

	@Override
	public void slideSlotPos()
	{
		super.slideSlotPos();
		int s = PageNum*4*6;
		int e = (PageNum+1)*4*6;
		int max = ferrisPart.getSizeInventory();
		for (Slot slot : inventorySlots)
		{
			int idx = slot.getSlotIndex();

			if(slot instanceof slotFerrisCore) {
				if (s <= idx && idx < e) {
//					idx -= 36;
					int horzNum = 4;
					slot.xPos = (idx % horzNum) * 18 + 5;
					slot.yPos = (idx / horzNum) * 26 + 30;
					//  MFW_Logger.debugInfo(idx + " : " + slot.xDisplayPosition + "."+slot.yDisplayPosition);
				} else {
					slot.xPos = -1000;
					slot.yPos = -1000;
				}
			}
		}
	}
	

    @Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
	{
		if(ferrisPart.IsLock() && slotId >= 36) return ItemStack.EMPTY;
        FerrisPartBase.ChangeUser = player;
	    return super.slotClick(slotId, dragType, clickTypeIn, player);
	}


    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
//        return this.blockModel.isUseableByPlayer(p_75145_1_);
    	return true;
    }

//    /**
//     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
//     */
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        return ferrisPart.getStackInSlot(index);
    }
    
}
    