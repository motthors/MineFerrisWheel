package mfw.manager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mochisystems.blockcopier.ILimitLine;

@SideOnly(Side.CLIENT)
public class ChangingLimitLineManager {
	
	private ChangingLimitLineManager(){}
	
	public static ChangingLimitLineManager INSTANCE = new ChangingLimitLineManager();

	ILimitLine savetile;
	
	public void saveTile(ILimitLine tile)
	{
		savetile = tile;
	}
	
	public ILimitLine getSaveTile()
	{
		return savetile;
	}
	
	public void reset()
	{
		savetile = null;
	}
}
