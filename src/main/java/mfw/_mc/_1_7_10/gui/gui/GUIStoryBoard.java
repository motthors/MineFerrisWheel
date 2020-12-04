package mfw._mc._1_7_10.gui.gui;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.*;

import mfw.storyboard.programpanel.*;
import mfw.ferriswheel.Connector;
import mfw.ferriswheel.FerrisPartBase;
import mfw._mc._1_7_10.gui.CustomTexButton;
import mfw.storyboard.programpanel.IProgramPanel.Mode;
import mochisystems._mc._1_7_10._core._Core;
import mochisystems._mc._1_7_10.gui.GUIBlockModelerBase;
import mochisystems._mc._1_7_10.gui.GuiToggleButton;
import mochisystems.math.Math;
import mochisystems.math.Vec3d;
import mochisystems.util.gui.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import mfw._mc._1_7_10._core.MFW_Core;
import mfw._mc._1_7_10.message.MFW_PacketHandler;
import mfw._mc._1_7_10.message.MessageFerrisMisc;
import mfw.storyboard.StoryBoardManager;
import net.minecraft.util.ResourceLocation;

public class GUIStoryBoard extends GUIBlockModelerBase {

	private static final ResourceLocation AddNewBase = new ResourceLocation(MFW_Core.MODID, "textures/gui/addnewbase.png");
	private static final ResourceLocation Inspector = new ResourceLocation(MFW_Core.MODID, "textures/gui/inspector.png");
//	private static final ResourceLocation TEX_ARROW = new ResourceLocation(MFW_Core.MODID, "textures/gui/sbarrow.png");
	private static final ResourceLocation cancelTex = new ResourceLocation(MFW_Core.MODID, "textures/gui/cancel.png");
	private static final ResourceLocation set = new ResourceLocation(MFW_Core.MODID, "textures/gui/set.png");
	private static final ResourceLocation timer = new ResourceLocation(MFW_Core.MODID, "textures/gui/timer.png");
	private static final ResourceLocation wait = new ResourceLocation(MFW_Core.MODID, "textures/gui/wait.png");
	private static final ResourceLocation notify = new ResourceLocation(MFW_Core.MODID, "textures/gui/notify.png");
	private static final ResourceLocation keyframe = new ResourceLocation(MFW_Core.MODID, "textures/gui/keyframe.png");
	private static final ResourceLocation sound = new ResourceLocation(MFW_Core.MODID, "textures/gui/sound.png");
	private static final ResourceLocation loop = new ResourceLocation(MFW_Core.MODID, "textures/gui/loop.png");
	private static final ResourceLocation loopend = new ResourceLocation(MFW_Core.MODID, "textures/gui/loopend.png");
	private static final ResourceLocation current = new ResourceLocation(MFW_Core.MODID, "textures/gui/current.png");


	private GuiGroupCanvas canvas = new GuiGroupCanvas();
	private int GuiGroup_Def = 0;
	private int GuiGroup_AddPanel = 1;
	private int GuiGroup_Preset = 2;
	private int GuiGroup_Timeline = 3;
	private int GuiGroup_Activate = 4;
	private int GuiGroup_InspectorBase = 10;
	private int GuiGroup_Selections = 100;

    private LinkedList<GuiStoryPartPanel> PanelButtonList;
    private ArrayList<GuiLabel> TimeLineIndexLabel;
    private ArrayList<GUIStoryBoardSettingPart> currentSelectionButtons;
    private FerrisPartBase part;
	private int blockposX;
	private int blockposY;
	private int blockposZ;

	private GuiStoryPartPanel currentEditPanel;
	private int currentSelectionIndex;
	private int currentInspectorGroupId;
	private GuiButtonWrapper[] selectorButtons;
	private GuiButtonWrapper currentSelectionButton;
	private GuiToggleButton AddNewBaseButton;
	private GuiImage currentPanelPointer;
	private boolean isDirty;
	private boolean isActiveNewPanel;
	private boolean isActivePresetPanel;
	private float TimelineScale = 1;
	private float TimelineScaleTemp = 1;
	private float TimelineScroll = 1;
	private float TimelineScrollTemp = 1;
	private int TimeLineLength;

	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    private ArrayList<GUIStoryBoardSettingPart> ButtonListSet 		= new ArrayList<>();
    private ArrayList<GUIStoryBoardSettingPart> ButtonListTimer 	= new ArrayList<>();
    private ArrayList<GUIStoryBoardSettingPart> ButtonListKeyFrame 	= new ArrayList<>();
    private ArrayList<GUIStoryBoardSettingPart> ButtonListLoop		= new ArrayList<>();
    private ArrayList<GUIStoryBoardSettingPart> ButtonListWait		= new ArrayList<>();
    private ArrayList<GUIStoryBoardSettingPart> ButtonListNotify	= new ArrayList<>();
    private ArrayList<GUIStoryBoardSettingPart> ButtonListSound		= new ArrayList<>();

