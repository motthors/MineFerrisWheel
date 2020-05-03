package mfw.storyboard.programpanel;

import java.util.List;

import mfw.ferriswheel.FerrisPartBase;
import net.minecraft.util.MathHelper;

public class LoopPanel implements IProgramPanel {
	
	private static DataPack[] datapacks = {
			new DataPack(Type.inputValue, "LoopNum"),
	};

	private final int id_loopNum = 0;
	private final int id_ChildrenDataSource = 1;
	
	private int LoopNum = 1;
	private int LoopCount;
	private LoopEndPanel endPanel;

	public LoopPanel()
	{
		endPanel = new LoopEndPanel(this);
	}

	public void Reconnect(LoopEndPanel end)
	{
		endPanel = end;
		end.loopHead = this;
	}

	@Override
	public boolean CanUseWith(FerrisPartBase part)
	{
		return true;
	}


	@Override
	public Mode getMode() {
		return Mode.loop;
	}
	
	@Override
	public int ApiNum(){ return 1; }
	
//	@Override
//	public void insertSubPanelToList(List<IProgramPanel> inout_panel)
//	{
//		inout_panel.addAll(getPanelList());
//		inout_panel.add(new LoopEndPanel(this));
//	}
	
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
		case id_loopNum : return ""+LoopNum;
		}
		return "";
	}
	
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			int temp;
			switch(apiIndex)
			{
			case id_loopNum : 
				temp = (Integer.parseInt((String)value)); 
				LoopNum = (temp<1) ? 1 : temp;
				break;
//			case id_ChildrenDataSource : createFromSerialCode((String)value); break;
			}
			return new int[]{apiIndex};
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
		LoopCount = 0;
	}
	
	@Override
	public boolean CanDoNext()
	{
		return true;
	}
	
	@Override
	public boolean run() 
	{
		return true;
	}		
	
	public LoopEndPanel GetLoopEndPanel()
	{
		return endPanel;
	}

	@Override
	public void RSHandler(){}
	@Override
	public void NotifyHandler(){}
	
	@Override
	public String toString()
	{
		return "Lx"+LoopNum+";";
	}
	
	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_loopNum, p[1]);
		int St = source.indexOf("[");
		int Ed = source.indexOf("]");
		String childrenSource = source.substring(St+1, Ed);
		setValue(id_ChildrenDataSource, childrenSource);
	}


	@Override
	public String displayDescription()
	{
		return "Loop "+LoopNum+" time(s)";
	}
	
	public static class LoopEndPanel implements IProgramPanel{

		public LoopPanel loopHead;
		public LoopEndPanel(LoopPanel loopHead)
		{
			this.loopHead = loopHead;
		}
		
		@Override
		public int ApiNum() {
			return 0;
		}

		@Override
		public Mode getMode() {
			return Mode.loopend;
		}

		@Override
		public Type getType(int apiIndex) {
			return null;
		}

		@Override
		public String getDescription(int apiIndex) {
			return null;
		}

		@Override
		public boolean CanUseWith(FerrisPartBase part) {
		    return true;
		}

		@Override
		public int[] Clicked(int apiIndex) {
			return null;
		}

		@Override
		public String getValue(int apiIndex) {
			return null;
		}

		@Override
		public int[] setValue(int apiIndex, Object value) {
			return new int[]{};
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
		public void start() {
		}

		@Override
		public boolean CanDoNext() {
			return true;
		}

		@Override
		public boolean run()
		{
			return (++loopHead.LoopCount >= loopHead.LoopNum);
		}
		
		@Override
		public void RSHandler(){}
		@Override
		public void NotifyHandler(){}
		
		@Override
		public String toString()
		{
			return "l;";
		}
		
		@Override
		public void fromString(String source)
		{
		}
		
		@Override
		public String displayDescription()
		{
			return "";
		}
	}
}
