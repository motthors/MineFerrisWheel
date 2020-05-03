package mfw._mc.gui.container;

import java.util.List;

import mfw.ferriswheel.FerrisPartBase;
import mfw._mc.gui.slot.slotFerrisCore;
import mochisystems._mc.gui.ContainerBlockModelerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFerrisCore extends ContainerBlockModelerBase {
	
    private FerrisPartBase ferrisPart;
    private InventoryPlayer playerInventory;
    private int PageNum = 0;
	public int getPageNum()
	{
		return PageNum;
	}

	public ContainerFerrisCore(InventoryPlayer playerInventory, FerrisPartBase part)
	{
		super(playerInventory);
		this.ferrisPart = part;
        this.playerInventory = playerInventory;
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
		@SuppressWarnings("unchecked")
		List<Slot> list = (List<Slot>)inventorySlots;
		int s = PageNum*4*6;
		int e = (PageNum+1)*4*6;
		int max = ferrisPart.getSizeInventory();
		for (Slot slot : list)
		{
			int idx = slot.getSlotIndex();

			if(slot instanceof slotFerrisCore) {
				if (s <= idx && idx < e) {
//					idx -= 36;
					int horzNum = 4;
					slot.xDisplayPosition = (idx % horzNum) * 18 + 5;
					slot.yDisplayPosition = (idx / horzNum) * 26 + 22;
					//  MFW_Logger.debugInfo(idx + " : " + slot.xDisplayPosition + "."+slot.yDisplayPosition);
				} else {
					slot.xDisplayPosition = -1000;
					slot.yDisplayPosition = -1000;
				}
			}
		}
	}
	

    @Override
	public ItemStack slotClick(int p_75144_1_, int p_75144_2_, int p_75144_3_, EntityPlayer player)
	{
        FerrisPartBase.ChangeUser = player;
	    return super.slotClick(p_75144_1_, p_75144_2_, p_75144_3_, player);
	}


    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
//        return this.blockModel.isUseableByPlayer(p_75145_1_);
    	return true;
    }

//    /**
//     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
//     */
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        return null;
    }
    
}
    