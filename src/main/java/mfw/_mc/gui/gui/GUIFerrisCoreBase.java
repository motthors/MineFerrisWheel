package mfw._mc.gui.gui;

import java.util.*;

import mfw.ferriswheel.Connector;
import mfw.ferriswheel.FerrisPartAddress;
import mfw.ferriswheel.FerrisPartBase;

import mfw.message.MessageMoveInGui;
import mochisystems._mc.gui.GuiToggleButton;
import mfw._mc.gui.container.ContainerFerrisCore;
import mfw._mc.gui.slot.slotFerrisCore;
import mfw.manager.SyncTargetRegisterManager;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import mfw.message.MessageRegistSyncRotParentCtS;
import mochisystems._mc.gui.GUIBlockModelerBase;
import mochisystems.math.Vec3d;
import mochisystems.util.gui.GuiButtonWrapper;
import mochisystems.util.gui.GuiGroupCanvas;
import mochisystems.util.gui.GuiLabel;
import mochisystems.util.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import static mfw.message.MessageFerrisMisc.GUICoreLock;

public abstract class GUIFerrisCoreBase extends GUIBlockModelerBase {

    protected GuiGroupCanvas Canvas = new GuiGroupCanvas();
    protected FerrisPartBase part;
    protected ContainerFerrisCore container;

    protected int blockposX;
    protected int blockposY;
    protected int blockposZ;

    protected final int PageGroupIdOffset = 999;


	public GUIFerrisCoreBase(int x, int y, int z, InventoryPlayer playerInventory, FerrisPartBase part)
	{
		super(x, y, z, new ContainerFerrisCore(playerInventory, part));
		this.part = part;
        blockposX = x;
        blockposY = y;
        blockposZ = z;
		container = (ContainerFerrisCore) inventorySlots;
	}

    protected void UpdateCameraPosFromCore(Vec3d dest, float tick)
    {
        Connector.Fix(dest, part.connectorFromParent, tick);
    }