    private int maxPanelCount;

	private String serialCode;


    public GUIStoryBoard(int x, int y, int z, FerrisPartBase part)
    {
    	super(x, y, z, null);
        this.part = part;
		blockposX = x;
		blockposY = y;
		blockposZ = z;
		xSize = 370;
		PanelButtonList = new LinkedList<>();
		TimeLineIndexLabel = new ArrayList<>();
		currentSelectionButtons = new ArrayList<>();
	}

//	private int PosXFromIndex(int index)
//	{
//		PosXFromIndex(index, false);
//	}
//	private int PosXFromIndex(int index, boolean isEnd)
//	{
//		return 10 + (isEnd?10:20 + 4) * index;
//	}

	@Override
	protected boolean CanDrag(int x, int y, int buttonid)
	{
		return 0 < x && x < width && 40 < y && y < height-30;
	}

	@Override
	protected void UpdateCameraPosFromCore(Vec3d dest, float tick)
	{
		Connector.Fix(dest, part.connectorFromParent, tick);
	}

	@Override
	public void handleMouseInput()
	{
		int i = Mouse.getEventDWheel();
		int y = Mouse.getEventY();
		if(i != 0 && y > (height - 40)*2){
			CloseInspector();
			TimelineScrollTemp += -0.1f * i;
			TimelineScrollTemp = Math.Clamp(TimelineScrollTemp, 0, (TimeLineLength-20) * TimelineScaleTemp);
		}
		else super.handleMouseInput();
	}

	private void ScaleTimeline(float add)
	{
		CloseInspector();
		float prev = TimelineScaleTemp;
		TimelineScaleTemp += add;
		TimelineScaleTemp = Math.Clamp(TimelineScaleTemp, 0.1f, 2f);
		TimelineScrollTemp *= TimelineScaleTemp / prev;
	}

