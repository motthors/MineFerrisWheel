package mfw._mc.gui.gui;

import mfw.ferriswheel.FerrisWheel;
import mfw.ferriswheel.IFerrisParamGetter;
import mfw.manager.SyncTargetRegisterManager;
import mochisystems._mc.gui.GuiToggleButton;
import mfw.message.MessageFerrisMisc;
import mfw.sound.SoundManager;
import mochisystems.util.gui.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static mfw.message.MessageFerrisMisc.*;

public class GUIFerrisWheel extends GUIFerrisCoreBase {

	private FerrisWheel wheel;

	public GUIFerrisWheel(int x, int y, int z, InventoryPlayer playerInventory, FerrisWheel wheel)
	{
		super(x, y, z, playerInventory, wheel);
		this.wheel = wheel;
	}

    private int gDef = -1;
    private int ButtonGroup_Main = 0;
    private int ButtonGroup_Accel = 1;
    private int ButtonGroup_AmpPhase = 2;
    private int ButtonGroup_Weight = 3;
    private int ButtonGroup_Sync = 4;
    private int ButtonGroup_isSync = 5;
    private int ButtonGroup_RsMode = 6;
    private int ButtonGroup_StoryBoard = 7;
    private int ButtonGroup_isStory = 8;
    private int ButtonGroup_isPendulum = 9;

    // GUIを開くたび呼ばれる初期化関数 WindowSize変更でも呼ばれる
    @Override
    public void initGui()
    {
        super.initGui();

        Canvas.Register(gDef, new GuiButtonWrapper(0, width - 48, 84, 42, 14, "reset",
                () -> SendMessageToSetParam(GUICoreRotReset, 0)));

//        GuiUtil.addButton1(Canvas, buttonList, -1,40, 13, width - 122, 2, "core", MessageFerrisMisc.GUICoreToggleDrawCore, true, wheel.ShouldDrawCore(), "core");
        Canvas.Register(gDef,
                new GuiToggleButton(0,  width - 122, 2, 40, 13, "core", "core",
                () -> wheel.ShouldDrawCore(),
                isOn -> SendMessageToSetParam(GUICoreToggleDrawCore, 0)));


//        GuiUtil.addButton1(Canvas, buttonList, -1,46, 13, width - 128, 17, "collider", MessageFerrisMisc.GUICoreToggleEnableCollider, true, wheel.IsEnableCollider(), "collider");
        Canvas.Register(gDef,
                new GuiToggleButton(0,  width - 128, 17, 46, 13, "collider", "collider",
                        () -> wheel.IsEnableCollider(),
                        isOn -> SendMessageToSetParam(GUICoreToggleEnableCollider, 0)));


//        GuiUtil.addButton1(Canvas, buttonList, -1,70, 13, width - 76, 68, "pos only",
//                MessageFerrisMisc.GUICoreToggleForrowTransform, true, !part.GetIsForrowParentTransform(), "pos only");
        Canvas.Register(gDef,
                new GuiToggleButton(0,  width - 76, 68, 70, 13, "pos only", "pos only",
                        () -> part.GetIsForrowParentTransform(),
                        isOn -> SendMessageToSetParam(GUICoreToggleForrowTransform, 0)));


        GuiUtil.addButton2(Canvas, gDef,-60, 64, "Sound", MessageFerrisMisc.GUICoreSoundSelectUp,
                () -> SendMessageToSetParam(GUICoreSoundSelectUp, 0),
                () -> SendMessageToSetParam(GUICoreSoundSelectDown, 0));


        Scale(Canvas, fontRendererObj, width - 70, 3,
                wheel, gDef,-1,
                () -> String.format("%4.1f", wheel.localScale),
                t -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetSize, t));

        Rotate(Canvas, fontRendererObj, width - 70, 25,
                wheel, gDef, MessageFerrisMisc.GUICoreRot1, MessageFerrisMisc.GUICoreRot2,
                () -> String.format("%4.1f", wheel.pitch),
                t -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetSetPitch, t),
                () -> String.format("%4.1f", wheel.yaw),
                t -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetSetYaw, t));

        Offset(Canvas, fontRendererObj, width - 70, 134,
                wheel, gDef, GUICoreOffsetX, GUICoreOffsetY, GUICoreOffsetZ,
                () -> String.format("%4.2f", wheel.offset.x),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreOffsetX, v),
                () -> String.format("%4.2f", wheel.offset.y),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreOffsetY, v),
                () -> String.format("%4.2f", wheel.offset.z),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreOffsetZ, v));

