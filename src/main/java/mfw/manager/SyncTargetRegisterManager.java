package mfw.manager;

import mfw.ferriswheel.FerrisPartAddress;

public class SyncTargetRegisterManager {

	public static SyncTargetRegisterManager INSTANCE = new SyncTargetRegisterManager();
	FerrisPartAddress targetParent;
	
	private SyncTargetRegisterManager(){}
	
	public void Save(FerrisPartAddress target)
	{
		targetParent = target;
	}
	
	public FerrisPartAddress GetSavedTarget()
	{
		return targetParent;
	}

}
