package jp.mochisystems.mfw._mc._core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MFW_CreateCreativeTab extends CreativeTabs{
	
	public MFW_CreateCreativeTab(String label)
	{
		super(label);
	}
 
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem()
	{
		return new ItemStack(MFW.ItemFerrisCore);
	}
 
	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel()
	{
		return "MineFerrisWheel";
	}
}
