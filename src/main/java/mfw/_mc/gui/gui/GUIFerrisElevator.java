package mfw._mc.gui.gui;

import mfw.ferriswheel.FerrisElevator;
import mfw.manager.SyncTargetRegisterManager;
import mochisystems._mc.gui.GuiToggleButton;
import mfw.message.MessageFerrisMisc;
import mfw.sound.SoundManager;
import mochisystems.util.gui.GuiButtonWrapper;
import mochisystems.util.gui.GuiUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import static mfw.message.MessageFerrisMisc.*;

public class GUIFerrisElevator extends GUIFerrisCoreBase {

	private FerrisElevator elevator;

    private GuiButton StartStopButton;

	public GUIFerrisElevator(int x, int y, int z, InventoryPlayer playerInventory, FerrisElevator elevator)
	{
		super(x, y, z, playerInventory, elevator);
		this.elevator = elevator;
	}

    private final int ButtonGroup_Main = 0;
    private final int ButtonGroup_Accel = 1;
    private final int ButtonGroup_AmpPhase = 2;
    private final int ButtonGroup_Weight = 3;
    private final int ButtonGroup_Sync = 4;
    private final int ButtonGroup_isSync = 5;
    private final int ButtonGroup_RsMode = 6;
    private final int ButtonGroup_StoryBoard = 7;
    private final int ButtonGroup_isStory = 8;
    private final int ButtonGroup_isPendulum = 9;

	// GUIを開くたび呼ばれる初期化関数 WindowSize変更でも呼ばれる
	@Override
	public void initGui()
	{
		super.initGui();

		int gDef = -1;

        GUIFerrisWheel.Scale(Canvas, fontRendererObj, width-70, 0, elevator, gDef,-1,
                () -> String.format("%4.1f", elevator.localScale),
                t -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetSize, t));

