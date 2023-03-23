package jp.mochisystems.mfw._mc.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class blockFerrisSupporter extends Block{

	public blockFerrisSupporter()
	{
		super(Material.GROUND);
		this.setHardness(1.0f);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
//		this.setBlockBounds(0.1875F, 0.1875F, 0.1875F, 0.8125F, 0.8125F, 0.8125F);
		this.setLightLevel(0.0F);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

}
