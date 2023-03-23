package jp.mochisystems.mfw.storyboard.programpanel;

import jp.mochisystems.core.math.Math;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;

public class NotifyPanel implements IProgramPanel {
	
	private static String[] targets = {
			"All",
			"Parent",
			"Children",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Target"),
	};
	private FerrisSelfMover part;

	private final int id_Target = 0;

	private int index = 0;

	@Override
	public boolean CanUseWith(FerrisSelfMover part)
	{
		this.part = part;
		return true;
	}

	@Override
	public Mode getMode() {
		return Mode.notify;
	}
	
	@Override
	public int ApiNum(){ return 1; }
	
//	@Override
//	public void insertSubPanelToList(List<IProgramPanel> inout_panel)
//	{}
	
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
		return targets[index];
	}

	@Override
	public void addValue(int apiIndex, Number value) {
	}
	@Override
	public void setValue(int apiIndex, Number value) {
		index = value.intValue();
	}

	@Override
	public void setSelection(int apiIndex, int value)
	{
		if(datapacks[apiIndex].type != Type.change) return;
		if (apiIndex == id_Target) {
			index = value;
			index = Math.Clamp(index, 0, targets.length - 1);
		}
	}

	@Override
	public String[] GetSelectionLabels(int apiIndex)
	{
		switch(apiIndex)
		{
			case id_Target: return targets;
			default: return null;
		}
	}

	@Override
	public void start()
	{
	}
	
	@Override
	public boolean CanDoNext()
	{
		return false;
	}
	
	@Override
	public boolean run() {
		switch(index){
		case 0 : // all
			for(FerrisPartBase part : part.BreadthFirstPartTreeArray){
				if(part instanceof FerrisSelfMover)
					((FerrisSelfMover)part).storyboardManager.OnNotify();
			}
			break;
		case 1 : // loopHead
            FerrisPartBase parent = part.GetParent();
			if(parent!=null)
				if(parent instanceof FerrisSelfMover)
					((FerrisSelfMover)parent).storyboardManager.OnNotify();
			break;
		case 2 : // child
			for(FerrisPartBase child : part.GetChildren()){
				if(child==null)continue;
				((FerrisSelfMover)child).storyboardManager.OnNotify();
			}
			break;
		}
		return true;
	}

	@Override
	public void RSHandler(){}
	@Override
	public void NotifyHandler(){}
	
	@Override
	public String toString()
	{
		return "Nx"+index+";";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setSelection(id_Target, Integer.parseInt(p[1]));
	}
	
	@Override
	public String displayDescription()
	{
		return "notify to "+targets[index];
	}
}