        GUIFerrisWheel.Rotate(Canvas, fontRendererObj, width-70, 0, elevator, gDef, MessageFerrisMisc.GUICoreRot1, MessageFerrisMisc.GUICoreRot2,
                () -> String.format("%4.1f", elevator.pitch),
                t -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetSetPitch, t),
                () -> String.format("%4.1f", elevator.yaw),
                t -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetSetYaw, t));

        GUIFerrisWheel.Offset(Canvas, fontRendererObj, width-70, 0, elevator,
                gDef, MessageFerrisMisc.GUICoreOffsetX, MessageFerrisMisc.GUICoreOffsetY, MessageFerrisMisc.GUICoreOffsetZ,
                () -> String.format("%4.2f", elevator.offset.x),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreOffsetX, v),
                () -> String.format("%4.2f", elevator.offset.y),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreOffsetY, v),
                () -> String.format("%4.2f", elevator.offset.z),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreOffsetZ, v));

        GUIFerrisWheel.Offset(Canvas, fontRendererObj, width-70, 0, elevator,
                gDef, MessageFerrisMisc.GUICoreOffsetX, MessageFerrisMisc.GUICoreOffsetY, MessageFerrisMisc.GUICoreOffsetZ,
                () -> String.format("%4.2f", elevator.offset.x),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreOffsetX, v),
                () -> String.format("%4.2f", elevator.offset.y),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreOffsetY, v),
                () -> String.format("%4.2f", elevator.offset.z),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreOffsetZ, v));

        GUIFerrisWheel.AmpPhase(Canvas, fontRendererObj, ButtonGroup_AmpPhase, width, height, elevator,
                () -> String.format("%4.1f", elevator.syncAmplitude.get()),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetAmp, v),
                () -> String.format("%4.1f", elevator.syncPhase.get()),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetPhase, v));

        GUIFerrisWheel.Regist(Canvas, fontRendererObj, ButtonGroup_AmpPhase, width, height, elevator,
                () -> String.format("%4.2f", 1/elevator.resist),
                v -> SendMessageToSetParam(GUICoreSetResist, 1/v));

        Canvas.Register(gDef, new GuiButtonWrapper(0, width - 48, 84, 42, 14, "reset",
                () -> SendMessageToSetParam(GUICoreRotReset, 0)));

        GuiUtil.addButton2(Canvas, gDef,-60, 64, "Sound", MessageFerrisMisc.GUICoreSoundSelectUp,
                () -> SendMessageToSetParam(GUICoreSoundSelectUp, 0),
                () -> SendMessageToSetParam(GUICoreSoundSelectDown, 0));

        Canvas.Register(gDef,
                new GuiToggleButton(0,  width - 122, 2, 40, 13, "core", "core",
                        () -> elevator.ShouldDrawCore(),
                        isOn -> SendMessageToSetParam(GUICoreToggleDrawCore, 0)));

        Canvas.Register(gDef,
                new GuiToggleButton(0,  width - 76, 68, 70, 13, "pos only", "pos only",
                        () -> part.GetIsForrowParentTransform(),
                        isOn -> SendMessageToSetParam(GUICoreToggleForrowTransform, 0)));


        Canvas.Register(ButtonGroup_Main,
                new GuiToggleButton(0,  170, height-52, 28, 28,  "||", "|>",
                        () -> elevator.stopFlag,
                        isOn -> SendMessageToSetParam(GUICoreStop, 0)));

        Canvas.Register(ButtonGroup_Main,
                new GuiButtonWrapper(0, 204, height-52, 28, 28, "Rev",
                        () -> SendMessageToSetParam(GUICoreTurn, 0)));

        GUIFerrisWheel.Accel(Canvas, fontRendererObj, ButtonGroup_Accel, height, elevator,
                () -> String.format("%4.1f", elevator.GetAccel()),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetAccel, v));


        Canvas.Register(ButtonGroup_Sync,
                new GuiButtonWrapper(0, 170, height - 52, 36, 34, "SaveParent",
                        () -> SyncTargetRegisterManager.INSTANCE.Save(part.GetMyAddress())));
        Canvas.Register(ButtonGroup_Sync,
                new GuiButtonWrapper(0, 210, height - 52, 50, 13, "register",
                        this::RegisterSyncParent));
        Canvas.Register(ButtonGroup_Sync,
                new GuiButtonWrapper(0, 210, height - 32, 50, 13, "clear",
                        () -> SendMessageToSetParam(GUICoreSyncClear, 0)));
        Canvas.Register(ButtonGroup_Sync,
                new GuiButtonWrapper(0, 265, height - 52, 70, 13, StatCollector.translateToLocal("gui.core.switch.syncmode"),
                        () -> SendMessageToSetParam(GUICoreSyncMode, 0)));


        Canvas.Register(ButtonGroup_RsMode,
                new GuiButtonWrapper(0, width - 76, 106, 70, 13,
                        StatCollector.translateToLocal("gui.core.switch.rsmode"),
                        () -> SendMessageToSetParam(GUICoreRSFlagRotate, 0)));

        Canvas.Register(ButtonGroup_StoryBoard,
                new GuiButtonWrapper(0, 210, height-52, 140, 28, "StoryBoard",
                        () -> SendMessageToSetParam(GUIOpenStoryBoard, 0)));


        String text = StatCollector.translateToLocal("gui.core.isEnableSyncTarget");
        Canvas.Register(ButtonGroup_isSync,
                new GuiToggleButton(0,   width - 160, height - 75,  70, 13,  text, text,
                        () -> elevator.isEnableSync,
                        isOn -> {
                            elevator.toggleSyncFlag();
                            if(isOn) ChangeUIForSync();
                            else ChangeUIForNormal();
                            SendMessageToSetParam(GUICoreSyncTargetChange, 0);
                        }));

        text = StatCollector.translateToLocal("gui.core.isEnableStoryBoard");
        Canvas.Register(ButtonGroup_isStory,
                new GuiToggleButton(0,    width - 340, height - 75, 70, 13,  text, text,
                        () -> elevator.isEnableStoryBoard,
                        isOn -> {
                            if(isOn) ChangeUIForStoryboard();
                            else ChangeUIForNormal();
                            SendMessageToSetParam(GUICoreStoryBoardChange, 0);
                        }));

        //postinit
        if(elevator.isEnableSync) ChangeUIForSync();
        else if(elevator.isEnableStoryBoard) ChangeUIForStoryboard();
        else ChangeUIForNormal();
		stringRSMode = getRSModeDescription(elevator.rsFlag);
        SetEnableOpenChildButtonByID();
    }


    private void ChangeUIForNormal()
    {
        Canvas.ActiveGroup(ButtonGroup_Main);
        Canvas.ActiveGroup(ButtonGroup_Accel);
        Canvas.ActiveGroup(ButtonGroup_Weight);
        Canvas.DisableGroup(ButtonGroup_AmpPhase);
        Canvas.DisableGroup(ButtonGroup_Sync);
        Canvas.ActiveGroup(ButtonGroup_isPendulum);
        Canvas.ActiveGroup(ButtonGroup_isSync);
        Canvas.ActiveGroup(ButtonGroup_isStory);
        Canvas.DisableGroup(ButtonGroup_StoryBoard);
        Canvas.ActiveGroup(ButtonGroup_RsMode);
    }

    private void ChangeUIForSync()
    {
        Canvas.DisableGroup(ButtonGroup_Main);
        Canvas.DisableGroup(ButtonGroup_Accel);
        Canvas.DisableGroup(ButtonGroup_Weight);
        Canvas.ActiveGroup(ButtonGroup_AmpPhase);
        Canvas.ActiveGroup(ButtonGroup_Sync);
        Canvas.ActiveGroup(ButtonGroup_isPendulum);
        Canvas.ActiveGroup(ButtonGroup_isSync);
        Canvas.DisableGroup(ButtonGroup_isStory);
        Canvas.DisableGroup(ButtonGroup_StoryBoard);
        Canvas.DisableGroup(ButtonGroup_RsMode);
    }

    private void ChangeUIForStoryboard()
    {
        Canvas.DisableGroup(ButtonGroup_Main);
        Canvas.DisableGroup(ButtonGroup_Accel);
        Canvas.DisableGroup(ButtonGroup_Weight);
        Canvas.DisableGroup(ButtonGroup_AmpPhase);
        Canvas.DisableGroup(ButtonGroup_Sync);
        Canvas.DisableGroup(ButtonGroup_isPendulum);
        Canvas.DisableGroup(ButtonGroup_isSync);
        Canvas.ActiveGroup(ButtonGroup_isStory);
        Canvas.ActiveGroup(ButtonGroup_StoryBoard);
        Canvas.DisableGroup(ButtonGroup_RsMode);
    }