	// GUIを開くたび呼ばれる初期化関数
	@Override
	public void initGui()
    {
		super.initGui();
		canvas.Init();
		part.storyboardManager.stop();

		PanelButtonList.clear();
		// ボタン登録
		ySize = mc.displayHeight;
		maxPanelCount = 0;


		GuiToggleButton addPanelBase = new GuiToggleButton(0, width/6-30, height - 24, 60, 18,
				"New Panel", "New Panel",
				() -> isActiveNewPanel,
				isOn -> {
					isActiveNewPanel = isOn;
					if(isOn) canvas.ActiveGroup(GuiGroup_AddPanel);
					else canvas.DisableGroup(GuiGroup_AddPanel);
					CloseInspector();
		});
		canvas.Register(GuiGroup_Def, addPanelBase);
		AddNewBaseButton = addPanelBase;

		currentPanelPointer = new GuiImage(current, 2, -18, 37, 37);
		canvas.Register(GuiGroup_Timeline, currentPanelPointer);

		GuiUtil.addButton2(canvas, GuiGroup_Def, 0, 40, "", 0,
				() -> ScaleTimeline(-0.25f),
				() -> ScaleTimeline(0.25f));
		canvas.Register(GuiGroup_Def,
				new GuiButtonWrapper(0, 60, 40, 40, 12, "reset",
						() -> {
							TimelineScale = 1;
							TimelineScaleTemp = 1;
						}
				));

		GuiImage newpanelBase = new GuiImage(AddNewBase, 0, 0, 63, 148);
		canvas.Register(GuiGroup_AddPanel, newpanelBase);
		CustomTexButton addPanel;
		addPanel = new CustomTexButton(set,10, 10, 20, 20, ()->AddNewPanel(new SetValuePanel(), false));
		canvas.Register(GuiGroup_AddPanel ,addPanel);
		addPanel = new CustomTexButton(keyframe, 10, 32, 20, 20, ()->AddNewPanel(new KeyFramePanel(), false));
		canvas.Register(GuiGroup_AddPanel, addPanel);
		addPanel = new CustomTexButton(sound, 10, 54, 20, 20, ()->AddNewPanel(new SoundPanel(), false));
		canvas.Register(GuiGroup_AddPanel, addPanel);
		addPanel = new CustomTexButton(timer,  32, 10, 20, 20, ()->AddNewPanel(new TimerPanel(), false));
		canvas.Register(GuiGroup_AddPanel, addPanel);
		addPanel = new CustomTexButton(wait,32, 32, 20, 20, ()->AddNewPanel(new WaitPanel(), false));
		canvas.Register(GuiGroup_AddPanel, addPanel);
		addPanel = new CustomTexButton(loop,32, 54, 20, 20, ()->AddNewPanel(new LoopPanel(), false));
		canvas.Register(GuiGroup_AddPanel, addPanel);
		addPanel = new CustomTexButton(notify,32, 76, 20, 20, ()->AddNewPanel(new NotifyPanel(), false));
		canvas.Register(GuiGroup_AddPanel, addPanel);
		canvas.MoveGroup(GuiGroup_AddPanel, width/6-31, height - 174, false);


		// Inspector
		MakeInspector(Mode.set, new SetValuePanel(), ButtonListSet);
		MakeInspector(Mode.timer, new TimerPanel(), ButtonListTimer);
		MakeInspector(Mode.keyframe, new KeyFramePanel(), ButtonListKeyFrame);
		MakeInspector(Mode.loop, new LoopPanel(), ButtonListLoop);
		MakeInspector(Mode.wait, new WaitPanel(), ButtonListWait);
		MakeInspector(Mode.notify, new NotifyPanel(), ButtonListNotify);
		MakeInspector(Mode.sound, new SoundPanel(), ButtonListSound	);



		GuiToggleButton OpenPreset = new GuiToggleButton(0, width*3/6-30, height - 24, 60, 18,
				_Core.Instance.I18n("gui.core.story.preset"),
				_Core.Instance.I18n("gui.core.story.preset"),
				() -> isActivePresetPanel,
				isOn -> {
					isActivePresetPanel = isOn;
					if(isOn) canvas.ActiveGroup(GuiGroup_Preset);
					else canvas.DisableGroup(GuiGroup_Preset);
					CloseInspector();
				});
		canvas.Register(GuiGroup_Def, OpenPreset);

		// Preset
		GuiImage presetBase = new GuiImage(AddNewBase, 0, 0, 120, 148);
		canvas.Register(GuiGroup_Preset, presetBase);
		GuiButtonWrapper preset;
		preset = new GuiButtonWrapper(0,10, 10, 100, 20,
				_Core.Instance.I18n("gui.core.story.preset.roundtrip"),
				() -> {
					DeleteAllPanel();
					AddNewPanel(new WaitPanel(0), true);
					AddNewPanel(new KeyFramePanel("Position", "Set", "Linear", 90, 20, false), true);
					AddNewPanel(new WaitPanel(0), true);
					AddNewPanel(new KeyFramePanel("Position", "Set", "Linear", 0, 20, false), true);
					FormatPanelButtonPosition();
				});
		canvas.Register(GuiGroup_Preset ,preset);
		preset = new GuiButtonWrapper(0,10, 32, 100, 20,
				_Core.Instance.I18n("gui.core.story.preset.autoroundtrip"),
				() -> {
					DeleteAllPanel();
					AddNewPanel(new KeyFramePanel("Position", "Set", "Linear", 90, 20, false), true);
					AddNewPanel(new KeyFramePanel("Position", "Set", "Linear", 0, 20, false), true);
					FormatPanelButtonPosition();
				});
		canvas.Register(GuiGroup_Preset ,preset);
		preset = new GuiButtonWrapper(0,10, 54, 100, 20,
				_Core.Instance.I18n("gui.core.story.preset.step"),
				() -> {
					DeleteAllPanel();
					AddNewPanel(new WaitPanel(0), true);
					AddNewPanel(new KeyFramePanel("Position", "Add", "Linear", 90, 20, false), true);
					FormatPanelButtonPosition();
				});
		canvas.Register(GuiGroup_Preset ,preset);
		preset = new GuiButtonWrapper(0,10, 76, 100, 20,
				_Core.Instance.I18n("gui.core.story.preset.clock"),
				() -> {
					DeleteAllPanel();
					AddNewPanel(new TimerPanel(20), true);
					AddNewPanel(new SetValuePanel("Position", "Add", 10), true);
					FormatPanelButtonPosition();
				});
		canvas.Register(GuiGroup_Preset ,preset);

		canvas.MoveGroup(GuiGroup_Preset, width/2-60, height - 174, false);


		//serializer
		GuiButtonWrapper copy = new GuiButtonWrapper(0, width*6/9, height - 18, 40, 13, "Copy",
				() -> {
					clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection selection = new StringSelection(serialCode);
					clipboard.setContents(selection, null);
				});
		GuiButtonWrapper paste = new GuiButtonWrapper(0, width*8/9, height - 18, 40, 13, "Paste",
				() -> {
					Transferable object = clipboard.getContents(null);
					try {
						serialCode = ((String)object.getTransferData(DataFlavor.stringFlavor));
						this.part.storyboardManager.createFromSerialCode(serialCode);
						//パネルタイムライン更新
						PanelButtonList.clear();
						for( IProgramPanel p : this.part.storyboardManager.getPanelList()){AddNewPanel(p, true);}
						FormatPanelButtonPosition();
						updateToServer();
						serialCode = (this.part.storyboardManager.ToSerialCode());
					} catch(Exception e) {
						e.printStackTrace();
					}
				});
		canvas.Register(GuiGroup_Def, copy);
		canvas.Register(GuiGroup_Def, paste);





		//現在のストーリーを適用
		GuiButtonWrapper Activate = new GuiButtonWrapper(0, width-60, 40, 60, 18, "Activate", this::updateToServer);
		canvas.Register(GuiGroup_Activate, Activate);



		//selections
		int selectorNum = 10;
		selectorButtons = new GuiButtonWrapper[selectorNum];
		for(int i = 0; i < selectorNum; ++i)
		{
			int idx = i;
			GuiButtonWrapper selector = new GuiButtonWrapper(0, 0, idx * 19, 60, 18, "Paste",
					() -> {
						canvas.DisableGroup(GuiGroup_Selections);
						currentEditPanel.panel.setSelection(currentSelectionIndex, idx);
						if(currentSelectionButton!=null)currentSelectionButton.displayString = currentEditPanel.panel.getValue(currentSelectionIndex);
					});
			selectorButtons[i] = selector;
			canvas.Register(GuiGroup_Selections, selector);
		}


		for(IProgramPanel panel : this.part.storyboardManager.getPanelList())
		{
			AddNewPanel(panel, true);
		}
		FormatPanelButtonPosition();
		isDirty = false;
		canvas.DisableGroup(GuiGroup_Activate);

		if(PanelButtonList.isEmpty())
		{
			AddNewBaseButton.SetState(true);
			canvas.ActiveGroup(GuiGroup_AddPanel);
			isActiveNewPanel = true;
		}
		canvas.ActiveGroup(GuiGroup_Def);
		canvas.MoveGroup(GuiGroup_Timeline, 10, 10, false);
		canvas.ActiveGroup(GuiGroup_Timeline);
    }

