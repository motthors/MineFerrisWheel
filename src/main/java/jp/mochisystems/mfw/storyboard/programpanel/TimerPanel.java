package jp.mochisystems.mfw.storyboard.programpanel;

import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;

public class TimerPanel implements IProgramPanel {
	
	private static DataPack[] datapacks = {
			new DataPack(Type.inputValue, "Second"),
			new DataPack(Type.inputValue, "Tick"),
	};

	private final int id_Time = 0;
	private final int id_Tick = 1;

	private int tickTimeCount;
	private int tickTimeTarget = 20;

	public TimerPanel(){}
	public TimerPanel(int tick)
	{
		tickTimeTarget = tick;
	}

	@Override
	public boolean CanUseWith(FerrisSelfMover part)
	{
		return true;
	}

	@Override
	public Mode getMode() {
		return Mode.timer;
	}
	
	@Override
	public int ApiNum(){ return 2; }

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
		switch(apiIndex)
		{
		case id_Tick : return ""+tickTimeTarget;
		case id_Time : return String.format("%.2f",tickTimeTarget/20.0);
		}
		return "";
	}

	@Override
	public void addValue(int apiIndex, Number value) {
		switch(apiIndex)
		{
			case id_Tick :
			case id_Time :
				tickTimeTarget += value.intValue();
				break;
		}
		tickTimeTarget = Math.max(tickTimeTarget, 0);
	}
	@Override
	public void setValue(int apiIndex, Number value) {
		switch(apiIndex)
		{
			case id_Tick :
				tickTimeTarget = value.intValue();
				break;
			case id_Time :
				tickTimeTarget = (int)(value.doubleValue()*20);
				break;
		}
		tickTimeTarget = Math.max(tickTimeTarget, 0);
	}

	@Override
	public void setSelection(int apiIndex, int value)
	{

	}

	@Override
	public String[] GetSelectionLabels(int apiIndex)
	{
		return null;
	}

	@Override
	public void start()
	{
		tickTimeCount = 0;
		canDispose = false;
	}
	
	private boolean canDispose = false;
	@Override
	public boolean CanDoNext()
	{
		return false;
	}
	
	@Override
	public boolean run() {
		if(tickTimeCount >= tickTimeTarget)
		{
			canDispose = true;
			return true;
		}
		tickTimeCount ++ ;
		return canDispose;
	}

	@Override
	public void RSHandler(){}
	@Override
	public void NotifyHandler(){}
	
	@Override
	public String toString()
	{
		return "Tx"+tickTimeTarget+";";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Tick, Integer.parseInt(p[1]));
	}
	
	@Override
	public String displayDescription()
	{
		return "wait "+tickTimeTarget+" tick"
				+"("+(tickTimeTarget/20.f)+" second)";
	}
}
