package mfw.storyboard.programpanel;

import java.util.List;

import mfw.ferriswheel.FerrisPartBase;

public class TimerPanel implements IProgramPanel {
	
	private static DataPack[] datapacks = {
			new DataPack(Type.inputValue, "Second"),
			new DataPack(Type.inputValue, "Tick"),
	};

	private final int id_Time = 0;
	private final int id_Tick = 1;

	private int tickTimeCount;
	private int tickTimeTarget = 20;

	@Override
	public boolean CanUseWith(FerrisPartBase part)
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
	public int[] Clicked(int apiIndex)
	{
		return new int[]{};
	}
	
	@Override
	public String getValue(int apiIndex){
		switch(apiIndex)
		{
		case id_Tick : return ""+tickTimeTarget;
		case id_Time : return String.format("%.2f",tickTimeTarget/20.0);
		}
		return "";
	}
	
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			int[] ret = new int[]{};
			double v = (Double.parseDouble((String)value));
			switch(apiIndex)
			{
			case id_Tick : 
				tickTimeTarget = (int)v;
				ret = new int[]{id_Tick, id_Time};
				break;
			case id_Time : 
				tickTimeTarget = (int)(v*20);
				ret = new int[]{id_Time, id_Tick};
				break;
			}
			tickTimeTarget = (tickTimeTarget < 0) ? 0 : tickTimeTarget;
			return ret;
		}catch(NumberFormatException e){
			
			return new int[]{};
		}
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
		setValue(id_Tick, p[1]);
	}
	
	@Override
	public String displayDescription()
	{
		return "wait "+tickTimeTarget+" tick"
				+"("+(tickTimeTarget/20.f)+" second)";
	}
}