    private void MakeInspector(Mode mode, IProgramPanel panel, ArrayList<GUIStoryBoardSettingPart> partList)
	{
		int x = 5, y = 10;
		int groupId = mode.GetIdx() + GuiGroup_InspectorBase;
		canvas.Register(groupId, new GuiImage(Inspector, 0, y, 80, 200));
		y +=12;

		CustomTexButton close = new CustomTexButton(cancelTex,-13, 150, 15, 15, this::DeletePanel);
		canvas.Register(groupId, close);

		for(int i = 0; i < panel.ApiNum(); ++i)
		{
			int apiIdx = i;
			GuiLabel label = new GuiLabel(panel.getDescription(i), fontRendererObj, x, y, 0xffffff);
			canvas.Register(groupId, label);
			GUIStoryBoardSettingPart part = new GUIStoryBoardSettingPart(panel.getType(apiIdx), apiIdx, panel.getDescription(apiIdx));
			part.SetLabel(label);
			y += 8;
			switch(panel.getType(i))
			{
				case change:
					GuiButtonWrapper button = new GuiButtonWrapper(0, x, y, 69, 14, "",null);
					canvas.Register(groupId, button);
					button.SetAction(()->OpenSelection(currentEditPanel.panel, apiIdx, button));
					part.SetButton(button);
					break;

				case inputValue:
					GuiFormatedTextField field = new GuiFormatedTextField(fontRendererObj, x, y, 69, 14, 0xffffff, 12,
							() -> (currentEditPanel!=null) ? currentEditPanel.panel.getValue(apiIdx) : "",
							s -> s.matches(GuiFormatedTextField.regexNumber),
							(t) -> currentEditPanel.panel.setValue(apiIdx, t));
					canvas.Register(groupId, field);
					field.setEnableBackgroundDrawing(true);
					part.SetTextField(field);
					break;

				case soundSelector:

					break;
			}
			y += 16;
			partList.add(part);
		}
		canvas.DisableGroup(groupId);
	}