//    private void ChangeButton_ChangeRSMode()  //RSフラグ用ボタンの名前かえるだけ用
//    {
////    	GuiButtonExt bt = ((GuiButtonExt)buttonList.get(rsRotateButtonID));
////		bt.displayString = elevator.getSRTitleStringForGUI(elevator.rsFlag + 1);
//		stringRSMode = getRSModeDescription(elevator.rsFlag + 1);
//    }

    
	/*GUIの文字等の描画処理*/
    //String stringRotMode;
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        Canvas.Update();
        drawCenteredString(fontRendererObj, elevator.GetName(),width/2,4,0xffffff);
//        if(!elevator.isEnableStoryBoard && !elevator.isEnableSync) {
//            drawString(fontRendererObj, "Regist :", 240, height - 58, 0xffffff);
//            drawString(fontRendererObj, "Weight :", 240, height - 38, 0xffffff);
//        }
//
//        if(elevator.isEnableSync && !elevator.isEnableStoryBoard) {
//            drawString(fontRendererObj, "Amp :", width - 80, height - 44, 0xffffff);
//            drawString(fontRendererObj, "Phase", width - 80, height - 24, 0xffffff);
//        }

        drawString(fontRendererObj, "Size :", width - 75, 4, 0xffffff);
        drawString(fontRendererObj, "pitch :", width - 75, 24, 0xffffff);
        drawString(fontRendererObj, "yaw  :", width - 75, 44, 0xffffff);

        drawString(fontRendererObj, "x :", width - 75, 134, 0xffffff);
        drawString(fontRendererObj, "y :", width - 75, 154, 0xffffff);
        drawString(fontRendererObj, "z :", width - 75, 174, 0xffffff);

        //debug
        drawRightedString(this.fontRendererObj, stringRSMode, width - 6, 121, 0xffffff);
        drawRightedString(this.fontRendererObj, String.format("Input RS Power : %1.2f", elevator.getRSPower()), 260, -10, 0xffffff);
    
        drawRightedString(this.fontRendererObj, SoundManager.sounds.get(elevator.soundManager.GetSoundIndex()), -0, 79, 0xffffff);

        if(elevator.isEnableSync)
        {
            drawCenteredString(this.fontRendererObj,
            		elevator.isSyncTargetSpeed ? StatCollector.translateToLocal("gui.core.SyncTarget.speed")
            								: StatCollector.translateToLocal("gui.core.SyncTarget.rot"),
            				315, height - 35, 0xffffff);
            drawCenteredString(this.fontRendererObj,
	        		elevator.syncTarget.isSyncing() ? StatCollector.translateToLocal("gui.core.Sync.Parentregistered")
	        								: StatCollector.translateToLocal("gui.core.Sync.ParentUnregistered"),
	        				width - 123, height - 85, 0xffffff);
        }
    }


	public FontRenderer GetFontRenderer(){
		return fontRendererObj;
	}


}