package jp.mochisystems.mfw.storyboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;
import jp.mochisystems.mfw.storyboard.programpanel.*;

public class StoryBoardManager {

	protected FerrisSelfMover part;
	protected String savedSerialCode;


	private int currentTargetIndex;
	public int getCurrentTargetIndex()
	{
		return currentTargetIndex;
	}
	protected ArrayList<IProgramPanel> PanelList = new ArrayList<>();
//	protected Iterator<IProgramPanel> nowTargetPanelItr;
	protected LinkedList<IProgramPanel> asyncRunningPanelList = new LinkedList<>();
	protected IProgramPanel currentTargetPanel;
	
	public StoryBoardManager(FerrisSelfMover part)
	{
		Init(part);
	}
	
	public void Init(FerrisSelfMover part)
	{
		this.part = part;
		savedSerialCode = "";
		clear();
	}

	public void clear()
	{
//		isInLoop = false;
		currentTargetPanel = null;
		PanelList.clear();
		asyncRunningPanelList.clear();
	}

//	private boolean isInLoop = false;
//	private LoopPanel looppanel = null;
	public void addPanel(IProgramPanel panel)
	{
//		if(isInLoop){
//			if(panel.getMode()==IProgramPanel.Mode.loopend){
//				isInLoop = false;
//				return;
//			}
//			looppanel.addPanel(panel);
//			return;
//		}
		PanelList.add(panel);
//		if(panel.getMode()==IProgramPanel.Mode.loop){
//			isInLoop = true;
//			looppanel = (LoopPanel) panel;
//		}
	}

	
	public ArrayList<IProgramPanel> getPanelList()
	{
//		ArrayList<IProgramPanel> result = new ArrayList<>();
//		for(IProgramPanel panel : PanelList)
//		{
//			result.add(panel);
//			panel.insertSubPanelToList(result);
//		}
//		return result;
		return PanelList;
	}

	
	public void OnRSEnable()
	{
		if(currentTargetPanel == null) return;
		currentTargetPanel.RSHandler();
		for(IProgramPanel panel : asyncRunningPanelList)panel.RSHandler();
	}
	
	public void OnNotify()
	{
		if(currentTargetPanel == null) return;
		currentTargetPanel.NotifyHandler();
		for(IProgramPanel panel : asyncRunningPanelList)panel.NotifyHandler();
	}
	
	public void Start()
	{
		if(currentTargetPanel == null)
		{
//			nowTargetPanelItr = PanelList.iterator();
			currentTargetIndex = -1;
			SetNextPanel();
		}
	}

	private void SetNextPanel()
	{
		boolean doAsync = false;
		do
		{
			if(currentTargetIndex < PanelList.size())
			{
				currentTargetPanel = PanelList.get(currentTargetIndex++);
				if(currentTargetPanel.getMode()== IProgramPanel.Mode.loopend)
				{
					boolean isBreak = currentTargetPanel.run();
					if(isBreak)
					{
						SetNextPanel();
					}
					else
					{
						currentTargetIndex = PanelList.indexOf(((LoopPanel.LoopEndPanel)currentTargetPanel).loopHead);
						currentTargetPanel = PanelList.get(currentTargetIndex++);
					}
					return;
				}
//				Logger.debugInfo("next:" + currentTargetIndex + " : " + currentTargetPanel.toString());
				currentTargetPanel.start();
				doAsync = currentTargetPanel.CanDoNext();
				if(doAsync) asyncRunningPanelList.add(currentTargetPanel);
			}
			else
			{
//				if(PanelList.size() > 0)Logger.debugInfo("end : "+currentTargetIndex);
				currentTargetIndex = 0;
				currentTargetPanel = null;
				return;
			}
		}while(doAsync);
	}