    private void AddNewPanel(IProgramPanel panel, boolean isInit)
	{
		ResourceLocation tex = PanelModeToTex(panel.getMode());
		GuiStoryPartPanel btn;
		boolean isEnd = panel.getMode()==Mode.loopend;
		btn = new GuiStoryPartPanel(tex, this, panel,
				canvas.GetGroupInfo(GuiGroup_Timeline), panel.getMode()==Mode.loop,
				0, 0, isEnd?10:20, 20,
				isEnd ? p->{} : this::OpenOrCloseInspector);

		canvas.Register(GuiGroup_Timeline, btn);

		if(currentEditPanel == null) this.PanelButtonList.add(btn);
		else
		{
			int idx = PanelButtonList.indexOf(currentEditPanel);
			if(idx > 1) PanelButtonList.add(idx, btn);
		}

		if(maxPanelCount < PanelButtonList.size() && !isEnd)
		{
			maxPanelCount++;
			GuiLabel label = new GuiLabel(""+maxPanelCount, fontRendererObj, 0, -5, 0xffffff);
			canvas.Register(GuiGroup_Timeline, label);
			TimeLineIndexLabel.add(label);
		}

		if(!isInit && panel.getMode() == Mode.loop)
		{
			AddNewPanel(((LoopPanel)panel).GetLoopEndPanel(), false);
		}

		if(!isInit) FormatPanelButtonPosition();
		isDirty = true;
		canvas.ActiveGroup(GuiGroup_Activate);
	}

	private void DeletePanel()
	{
		if(currentEditPanel == null)return;
		if(currentEditPanel.panel.getMode()==Mode.loopend)return;
		PanelButtonList.remove(currentEditPanel);
		canvas.DeleteElement(GuiGroup_Timeline, currentEditPanel);

		if(currentEditPanel.panel.getMode()==Mode.loop){
			int i = 1 + PanelButtonList.indexOf(currentEditPanel);
			for(; i<PanelButtonList.size(); ++i){
				GuiStoryPartPanel panel = PanelButtonList.get(i);
					if(panel.panel == ((LoopPanel)currentEditPanel.panel).GetLoopEndPanel()){
					PanelButtonList.remove(i);
					break;
				}
			}
		}
		FormatPanelButtonPosition();
		canvas.DisableGroup(currentInspectorGroupId);
		isDirty = true;
		canvas.ActiveGroup(GuiGroup_Activate);
	}

	private void DeleteAllPanel()
	{
		for(GuiStoryPartPanel panel : PanelButtonList)
		{
			canvas.DeleteElement(GuiGroup_Timeline, panel);
		}
		PanelButtonList.clear();

//		for(GuiLabel label: TimeLineIndexLabel)
//		{
//			canvas.DeleteElement(GuiGroup_Timeline, label);
//		}
//		TimeLineIndexLabel.clear();

		isDirty = true;
		canvas.ActiveGroup(GuiGroup_Activate);}

