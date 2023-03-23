package jp.mochisystems.mfw._mc.item;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.manager.EntityWearingModelManager;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import jp.mochisystems.mfw.ferriswheel.FerrisWheel;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemBlockFerrisCore extends ItemBlock implements IItemBlockModelHolder, ISpecialArmor {

	@Override
	public IModel GetBlockModel(IModelController controller)
	{
		return new FerrisWheel(controller);
	}



	@Nonnull
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		if(stack.getTagCompound()!=null && stack.getTagCompound().getCompoundTag("model")!=null){
			return _Core.I18n(this.getUnlocalizedNameInefficiently(stack) + ".name").trim()
			+ " : " + stack.getTagCompound().getCompoundTag("model").getString("ModelName");
		}
		return super.getItemStackDisplayName(stack);
	}
	
	public ItemBlockFerrisCore(Block block)
	{
		super(block);
		setContainerItem(this);
		setMaxStackSize(64);

		this.addPropertyOverride(new ResourceLocation("heading"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityIn)
			{
				if(entityIn == null) return 0;
				if(worldIn == null) return 0;
				return entityIn.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == stack ? 1f : 0;
			}
		});
	}

	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn)
	{
		// アイテムから直接GUI出すの、普通において編集したほうがよくね？
//		if(playerIn.isSneaking())
//		{
//			playerIn.openGui(MFW.INSTANCE, MFW.GUIID_FerrisCore, worldIn, 0, 0, 0);
//			return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
//		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if(!stack.hasTagCompound()) return EnumActionResult.FAIL;
		stack.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
//		super.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
		stack.shrink(1);

		return EnumActionResult.SUCCESS;
	}

//	@Override
//	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
//	{
////		ItemStack stack = player.getHeldItem(hand);
////		stack.grow(1);
//////		Logger.debugInfo("onItemUse : " + stack.getCount());
//////		(new Throwable()).printStackTrace(); //TODOちゃんとクリエイティブでも減っているのを確認する
////		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
//		return EnumActionResult.FAIL;
//	}





	///////////////////////////// For Armor

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
	{
		if(!stack.hasTagCompound()) return false;

//		Logger.debugInfo("isValidArmor");
		EntityWearingModelManager.OnWear(entity, EntityWearingModelManager.GetModel(entity, stack, armorType.getIndex()), armorType.getIndex());
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
	{
//		Logger.debugInfo("getArmorModel");
		return null;
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


	///////////// ISpecialArmor

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, int slot) {
		return new ArmorProperties(0, 1, 0);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, @Nonnull ItemStack armor, int slot) {
		return 0;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, @Nonnull ItemStack stack, DamageSource source, int damage, int slot) {

	}




	/////////////////////////// ItemBlock

//	protected final Block block;
//
//	public EnumActionResult ItemBlock_onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
//	{
//		IBlockState iblockstate = worldIn.getBlockState(pos);
//		Block block = iblockstate.getBlock();
//
//		if (!block.isReplaceable(worldIn, pos))
//		{
//			pos = pos.offset(facing);
//		}
//
//		ItemStack itemstack = player.getHeldItem(hand);
//
//		if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, player))
//		{
//			int i = this.getMetadata(itemstack.getMetadata());
//			IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);
//
//			if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
//			{
//				iblockstate1 = worldIn.getBlockState(pos);
//				SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
//				worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
//				itemstack.shrink(1);
//			}
//
//			return EnumActionResult.SUCCESS;
//		}
//		else
//		{
//			return EnumActionResult.FAIL;
//		}
//	}
//
//	public static boolean setTileEntityNBT(World worldIn, @Nullable EntityPlayer player, BlockPos pos, ItemStack stackIn)
//	{
//		MinecraftServer minecraftserver = worldIn.getMinecraftServer();
//
//		if (minecraftserver == null)
//		{
//			return false;
//		}
//		else
//		{
//			NBTTagCompound nbttagcompound = stackIn.getSubCompound("BlockEntityTag");
//
//			if (nbttagcompound != null)
//			{
//				TileEntity tileentity = worldIn.getTileEntity(pos);
//
//				if (tileentity != null)
//				{
//					if (!worldIn.isRemote && tileentity.onlyOpsCanSetNbt() && (player == null || !player.canUseCommandBlock()))
//					{
//						return false;
//					}
//
//					NBTTagCompound nbttagcompound1 = tileentity.writeToNBT(new NBTTagCompound());
//					NBTTagCompound nbttagcompound2 = nbttagcompound1.copy();
//					nbttagcompound1.merge(nbttagcompound);
//					nbttagcompound1.setInteger("x", pos.getX());
//					nbttagcompound1.setInteger("y", pos.getY());
//					nbttagcompound1.setInteger("z", pos.getZ());
//
//					if (!nbttagcompound1.equals(nbttagcompound2))
//					{
//						tileentity.readFromNBT(nbttagcompound1);
//						tileentity.markDirty();
//						return true;
//					}
//				}
//			}
//
//			return false;
//		}
//	}
//
//	@SideOnly(Side.CLIENT)
//	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
//	{
//		Block block = worldIn.getBlockState(pos).getBlock();
//
//		if (block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos))
//		{
//			side = EnumFacing.UP;
//		}
//		else if (!block.isReplaceable(worldIn, pos))
//		{
//			pos = pos.offset(side);
//		}
//
//		return worldIn.mayPlace(this.block, pos, false, side, player);
//	}
//
//	public String getUnlocalizedName(ItemStack stack)
//	{
//		return this.block.getUnlocalizedName();
//	}
//
//	public String getUnlocalizedName()
//	{
//		return this.block.getUnlocalizedName();
//	}
//
//	public CreativeTabs getCreativeTab()
//	{
//		return this.block.getCreativeTabToDisplayOn();
//	}
//
//	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
//	{
//		if (this.isInCreativeTab(tab))
//		{
//			this.block.getSubBlocks(tab, items);
//		}
//	}
//
//	@SideOnly(Side.CLIENT)
//	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
//	{
//		super.addInformation(stack, worldIn, tooltip, flagIn);
//		this.block.addInformation(stack, worldIn, tooltip, flagIn);
//	}
//
//	public Block getBlock()
//	{
//		return this.getBlockRaw() == null ? null : this.getBlockRaw().delegate.get();
//	}
//
//	private Block getBlockRaw()
//	{
//		return this.block;
//	}
//
//	/**
//	 * Called to actually place the block, after the location is determined
//	 * and all permission checks have been made.
//	 *
//	 * @param stack The item stack that was used to place the block. This can be changed inside the method.
//	 * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
//	 * @param side The side the player (or machine) right-clicked on.
//	 */
//	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
//	{
//		if (!world.setBlockState(pos, newState, 11)) return false;
//
//		IBlockState state = world.getBlockState(pos);
//		if (state.getBlock() == this.block)
//		{
//			setTileEntityNBT(world, player, pos, stack);
//			this.block.onBlockPlacedBy(world, pos, state, player, stack);
//
//			if (player instanceof EntityPlayerMP)
//				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
//		}
//
//		return true;
//	}
}