    // GUIを開くたび呼ばれる初期化関数
	@Override
	public void initGui()
	{
		super.initGui();
        Canvas.Init();
        container.SetWindowSize(width, height);

        Canvas.Register(-1,
                new GuiButtonWrapper(0, 2, 2, 29, 12, "back", this::BackParentGui));

        GuiUtil.addButton2(Canvas,-1,32, 2, "", MessageFerrisMisc.GUICoreSlotPage,
                () -> ChangeSlotPage(-1),
                () -> ChangeSlotPage(1) );

//        GuiUtil.addButton1(Canvas, buttonList, -1,40, 13, 82, 2, "unlock", GUICoreLock, true, part.IsLock(), "lock");
        Canvas.Register(-1,
                new GuiToggleButton(0,  82, 2,40, 13, "unlock", "lock",
                        () -> part.IsLock(),
                        isOn -> SendMessageToSetParam(GUICoreLock, 0)));

        Canvas.Register(-1, new GuiLabel("Layer", fontRendererObj, 4, 18, 0xffffff));

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

    static String names[] = {
            StatCollector.translateToLocal("gui.core.switch.rsmode0"),
            StatCollector.translateToLocal("gui.core.switch.rsmode1"),
            StatCollector.translateToLocal("gui.core.switch.rsmode2"),
            StatCollector.translateToLocal("gui.core.switch.rsmode3"),
            StatCollector.translateToLocal("gui.core.switch.rsmode4")
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
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
    {
        Canvas.DrawContents(mouseX, mouseY);
    }

	/*GUIの文字等の描画処理*/
    //String stringRotMode;
    String stringRSMode;
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        Canvas.Update();

        int slotNum = part.GetChildren().length;
        int start = container.getPageNum() * 4*6;
        int end = start + 4 * 6;
        end = (slotNum < end) ? slotNum : end;
        int i = 0;
        for(int idx = container.getPageNum() * 4*6; idx < end; ++idx)
        {
            String Char = part.connectors[idx].GetName();
            if(Char != null && !Char.isEmpty()) Char = Char.substring(0, 1);
            drawCenteredString(fontRendererObj, Char, i % 4 * 18 + 14, (i / 4) * 26 + 26, 0x808080);
            i++;
        }
        drawCenteredString(fontRendererObj, Integer.toString(container.getPageNum()),54,5,0xffffff);
        drawCenteredString(fontRendererObj, Integer.toString(part.layer),39,18,0xffffff);
    }

    @Override
    public void drawScreen(int x, int y, float partialTick)
    {
        super.drawScreen(x, y, partialTick);
        for (int slotIdx = 0; slotIdx < this.inventorySlots.inventorySlots.size(); ++slotIdx)
        {
            Slot slot = (Slot) this.inventorySlots.inventorySlots.get(slotIdx);
            if (isMouseOverSlot(slot, x, y) && slot instanceof slotFerrisCore)
            {
                String name = part.connectors[slot.getSlotIndex()].GetName();
                drawConnectorTarget(slot.getSlotIndex(), name, partialTick);
                drawHoveringText(Arrays.asList(new String[]{name}), x, y-12, fontRendererObj);
                break;
            }
        }
    }

    private boolean isMouseOverSlot(Slot p_146981_1_, int p_146981_2_, int p_146981_3_)
    {
        return this.func_146978_c(p_146981_1_.xDisplayPosition, p_146981_1_.yDisplayPosition, 16, 16, p_146981_2_, p_146981_3_);
    }

    public void mouseClicked(int x, int y, int buttonId)
    {
        super.mouseClicked(x, y, buttonId);
        Canvas.MouseClicked(x, y, buttonId);
    }

    protected void keyTyped(char c, int keyCode)
    {
        if (! Canvas.KeyTyped(c, keyCode))
        {
            super.keyTyped(c, keyCode);
        }
    }

    private Vec3d TargetCorePos = new Vec3d();
    private void drawConnectorTarget(int connectorIdx, String name, float partialTick)
    {
        Connector c = part.connectors[connectorIdx];
        Connector.Fix(TargetCorePos, c, partialTick);

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        TargetCorePos.Rotate(Vec3d.Up, Math.toRadians(player.rotationYaw));
        TargetCorePos.Rotate(Vec3d.Right, Math.toRadians(player.rotationPitch));

        float fov = mc.gameSettings.fovSetting;
        double cot = 1f / Math.tan(Math.toRadians(fov * 0.5));
        double compressRatio = cot * (this.width / 4) / (GetCameraDistance()+TargetCorePos.z);
        int x = -(int)(TargetCorePos.x * compressRatio) + this.width / 2 + 4;
        int y = -(int)(TargetCorePos.y * compressRatio) + this.height / 2 - 3;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawRect(x-2, y-2, x+50, y+10, 0xD0101010); // bottom
        drawString(fontRendererObj, "< "+name, x, y, -1);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }


	public FontRenderer GetFontRenderer(){
		return fontRendererObj;
	}

	public void SendMessageToSetParam(int flag, float param)
    {
        MessageFerrisMisc packet = new MessageFerrisMisc(blockposX, blockposY, blockposZ, flag, 0, param);
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }

    private void OpenChildGui(int index)
    {
        if(!part.canOpenChildGUI(index)) return;
        part.SetSelectedPartInGUI(part.GetChildren()[index]);

        MessageMoveInGui packet = new MessageMoveInGui(blockposX, blockposY, blockposZ).Open(index);
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }

    private void BackParentGui()
    {
        if(part.isRoot())
        {
            Close();
            mc.thePlayer.closeScreen();
            return;
        }
        part.BackSelectedPart();

        MessageMoveInGui packet = new MessageMoveInGui(blockposX, blockposY, blockposZ).Back();
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }

    private void ChangeSlotPage(int add)
    {
        container.changePage(add);
        SetEnableOpenChildButtonByID();
    }

    protected void RegisterSyncParent()
    {
        if(false /*part.childSyncTileList.size() != 0*/) // TODO 循環参照を見つけるようにしたい
        {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
                    StatCollector.translateToLocal("message.coreGUI.sync.cannotregisttoparent")
                    // 同期回転の親になっている人は同期設定ができません
            ));
            return;
        }

        FerrisPartAddress parentAddress = SyncTargetRegisterManager.INSTANCE.GetSavedTarget();
        FerrisPartAddress childAddress = part.GetMyAddress();
        if(parentAddress == null || parentAddress.Equals(childAddress) ) return;
        MessageRegistSyncRotParentCtS packet = new MessageRegistSyncRotParentCtS(parentAddress, childAddress);
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
        part.SetNewSyncParent(parentAddress);
    }
}