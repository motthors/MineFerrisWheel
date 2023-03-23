package jp.mochisystems.mfw._mc.gui.gui;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.*;

import jp.mochisystems.core._mc.gui.GUICanvasGroupControl;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.gui.container.DefContainer;
import jp.mochisystems.core.util.gui.GuiGroupCanvas;
import jp.mochisystems.core.util.gui.GuiLabel;
import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.GuiToggleButton;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.gui.*;
import jp.mochisystems.core.util.Connector;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw._mc.message.MFW_PacketHandler;
import jp.mochisystems.mfw._mc.message.MessageFerrisMisc;
import jp.mochisystems.mfw._mc.message.MessageSyncNbtForMFWCtS;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;
import jp.mochisystems.mfw.storyboard.programpanel.*;
import jp.mochisystems.mfw.storyboard.StoryBoardManager;
import jp.mochisystems.mfw._mc.gui.CustomTexButton;
import jp.mochisystems.mfw.storyboard.programpanel.IProgramPanel.Mode;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GUIStoryBoard extends GUICanvasGroupControl {

	private static final ResourceLocation AddNewBase = new ResourceLocation(MFW.MODID, "textures/gui/addnewbase.png");
	private static final ResourceLocation Inspector = new ResourceLocation(MFW.MODID, "textures/gui/inspector.png");
//	private static final ResourceLocation TEX_ARROW = new ResourceLocation(MFW_Core.MODID, "textures/gui/sbarrow.png");
	private static final ResourceLocation cancelTex = new ResourceLocation(MFW.MODID, "textures/gui/cancel.png");
	private static final ResourceLocation set = new ResourceLocation(MFW.MODID, "textures/gui/set.png");
	private static final ResourceLocation timer = new ResourceLocation(MFW.MODID, "textures/gui/timer.png");
	private static final ResourceLocation wait = new ResourceLocation(MFW.MODID, "textures/gui/wait.png");
	private static final ResourceLocation notify = new ResourceLocation(MFW.MODID, "textures/gui/notify.png");
	private static final ResourceLocation keyframe = new ResourceLocation(MFW.MODID, "textures/gui/keyframe.png");
	private static final ResourceLocation sound = new ResourceLocation(MFW.MODID, "textures/gui/sound.png");
	private static final ResourceLocation loop = new ResourceLocation(MFW.MODID, "textures/gui/loop.png");
	private static final ResourceLocation loopEnd = new ResourceLocation(MFW.MODID, "textures/gui/loopend.png");
	private static final ResourceLocation current = new ResourceLocation(MFW.MODID, "textures/gui/current.png");


	private final int GuiGroup_AddPanel = 1;
	private final int GuiGroup_Preset = 2;
	private final int GuiGroup_Timeline = 3;
	private final int GuiGroup_Activate = 4;
	private final int GuiGroup_InspectorBase = 10;
	private final int GuiGroup_Selections = 100;

    private final LinkedList<GuiStoryPartPanel> PanelButtonList;
    private final ArrayList<GuiLabel> TimeLineIndexLabel;
    private ArrayList<GUIStoryBoardSettingPart> currentSelectionButtons;
    private final FerrisSelfMover part;
	private final int blockPosX;
	private final int blockPosY;
	private final int blockPosZ;

	private GuiColorPanel activationStatus;

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

    private final ArrayList<GUIStoryBoardSettingPart> ButtonListSet 		= new ArrayList<>();
    private final ArrayList<GUIStoryBoardSettingPart> ButtonListTimer 		= new ArrayList<>();
    private final ArrayList<GUIStoryBoardSettingPart> ButtonListKeyFrame 	= new ArrayList<>();
    private final ArrayList<GUIStoryBoardSettingPart> ButtonListLoop		= new ArrayList<>();
    private final ArrayList<GUIStoryBoardSettingPart> ButtonListWait		= new ArrayList<>();
    private final ArrayList<GUIStoryBoardSettingPart> ButtonListNotify		= new ArrayList<>();
    private final ArrayList<GUIStoryBoardSettingPart> ButtonListSound		= new ArrayList<>();

	private String serialCode;


    public GUIStoryBoard(FerrisSelfMover part)
    {
    	super(new DefContainer());
        this.part = part;
		blockPosX = (int) part.controller.CorePosX();
		blockPosY = (int) part.controller.CorePosY();
		blockPosZ = (int) part.controller.CorePosZ();
		xSize = 370;
		PanelButtonList = new LinkedList<>();
		TimeLineIndexLabel = new ArrayList<>();
		currentSelectionButtons = new ArrayList<>();
	}


	@Override
	protected boolean CanDrag(int x, int y, int buttonid)
	{
		return 0 < x && x < width && 40 < y && y < height-30;
	}

	@Override
	protected void UpdateCameraPosFromCore(Vec3d dest, float tick)
	{
		Connector.Fix(dest, part.connectorFromParent, tick);
		dest.add(part.offset);
		dest.add(part.controller.CorePosX(), part.controller.CorePosY(), part.controller.CorePosZ());
	}

	@Override
	public void handleMouseInput() throws IOException {
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
		part.storyboardManager.stop();

		PanelButtonList.clear();
		ySize = mc.displayHeight;


		GuiToggleButton addPanelBase = new GuiToggleButton(0, width/6-30, height - 24, 60, 18,
				"New Panel", "New Panel",
				() -> isActiveNewPanel,
				isOn -> {
					isActiveNewPanel = isOn;
					if(isOn) Canvas.ActiveGroup(GuiGroup_AddPanel);
					else Canvas.DisableGroup(GuiGroup_AddPanel);
					CloseInspector();
		});
		int guiGroup_Def = 0;
		Canvas.Register(guiGroup_Def, addPanelBase);
		AddNewBaseButton = addPanelBase;

		currentPanelPointer = new GuiImage(current, 2, -18, 37, 37);
		Canvas.Register(GuiGroup_Timeline, currentPanelPointer);

		GuiUtil.addButton2(Canvas, guiGroup_Def, 0, 40,
				() -> ScaleTimeline(-0.25f),
				() -> ScaleTimeline(0.25f));
		Canvas.Register(guiGroup_Def,
				new GuiButtonWrapper(0, 60, 40, 60, 12, _Core.I18n("gui.core.text.reset"),
						() -> {
							TimelineScale = 1;
							TimelineScaleTemp = 1;
						}
				));

		Canvas.Register(GuiGroup_AddPanel, new GuiLabel("TEST", fontRenderer, 0, 0, -1));

		GuiImage newpanelBase = new GuiImage(AddNewBase, 0, 0, 63, 148);
		Canvas.Register(GuiGroup_AddPanel, newpanelBase);
		CustomTexButton addPanel;
		addPanel = new CustomTexButton(set,10, 10, 20, 20, ()->AddNewPanel(new SetValuePanel(), false));
		Canvas.Register(GuiGroup_AddPanel ,addPanel);
		addPanel = new CustomTexButton(keyframe, 10, 32, 20, 20, ()->AddNewPanel(new KeyFramePanel(), false));
		Canvas.Register(GuiGroup_AddPanel, addPanel);
		addPanel = new CustomTexButton(sound, 10, 54, 20, 20, ()->AddNewPanel(new SoundPanel(), false));
		Canvas.Register(GuiGroup_AddPanel, addPanel);
		addPanel = new CustomTexButton(timer,  32, 10, 20, 20, ()->AddNewPanel(new TimerPanel(), false));
		Canvas.Register(GuiGroup_AddPanel, addPanel);
		addPanel = new CustomTexButton(wait,32, 32, 20, 20, ()->AddNewPanel(new WaitPanel(), false));
		Canvas.Register(GuiGroup_AddPanel, addPanel);
		addPanel = new CustomTexButton(loop,32, 54, 20, 20, ()->AddNewPanel(new LoopPanel(), false));
		Canvas.Register(GuiGroup_AddPanel, addPanel);
		addPanel = new CustomTexButton(notify,32, 76, 20, 20, ()->AddNewPanel(new NotifyPanel(), false));
		Canvas.Register(GuiGroup_AddPanel, addPanel);
		Canvas.MoveGroup(GuiGroup_AddPanel, width/6-31, height - 174, false);


		// Inspector
		MakeInspector(Mode.set, new SetValuePanel(), ButtonListSet);
		MakeInspector(Mode.timer, new TimerPanel(), ButtonListTimer);
		MakeInspector(Mode.keyframe, new KeyFramePanel(), ButtonListKeyFrame);
		MakeInspector(Mode.loop, new LoopPanel(), ButtonListLoop);
		MakeInspector(Mode.wait, new WaitPanel(), ButtonListWait);
		MakeInspector(Mode.notify, new NotifyPanel(), ButtonListNotify);
		MakeInspector(Mode.sound, new SoundPanel(), ButtonListSound	);



		GuiToggleButton OpenPreset = new GuiToggleButton(0, width*3/6-30, height - 24, 60, 18,
				_Core.I18n("gui.core.story.preset"),
				_Core.I18n("gui.core.story.preset"),
				() -> isActivePresetPanel,
				isOn -> {
					isActivePresetPanel = isOn;
					if(isOn) Canvas.ActiveGroup(GuiGroup_Preset);
					else Canvas.DisableGroup(GuiGroup_Preset);
					CloseInspector();
				});
		Canvas.Register(guiGroup_Def, OpenPreset);

		// Preset
		GuiImage presetBase = new GuiImage(AddNewBase, 0, 0, 120, 148);
		Canvas.Register(GuiGroup_Preset, presetBase);
		GuiButtonWrapper preset;
		preset = new GuiButtonWrapper(0,10, 10, 100, 16,
				_Core.I18n("gui.core.story.preset.roundtrip"),
				() -> {
					DeleteAllPanel();
					AddNewPanel(new WaitPanel(0), true);
					AddNewPanel(new KeyFramePanel("Position", "Set", "Linear", 90, 20, false), true);
					AddNewPanel(new WaitPanel(0), true);
					AddNewPanel(new KeyFramePanel("Position", "Set", "Linear", 0, 20, false), true);
					FormatPanelButtonPosition();
				});
		Canvas.Register(GuiGroup_Preset ,preset);
		preset = new GuiButtonWrapper(0,10, 30, 100, 16,
				_Core.I18n("gui.core.story.preset.autoroundtrip"),
				() -> {
					DeleteAllPanel();
					AddNewPanel(new KeyFramePanel("Position", "Set", "Linear", 90, 20, false), true);
					AddNewPanel(new KeyFramePanel("Position", "Set", "Linear", 0, 20, false), true);
					FormatPanelButtonPosition();
				});
		Canvas.Register(GuiGroup_Preset ,preset);
		preset = new GuiButtonWrapper(0,10, 50, 100, 16,
				_Core.I18n("gui.core.story.preset.step"),
				() -> {
					DeleteAllPanel();
					AddNewPanel(new WaitPanel(0), true);
					AddNewPanel(new KeyFramePanel("Position", "Add", "Linear", 90, 20, false), true);
					FormatPanelButtonPosition();
				});
		Canvas.Register(GuiGroup_Preset ,preset);
		preset = new GuiButtonWrapper(0,10, 70, 100, 16,
				_Core.I18n("gui.core.story.preset.clock"),
				() -> {
					DeleteAllPanel();
					AddNewPanel(new TimerPanel(20), true);
					AddNewPanel(new SetValuePanel("Position", "Add", 10), true);
					FormatPanelButtonPosition();
				});
		Canvas.Register(GuiGroup_Preset ,preset);
		preset = new GuiButtonWrapper(0,10, 90, 100, 16,
				_Core.I18n("gui.core.story.preset.loop"),
				() -> {
					DeleteAllPanel();
					LoopPanel loop = new LoopPanel(4);
					AddNewPanel(loop, true);
					{
						AddNewPanel(new TimerPanel(5), true);
						AddNewPanel(new KeyFramePanel("Position", "Add", "Linear", 40, 5, false), true);
					}
					AddNewPanel(new LoopPanel.LoopEndPanel(loop), true);
					AddNewPanel(new KeyFramePanel("Position", "Set", "OutSpring", 0, 30, false), true);
					FormatPanelButtonPosition();
				});
		Canvas.Register(GuiGroup_Preset ,preset);

		Canvas.MoveGroup(GuiGroup_Preset, width/2-60, height - 174, false);


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
		Canvas.Register(guiGroup_Def, copy);
		Canvas.Register(guiGroup_Def, paste);



		//現在のストーリーを適用
		GuiButtonWrapper Activate = new GuiButtonWrapper(0,
				width/2-30, 40,
				60, 18,
				"Activate", this::updateToServer);
		Canvas.Register(GuiGroup_Activate, Activate);

		activationStatus = new GuiColorPanel(
				0xD000ff00, 0, 38, width, 40);
		Canvas.Register(guiGroup_Def, activationStatus);

		//selections
		int selectorNum = 10;
		selectorButtons = new GuiButtonWrapper[selectorNum];
		for(int i = 0; i < selectorNum; ++i)
		{
			int idx = i;
			GuiButtonWrapper selector = new GuiButtonWrapper(0, 0, idx * 19, 60, 18, "Paste",
					() -> {
						Canvas.DisableGroup(GuiGroup_Selections);
						currentEditPanel.panel.setSelection(currentSelectionIndex, idx);
						if(currentSelectionButton!=null)currentSelectionButton.displayString = currentEditPanel.panel.getValueString(currentSelectionIndex);
						SetDirty();
			});
			selectorButtons[i] = selector;
			Canvas.Register(GuiGroup_Selections, selector);
		}


		for(IProgramPanel panel : this.part.storyboardManager.getPanelList())
		{
			AddNewPanel(panel, true);
		}
		FormatPanelButtonPosition();
		ClearDirty();

		if(PanelButtonList.isEmpty())
		{
			AddNewBaseButton.SetState(true);
			Canvas.ActiveGroup(GuiGroup_AddPanel);
			isActiveNewPanel = true;
		}
		Canvas.ActiveGroup(guiGroup_Def);
		Canvas.MoveGroup(GuiGroup_Timeline, 10, 10, false);
		Canvas.ActiveGroup(GuiGroup_Timeline);
    }

    private void MakeInspector(Mode mode, IProgramPanel panel, ArrayList<GUIStoryBoardSettingPart> partList)
	{
		int x = 5, y = 10;
		int groupId = mode.GetIdx() + GuiGroup_InspectorBase;
		Canvas.Register(groupId, new GuiImage(Inspector, 0, y, 80, 200));
		y += 20;

		CustomTexButton close = new CustomTexButton(cancelTex,-13, 150, 15, 15, this::DeletePanel);
		Canvas.Register(groupId, close);

		for(int i = 0; i < panel.ApiNum(); ++i)
		{
			int apiIdx = i;
			GuiLabel label = null;
			GUIStoryBoardSettingPart part = new GUIStoryBoardSettingPart(panel.getType(apiIdx), apiIdx, panel.getDescription(apiIdx));
			switch(panel.getType(i))
			{
				case change:
					label = new GuiLabel(panel.getDescription(i), fontRenderer, x, y, 0xffffff);
					GuiButtonWrapper button = new GuiButtonWrapper(0, x, y+8, 69, 14, "",null);
					Canvas.Register(groupId, button);
					button.SetAction(()->OpenSelection(currentEditPanel.panel, apiIdx, button));
					part.SetButton(button);
					break;

				case inputValue:
					label = new GuiDragChangerLabel(panel.getDescription(i), fontRenderer, x, y, 0xffffff,
							d->currentEditPanel.panel.addValue(apiIdx, d),
							this::SetDirty);
					GuiFormattedTextField field = new GuiFormattedTextField(0, fontRenderer, x, y+8, 69, 14, 0xffffff, 12,
							() -> (currentEditPanel!=null) ? currentEditPanel.panel.getValueString(apiIdx) : "",
							s -> s.matches(GuiFormattedTextField.regexNumber),
							t -> {currentEditPanel.panel.setValue(apiIdx, Double.parseDouble(t)); SetDirty();});
					Canvas.Register(groupId, field);
					field.setEnableBackgroundDrawing(true);
					part.SetTextField(field);
					break;

//				case soundSelector:
//					label = new GuiLabel(panel.getDescription(i), fontRenderer, x, y, 0xffffff);
//					break;
			}
			part.SetLabel(label);
			Canvas.Register(groupId, label);

			y += 16+8;
			partList.add(part);
		}
		Canvas.DisableGroup(groupId);
	}

    private void AddNewPanel(IProgramPanel panel, boolean isInit)
	{
		ResourceLocation tex = PanelModeToTex(panel.getMode());
		GuiStoryPartPanel btn;
		boolean isEnd = panel.getMode()==Mode.loopend;
		btn = new GuiStoryPartPanel(tex, this, panel,
				Canvas.GetGroupInfo(GuiGroup_Timeline), panel.getMode()==Mode.loop,
				0, 0, isEnd?10:20, 20,
				isEnd ? p->{} : this::OpenOrCloseInspector);

		Canvas.Register(GuiGroup_Timeline, btn);

		if(currentEditPanel == null) this.PanelButtonList.add(btn);
		else
		{
			int idx = PanelButtonList.indexOf(currentEditPanel);
			if(idx > 1) PanelButtonList.add(idx, btn);
		}

//		if(maxPanelCount < PanelButtonList.size() && !isEnd)
//		{
//			maxPanelCount++;
//			GuiLabel label = new GuiLabel(""+maxPanelCount, fontRenderer, 0, -5, 0xffffff);
//			Canvas.Register(GuiGroup_Timeline, label);
//			TimeLineIndexLabel.add(label);
//		}

		if(!isInit && panel.getMode() == Mode.loop)
		{
			AddNewPanel(((LoopPanel)panel).GetLoopEndPanel(), false);
		}

		if(!isInit) FormatPanelButtonPosition();
		SetDirty();
	}

	private void DeletePanel()
	{
		if(currentEditPanel == null)return;
		if(currentEditPanel.panel.getMode()==Mode.loopend)return;
		PanelButtonList.remove(currentEditPanel);
		Canvas.DeleteElement(GuiGroup_Timeline, currentEditPanel);

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
		Canvas.DisableGroup(currentInspectorGroupId);
		SetDirty();
	}

	private void DeleteAllPanel() {
		for (GuiStoryPartPanel panel : PanelButtonList) {
			Canvas.DeleteElement(GuiGroup_Timeline, panel);
		}
		PanelButtonList.clear();

		SetDirty();
	}

	private void OpenOrCloseInspector(GuiStoryPartPanel panel)
	{
		if(currentEditPanel == panel) {
			Canvas.DisableGroup(currentInspectorGroupId);
			currentInspectorGroupId = -1;
			currentEditPanel = null;
		}
		else {
			currentEditPanel = panel;
			if (currentInspectorGroupId >= GuiGroup_InspectorBase) Canvas.DisableGroup(currentInspectorGroupId);
			currentInspectorGroupId = panel.panel.getMode().GetIdx() + GuiGroup_InspectorBase;
			GuiGroupCanvas.Group g = Canvas.GetGroupInfo(GuiGroup_Timeline);
			int x = (int)((panel.GetPositionX() + 10) / g.scale.x);
			int y = (int)((panel.GetPositionY() + 25) / g.scale.y);
			Canvas.ActiveGroup(currentInspectorGroupId);
			Canvas.MoveGroup(currentInspectorGroupId, x, y, false);
			changeSelectionButtonDisplay(panel.panel.getMode());
		}
		isActiveNewPanel = false;
		isActivePresetPanel = false;
		Canvas.DisableGroup(GuiGroup_AddPanel);
		Canvas.DisableGroup(GuiGroup_Preset);
		Canvas.DisableGroup(GuiGroup_Selections);
		AddNewBaseButton.SetState(false);
	}

	private void CloseInspector()
	{
		if(currentInspectorGroupId > GuiGroup_InspectorBase) {
			Canvas.DisableGroup(currentInspectorGroupId);
			currentInspectorGroupId = -1;
			currentEditPanel = null;
		}
	}

	private void OpenSelection(IProgramPanel panel, int apiIdx, GuiButtonWrapper button)
	{
		currentSelectionButton = button;
		currentSelectionIndex = apiIdx;
		GuiGroupCanvas.Group info = Canvas.GetGroupInfo(currentInspectorGroupId);
		Canvas.ActiveGroup(GuiGroup_Selections);
		String[] datas = panel.GetSelectionLabels(apiIdx);
		int x = info.offsetX + 90;
		int y = info.offsetY + 12 + 24 * apiIdx;
		y = Math.Clamp(y, 10, height - datas.length * 19);
		Canvas.MoveGroup(GuiGroup_Selections, x, y, false);
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
		button.displayString = panel.getValueString(apiIdx);
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
		case loopend : return loopEnd;
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
		Canvas.DisableGroup(GuiGroup_AddPanel);
		Canvas.DisableGroup(GuiGroup_Preset);
		Canvas.DisableGroup(GuiGroup_Selections);
		isActivePresetPanel = false;
		isActiveNewPanel = false;

    	StoryBoardManager manager = part.storyboardManager;
    	manager.clear();
    	for(GuiStoryPartPanel panel : PanelButtonList)
    	{
			if(!panel.panel.CanUseWith(this.part)) continue;
			manager.addPanel(panel.panel);
    	}
//    	byte[] serialArray = manager.ToSerialCode().getBytes();
//    	int flag = MessageFerrisMisc.GUIStoryBoardSendData;
//    	MFW_Logger.debugInfo(part.getStoryBoardManager().ToSerialCode());
//    	MessageFerrisMisc packet = new MessageFerrisMisc(blockPosX, blockPosY, blockPosZ, flag, 0, 0, serialArray);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("storyboard", manager.ToSerialCode());
		MFW_PacketHandler.INSTANCE.sendToServer(new MessageSyncNbtForMFWCtS(part, nbt));
		ClearDirty();
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
		int offsetX = 10;
		int offsetY = -10;
		int labelIdx = 0;
		Stack<GuiStoryPartPanel> loopPanelStack = new Stack<>();
		for (GuiStoryPartPanel button : PanelButtonList) {
			Mode mode = button.panel.getMode();
			int panelWidth = (mode == Mode.loopend) ? 10 : 20;
			if (mode == Mode.loop) loopPanelStack.push(button);
			if (mode == Mode.loopend) {
				offsetY -= 2;
				if (loopPanelStack.size() > 0) loopPanelStack.pop().SetEndPanel(button, loopPanelStack.size());
			}
			if (button != Canvas.GetDraggingElement()) {
				button.x = offsetX;
				button.y = offsetY;
			} else {
				startPos = offsetX;
			}

			if (mode != Mode.loopend) {
				if (labelIdx < TimeLineIndexLabel.size()) {
					GuiLabel label = TimeLineIndexLabel.get(labelIdx);
					label.SetPosition(offsetX, -17);
				} else {
					GuiLabel label = new GuiLabel("" + (labelIdx + 1), fontRenderer, 0, -5, 0xffffff);
					Canvas.Register(GuiGroup_Timeline, label);
					TimeLineIndexLabel.add(label);
					label.SetPosition(offsetX, -17);
				}
				labelIdx++;
			}

			offsetX += panelWidth + 4;
			if (mode == Mode.loop) offsetY += 2;
		}
		while(TimeLineIndexLabel.size() > labelIdx)
		{
			int last = TimeLineIndexLabel.size()-1;
			GuiLabel l = TimeLineIndexLabel.get(last);
			Canvas.DeleteElement(GuiGroup_Timeline, l);
			TimeLineIndexLabel.remove(last);
		}

		TimeLineLength = offsetX;
	}

	private int startPos;
	private void DragMotion()
	{
		IGuiDraggable drag = Canvas.GetDraggingElement();
		if(drag != null)
		{
			int orgX = (startPos < 0) ? drag.GetStartPos() : startPos;
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
			if(part.buttonId == buttonid)return part;
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

		TimelineScale = (float)Math.Lerp(0.1, TimelineScale, TimelineScaleTemp);
		Canvas.SetScale(GuiGroup_Timeline, TimelineScale, TimelineScale, TimelineScale, false);

		TimelineScroll = (float)Math.Lerp(0.1f, TimelineScroll, TimelineScrollTemp);
		Canvas.MoveGroup(GuiGroup_Timeline, (int)-TimelineScroll, 20, false);

        int idx = part.storyboardManager.getCurrentTargetIndex()-1;
        if(!isDirty && 0 <= idx && idx < PanelButtonList.size())
		{
			GuiStoryPartPanel partPanel = PanelButtonList.get(idx);
			currentPanelPointer.SetPosition(partPanel.x-8, partPanel.y-8);
		}
		else
		{
			currentPanelPointer.SetPosition(-1000, -1000);
		}
    }
 

    /*GUIが開いている時にゲームの処理を止めるかどうか。*/
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    

	@Override
	protected void mouseClickMove(int x, int y, int event, long time)
	{
		super.mouseClickMove(x, y, event, time);
		DragMotion();
	}

	@Override
	protected void mouseReleased(int x, int y, int state)
	{
		super.mouseReleased(x, y, state);
		boolean onUp = state == 0 || state == 1;
		if(drag != null && onUp)
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


	private void SetDirty()
	{
		isDirty = true;
		activationStatus.SetColor(0xD0ff8822);
		Canvas.ActiveGroup(GuiGroup_Activate);
	}
	private void ClearDirty()
	{
		isDirty = false;
		activationStatus.SetColor(0xD000ff00);
		Canvas.DisableGroup(GuiGroup_Activate);
	}

	@Override
	protected void Close()
	{
		super.Close();
		GUIHandler.OpenBlockModelGuiInClient(part);
	}
}