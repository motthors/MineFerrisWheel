//package jp.mochisystems.mfw._mc.block;
//
//import jp.mochisystems.mfw._mc.tileEntity.TileEntityChunkLoader;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockContainer;
//import net.minecraft.block.material.Material;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//public class blockChunkLoader extends BlockContainer {
//
//	public blockChunkLoader()
//	{
//		super(Material.GROUND);
//	}
//
//	public TileEntity createNewTileEntity(World world, int meta)
//	{
//		return new TileEntityChunkLoader();
//	}
//
//	@Override
//	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
//	{
//		((TileEntityChunkLoader)worldIn.getTileEntity(pos)).stopChunkLoading();
//		super.breakBlock(worldIn, pos, state);
//	}
//
//
//}