	private void OpenOrCloseInspector(GuiStoryPartPanel panel)
	{
		if(currentEditPanel == panel) {
			canvas.DisableGroup(currentInspectorGroupId);
			currentInspectorGroupId = -1;
			currentEditPanel = null;
		}
		else {
			currentEditPanel = panel;
			if (currentInspectorGroupId >= GuiGroup_InspectorBase) canvas.DisableGroup(currentInspectorGroupId);
			currentInspectorGroupId = panel.panel.getMode().GetIdx() + GuiGroup_InspectorBase;
			GuiGroupCanvas.Group g = canvas.GetGroupInfo(GuiGroup_Timeline);
			int x = (int)((panel.GetPositionX() + 10) / g.scale.x);
			int y = (int)((panel.GetPositionY() + 25) / g.scale.y);
			canvas.ActiveGroup(currentInspectorGroupId);
			canvas.MoveGroup(currentInspectorGroupId, x, y, false);
			changeSelectionButtonDisplay(panel.panel.getMode());
		}
		isActiveNewPanel = false;
		isActivePresetPanel = false;
		canvas.DisableGroup(GuiGroup_AddPanel);
		canvas.DisableGroup(GuiGroup_Preset);
		canvas.DisableGroup(GuiGroup_Selections);
		AddNewBaseButton.SetState(false);
	}

	private void CloseInspector()
	{
		if(currentInspectorGroupId > GuiGroup_InspectorBase) {
			canvas.DisableGroup(currentInspectorGroupId);
			currentInspectorGroupId = -1;
			currentEditPanel = null;
		}
	}

	private void OpenSelection(IProgramPanel panel, int apiIdx, GuiButtonWrapper button)
	{
		currentSelectionButton = button;
		currentSelectionIndex = apiIdx;
		GuiGroupCanvas.Group info = canvas.GetGroupInfo(currentInspectorGroupId);
		canvas.ActiveGroup(GuiGroup_Selections);
		String[] datas = panel.GetSelectionLabels(apiIdx);
		int x = info.offsetX + 90;
		int y = info.offsetY + 12 + 24 * apiIdx;
		y = Math.Clamp(y, 10, height - datas.length * 19);
		canvas.MoveGroup(GuiGroup_Selections, x, y, false);
		int i = 0;
		for(; i < datas.length; ++i)
		{
			selectorButtons[i].SetPosition(0, i * 19);
			selectorButtons[i].displayString = datas[i];
		}
		for(; i < selectorButtons.length; ++i)
		{
			selectorButtons[i].SetPosition(-1000, 0);
		}
		button.displayString = panel.getValue(apiIdx);
	}

	private ResourceLocation DefaultTex()
	{
		return PanelModeToTex(Mode.set);
	}

	private ResourceLocation PanelModeToTex(Mode mode)
	{
		switch(mode)
		{
		case set : return set;
		case timer : return timer;
		case loop : return loop;
		case loopend : return loopend;
		case keyframe : return keyframe;
		case wait : return wait;
		case notify : return notify;
		case sound : return sound;
		default : return DefaultTex();
		}
	}


	public void clearPanel()
	{
		PanelButtonList.clear();
		part.storyboardManager.clear();
	}

    @Override
	public void onGuiClosed()
    {
    	updateToServer();
		super.onGuiClosed();
	}

    private void updateToServer()
    {
		canvas.DisableGroup(GuiGroup_AddPanel);
		canvas.DisableGroup(GuiGroup_Preset);
		canvas.DisableGroup(GuiGroup_Selections);
		isActivePresetPanel = false;
		isActiveNewPanel = false;

    	StoryBoardManager manager = part.storyboardManager;
    	manager.clear();
    	for(GuiStoryPartPanel panel : PanelButtonList)
    	{
			if(!panel.panel.CanUseWith(this.part)) continue;
			manager.addPanel(panel.panel);
    	}
    	byte[] serialArray = manager.ToSerialCode().getBytes();
    	int flag = MessageFerrisMisc.GUIStoryBoardSendData;
//    	MFW_Logger.debugInfo(part.getStoryBoardManager().ToSerialCode());
    	MessageFerrisMisc packet = new MessageFerrisMisc(blockposX,blockposY,blockposZ, flag, 0, 0, serialArray);
	    MFW_PacketHandler.INSTANCE.sendToServer(packet);
		isDirty = false;
		canvas.ActiveGroup(GuiGroup_Activate);
	}



//	public void actionPerformed(GUIStoryBoardSettingPart part, String value)
//	{
//		int[] update = currentEditPanel.panel.setValue(part.apiIndex, value);
//		for(int i : update){
//			nowSelectedSettingList.get(i).setValue(currentEditPanel.panel.getValue(i));
//		}
//	}

//	private void PageUp()
//	{
//		PanelPageIndex--;
//		if(PanelPageIndex<0)PanelPageIndex = 0;
//		FormatPanelButtonPosition();
//	}
//	private void PageDown()
//	{
//		PanelPageIndex++;
//		if(PanelPageIndex >= PanelButtonList.size()-1)PanelPageIndex = PanelButtonList.size()-1;
//		FormatPanelButtonPosition();
//	}


