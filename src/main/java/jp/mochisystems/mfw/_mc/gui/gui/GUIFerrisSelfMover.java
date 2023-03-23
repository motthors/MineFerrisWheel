package jp.mochisystems.mfw._mc.gui.gui;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.gui.GuiToggleButton;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.NbtParamsShadow;
import jp.mochisystems.core.util.gui.*;
import jp.mochisystems.mfw._mc.message.*;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;
import jp.mochisystems.mfw.manager.SyncTargetRegisterManager;
import jp.mochisystems.mfw.sound.SoundLoader;
import jp.mochisystems.mfw._mc.message.MessageSyncNbtForMFWCtS.Action;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GUIFerrisSelfMover extends GUIFerrisCoreBase {

    FerrisSelfMover mover;

	public GUIFerrisSelfMover(InventoryPlayer playerInventory, IModel part)
	{
		super(playerInventory, (FerrisPartBase) part);
        this.mover = (FerrisSelfMover) part;

        accel = shadow.Create("accel", n->n::setFloat, NBTTagCompound::getFloat);
        amp = shadow.Create("amplitude", n->n::setFloat, NBTTagCompound::getFloat);
        phase = shadow.Create("phase", n->n::setFloat, NBTTagCompound::getFloat);
        resist = shadow.Create("resist", n->n::setFloat, NBTTagCompound::getFloat);
        tilt = shadow.Create("tilt", mover.tilt, v->v::WriteToNBT);
        scale = shadow.Create("scale", mover.localScale, v->v::WriteToNBT);
        offset = shadow.Create("offset", mover.offset, v->v::WriteToNBT);
        drawCore = shadow.Create("isDrawCore", n->n::setBoolean, NBTTagCompound::getBoolean);
        isDrawModel = shadow.Create("isActive", n -> n::setBoolean, NBTTagCompound::getBoolean);
        stop = shadow.Create("stopflag", n->n::setBoolean, NBTTagCompound::getBoolean);
        sync = shadow.Create("enablesyncrot", n->n::setBoolean, NBTTagCompound::getBoolean);
        pendulum = shadow.Create("enablesinconvert", n->n::setBoolean, NBTTagCompound::getBoolean);
        storyboard = shadow.Create("enablestoryboard", n->n::setBoolean, NBTTagCompound::getBoolean);
        hanging = shadow.Create("isIndependentTransform", n->n::setBoolean, NBTTagCompound::getBoolean);
    }
    private final NbtParamsShadow.Param<Float> accel;
    private final NbtParamsShadow.Param<Float> amp;
    private final NbtParamsShadow.Param<Float> phase;
    private final NbtParamsShadow.Param<Float> resist;
    private final NbtParamsShadow.Class<Quaternion> tilt;
    private final NbtParamsShadow.Class<Vec3d> scale;
    private final NbtParamsShadow.Class<Vec3d> offset;
    private final NbtParamsShadow.Param<Boolean> drawCore;
    private final NbtParamsShadow.Param<Boolean> stop;
    private final NbtParamsShadow.Param<Boolean> sync;
    private final NbtParamsShadow.Param<Boolean> pendulum;
    private final NbtParamsShadow.Param<Boolean> storyboard;
    private final NbtParamsShadow.Param<Boolean> hanging;
    private final NbtParamsShadow.Param<Boolean> isDrawModel;

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

    private double beginnerScale = 1;
    // GUIを開くたび呼ばれる初期化関数 WindowSize変更でも呼ばれる
    @Override
    public void initGui()
    {
        int gDef = -1;
        super.initGui();

        Canvas.Register(gDef, new GuiButtonWrapper(0, width - 48, height - 18, 42, 14, _Core.I18n("gui.core.text.reset"),
                () -> {
                    part.Reset();
                    NBTTagCompound nbt = new NBTTagCompound();
                    part.writeMineToNBT(nbt);
                    shadow.Reset(nbt);
                    Act(Action.Reset);
                }));

        Canvas.Register(gDef,
                new GuiToggleButton(0,  width - 98, 2, 16, 13, _Core.I18n("gui.core.text.core"), _Core.I18n("gui.core.text.core"),
                        drawCore::Get,
                        isOn -> {drawCore.Set(isOn); this.SyncToServer(); }));

        Canvas.Register(gDef,
                new GuiToggleButton(0,  width - 134, 2, 35, 13, _Core.I18n("gui.core.text.model"), _Core.I18n("gui.core.text.model"),
                        isDrawModel::Get,
                        isOn -> {isDrawModel.Set(isOn); this.SyncToServer(); }));



        GuiUtil.addButton2(Canvas, gDef,-60, 64,
                () -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSoundSelectUp, 0),
                () -> SendMessageToSetParam(MessageFerrisMisc.GUICoreSoundSelectDown, 0));


        if(_Core.CONFIG_ANNOTATIONS.isProGui) {
            Canvas.Register(gDef,
                    new GuiToggleButton(0,  width - 136, 32, 54, 13,
                            _Core.I18n("gui.core.text.hanging"), _Core.I18n("gui.core.text.hanging"),
                            () -> part.GetIsFollowParentTransform(),
                            isOn -> {
                                hanging.Set(isOn);
                                SyncToServer();
                            }));


            GuiUtil.Vec3(_Core.I18n("gui.core.text.scale"), scale, Canvas, fontRenderer,
                    width - 75, 5, gDef, 0.05f,
                    () -> {
                        mover.SetLocalScale(scale.Get());
                        SyncToServer();
                    }
            );

            if(part.controller instanceof TileEntity) {
                GuiUtil.Quaternion(_Core.I18n("gui.core.text.rotate"), tilt, Canvas, fontRenderer,
                        width - 75, 50, gDef, 0.5f,
                        this::SyncToServer);
            }else{
                Canvas.Register(gDef, new GuiLabel(_Core.I18n("gui.NotEnableTilt"), fontRenderer,-76, 50, 0x888888));
            }

            GuiUtil.Vec3(_Core.I18n("gui.core.text.offset"), offset, Canvas, fontRenderer,
                    width - 75, 95,
                    gDef, 0.01f,
                    this::SyncToServer);
        }
        else {
            beginnerScale = scale.Get().x;
            GuiUtil.AddInspector(
                    Canvas, fontRenderer,
                    _Core.I18n("gui.core.text.scale"),
                    gDef, width - 75, 10, false,
                    () -> (float)beginnerScale,
                    v -> {
                        beginnerScale = v;
                        scale.Get().SetFrom(v, v, v);
                        mover.SetLocalScale(scale.Get());
                    }, 0.5f,
                    this::SyncToServer
            );
        }

//        通常  : StopRev Regist          Weight      rs            isSync isStory isPendulum
//        Sin   : StopRev Regist AmpPhase Weight      rs　　　　　　isSync isStory isPendulum
//        Sync　:               AmpPhase        sync  　   　　　　isSync
//        Story :　　　　　　　　　　　　　　　　　　   StoryBoard        isStory


        Canvas.Register(ButtonGroup_Main,
                new GuiToggleButton(0,  170, height-52, 28, 28,
                    "||", "|>",
                    () -> !stop.Get(),
                    isOn -> {
                        stop.Set(!isOn);
                        SyncToServer();
                }));


        Canvas.Register(ButtonGroup_Main,
                new GuiButtonWrapper(0, 287, height-55, 48, 12,
                        _Core.I18n("gui.core.text.reverse"),
                () -> {
                    accel.Set(accel.Get() * -1);
                    SyncToServer();
                }));

        Accel(Canvas, fontRenderer, ButtonGroup_Accel, height, mover,
                () -> String.format("%6.1f", mover.GetAccel()),
                v -> {
                    accel.Set(v);
                    SyncToServer();
                });


        AmpPhase(Canvas, fontRenderer, ButtonGroup_AmpPhase, width, height, mover,
                () -> String.format("%6.1f", mover.Amp().get()),
                v -> {
                    mover.Amp().set(v);
                    amp.Set(v);
                    SyncToServer();
                },
                () -> String.format("%6.1f", mover.Phase().get()),
                v -> {
                    mover.Phase().set(v);
                    phase.Set(v);
                    SyncToServer();
                });

        if(_Core.CONFIG_ANNOTATIONS.isProGui) {
            Regist(Canvas, fontRenderer, ButtonGroup_Weight, width, height, mover,
                    () -> String.format("%6.2f", 1 / mover.GetResist()),
                    v -> {
                        resist.Set(v);
                        SyncToServer();
                    });
        }


        if(_Core.CONFIG_ANNOTATIONS.isProGui) {

            Canvas.Register(gDef,
                    new GuiButtonWrapper(0, 82, 32, 52, 12, _Core.I18n("gui.core.text.marking"),
                            () -> SyncTargetRegisterManager.INSTANCE.Save(part)));
            Canvas.Register(ButtonGroup_Sync,
                    new GuiButtonWrapper(0, 175, height - 52, 70, 13, _Core.I18n("gui.core.text.sync.register"),
                            this::RegisterSyncParent));
            Canvas.Register(ButtonGroup_Sync,
                    new GuiButtonWrapper(0, 175, height - 35, 50, 13, _Core.I18n("gui.core.text.sync.clear"),
                            () -> Act(Action.ClearSync)));
            Canvas.Register(ButtonGroup_Sync,
                    new GuiButtonWrapper(0, 265, height - 52, 70, 13, _Core.I18n("gui.core.switch.syncmode"),
                            () -> Act(Action.ToggleSyncMode)));
            Canvas.Register(ButtonGroup_Sync,
                    new GuiButtonWrapper(0, 265, height - 32, 70, 13, _Core.I18n("gui.core.text.sync.copymode"),
                            () -> Act(Action.ToggleSyncCopyMode)));

            Canvas.Register(ButtonGroup_RsMode,
                    new GuiButtonWrapper(0, width - 76, height - 50, 70, 13,
                            _Core.I18n("gui.core.switch.rsmode"),
                            () -> Act(Action.RsMode)));


            Canvas.Register(ButtonGroup_StoryBoard,
                    new GuiButtonWrapper(0, 210, height - 52, 140, 28, "Open Timeline",
                            () -> {
                                CommonAddress ca = mover.GetCommonAddress();
                                GUIHandler.OpenCustomGuiInClient(ca);
                                MFW_PacketHandler.INSTANCE.sendToServer(new MessageOpenModelStoryBoardGui(ca));
//                    SendMessageToSetParam(MessageFerrisMisc.GUIOpenStoryBoard, 0)
                            }));


            String text = _Core.I18n("gui.core.isEnableSyncTarget");
            Canvas.Register(ButtonGroup_isSync,
                    new GuiToggleButton(0, width - 160, height - 75, 70, 13, text, text,
                            sync::Get,
                            isOn -> {
                                sync.Set(isOn);
                                if (isOn) ChangeUIForSync();
                                else ChangeUIForNormal();
                                SyncToServer();
                            }));

            text = _Core.I18n("gui.core.isEnableStoryBoard");
            Canvas.Register(ButtonGroup_isStory,
                    new GuiToggleButton(0, width - 340, height - 75, 70, 13, text, text,
                            storyboard::Get,
                            isOn -> {
                                storyboard.Set(isOn);
                                if (isOn) ChangeUIForStoryboard();
                                else ChangeUIForNormal();
                                SyncToServer();
                            }));

            text = _Core.I18n("gui.core.isEnableSinConvert");
            Canvas.Register(ButtonGroup_isPendulum,
                    new GuiToggleButton(0, width - 250, height - 75, 70, 13, text, text,
                            pendulum::Get,
                            isOn -> {
                                pendulum.Set(isOn);
                                if (isOn) ChangeUIForPendulum();
                                else ChangeUIForNormal();
                                SyncToServer();
                            }));
        }

        if (mover.isEnableSync) ChangeUIForSync();
        else if (mover.GetEnableStoryBoard()) ChangeUIForStoryboard();
        else if (mover.GetEnableSinConvert()) ChangeUIForPendulum();
        else ChangeUIForNormal();
        stringRSMode = getRSModeDescription(part.rsFlag);
        SetEnableOpenChildButtonByID();
    }



    protected void RegisterSyncParent()
    {
        if(false /*part.childSyncTileList.size() != 0*/) // TODO 循環参照を見つけるようにしたい
        {
            mc.player.sendChatMessage(
                    _Core.I18n("message.coreGUI.sync.cannotregisttoparent")
                    // 同期回転の親になっている人は同期設定ができません
            );
            return;
        }

        FerrisPartBase parent = SyncTargetRegisterManager.INSTANCE.GetSavedTarget();
        if(parent == null) return;
        CommonAddress parentAddress = parent.GetCommonAddress();
        CommonAddress childAddress = part.GetCommonAddress();
        if(parentAddress == null || parentAddress.Equals(childAddress) ) return;
        MessageRegisterSyncRotParentCtS packet = new MessageRegisterSyncRotParentCtS(parentAddress, childAddress);
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
        mover.SetNewSyncParent(parentAddress);
    }








    public static void AmpPhase(GuiGroupCanvas Canvas, FontRenderer fontRenderer, int groupId, int width, int height, FerrisSelfMover model,
                              Supplier<String> updateTextAmp, Consumer<Float> confirmedAmp,
                              Supplier<String> updateTextPhase, Consumer<Float> confirmedPhase)
    {
        int x = width - 300;
        int y = height - 104;

        GuiUtil.addButton6(Canvas, groupId,x, y+8,
                () -> confirmedAmp.accept(model.Amp().get()-10f),
                () -> confirmedAmp.accept(model.Amp().get()-1f),
                () -> confirmedAmp.accept(model.Amp().get()-0.1f),
                () -> confirmedAmp.accept(model.Amp().get()+0.1f),
                () -> confirmedAmp.accept(model.Amp().get()+1f),
                () -> confirmedAmp.accept(model.Amp().get()+10f)
        );

        GuiUtil.addButton6(Canvas, groupId, x, y+8+20,
                () -> confirmedPhase.accept(model.Phase().get()-10f),
                () -> confirmedPhase.accept(model.Phase().get()-1f),
                () -> confirmedPhase.accept(model.Phase().get()-0.1f),
                () -> confirmedPhase.accept(model.Phase().get()+0.1f),
                () -> confirmedPhase.accept(model.Phase().get()+1f),
                () -> confirmedPhase.accept(model.Phase().get()+10f)
        );

        Canvas.Register(groupId,
                new GuiFormattedTextField(0, fontRenderer, x, y, 60, 11, 0xffffff, 12,
                        updateTextAmp,
                        s -> s.matches(GuiFormattedTextField.regexNumber),
                        t -> confirmedAmp.accept(Float.parseFloat(t))));
        Canvas.Register(groupId,
                new GuiFormattedTextField(0, fontRenderer, x, y+20, 60, 11, 0xffffff, 12,
                        updateTextPhase,
                        s -> s.matches(GuiFormattedTextField.regexNumber),
                        t -> confirmedPhase.accept(Float.parseFloat(t))));

        Canvas.Register(groupId,
                new GuiLabel("Amp:", fontRenderer, x-40, y, 0xffffff));
        Canvas.Register(groupId,
                new GuiLabel("Phase:", fontRenderer, x-40, y+20, 0xffffff));
    }

    static void Regist(GuiGroupCanvas Canvas, FontRenderer fontRenderer, int groupId, int width, int height, FerrisSelfMover model,
                       Supplier<String> updateText, Consumer<Float> confirmed)
    {
        GuiUtil.addButton4(Canvas, groupId, 212, height-30,
                () -> confirmed.accept(model.GetResist() * 1.1f),
                () -> confirmed.accept(model.GetResist() * 1.01f),
                () -> confirmed.accept(model.GetResist() / 1.01f),
                () -> confirmed.accept(model.GetResist() / 1.1f));
        Canvas.Register(groupId,
                new GuiFormattedTextField(0, fontRenderer, 247, height - 38, 60, 11, 0xffffff, 12,
                        updateText,
                        s -> s.matches(GuiFormattedTextField.regexNumber),
                        t -> confirmed.accept(Float.parseFloat(t))));
        Canvas.Register(groupId,
                new GuiLabel("Weight:", fontRenderer, 210, height - 38, 0xffffff));
    }

    static void Accel(GuiGroupCanvas Canvas, FontRenderer fontRenderer, int groupId, int height, FerrisSelfMover model,
                       Supplier<String> updateText, Consumer<Float> confirmed)
    {
        GuiUtil.addButton6(Canvas, groupId, 246, height-50,
                () -> confirmed.accept(model.GetAccel()-10),
                () -> confirmed.accept(model.GetAccel()-1f),
                () -> confirmed.accept(model.GetAccel()-0.1f),
                () -> confirmed.accept(model.GetAccel()+0.1f),
                () -> confirmed.accept(model.GetAccel()+1f),
                () -> confirmed.accept(model.GetAccel()+10));
        Canvas.Register(groupId,
                new GuiFormattedTextField(0, fontRenderer, 243, height - 59, 60, 11, 0xffffff, 12,
                        updateText,
                        s -> s.matches(GuiFormattedTextField.regexNumber),
                        t -> confirmed.accept(Float.parseFloat(t))));
        Canvas.Register(groupId,
                new GuiLabel("Accel:", fontRenderer, 210, height - 59, 0xffffff));
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

    private void ChangeUIForPendulum()
    {
        Canvas.ActiveGroup(ButtonGroup_Main);
        Canvas.ActiveGroup(ButtonGroup_Accel);
        Canvas.ActiveGroup(ButtonGroup_Weight);
        Canvas.ActiveGroup(ButtonGroup_AmpPhase);
        Canvas.DisableGroup(ButtonGroup_Sync);
        Canvas.ActiveGroup(ButtonGroup_isPendulum);
        Canvas.ActiveGroup(ButtonGroup_isSync);
        Canvas.DisableGroup(ButtonGroup_isStory);
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
        Canvas.DisableGroup(ButtonGroup_isPendulum);
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
        drawCenteredString(fontRenderer, part.GetName(),width/2,4,0xffffff);

        if(_Core.CONFIG_ANNOTATIONS.isProGui) {
            drawRightedString(this.fontRenderer, getRSModeDescription(part.rsFlag), width - 6, height - 36, 0xffffff);
            drawRightedString(this.fontRenderer, String.format("RS Signal : %1.2f", part.getRSPower()), width, height - 60, 0xffffff);
        }
        drawRightedString(this.fontRenderer, SoundLoader.Instance.sounds.get(mover.soundManager.GetSoundIndex()), -0, 79, 0xffffff);

        FerrisPartBase marked = SyncTargetRegisterManager.INSTANCE.GetSavedTarget();
        if(marked!=null)
        {
            String name = marked.GetName();
            CommonAddress ad = SyncTargetRegisterManager.INSTANCE.GetAddress();
            String format = String.format("%s [x=%d : y=%d : z=%d]", name, ad.x, ad.y, ad.z);
            int len = fontRenderer.drawStringWithShadow(format, 84, 46, -1);
            drawRect(82, 45, len, 39, 0xD0202020);
            fontRenderer.drawStringWithShadow(format, 84, 46, -1);
        }

        if(mover.isEnableSync)
        {
            drawCenteredString(this.fontRenderer,
                    mover.isSyncTargetSpeed ? _Core.I18n("gui.core.SyncTarget.speed")
            								: _Core.I18n("gui.core.SyncTarget.rot"),
            				350, height - 50, 0xffffff);
            drawCenteredString(this.fontRenderer,
                    mover.isSyncModeAbsolute ? _Core.I18n("gui.core.text.sync.copymode.absolute")
                                             : _Core.I18n("gui.core.text.sync.copymode.relative"),
                    350, height - 30, 0xffffff);
            drawCenteredString(this.fontRenderer,
                    mover.syncTarget.isSyncing() ? _Core.I18n("gui.core.Sync.Parentregistered")
	        								: _Core.I18n("gui.core.Sync.ParentUnregistered"),
	        				width - 123, height - 85, 0xffffff);
        }
    }

}