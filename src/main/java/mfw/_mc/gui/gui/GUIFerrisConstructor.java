package mfw._mc.gui.gui;

import mochisystems._mc.gui.GuiToggleButton;
import mochisystems._mc.gui.GUIBlockModelerBase;
import mochisystems._mc.tileentity.TileEntityBlocksScannerBase;
import mochisystems.blockcopier.message.MessageChangeLimitLine;
import mochisystems.blockcopier.message.PacketHandler;
import mochisystems.math.Vec3d;
import mochisystems.util.gui.*;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import mfw._mc.gui.container.ContainerFerrisConstructor;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import net.minecraft.entity.player.InventoryPlayer;

import static mfw.message.MessageFerrisMisc.*;

public class GUIFerrisConstructor extends GUIBlockModelerBase {

    private GuiGroupCanvas Canvas = new GuiGroupCanvas();
    private TileEntityBlocksScannerBase tile;

    protected ContainerFerrisConstructor container;

    private int blockPosX;
    private int blockPosY;
    private int blockPosZ;

    public GUIFerrisConstructor(int x, int y, int z, InventoryPlayer playerInventory, TileEntityBlocksScannerBase tile)
    {
        super(x, y, z, new ContainerFerrisConstructor(playerInventory, tile));
        this.tile = tile;
        blockPosX = x;
        blockPosY = y;
        blockPosZ = z;
        container = (ContainerFerrisConstructor) inventorySlots;
    }

    protected void UpdateCameraPosFromCore(Vec3d dest, float tick)
    {
       dest.CopyFrom(Vec3d.Zero);
    }
 
	@Override
	public void initGui()
    {
        super.initGui();
        Canvas.Init();

        int gDef = -1;
        GuiFormatedTextField field = new GuiFormatedTextField(fontRendererObj, (width-95)/2, 4, 95, 12, 0xffffff,40,
                () -> tile.modelName,
                t -> true,
                t -> tile.modelName = t);
        Canvas.Register(gDef, field);

        Canvas.Register(gDef, new GuiLabel("Length", fontRendererObj, 2, 20, 0xffffff));
        Canvas.Register(gDef, new GuiFormatedTextField(fontRendererObj, 50, 20, 50, 10, 0xffffff, 5,
                () -> String.format("%d", tile.getLimitFrameLength()),
                s -> s.matches(GuiFormatedTextField.regexNumber),
                t -> ChangeLimitLine(MessageChangeLimitLine.Length, Integer.parseInt(t))));
        GuiUtil.addButton6(Canvas, gDef,42, 30,
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, tile.getLimitFrameLength()-100),
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, tile.getLimitFrameLength()-10),
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, tile.getLimitFrameLength()-2),
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, tile.getLimitFrameLength()+2),
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, tile.getLimitFrameLength()+10),
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, tile.getLimitFrameLength()+100)
        );

        Canvas.Register(gDef, new GuiLabel("Width", fontRendererObj, 2, 50, 0xffffff));
        Canvas.Register(gDef, new GuiFormatedTextField(fontRendererObj, 50, 50, 50, 10, 0xffffff, 5,
                () -> String.format("%d", tile.getLimitFrameWidth()),
                s -> s.matches(GuiFormatedTextField.regexNumber),
                t -> ChangeLimitLine(MessageChangeLimitLine.Width, Integer.parseInt(t))));
        GuiUtil.addButton6(Canvas, gDef,42, 60,
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, tile.getLimitFrameWidth()-100),
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, tile.getLimitFrameWidth()-10),
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, tile.getLimitFrameWidth()-2),
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, tile.getLimitFrameWidth()+2),
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, tile.getLimitFrameWidth()+10),
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, tile.getLimitFrameWidth()+100)
        );