	private void FormatPanelButtonPosition()
	{
		Mode mode;
		int offsetX = 10;
		int offsetY = -10;
		int size = PanelButtonList.size();
		int labelIdx = 0;
		Stack<GuiStoryPartPanel> loopPanelStack = new Stack<>();
		for(int i = 0; i < size; ++i)
		{
			GuiStoryPartPanel button = PanelButtonList.get(i);
			mode = button.panel.getMode();
			int panelWidth = (mode == Mode.loopend) ? 10 : 20;
			if(mode == Mode.loop) loopPanelStack.push(button);
			if(mode == Mode.loopend)
			{
				offsetY -= 2;
				if(loopPanelStack.size()>0)loopPanelStack.pop().SetEndPanel(button, loopPanelStack.size());
			}
			if(button != canvas.GetDraggingElement())
			{
				button.xPosition = offsetX;
				button.yPosition = offsetY;
			}
			else
			{
				startPos = offsetX;
			}

			if(mode != Mode.loopend)
			{
				GuiLabel label = TimeLineIndexLabel.get(labelIdx);
				label.SetPosition(offsetX, -17);
				labelIdx++;
			}

			offsetX += panelWidth + 4;
			if(mode == Mode.loop) offsetY +=2;
		}
		TimeLineLength = offsetX;
	}

	private int startPos;
	private void DragMotion()
	{
		IGuiDraggable drag = canvas.GetDraggingElement();
		if(drag != null)
		{
			int orgX = (startPos < 0) ? drag.GetStartPosX() : startPos;
			int x = drag.GetPositionX();
			int y = drag.GetPositionY();
			if(-10 <= y && y <= 20){
				if(x < orgX - 14 || orgX + 14 < x){
					int index = PanelButtonList.indexOf(drag);
					int nextIndex = index + (x < orgX ? -1 : 1);
					nextIndex = Math.Clamp(nextIndex, 0, PanelButtonList.size()-1);
//					if( PanelButtonList.get(index).panel.getMode()==Mode.loopend && PanelButtonList.get(nextIndex).panel.getMode()==Mode.loop)
//					{
//
//					}
//					if( PanelButtonList.get(index).panel.getMode()==Mode.loop && PanelButtonList.get(nextIndex).panel.getMode()==Mode.loopend)
//					{
//
//					}
					PanelButtonList.remove(drag);
					PanelButtonList.add(nextIndex, (GuiStoryPartPanel) drag);
					FormatPanelButtonPosition();
					CloseInspector();
				}
			}
		}
	}

	private GUIStoryBoardSettingPart findSettingPartFromButtonID(int buttonid, ArrayList<GUIStoryBoardSettingPart> source)
	{
		for(GUIStoryBoardSettingPart part : source)
		{
			if(part.buttonid == buttonid)return part;
		}
		return null;
	}

//	private void changePanelSettingGui(CustomTexButton panelButton)
//	{
//		if(currentEditPanel !=null)changePanelSettingGuiPos(currentEditPanel.panel.getMode(), -1000);
//		currentEditPanel = panelButton;
//		if(currentEditPanel !=null)changePanelSettingGuiPos(currentEditPanel.panel.getMode(), settingButtonsOffsetX+guiLeft);
//	}

	private void changeSelectionButtonDisplay(Mode mode)
	{
		switch(mode)
		{
		case set : currentSelectionButtons = ButtonListSet; break;
		case timer : currentSelectionButtons = ButtonListTimer; break;
		case keyframe : currentSelectionButtons = ButtonListKeyFrame; break;
		case loop : currentSelectionButtons = ButtonListLoop; break;
		case wait : currentSelectionButtons = ButtonListWait; break;
		case notify : currentSelectionButtons = ButtonListNotify; break;
		case sound : currentSelectionButtons = ButtonListSound; break;
		case loopend : return;
		}
		for(GUIStoryBoardSettingPart part : currentSelectionButtons)
		{
			part.LoadCurrentValueFromPanel(currentEditPanel.panel);
		}
	}

