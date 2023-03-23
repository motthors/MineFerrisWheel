package jp.mochisystems.mfw._mc.gui.gui;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.gui.GuiToggleButton;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.NbtParamsShadow;
import jp.mochisystems.mfw._mc.message.MFW_PacketHandler;
import jp.mochisystems.mfw._mc.message.MessageFerrisMisc;
import jp.mochisystems.mfw._mc.message.MessageSyncNbtForMFWCtS.Action;
import jp.mochisystems.mfw.ferriswheel.FerrisGarland;
import jp.mochisystems.core.util.gui.*;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class GUIFerrisGarland extends GUIFerrisCoreBase {

	private final FerrisGarland garland;

	private final int stopButtonID = -1;

	public GUIFerrisGarland(InventoryPlayer playerInventory, IModel garland)
	{
        super(playerInventory, ((FerrisGarland) garland).GetLead());
		this.garland = ((FerrisGarland) garland).GetLead();

        drawCore = shadow.Create("isDrawCore", n->n::setBoolean, NBTTagCompound::getBoolean);
    }
    private final NbtParamsShadow.Param<Boolean> drawCore;


	// GUIを開くたび呼ばれる初期化関数 WindowSize変更でも呼ばれる
	@Override
	public void initGui()
	{
		super.initGui();
        Canvas.Register(-1,
                new GuiToggleButton(0,  width - 122, 2, 40, 13,
                        _Core.I18n("gui.core.text.core"), _Core.I18n("gui.core.text.core"),
                        drawCore::Get,
                        isOn -> {drawCore.Set(isOn); this.SyncToServer(); }));

        GuiUtil.addButton2(Canvas, -1,width-60, 64,
                () -> {
                    garland.ChangeCore(garland.GetCoreNum() - 1);
                    Act(Action.GarlandCoreDown);
                    GUIHandler.OpenBlockModelGuiInClient(garland);
                },
                () -> {
                    garland.ChangeCore(garland.GetCoreNum() + 1);
                    Act(Action.GarlandCoreUp);
                    GUIHandler.OpenBlockModelGuiInClient(garland);
                });


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
        drawCenteredString(fontRenderer, garland.GetName(),width/2,4,0xffffff);
        drawString(fontRenderer, "Connector Num", width - 76, 24, 0xffffff);
        drawString(fontRenderer, Integer.toString(garland.GetCoreNum()), width - 50, 43, 0xffffff);
    }

}