package jp.mochisystems.mfw._mc.gui.gui;

import java.util.*;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.GUICanvasGroupControl;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.gui.GuiToggleButton;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.IBlockModel;
import jp.mochisystems.core.util.NbtParamsShadow;
import jp.mochisystems.core.util.gui.GuiButtonWrapper;
import jp.mochisystems.core.util.gui.GuiLabel;
import jp.mochisystems.core.util.gui.GuiUtil;
import jp.mochisystems.mfw._mc.message.MFW_PacketHandler;
import jp.mochisystems.mfw._mc.message.MessageFerrisMisc;
import jp.mochisystems.mfw._mc.message.MessageSyncNbtForMFWCtS.Action;
import jp.mochisystems.core.util.Connector;
import jp.mochisystems.mfw._mc.message.MessageSyncNbtForMFWCtS;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import jp.mochisystems.mfw._mc.gui.container.ContainerFerrisCore;
import jp.mochisystems.mfw._mc.gui.slot.slotFerrisCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import org.lwjgl.opengl.GL11;

public abstract class GUIFerrisCoreBase extends GUICanvasGroupControl {

    protected FerrisPartBase part;
    protected ContainerFerrisCore container;

    protected final int PageGroupIdOffset = 999;

    final NbtParamsShadow shadow;
    final NbtParamsShadow.Param<Boolean> lock;

	public GUIFerrisCoreBase(InventoryPlayer playerInventory, FerrisPartBase part)
	{
		super(null);
//        part = part.GetSelectedPartInGUI();
        this.container = new ContainerFerrisCore(playerInventory, part);
        this.inventorySlots = container;
        this.part = part;

        NBTTagCompound nbt = new NBTTagCompound();
        part.writeMineToNBT(nbt);
        this.shadow = new NbtParamsShadow(nbt);
        lock = shadow.Create("isenablecollider", n->n::setBoolean, NBTTagCompound::getBoolean);
	}

    protected void UpdateCameraPosFromCore(Vec3d dest, float tick)
    {
//        if(part == null) return;
        Connector.Fix(dest, part.connectorFromParent, tick);
        dest.add(part.offset);
        dest.add(part.controller.CorePosX(), part.controller.CorePosY(), part.controller.CorePosZ());
    }


    // GUIを開くたび呼ばれる初期化関数
	@Override
	public void initGui()
	{
        int gDef = -1;
        super.initGui();
        container.SetWindowSize(width, height);

        Canvas.Register(gDef,
                new GuiButtonWrapper(0, 2, 2, 29, 12, _Core.I18n("gui.core.text.back"),
                        this::BackParentGui));

            GuiUtil.addButton2(Canvas, -1, 32, 2,
                    () -> ChangeSlotPage(-1),
                    () -> ChangeSlotPage(1));

        Canvas.Register(gDef, new GuiToggleButton(0, 82, 2, 36, 12,
                "Pro", "Pro",
                () -> _Core.CONFIG_ANNOTATIONS.isProGui,
                b -> {
                    _Core.CONFIG_ANNOTATIONS.isProGui = b;
                    ConfigManager.sync(_Core.MODID, Config.Type.INSTANCE);
                    this.initGui();
                }));

        Canvas.Register(gDef,
                new GuiToggleButton(0,  82, 17,43, 13, _Core.I18n("gui.core.text.unlock"), _Core.I18n("gui.core.text.lock"),
                        () -> part.IsLock(),
                        isOn -> {
//                            part.toggleLock();
                            Act(Action.Lock);
                        }));

        Canvas.Register(gDef, new GuiLabel(_Core.I18n("gui.core.text.layer"), fontRenderer, 4, 18, 0xffffff));

        if(_Core.CONFIG_ANNOTATIONS.isProGui) {
            if (part instanceof IBlockModel) {
                Canvas.Register(gDef,
                        new GuiToggleButton(0, width - 134, 17, 52, 13, _Core.I18n("gui.core.text.collider"), _Core.I18n("gui.core.text.collider"),
                                () -> ((IBlockModel) part).IsEnableCollider(),
                                isOn -> {
//                                    lock.Set(!lock.Get());
                                    Act(Action.ToggleCol);
                                }));
            }
        }


        RegisterChildPartGuiButton();
    }

    private void RegisterChildPartGuiButton()
    {
        int count = 0, page = 0;
        int num = part.getSizeInventory();
        while(true) {
            for (int vert = 0; vert < 6; ++vert) {
                for (int horz = 0; horz < 4; ++horz) {
                    if(count >= num) return;
//                    if(part.takeChildPart(count)!=null && part.GetChildren()[count]==null) { count++; continue; }
                    int x = horz * 18 + 5;
                    int y = 46 + vert * 26;
                    int groupId = PageGroupIdOffset + page;
                    int childIndex = count;
                    Canvas.Register(groupId,
                            new GuiButtonWrapper(0, x, y, 16, 8, "",
                                    () -> OpenChildGui(childIndex)));
                    count++;
                }
            }
            page++;
        }
    }

    static String[] names = {
            _Core.I18n("gui.core.switch.rsmode0"),
            _Core.I18n("gui.core.switch.rsmode1"),
            _Core.I18n("gui.core.switch.rsmode2"),
            _Core.I18n("gui.core.switch.rsmode3"),
            _Core.I18n("gui.core.switch.rsmode4")
    };
	public String getRSModeDescription(int flag)
	{
//		public final byte rsFlag_Non = 0; //効果なし
//		public final byte rsFlag_StopWhenOn = 1; //ON時停止
//		public final byte rsFlag_StopWhenOff = 2; //OFF時停止
//		public final byte rsFlag_RatioPositive = 3; //大きいほど高倍率
//		public final byte rsFlag_RatioNegative = 4; //小さいほど高倍率

		return names[flag % FerrisPartBase.rsFlag_End];
	}


