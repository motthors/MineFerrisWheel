package mfw._mc._1_7_10.gui.container;

import mfw._mc._1_7_10.gui.slot.slotFileManager;
import mfw._mc._1_7_10.item.itemBlockFerrisCore;
import mfw._mc._1_7_10.tileEntity.TileEntityFileManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFileManager extends Container
{
    private TileEntityFileManager tile;
    
    public ContainerFileManager(InventoryPlayer invPlayer, TileEntityFileManager tile)
    {
        this.tile = tile;
        this.addSlotToContainer(new slotFileManager(tile, 0, 148, 57));

        for (int i = 0; i < 3; ++i)
        {
        	for (int j = 0; j < 9; ++j)
        	{
        		this.addSlotToContainer(new Slot(invPlayer, 9+j+i*9, 8+j*18, 84+i*18));
        	}
        }
        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
        }
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return this.tile.isUseableByPlayer(p_75145_1_);
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
//    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
//    {
//    	ItemStack itemstack = null;
//    	//�N���b�N���ꂽ�X���b�g���擾
//        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);
//        if(slot == null)return null;
//        
//        if (slot.getHasStack()==false)return null;
//        
//        //�N���b�N���ꂽ�X���b�g��ItemStack���擾
//        ItemStack itemstack1 = slot.getStack();
//        //���������邽���r�������̂ŕύX�O��ItemStack�̏�Ԃ�ێ����Ă���
//        itemstack = itemstack1.copy();
//        if(!isItemCanSetSlot(itemstack1))return null;
//        
//        if (p_82846_2_ == 0) // �R���e�i�ɕۑ����ꂽ�X���b�g��Idx��0��GUI�X���b�g�@����ȊO�̓v���C���[�C���CorePosX���g��
//        {
//            if (!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true))
//            {
//                return null;
//            }
//            slot.onSlotChanged();
//        }
//        else
//        {
//        	if (!this.mergeItemStack(itemstack1, 0, 1, false))
//        	{
//        		return null;
//        	}
//        	slot.onSlotChanged();
//        }
//
//        if (itemstack1.stackSize == 0) //�V�t�g�N���b�N�ňړ���X���b�g�����Ȃ������ꍇ�͈ړ����X���b�g����ɂ���
//        {
//            slot.putStack((ItemStack)null);
//        }
//        else //�ړ���X���b�g����ꂽ�ꍇ�͐������ς���Č��X���b�g�ɃA�C�e�����c��̂ōX�V�ʒm
//        {
//            slot.onSlotChanged();
//        }
//    
//        //�V�t�g�N���b�N�O��Ő����ς��Ȃ��������ړ����s
//  		if (itemstack.stackSize == itemstack1.stackSize) {
//  			return null;
//  		}
//  		
//  		slot.onPickupFromSlot(p_82846_1_, itemstack1);
//  		
//        return itemstack;
//    }
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(!(itemstack1.getItem() instanceof itemBlockFerrisCore))return null;
            
            if (p_82846_2_ < 1)
            {
//                if (!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true))
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