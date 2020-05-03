package mfw.storyboard.programpanel;

import java.util.List;

import mfw.ferriswheel.FerrisPartBase;
import net.minecraft.util.MathHelper;

public class NotifyPanel implements IProgramPanel {
	
	private static String[] targets = {
			"All",
			"Parent",
			"Children",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Target"),
	};
	private FerrisPartBase part;

	private final int id_Target = 0;

	private int index = 0;

	@Override
	public boolean CanUseWith(FerrisPartBase part)
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
	public int[] Clicked(int apiIndex)
	{
		index = (index + 1) % targets.length;
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		return targets[index];
	}
	
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			index = Integer.parseInt((String) value);
			return new int[]{apiIndex};
		}catch(NumberFormatException e){
			return new int[]{};
		}
	}

	@Override
	public void setSelection(int apiIndex, int value)
	{
		if(datapacks[apiIndex].type != Type.change) return;
		switch(apiIndex) {
			case id_Target:
				index = value;
				index = MathHelper.clamp_int(index, 0, targets.length - 1);
				break;
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
				part.storyboardManager.OnNotify();
			}
			break;
		case 1 : // loopHead
            FerrisPartBase parent = part.GetParent();
			if(parent!=null)parent.storyboardManager.OnNotify();
			break;
		case 2 : // child
			for(FerrisPartBase child : part.GetChildren()){
				if(child==null)continue;
                child.storyboardManager.OnNotify();
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
		setValue(id_Target, p[1]);
	}
	
	@Override
	public String displayDescription()
	{
		return "notify to "+targets[index];
	}
}