	public void Update()
	{
		if(currentTargetPanel==null)
		{
			SetNextPanel();
			if(currentTargetPanel==null) return;
		}
		boolean isFinished = currentTargetPanel.run();
		if(isFinished)
		{
			SetNextPanel();
		}
		Iterator<IProgramPanel> itr = asyncRunningPanelList.iterator();
		while(itr.hasNext())
		{
			isFinished = itr.next().run();
			if(isFinished) itr.remove();
		}
	}

	public void stop()
	{
		currentTargetIndex = 0;
		currentTargetPanel = null;
		asyncRunningPanelList.clear();
	}
	
	public String ToSerialCode()
	{
		String serial = "";
		for(IProgramPanel panel : PanelList)
		{
			serial += panel.toString();
		}
		return serial;
	}
	
	public boolean createFromSerialCode(String source)
	{
		currentTargetIndex = 0;
		currentTargetPanel = null;
		ArrayList<IProgramPanel> keep = new ArrayList<>(PanelList);
		Stack<LoopPanel> loopStack = new Stack<>();
		try{
			if(savedSerialCode.equals(source))return false;
			this.clear();
			if(source.equals(""))return true;
//			Logger.debugInfo("recieve serial : " + part.GetName() + source);
			source.replace("\r\n", "");
			source.replace("\n", "");
			source.replace(" ", "");
			int start = 0;
			
			while(true)
			{
				if("".equals(source))break;
				char id = source.charAt(0);
				int end = source.indexOf(";");

				if(end < 0)return false;
				String sub = source.substring(start, end);
				source = source.substring(end+1);
				
				//decode
				{
					IProgramPanel panel = createPanel_forSerial(id);
					if(!panel.CanUseWith(this.part)) continue;
					panel.fromString(sub);
					if(id == 'L') loopStack.push((LoopPanel)panel);
					if(id == 'l')
					{
						LoopPanel loop = loopStack.pop();
						((LoopPanel.LoopEndPanel)panel).loopHead = loop;
					}
					PanelList.add(panel); //MFW_Logger.debugInfo("add "+panel);
				}
			}

			savedSerialCode = ToSerialCode();
		}catch(Exception e){
			PanelList = keep;
			return false;
		}
		return true;
	}
	
//	protected int findLoopEndCode(String code)
//	{
//		//�ŏ��̊��ʂ܂ňړ�
//		int end = code.length();
//		int i = code.indexOf("[");
//		//���̎�������銇�ʂ�������܂ŒT���@����Ɋ��ʂ�Idx+1�A�����ʂ�-1�A�O�̂Ƃ��ɕ����ʂŏI��
//		i++;
//		int idx = 0;
//		while(true){
//			if(code.charAt(i) == '[')idx++;
//			if(code.charAt(i) == ']'){
//				if(idx==0)return i+1;
//				else idx--;
//			}
//			i++;
//			if(i>=end)return -1;
//		}
//	}
	
	public static IProgramPanel createPanel_forSerial(char mode)
	{
		switch(mode){
		case 'S' : return new SetValuePanel();
		case 'T' : return new TimerPanel();
		case 'L' : return new LoopPanel();
		case 'l' : return new LoopPanel.LoopEndPanel(null);
		case 'k' : 
		case 'K' : return new KeyFramePanel();
		case 'W' : return new WaitPanel();
		case 'N' : return new NotifyPanel();
		case 'M' : return new SoundPanel();
		}
		return new SetValuePanel();
	}
	public static IProgramPanel createPanel(String mode)
	{
		return createPanel(IProgramPanel.Mode.getType(mode));
	}
	public static IProgramPanel createPanel(IProgramPanel.Mode mode)
	{
		switch(mode){
		case set : return new SetValuePanel();
		case timer : return new TimerPanel();
		case loop : return new LoopPanel();
		case loopend : return new LoopPanel.LoopEndPanel(null);
		case keyframe : return new KeyFramePanel();
		case wait : return new WaitPanel();
		case notify : return new NotifyPanel();
		case sound : return new SoundPanel();
		}
		return new SetValuePanel();
	}

}
