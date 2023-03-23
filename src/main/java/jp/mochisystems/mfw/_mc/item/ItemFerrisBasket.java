//package jp.mochisystems.mfw._mc.item;
//
//import java.util.List;
//
//import net.minecraft.client.util.ITooltipFlag;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//import javax.annotation.Nullable;
//
//public class ItemFerrisBasket extends Item{
//
//	public ItemFerrisBasket()
//	{
//		super();
//		setContainerItem(this);
//	}
//
//	@Override
//    @SideOnly(Side.CLIENT)
//	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
//	{
//		if(stack.stackTagCompound == null)return;
//		tooltip.add("BasketName:"+stack.stackTagCompound.getString("ModelName"));
////     	list.add("name:"+itemStack.getDisplayName());
//		tooltip.add("BasketAuthor:"+stack.stackTagCompound.getString("author"));
//	}
//
//	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
//    {
////    	if (!world.isRemote)
////    	{
////    		Entity e = new EntityFerrisCollider(world);
////    		world.spawnEntityInWorld(e);
////    	}
////    	--itemStack.stackSize;
//    	return true;
//    }
//
//}
