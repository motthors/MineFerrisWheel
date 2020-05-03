package mfw._mc.gui.gui;

import mfw.ferriswheel.FerrisGarland;
import mochisystems._mc.gui.GuiToggleButton;
import mochisystems.util.gui.GuiUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;

import static mfw.message.MessageFerrisMisc.*;

public class GUIFerrisGarland extends GUIFerrisCoreBase {

	private FerrisGarland garland;

	private int stopButtonID = -1;

	public GUIFerrisGarland(int x, int y, int z, InventoryPlayer playerInventory, FerrisGarland garland)
	{
        super(x, y, z, playerInventory, garland);
        if(garland instanceof FerrisGarland.End)
		{
            garland = (FerrisGarland)garland.pairAddress.GetInstance(garland.controller);
            part = garland;
            blockposX = garland.pairAddress.x;
            blockposY = garland.pairAddress.y;
            blockposZ = garland.pairAddress.z;
		}
		this.garland = garland;
	}


	// GUIを開くたび呼ばれる初期化関数 WindowSize変更でも呼ばれる
	@Override
	public void initGui()
	{
		super.initGui();

//        通常  : StopRev Regist          Weight      rs            isSync isStory isPendulum
//        Sin   : StopRev Regist AmpPhase Weight      rs　　　　　　isSync isStory isPendulum
//        Sync　:               AmpPhase        sync  　   　　　　isSync
//        Story :　　　　　　　　　　　　　　　　　　   StoryBoard        isStory
//        GuiUtil.addButton1(Canvas, buttonList, -1, 29, 12, 2, 2, "back", MessageFerrisMisc.GUIBackPartGUIOpen);
//        GuiUtil.addButton1(Canvas, buttonList, -1, 29, 12, width - 32, 2, "back", MessageFerrisMisc.GUIBackPartGUIOpen);
//        GuiUtil.addButton2(Canvas, buttonList, -1, 32, 2, "", MessageFerrisMisc.GUICoreSlotPage);
//        addButton4(width - 70, 12, "Size", MessageFerrisMisc.GUICoreSizes);
//        addButton4(width - 70, 32, "Rot1", MessageFerrisMisc.GUICoreRot1);
//        addButton4(width - 70, 52, "Rot2", MessageFerrisMisc.GUICoreRot2);
//        addButton1(42, 14, width - 48, 84, "reset", MessageFerrisMisc.GUICoreRotReset);
//        addButton1(40, 10, -60, 64, "soundManager▲", MessageFerrisMisc.GUICoreSoundSelectUp);
//        addButton1(40, 10, -60, 94, "soundManager▼", MessageFerrisMisc.GUICoreSoundSelectDown);
//        GuiUtil.addButton1(Canvas, buttonList, -1, 40, 13, 82, 2, "unlock", MessageFerrisMisc.GUICoreLock, true, part.IsLock(), "lock");
//        GuiUtil.addButton1(Canvas, buttonList, -1, 40, 13, width - 122, 2, "core", MessageFerrisMisc.GUICoreToggleDrawCore);
        Canvas.Register(-1,
                new GuiToggleButton(0,  width - 122, 2, 40, 13,
                        "core", "core", garland.ShouldDrawCore(),
                        isOn -> SendMessageToSetParam(GUICoreToggleDrawCore, 0)));

//        addButton1(70, 13, width - 76, 68, "pos only",
//                MessageFerrisMisc.GUICoreToggleForrowTransform, true, !part.GetIsForrowParentTransform(), "pos only");

//        GuiUtil.addButton2(Canvas, buttonList, -1, width-70, 40, "Core Num", MessageFerrisMisc.GUIGarlandChangeCoreNum);
        GuiUtil.addButton2(Canvas, -1,-60, 64, "Core Num", GUICoreSoundSelectUp,
                () -> SendMessageToSetParam(GUIGarlandChangeCoreNum, -1),
                () -> SendMessageToSetParam(GUIGarlandChangeCoreNum, 1));

        //postinit

		stringRSMode = getRSModeDescription(garland.rsFlag);
        SetEnableOpenChildButtonByID();
    }

    
	/*GUIの文字等の描画処理*/
    //String stringRotMode;
    String stringRSMode;
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        Canvas.Update();
        drawCenteredString(fontRendererObj, garland.GetName(),width/2,4,0xffffff);
//        if(!garland.isEnableStoryBoard && !garland.isEnableSync) {
//            drawString(fontRendererObj, String.format("%s : %4.2f", "Regist", garland.accel), 240, height - 58, 0xffffff);
//            drawString(fontRendererObj, String.format("%s : %4.1f", "Weight", 1f / garland.resist), 240, height - 38, 0xffffff);
//        }
        //        drawString(fontRendererObj, names[1]+" :"+String.format("% 4.2f", garland.rotSpeed), 268, 12, 0xffffff);
//        if((garland.isEnableSinConvert || garland.isEnableSync) && !garland.isEnableStoryBoard) {
//            drawString(fontRendererObj, String.format("%s : %4.1f", "Amp", garland.syncAmplitude.get()), width - 80, height - 44, 0xffffff);
//            drawString(fontRendererObj, String.format("%s : %4.1f", "Phase", garland.syncPhase.get()), width - 80, height - 24, 0xffffff);
//        }
//        drawString(fontRendererObj, names[4], 246, height-31, 0xffffff);

//        drawString(fontRendererObj, String.format("Size : %4.2f", garland.Scale.get()), width - 75, 4, 0xffffff);
        drawString(fontRendererObj, "Connector Num", width - 76, 24, 0xffffff);
        drawString(fontRendererObj, Integer.toString(garland.GetCoreNum()), width - 50, 43, 0xffffff);
//        drawString(fontRendererObj, String.format("rot1 : %4.2f", garland.pitch), width - 75, 24, 0xffffff);
//        drawString(fontRendererObj, String.format("rot2 : %4.2f", garland.yaw), width - 75, 44, 0xffffff);
        drawCenteredString(fontRendererObj, Integer.toString(container.getPageNum()),54,5,0xffffff);

        //debug
//        drawRightedString(this.fontRendererObj, stringRSMode, width - 6, 121, 0xffffff);

//        drawRightedString(this.fontRendererObj, SoundManager.sounds.get(garland.soundManager.GetSoundIndex()), -0, 79, 0xffffff);

//        if(garland.isEnableSync)
//        {
//            drawCenteredString(this.fontRendererObj,
//            		garland.isSyncTargetSpeed ? StatCollector.translateToLocal("gui.core.SyncTarget.speed")
//            								: StatCollector.translateToLocal("gui.core.SyncTarget.rot"),
//            				width - 40, 174, 0xffffff);
//	        drawString(this.fontRendererObj,
//	        		garland.syncTarget.isSyncing() ? StatCollector.translateToLocal("gui.core.Sync.Parentregistered")
//	        								: StatCollector.translateToLocal("gui.core.Sync.ParentUnregistered"),
//	        				280, 121, 0xffffff);
//        }
    }


	public FontRenderer GetFontRenderer(){
		return fontRendererObj;
	}


}