//        drawString(this.fontRendererObj, String.format("Width   %d",tile.getLimitFrameWidth()), 2, 50, 0xffffff);
        Canvas.Register(gDef, new GuiLabel("Height", fontRendererObj, 2, 80, 0xffffff));
        Canvas.Register(gDef, new GuiFormatedTextField(fontRendererObj, 50, 80, 50, 10, 0xffffff, 5,
                () -> String.format("%d", tile.getLimitFrameHeight()),
                s -> s.matches(GuiFormatedTextField.regexNumber),
                t -> ChangeLimitLine(MessageChangeLimitLine.Height, Integer.parseInt(t))));
        GuiUtil.addButton6(Canvas, gDef,42, 90,
                () -> ChangeLimitLine(MessageChangeLimitLine.Height, tile.getLimitFrameHeight()-100),
                () -> ChangeLimitLine(MessageChangeLimitLine.Height, tile.getLimitFrameHeight()-10),
                () -> ChangeLimitLine(MessageChangeLimitLine.Height, tile.getLimitFrameHeight()-2),
                () -> ChangeLimitLine(MessageChangeLimitLine.Height, tile.getLimitFrameHeight()+2),
                () -> ChangeLimitLine(MessageChangeLimitLine.Height, tile.getLimitFrameHeight()+10),
                () -> ChangeLimitLine(MessageChangeLimitLine.Height, tile.getLimitFrameHeight()+100)
        );


//        GuiUtil.addButton1(Canvas, buttonList, 0, 60, 26, width - 70, height - 50, "Create!", MessageFerrisMisc.GUIConstruct);
        Canvas.Register(-1,
                new GuiButtonWrapper(0,  width - 70, height - 44, 60, 26, "Create!",
                        tile::startConstructWheel));


//        GuiUtil.addButton2(Canvas, buttonList, 0, width-70, 14, "copy", GUIAddCopyNum);
        GuiUtil.addButton2(Canvas,-1,width-70, 14, "copy", GUIAddCopyNum,
                () -> SendMessageForIndex(GUIAddCopyNum, -1),
                () -> SendMessageForIndex(GUIAddCopyNum, 1) );


        GuiUtil.addCheckButton(Canvas, fontRendererObj, -1, width - 20, 42, tile.FlagDrawCore,
                "draw core",
                isOn -> SendMessageForIndex(GUIDrawCoreFlag, 0));
        GuiUtil.addCheckButton(Canvas, fontRendererObj, -1, width - 20, 82, tile.FlagDrawEntity,
                "draw Mobs",
                isOn -> SendMessageForIndex(GUIDrawEntityFlag, 0));
        GuiUtil.addCheckButton(Canvas, fontRendererObj, -1, width - 20, 122, tile.isCoreConnector,
                StatCollector.translateToLocal("gui.constructor.switch.coreconnector"),
                isOn -> SendMessageForIndex(GUIToggleCoreIsConnector, 0));

//        GuiUtil.addButton1(Canvas, buttonList, 0, 40, 16, width - 42, 132, tile.copyMode != 0 ? "Clone" : "Add", MessageFerrisMisc.GUICopyModeChange);
        Canvas.Register(-1,
                new GuiToggleButton(0,  width - 42, 162,  40, 16, "Clone", "Add", tile.copyMode !=0,
                        isOn -> SendMessageForIndex(GUICopyModeChange, 0)));

    }

    private void SendMessageForIndex(int flag, int index)
    {
        MessageFerrisMisc packet = new MessageFerrisMisc(blockPosX, blockPosY, blockPosZ, flag, index, 0);
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }

    @Override
	public void onGuiClosed()
    {
		super.onGuiClosed();
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

        mx = width - 92; my = height - 42;
        xx = mx + 20; xy = my + 20;
        drawRect(mx, my, xx, xy, color);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
    {
        Canvas.DrawContents(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);
        Canvas.Update();

        this.fontRendererObj.drawString("Name:", width/2-76, 4, 0xB0B0B0);
        drawString(this.fontRendererObj, "RotateCopy", width-76, 3, 0xffffff);
        drawString(this.fontRendererObj, "Copy Mode", width-76, 153, 0xffffff);
        drawRightedString(this.fontRendererObj, Integer.toString(tile.copyNum), width-2, 15, 0xffffff);
        drawRightedString(fontRendererObj, (int)(100*tile.getCookProgress())+"%", width - 70, height-15, 0xffffff);
    }

    
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }


	private void ChangeLimitLine(int dirType, int add)
    {
        MessageChangeLimitLine m = new MessageChangeLimitLine(blockPosX, blockPosY, blockPosZ, dirType, add);
        PacketHandler.INSTANCE.sendToServer(m);
    }
	
    ////////////////////////////text field//////////////////////////////////
	/**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char c, int keyCode)
    {
        if (! Canvas.KeyTyped(c, keyCode))
        {
            super.keyTyped(c, keyCode);
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    public void mouseClicked(int x, int y, int buttonId)
    {
        super.mouseClicked(x, y, buttonId);
        Canvas.MouseClicked(x, y, buttonId);
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