    @Override
    public void drawScreen(int x, int y, float partialtick)
    {
        super.drawScreen(x, y, partialtick);
//        GL11.glDisable(GL11.GL_LIGHTING);
//        GL11.glDisable(GL11.GL_BLEND);


//        int start = PanelPageIndex;
//        int yoffset = 0;
//        int end = PanelButtonList.size();// (start+num < PanelButtonList.size()) ? start+num : PanelButtonList.size();
//        for (int i = start; i < end; ++i)
//        {
//        	CustomTexButton button = PanelButtonList.get(i);
//        	button.drawButton2(this.mc, x, y);
//    		this.fontRendererObj.drawString(""+i, button.xPosition-15, button.yPosition, 0x404040);
//    		this.fontRendererObj.drawString(
//        			button.panel.displayDescription(),
//        			button.xPosition+25, button.yPosition+8, 0xffffff);
//    		yoffset += button.height;
//    		if(yoffset >= height-70)break;
//        }

//        for(GUIStoryBoardSettingPart part : SettingPartButtonlist)
//        {
//        	part.Draw();
//        }

    }
	@Override
	public void drawWorldBackground(int p_146270_1_)
	{
		int mx = 0, my = 0;
		int xx = width, xy = 40;
		int color = 0xD0101010;
		drawRect(mx, my, xx, xy, color);
		my = height - 30; xy = height;
		drawRect(mx, my, xx, xy, color);
	}

	/*GUIの文字等の描画処理*/
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);
        canvas.Update();

		TimelineScale = (float)Math.Lerp(0.1, TimelineScale, TimelineScaleTemp);
		canvas.SetScale(GuiGroup_Timeline, TimelineScale, TimelineScale, TimelineScale, false);

		TimelineScroll = (float)Math.Lerp(0.1f, TimelineScroll, TimelineScrollTemp);
		canvas.MoveGroup(GuiGroup_Timeline, (int)-TimelineScroll, 20, false);

        int idx = part.storyboardManager.getCurrentTargetIndex()-1;
        if(!isDirty && 0 < idx && idx < PanelButtonList.size())
		{
			GuiStoryPartPanel partPanel = PanelButtonList.get(idx);
			currentPanelPointer.SetPosition(partPanel.xPosition-8, partPanel.yPosition-8);
		}
    }
 
    /*GUIの背景の描画処理*/
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
    {
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//        this.mc.renderEngine.bindTexture(TEX_BASE);
        
        //partList.drawScreen(mouseX, mouseZ, partialTick);
        
//        int k = (this.width - this.xSize) / 2;
//        int l = 0;//(this.height - this.ySize) / 2;
//        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, ySize, this.xSize, 100);

		canvas.DrawContents(mouseX, mouseY);

		//選択中パネルの矢印
//        if(currentEditPanel !=null)
//        {
//        	int height = currentEditPanel.height;
//	        this.mc.renderEngine.bindTexture(TEX_ARROW);
////	        this.drawTexturedModalRect(currentEditPanel.xPosition+guiLeft+120, currentEditPanel.yPosition, 0, 0, 6, height, 6, height);
//        }

    }
    
    /*GUIが開いている時にゲームの処理を止めるかどうか。*/
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
	
    ////////////////////////////text field//////////////////////////////////
	/**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char c, int keyCode)
    {
		if (! canvas.KeyTyped(c, keyCode))
		{
			super.keyTyped(c, keyCode);
		}
    }


	@Override
	public void mouseClicked(int x, int y, int buttonId)
    {
        super.mouseClicked(x, y, buttonId);
		canvas.MouseClicked(x, y, buttonId);
	}

	@Override
	protected void mouseClickMove(int x, int y, int event, long time)
	{
		super.mouseClickMove(x, y, event, time);
		canvas.mouseClickMove(x, y, event, time);
		DragMotion();
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int buttonid)
	{
		super.mouseMovedOrUp(x, y, buttonid);
		canvas.mouseMoveOrUp(x, y, buttonid);
		if(drag != null && buttonid == 0)
		{
			FormatPanelButtonPosition();
			drag = null;
			startPos = -10;
		}
	}

	IGuiDraggable drag;
	void PanelDragEnd(GuiStoryPartPanel panel)
	{
		drag = panel;
	}
}