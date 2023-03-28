package jp.mochisystems.mfw._mc.block;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw._mc.message.MFW_PacketHandler;
import jp.mochisystems.mfw._mc.message.MessageSyncRSPowerStC;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisCore;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Random;

public abstract class blockFerrisCore extends BlockContainer {

	protected abstract Item GetItemClass();

	public static class Wheel extends blockFerrisCore{
		@Override
		public TileEntity createNewTileEntity(World world, int meta)
		{
			return new TileEntityFerrisCore.Wheel();
		}
		@Override
		protected Item GetItemClass()
		{
			return MFW.ItemFerrisCore;
		}
	}

	public static class Elevator extends blockFerrisCore{
        @Override
        public TileEntity createNewTileEntity(World world, int meta)
        {
            return new TileEntityFerrisCore.Elevator();
        }
		@Override
		protected Item GetItemClass()
		{
			return MFW.ItemFerrisElevator;
		}

		@Override
		public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
		{
			if(!(stack.getItem() instanceof IItemBlockModelHolder)) return;
			if(!stack.hasTagCompound())return;
			NBTTagCompound nbt = stack.getTagCompound();
			TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(pos);
//			EnumFacing direction = world.getBlockState(pos).getValue(FACING);
			EnumFacing direction = placer.getHorizontalFacing().getOpposite();
//			EnumFacing direction = EnumFacing.SOUTH;
			tile.SetSide(direction);
			tile.blockModel.Reset();
			tile.blockModel.readFromNBT(nbt);
			tile.blockModel.setRSPower(world.isBlockIndirectlyGettingPowered(pos));
		}
	}

	public static class Garland extends blockFerrisCore{
		@Override
		public TileEntity createNewTileEntity(World world, int meta)
		{
			return new TileEntityFerrisCore.Garland();
		}
		@Override
		protected Item GetItemClass()
		{
			return MFW.ItemFerrisGarland;
		}
	}

	public static class GarlandEnd extends Garland{
		@Override
		public TileEntity createNewTileEntity(World world, int meta)
		{
			return new TileEntityFerrisCore.Garland();
		}
		@Override
		protected Item GetItemClass()
		{
			return MFW.ItemFerrisGarlandEnd;
		}
	}




	public static final PropertyDirection FACING = BlockDirectional.FACING;

	public blockFerrisCore()
	{
		super(Material.GROUND);
		this.setHardness(0.4f);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
		this.setLightLevel(0.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(!(stack.getItem() instanceof IItemBlockModelHolder)) return;
		if(!stack.hasTagCompound())return;
		NBTTagCompound nbt = stack.getTagCompound();
		EnumFacing direction = world.getBlockState(pos).getValue(FACING);
//		world.setBlockState(pos, state.withProperty(FACING, direction), 2);
		TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(pos);
		tile.SetSide(direction);
//		tile.blockModel.Reset();
		nbt.getCompoundTag("model").removeTag("tiltvx"); // ちょっとキモイけど、置き直しでRootの向きを変えるため
		tile.blockModel.readFromNBT(nbt);
		tile.blockModel.setRSPower(world.isBlockIndirectlyGettingPowered(pos));
	}


	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(pos);
//		if(player.isSneaking()){
//			tile.blockModel.ResetSelectedPartIngGUI();
//		}

		if(world.isRemote){
			GUIHandler.OpenBlockModelGuiInClient(tile.blockModel);
		}

        return true;
    }

	// 独自にドロップさせるので、デフォのアイテムは落とさない
	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Items.AIR;
	}


	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
		ItemStack itemStack = new ItemStack(GetItemClass());
		NBTTagCompound nbt = new NBTTagCompound();
		TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(pos);
		tile.blockModel.writeToNBT(nbt);
		itemStack.setTagCompound(nbt);
		super.breakBlock(world, pos, state);
		InventoryHelper.spawnItemStack(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, itemStack);
    }


    @Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(pos);
        if(tile!=null && tile.blockModel.IsLock())return false;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }


	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if (!world.isRemote)
        {
			int indirect = world.isBlockIndirectlyGettingPowered(pos);
			TileEntityFerrisCore tile = (TileEntityFerrisCore)world.getTileEntity(pos);
			if(tile==null) return;
			tile.blockModel.setRSPower(indirect);
			MessageSyncRSPowerStC packet = new MessageSyncRSPowerStC(pos.getX(), pos.getY(), pos.getZ(), indirect);
			MFW_PacketHandler.INSTANCE.sendToAll(packet);
        }
    }

	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
    	if(!player.capabilities.isCreativeMode)return null;
    	
		ItemStack stack = super.getPickBlock(state, target, world, pos, player);
    	TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(pos);
    	NBTTagCompound nbt = new NBTTagCompound();
    	tile.blockModel.writeToNBT(nbt);
    	stack.setTagCompound(nbt);
    	return stack;
    }

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getIndex();
	}
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
				.withProperty(FACING, facing);
	}
}