//        通常  : StopRev Regist          Weight      rs            isSync isStory isPendulum
//        Sin   : StopRev Regist AmpPhase Weight      rs　　　　　　isSync isStory isPendulum
//        Sync　:               AmpPhase        sync  　   　　　　isSync
//        Story :　　　　　　　　　　　　　　　　　　   StoryBoard        isStory


        Canvas.Register(ButtonGroup_Main,
                new GuiToggleButton(0,  170, height-52, 28, 28,  "||", "|>",
                        () -> !wheel.stopFlag,
                        isOn -> SendMessageToSetParam(GUICoreStop, 0)));


        Canvas.Register(ButtonGroup_Main,
                new GuiButtonWrapper(0, 204, height-52, 28, 28, "Rev",
                () -> SendMessageToSetParam(GUICoreTurn, 0)));


        Accel(Canvas, fontRendererObj, ButtonGroup_Accel, height, wheel,
                () -> String.format("%6.1f", wheel.GetAccel()),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetAccel, v));


        AmpPhase(Canvas, fontRendererObj, ButtonGroup_AmpPhase, width, height, wheel,
                () -> String.format("%6.1f", wheel.syncAmplitude.get()),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetAmp, v),
                () -> String.format("%6.1f", wheel.syncPhase.get()),
                v -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSetPhase, v));

        Regist(Canvas, fontRendererObj, ButtonGroup_Weight, width, height, wheel,
                () -> String.format("%6.2f", 1/wheel.rotResist),
                v -> SendMessageToSetParam(GUICoreSetResist, v));


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
                        () -> wheel.isEnableSync,
                        isOn -> {
                            wheel.toggleSyncFlag();
                            if(isOn) ChangeUIForSync();
                            else ChangeUIForNormal();
                            SendMessageToSetParam(GUICoreSyncTargetChange, 0);
                        }));

        text = StatCollector.translateToLocal("gui.core.isEnableStoryBoard");
        Canvas.Register(ButtonGroup_isStory,
                new GuiToggleButton(0,    width - 340, height - 75, 70, 13,  text, text,
                        () -> wheel.isEnableStoryBoard,
                        isOn -> {
                            if(isOn) ChangeUIForStoryboard();
                            else ChangeUIForNormal();
                            SendMessageToSetParam(GUICoreStoryBoardChange, 0);
                        }));

        text = StatCollector.translateToLocal("gui.core.isEnableSinConvert");
        Canvas.Register(ButtonGroup_isPendulum,
                new GuiToggleButton(0, width - 250, height - 75, 70, 13,  text, text,
                        () -> wheel.isEnableSinConvert,
                        isOn -> {
                            wheel.toggleSinConvertFlag();
                            if(isOn) ChangeUIForPendium();
                            else ChangeUIForNormal();
                            SendMessageToSetParam(GUICoreSinConvertChange, 0);
                        }));


        //postinit
        if(wheel.isEnableSync) ChangeUIForSync();
        else if(wheel.isEnableStoryBoard) ChangeUIForStoryboard();
        else if(wheel.isEnableSinConvert) ChangeUIForPendium();
        else ChangeUIForNormal();
        stringRSMode = getRSModeDescription(wheel.rsFlag);
        SetEnableOpenChildButtonByID();
    }


    public static void Scale(GuiGroupCanvas Canvas, FontRenderer fontRenderer,
                             int offsetX, int offsetY,
                             IFerrisParamGetter model,
                             int groupId, int MessageId,
                             Supplier<String> updateText, Consumer<Float> confirmed)
    {
        Canvas.Register(groupId, new GuiLabel("scale :", fontRenderer, offsetX - 5, offsetY, 0xffffff));

        Canvas.Register(groupId,
                new GuiFormatedTextField(fontRenderer, offsetX + 25, offsetY, 60, 10, 0xffffff, 12,
                        updateText,
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        (v) -> confirmed.accept(Float.parseFloat(v))));

        GuiUtil.addButton4(Canvas, groupId, offsetX, offsetY + 9, "Size", MessageId,
                () -> confirmed.accept(model.GetLocalScale()-0.1f),
                () -> confirmed.accept(model.GetLocalScale()-0.01f),
                () -> confirmed.accept(model.GetLocalScale()+0.01f),
                () -> confirmed.accept(model.GetLocalScale()+0.1f)
        );
    }
    public static void Rotate(GuiGroupCanvas Canvas, FontRenderer fontRenderer,
                              int offsetX, int offsetY,
                              IFerrisParamGetter model,
                              int groupId, int MessageId_Rot1, int MessageId_Rot2,
                              Supplier<String> updateTextPitch, Consumer<Float> confirmedPitch,
                              Supplier<String> updateTextYaw, Consumer<Float> confirmedYaw)
    {
        Canvas.Register(groupId, new GuiLabel("pitch :", fontRenderer, offsetX - 5, offsetY, 0xffffff));
        Canvas.Register(groupId, new GuiLabel("yaw   :", fontRenderer, offsetX - 5, offsetY + 21, 0xffffff));

        Canvas.Register(groupId,
                new GuiFormatedTextField(fontRenderer, offsetX + 30, offsetY, 60, 10, 0xffffff, 12,
                        updateTextPitch,
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        (t) -> confirmedPitch.accept(Float.parseFloat(t))));
        Canvas.Register(groupId,
                new GuiFormatedTextField(fontRenderer, offsetX + 30, offsetY + 21, 60, 10, 0xffffff, 12,
                        updateTextYaw,
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        (t) ->  confirmedYaw.accept(Float.parseFloat(t))));

        GuiUtil.addButton4(Canvas, groupId,offsetX, offsetY + 9, "Rot1", MessageId_Rot1,
                () -> confirmedPitch.accept(model.GetPitch()-10f),
                () -> confirmedPitch.accept(model.GetPitch()-1f),
                () -> confirmedPitch.accept(model.GetPitch()+1f),
                () -> confirmedPitch.accept(model.GetPitch()+10f)
        );
        GuiUtil.addButton4(Canvas, groupId,offsetX, offsetY + 29, "Rot2", MessageId_Rot2,
                () -> confirmedYaw.accept(model.GetYaw()-10f),
                () -> confirmedYaw.accept(model.GetYaw()-1f),
                () -> confirmedYaw.accept(model.GetYaw()+1f),
                () -> confirmedYaw.accept(model.GetYaw()+10f)
        );
    }

    public static void Offset(GuiGroupCanvas Canvas, FontRenderer fontRenderer,
                              int offsetX, int offsetY,
                              IFerrisParamGetter model,
                              int groupId, int MessageId_OffsetX, int MessageId_OffsetY, int MessageId_OffsetZ,
                              Supplier<String> updateTextX, Consumer<Float> confirmedX,
                              Supplier<String> updateTextY, Consumer<Float> confirmedY,
                              Supplier<String> updateTextZ, Consumer<Float> confirmedZ)
    {
        Canvas.Register(groupId, new GuiLabel("x :", fontRenderer, offsetX - 5, offsetY + 1, 0xffffff));
        Canvas.Register(groupId, new GuiLabel("y :", fontRenderer, offsetX - 5, offsetY + 21, 0xffffff));
        Canvas.Register(groupId, new GuiLabel("z :", fontRenderer, offsetX - 5, offsetY + 41, 0xffffff));

        Canvas.Register(groupId,
                new GuiFormatedTextField(fontRenderer, offsetX + 12, offsetY + 1, 60, 10, 0xffffff, 12,
                        updateTextX,
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> confirmedX.accept(Float.parseFloat(t))));
        Canvas.Register(groupId,
                new GuiFormatedTextField(fontRenderer, offsetX + 12, offsetY + 21, 60, 10, 0xffffff, 12,
                        updateTextY,
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> confirmedY.accept(Float.parseFloat(t))));
        Canvas.Register(groupId,
                new GuiFormatedTextField(fontRenderer, offsetX + 12, offsetY + 41, 60, 10, 0xffffff, 12,
                        updateTextZ,
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> confirmedZ.accept(Float.parseFloat(t))));

        GuiUtil.addButton4(Canvas, groupId,offsetX, offsetY + 8, "Offset X", MessageId_OffsetX,
                () -> confirmedX.accept((float)model.GetOffset().x-1f),
                () -> confirmedX.accept((float)model.GetOffset().x-0.1f),
                () -> confirmedX.accept((float)model.GetOffset().x+0.1f),
                () -> confirmedX.accept((float)model.GetOffset().x+1f)
        );
        GuiUtil.addButton4(Canvas, groupId,offsetX, offsetY + 28, "Offset Y", MessageId_OffsetY,
                () -> confirmedY.accept((float)model.GetOffset().y-1f),
                () -> confirmedY.accept((float)model.GetOffset().y-0.1f),
                () -> confirmedY.accept((float)model.GetOffset().y+0.1f),
                () -> confirmedY.accept((float)model.GetOffset().y+1f)
        );
        GuiUtil.addButton4(Canvas, groupId,offsetX, offsetY + 48, "Offset Z", MessageId_OffsetZ,
                () -> confirmedZ.accept((float)model.GetOffset().z-1f),
                () -> confirmedZ.accept((float)model.GetOffset().z-0.1f),
                () -> confirmedZ.accept((float)model.GetOffset().z+0.1f),
                () -> confirmedZ.accept((float)model.GetOffset().z+1f)
        );

    }

    public static void AmpPhase(GuiGroupCanvas Canvas, FontRenderer fontRenderer, int groupId, int width, int height, IFerrisParamGetter model,
                              Supplier<String> updateTextAmp, Consumer<Float> confirmedAmp,
                              Supplier<String> updateTextPhase, Consumer<Float> confirmedPhase)
    {
        GuiUtil.addButton6(Canvas, groupId,width - 40, height-36,
                () -> confirmedAmp.accept(model.Amp().get()-10f),
                () -> confirmedAmp.accept(model.Amp().get()-1f),
                () -> confirmedAmp.accept(model.Amp().get()-0.1f),
                () -> confirmedAmp.accept(model.Amp().get()+0.1f),
                () -> confirmedAmp.accept(model.Amp().get()+1f),
                () -> confirmedAmp.accept(model.Amp().get()+10f)
        );

        GuiUtil.addButton6(Canvas, groupId,width - 40, height-16,
                () -> confirmedAmp.accept(model.Phase().get()-10f),
                () -> confirmedAmp.accept(model.Phase().get()-1f),
                () -> confirmedAmp.accept(model.Phase().get()-0.1f),
                () -> confirmedAmp.accept(model.Phase().get()+0.1f),
                () -> confirmedAmp.accept(model.Phase().get()+1f),
                () -> confirmedAmp.accept(model.Phase().get()+10f)
        );

        Canvas.Register(groupId,
                new GuiFormatedTextField(fontRenderer, width - 48, height - 45, 60, 11, 0xffffff, 12,
                        updateTextAmp,
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> confirmedAmp.accept(Float.parseFloat(t))));
        Canvas.Register(groupId,
                new GuiFormatedTextField(fontRenderer, width - 48, height - 25, 60, 11, 0xffffff, 12,
                        updateTextPhase,
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> confirmedPhase.accept(Float.parseFloat(t))));

        Canvas.Register(groupId,
                new GuiLabel("Amp:", fontRenderer, width - 80, height - 45, 0xffffff));
        Canvas.Register(groupId,
                new GuiLabel("Phase:", fontRenderer, width - 80, height - 25, 0xffffff));
    }

    static void Regist(GuiGroupCanvas Canvas, FontRenderer fontRenderer, int groupId, int width, int height, IFerrisParamGetter model,
                       Supplier<String> updateText, Consumer<Float> confirmed)
    {
        GuiUtil.addButton4(Canvas, groupId, 242, height-30, "Resist", -1,
                () -> confirmed.accept(model.GetRegist() * 1.1f),
                () -> confirmed.accept(model.GetRegist() * 1.01f),
                () -> confirmed.accept(model.GetRegist() / 1.01f),
                () -> confirmed.accept(model.GetRegist() / 1.1f));
        Canvas.Register(groupId,
                new GuiFormatedTextField(fontRenderer, 277, height - 38, 60, 11, 0xffffff, 12,
                        updateText,
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> confirmed.accept(Float.parseFloat(t))));
        Canvas.Register(groupId,
                new GuiLabel("Weight:", fontRenderer, 240, height - 38, 0xffffff));
    }

    static void Accel(GuiGroupCanvas Canvas, FontRenderer fontRenderer, int groupId, int height, IFerrisParamGetter model,
                       Supplier<String> updateText, Consumer<Float> confirmed)
    {
        GuiUtil.addButton6(Canvas, groupId, 276, height-50,
                () -> confirmed.accept(model.GetAccel()-10),
                () -> confirmed.accept(model.GetAccel()-1f),
                () -> confirmed.accept(model.GetAccel()-0.1f),
                () -> confirmed.accept(model.GetAccel()+0.1f),
                () -> confirmed.accept(model.GetAccel()+1f),
                () -> confirmed.accept(model.GetAccel()+10));
        Canvas.Register(groupId,
                new GuiFormatedTextField(fontRenderer, 273, height - 59, 60, 11, 0xffffff, 12,
                        updateText,
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> confirmed.accept(Float.parseFloat(t))));
        Canvas.Register(groupId,
                new GuiLabel("Accel:", fontRenderer, 240, height - 59, 0xffffff));
    }

    protected boolean flagManager(int groupid)
    {
    	if(groupid < 0) return true;

        final boolean T = true;
        final boolean F = false;

        if(wheel.isEnableStoryBoard)
            return new boolean[]{F, F, F, F, F, F, F, T, T, F}[groupid];
        if(wheel.isEnableSync)
            return new boolean[]{F, F, T, F, T, T, F, F, F, F}[groupid];
        if(wheel.isEnableSinConvert)
            return new boolean[]{T, T, T, T, F, T, T, F, T, T}[groupid];
        else // Normal
            return new boolean[]{T, T, F, T, F, T, T, F, T, T}[groupid];
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

    private void ChangeUIForPendium()
    {
        Canvas.ActiveGroup(ButtonGroup_Main);
        Canvas.ActiveGroup(ButtonGroup_Accel);
        Canvas.ActiveGroup(ButtonGroup_Weight);
        Canvas.ActiveGroup(ButtonGroup_AmpPhase);
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

	/*GUIの文字等の描画処理*/
    //String stringRotMode;
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        Canvas.Update();
        drawCenteredString(fontRendererObj, wheel.GetName(),width/2,4,0xffffff);
        if(!wheel.isEnableStoryBoard && !wheel.isEnableSync) {
//            drawString(fontRendererObj, "Regist", 240, height - 58, 0xffffff);
//            drawString(fontRendererObj, "Weight", 240, height - 38, 0xffffff);
        }
        //        drawString(fontRendererObj, names[1]+" :"+String.format("% 4.2f", wheel.rotSpeed), 268, 12, 0xffffff);
        if((wheel.isEnableSinConvert || wheel.isEnableSync) && !wheel.isEnableStoryBoard) {
//            drawString(fontRendererObj, "Amp :", width - 80, height - 44, 0xffffff);
//            drawString(fontRendererObj, "Phase", width - 80, height - 24, 0xffffff);
        }
//        drawString(fontRendererObj, names[4], 246, height-31, 0xffffff);

//        drawString(fontRendererObj, "Size :", width - 75, 4, 0xffffff);
//        drawString(fontRendererObj, "pitch :", width - 75, 24, 0xffffff);
//        drawString(fontRendererObj, "yaw  :", width - 75, 44, 0xffffff);

//        drawString(fontRendererObj, "x :", width - 75, 134, 0xffffff);
//        drawString(fontRendererObj, "y :", width - 75, 154, 0xffffff);
//        drawString(fontRendererObj, "z :", width - 75, 174, 0xffffff);

        //debug
        drawRightedString(this.fontRendererObj, getRSModeDescription(wheel.rsFlag), width - 6, 121, 0xffffff);
        drawRightedString(this.fontRendererObj, String.format("Input RS Power : %1.2f", wheel.getRSPower()), 260, -10, 0xffffff);
    
        drawRightedString(this.fontRendererObj, SoundManager.sounds.get(wheel.soundManager.GetSoundIndex()), -0, 79, 0xffffff);

        if(wheel.isEnableSync)
        {
            drawCenteredString(this.fontRendererObj,
            		wheel.isSyncTargetSpeed ? StatCollector.translateToLocal("gui.core.SyncTarget.speed")
            								: StatCollector.translateToLocal("gui.core.SyncTarget.rot"),
            				315, height - 35, 0xffffff);
            drawCenteredString(this.fontRendererObj,
	        		wheel.syncTarget.isSyncing() ? StatCollector.translateToLocal("gui.core.Sync.Parentregistered")
	        								: StatCollector.translateToLocal("gui.core.Sync.ParentUnregistered"),
	        				width - 123, height - 85, 0xffffff);
        }
    }

}