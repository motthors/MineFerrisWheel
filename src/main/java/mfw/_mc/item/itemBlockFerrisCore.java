package mfw._mc.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw.ferriswheel.*;
import mochisystems._core.Logger;
import mochisystems._core._Core;
import mochisystems.manager.EntityWearingModelManager;
import mochisystems.util.EntityModelWrapper;
import mochisystems.util.IModel;
import mochisystems.blockcopier.IItemBlockModelHolder;
import mochisystems.util.IModelController;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class itemBlockFerrisCore extends ItemBlock implements IItemBlockModelHolder {

	@Override
	public IModel GetBlockModel(IModelController controller)
	{
		return new FerrisWheel(controller);
   }

	@Override
	public void OnSetInventory(IModel part, int slotidx, ItemStack itemStack, EntityPlayer player){}


//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Override
//    @SideOnly(Side.CLIENT)
//    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advanced)
//	{
//		if(itemStack.stackTagCompound == null)return;
//        list.add("name : "+itemStack.stackTagCompound.getString("ModelName"));
//        list.add("author : "+itemStack.stackTagCompound.getString("author"));
//    }
	
	public itemBlockFerrisCore(Block block)
	{
		super(block);
		setContainerItem(this);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if(!stack.hasTagCompound()) return true;
		ItemBlock itemblock = (ItemBlock)stack.getItem();
		if (!itemblock.func_150936_a(world, x, y, z, side, player, stack)) return false;

		if(!world.getBlock(x, y, z).onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ))
		{
			stack.stackSize--; // to consume itemstack if in creative mode
			Logger.debugInfo("onItemUseFirst decr : " + stack.stackSize);
		}
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		stack.stackSize++;
		Logger.debugInfo("onItemUse : " + stack.stackSize);
		return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack)
	{
		ItemStack stack = super.getContainerItem(itemStack);
		if(_Core.proxy.checkSide()==Side.CLIENT)
		{

		}
		return stack;
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
	{
		Logger.debugInfo("isValidArmor");
		EntityWearingModelManager.OnWear(entity, EntityWearingModelManager.GetModel(entity, stack), armorType);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
	{
		Logger.debugInfo("getArmorModel");
		return null;
	}

	@SubscribeEvent
	public void onArmorRender(RenderPlayerEvent.SetArmorModel event)
	{
		if ( event.stack == null || !( event.stack.getItem() instanceof itemBlockFerrisCore ) )
		{
			return;
		}
		event.result = 1;
//		itemBlockFerrisCore item = (itemBlockFerrisCore) event.stack.getItem();

		mochisystems.util.ModelBiped model = EntityWearingModelManager.GetModelBiped(event.entityLiving, event.stack, event.slot);
		model.isSneak = event.entityLiving.isSneaking();
		if ( event.entityLiving instanceof EntityPlayer )
		{
			ItemStack held = event.entityPlayer.inventory.getCurrentItem();
			model.heldItemRight = ( held != null ) ? 0 : 1;
			if ( held != null )
			{
				model.aimedBow = event.entityPlayer.getItemInUseCount() > 0 && held.getItemUseAction().equals( EnumAction.bow );
			}
		}
		event.renderer.setRenderPassModel( model );
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
	{

	}

	@Override
	protected void finalize() throws Throwable{
		try{

		} finally {
			super.finalize();
		}
	}
}
