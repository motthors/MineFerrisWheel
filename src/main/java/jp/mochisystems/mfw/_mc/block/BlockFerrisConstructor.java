package jp.mochisystems.mfw._mc.block;

import jp.mochisystems.core._mc.block.BlockRotatedScanner;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFerrisConstructor extends BlockRotatedScanner {

	public BlockFerrisConstructor()
	{
		super();
	}

	@Override
	protected int GetGuiId()
	{
		return MFW.GUIID_FerrisConstructor;
	}

	@Override
	protected Object GetModCore()
	{
		return MFW.INSTANCE;
	}

	@Override
	protected TileEntity CreateTileEntityScanner()
	{
		return new TileEntityFerrisConstructor();
	}


	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		EnumFacing direction = world.getBlockState(pos).getValue(FACING);
		TileEntityFerrisConstructor tile = (TileEntityFerrisConstructor) world.getTileEntity(pos);
		tile.Init(direction);
	}




	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}
}
