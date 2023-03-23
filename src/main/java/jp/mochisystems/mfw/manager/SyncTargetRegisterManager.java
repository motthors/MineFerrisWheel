package jp.mochisystems.mfw.manager;

import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;

public class SyncTargetRegisterManager {

	public static SyncTargetRegisterManager INSTANCE = new SyncTargetRegisterManager();
	FerrisPartBase target;
	CommonAddress address;
	
	private SyncTargetRegisterManager(){}
	
	public void Save(FerrisPartBase target)
	{
		this.target = target;
		this.address = target.GetCommonAddress();
	}
	
	public FerrisPartBase GetSavedTarget()
	{
		return target;
	}
	public CommonAddress GetAddress()
	{
		return address;
	}

}
