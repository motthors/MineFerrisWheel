package jp.mochisystems.mfw.storyboard.programpanel;

import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;

public class LoopPanel implements IProgramPanel {
	
	private static DataPack[] datapacks = {
			new DataPack(Type.inputValue, "LoopNum"),
	};

	private final int id_loopNum = 0;

	private int targetCount = 1;
	private int currentCount;
	private LoopEndPanel endPanel;

	public LoopPanel()
	{
		endPanel = new LoopEndPanel(this);
	}
	public LoopPanel(int count)
	{
		super();
		targetCount = count;
	}
	public void Reconnect(LoopEndPanel end)
	{
		endPanel = end;
		end.loopHead = this;
	}

	@Override
	public boolean CanUseWith(FerrisSelfMover part)
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
	public String getValueString(int apiIndex){
		if (apiIndex == id_loopNum) {
			return "" + targetCount;
		}
		return "";
	}

	@Override
	public void addValue(int apiIndex, Number value) {
		if(apiIndex == id_loopNum) {
			targetCount = Math.max(targetCount + value.intValue(), 1);
		}

	}
	@Override
	public void setValue(int apiIndex, Number value) {
		int temp;
		if (apiIndex == id_loopNum) {
			temp = value.intValue();
			targetCount = Math.max(temp, 1);
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
		currentCount = 0;
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
		return "Lx"+ targetCount +";";
	}
	
	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_loopNum, Integer.parseInt(p[1]));
	}


	@Override
	public String displayDescription()
	{
		return "Loop "+ targetCount +" time(s)";
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
		public boolean CanUseWith(FerrisSelfMover part) {
		    return true;
		}

		@Override
		public String getValueString(int apiIndex) {
			return null;
		}

		@Override
		public void addValue(int apiIndex, Number value) {
		}

		@Override
		public void setValue(int apiIndex, Number value) {
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
			return (++loopHead.currentCount >= loopHead.targetCount);
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