    private int prevPageGroup = -999;
    protected void SetEnableOpenChildButtonByID()
    {
        int page = container.getPageNum();

        Canvas.DisableGroup(prevPageGroup);
        int newPageGroup = page + PageGroupIdOffset;
        Canvas.ActiveGroup(newPageGroup);
        prevPageGroup = newPageGroup;
    }

    @Override
    public void drawWorldBackground(int p_146270_1_)
    {
        super.drawWorldBackground(p_146270_1_);
        int offset = 2;
        int mx = offset, my = height - 19 * 3 + offset;
        int xx = 164 - offset, xy = height - offset;
        int color = 0x80777777;
        drawRect(mx, my, xx, xy, color);
        mx = 164;
        my = height - 20 + offset;
        xx = 164*2 - offset;
        drawRect(mx, my, xx, xy, color);
    }
    
	/*GUIの文字等の描画処理*/
    //String stringRotMode;
    String stringRSMode;
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);

        int slotNum = part.GetChildren().length;
        int start = container.getPageNum() * 4 * 6;
        int end = start + 4 * 6;
        end = java.lang.Math.min(slotNum, end);
        int i = 0;
        for (int idx = container.getPageNum() * 4 * 6; idx < end; ++idx) {
            String Char = part.connectors.get(idx).GetName();
            if (Char != null && !Char.isEmpty()) Char = Char.substring(0, 1);
            drawCenteredString(fontRenderer, Char, i % 4 * 18 + 14, (i / 4) * 26 + 34, 0x808080);
            i++;
        }
        drawCenteredString(fontRenderer, Integer.toString(container.getPageNum()), 54, 5, 0xffffff);
        drawCenteredString(fontRenderer, Integer.toString(part.layer), 39, 18, 0xffffff);

        if (part.IsLock()) {
            GL11.glTranslated(0, 0, 1000);
            drawRect(3, 30, 77, height - 60, 0x50aa0000); // bottom
            GL11.glTranslated(0, 0, -1000);
        }
    }

    @Override
    public void drawScreen(int x, int y, float partialTick)
    {
        super.drawScreen(x, y, partialTick);
        for (int slotIdx = 0; slotIdx < this.inventorySlots.inventorySlots.size(); ++slotIdx)
        {
            Slot slot = this.inventorySlots.inventorySlots.get(slotIdx);
            if (isMouseOverSlot(slot, x, y) && slot instanceof slotFerrisCore)
            {
                String name = part.connectors.get(slot.getSlotIndex()).GetName();
                drawConnectorTarget(slot.getSlotIndex(), name, partialTick);
                drawHoveringText(Collections.singletonList(name), x, y-12, fontRenderer);
                break;
            }
        }
    }

    private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY)
    {
        return this.isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
    }



    private final Vec3d TargetCorePos = new Vec3d();
    private void drawConnectorTarget(int connectorIdx, String name, float partialTick)
    {
        Connector c = part.connectors.get(connectorIdx);
        Connector.Fix(TargetCorePos, c, partialTick);

        TargetCorePos.sub(part.connectorFromParent.Current());


        EntityPlayer player = mc.player;

        double yaw = Math.Lerp(partialTick, mc.player.prevRotationYaw, mc.player.rotationYaw);
        double pitch = Math.Lerp(partialTick, mc.player.prevRotationPitch, mc.player.rotationPitch);
        TargetCorePos.Rotate(Vec3d.Up, Math.toRadians(yaw));
        TargetCorePos.Rotate(Vec3d.Right, Math.toRadians(pitch));

        float fov = mc.gameSettings.fovSetting;
        double cot = 1f / java.lang.Math.tan(Math.toRadians(fov * 0.5));
        double compressRatio = cot * (this.width / 4f) / (GetCameraDistance()+TargetCorePos.z);
        int x = -(int)(TargetCorePos.x * compressRatio) + this.width / 2 + 4;
        int y = -(int)(TargetCorePos.y * compressRatio) + this.height / 2 - 3;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawRect(x-2, y-2, x+50, y+10, 0xD0101010); // bottom
        drawString(fontRenderer, "< "+name, x, y, -1);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Deprecated
	public void SendMessageToSetParam(int flag, float param)
    {
        MessageFerrisMisc packet = new MessageFerrisMisc(part.controller, flag, 0, param);
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }
    public void SyncToServer()
    {
        shadow.WriteAll();
        MFW_PacketHandler.INSTANCE.sendToServer(new MessageSyncNbtForMFWCtS(part, shadow.GetNbtTag()));
    }
    public void Act(Action action)
    {
        MFW_PacketHandler.INSTANCE.sendToServer(new MessageSyncNbtForMFWCtS(part, action));
    }

    private void OpenChildGui(int index)
    {
        if(!part.canOpenChildGUI(index)) return;
        FerrisPartBase child = part.GetChildren()[index];
//        part.SetSelectedPartInGUI(child);

        GUIHandler.OpenBlockModelGuiInClient(child);
//        MessageOpenModelGui packet = new MessageOpenModelGui(part.GetSelectedPartInGUI().GetCommonAddress());
//        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }

    private void BackParentGui()
    {
        if(part.isRoot())
        {
            Close();
            mc.player.closeScreen();
            return;
        }
//        part.BackSelectedPart();

        GUIHandler.OpenBlockModelGuiInClient(part.GetParent());
//        MessageOpenModelGui packet = new MessageOpenModelGui(part.GetSelectedPartInGUI().GetCommonAddress());
//        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }

    private void ChangeSlotPage(int add)
    {
        container.changePage(add);
        SetEnableOpenChildButtonByID();
    }


}