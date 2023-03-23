package jp.mochisystems.mfw.storyboard.programpanel;

import jp.mochisystems.core.math.Math;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;

public class WaitPanel implements IProgramPanel {
	
	private static String[] modes = {
			"RsInput",
			"Notify",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Mode"),
	};

	private final int id_Mode = 0;
	
	private int modeIndex = 0;

	private boolean canDispose = false;

	public WaitPanel(){}

	public WaitPanel(int modeIdx)
	{
		modeIndex = modeIdx;
	}

	@Override
	public boolean CanUseWith(FerrisSelfMover part)
	{
		return true;
	}

	@Override
	public Mode getMode() {
		return Mode.wait;
	}
	
	@Override
	public int ApiNum(){ return 1; }

	@Override
	public Type getType(int apiIndex){
		return datapacks[apiIndex].type;
	}
	
	@Override
	public String getDescription(int apiIndex) {
		return datapacks[apiIndex].description;
	}
	

	@Override
	public String getValueString(int apiIndex){
		return modes[modeIndex];
	}

	@Override
	public void addValue(int apiIndex, Number value) {
	}
	@Override
	public void setValue(int apiIndex, Number value) {
		modeIndex = value.intValue();
	}

	@Override
	public void setSelection(int apiIndex, int value)
	{
		if(datapacks[apiIndex].type != Type.change) return;
		if (apiIndex == id_Mode) {
			modeIndex = value;
			modeIndex = Math.Clamp(modeIndex, 0, modes.length - 1);
		}
	}

	@Override
	public String[] GetSelectionLabels(int apiIndex)
	{
		switch(apiIndex)
		{
			case id_Mode : return modes;
			default: return null;
		}
	}

	@Override
	public void start()
	{
		canDispose = false;
	}
	
	
	@Override
	public boolean CanDoNext()
	{
		return canDispose;
	}
	
	@Override
	public boolean run() {
		return canDispose;
	}

	@Override
	public void RSHandler(){if(modeIndex ==0)canDispose=true;}
	@Override
	public void NotifyHandler(){if(modeIndex ==1)canDispose=true;}
	
	@Override
	public String toString()
	{
		return "Wx"+ modeIndex +";";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setSelection(id_Mode, Integer.parseInt(p[1]));
	}
	
	@Override
	public String displayDescription()
	{
		return "wait "+modes[modeIndex];
	}
}
