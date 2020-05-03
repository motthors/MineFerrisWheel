package mfw._mc.gui.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import mfw._mc.gui.container.ContainerConnector;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import mfw._mc.tileEntity.TileEntityConnector;
import mochisystems.util.gui.GuiButtonWrapper;
import mochisystems.util.gui.GuiFormatedTextField;
import mochisystems.util.gui.GuiGroupCanvas;
import mochisystems.util.gui.GuiLabel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import org.apache.commons.io.Charsets;
import org.lwjgl.opengl.GL11;

import static mfw.message.MessageFerrisMisc.GUIOpenStoryBoard;

public class GUIConnector extends GuiContainer {

    private TileEntityConnector tile;
    private GuiGroupCanvas Canvas = new GuiGroupCanvas();
//    public GuiTextField textField;
    private int posX, posY, posZ;

    private int slotActiveMode;

    private final int wheelGroup = 1;
    private final int elevatorGroup = 2;


    public GUIConnector(int x, int y, int z, InventoryPlayer invPlayer, TileEntityConnector tile)
    {
        super(new ContainerConnector(invPlayer, tile));
        posX = x;
        posY = y;
        posZ = z;
        this.tile = tile;
        xSize = 420;
        ySize = 270;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        Canvas.Init();
        int centerW = this.width / 2;
        int centerH = this.height / 2;
        int nameTextWidth = 80;
        int nameTextHeight = 10;
        int offset = 4;


        GuiFormatedTextField txt = new GuiFormatedTextField(fontRendererObj,
                        centerW-nameTextWidth+offset, 80+offset,
                        (nameTextWidth-offset) * 2, (nameTextHeight-offset) * 2, 0xffffff, 22,
                        () -> tile.GetName(),
                        s -> true,
                        t -> tile.SetName(t));
        txt.setEnableBackgroundDrawing(true);
        Canvas.Register(-1, txt);

        /////////////////// wheel

        Canvas.MoveGroup(wheelGroup, width - 80, 0, false);

        Canvas.Register(wheelGroup, new GuiLabel("Accel:", fontRendererObj, -5, 10, 0xffffff));
        Canvas.Register(wheelGroup, new GuiLabel("Speed:", fontRendererObj, -5, 30, 0xffffff));
        Canvas.Register(wheelGroup, new GuiLabel("Angle:", fontRendererObj, -5, 50, 0xffffff));

        Canvas.Register(wheelGroup,
                new GuiFormatedTextField(fontRendererObj, 25, 10, 30, 11, 0xffffff, 12,
                        () -> String.format("%7.1f", tile.wheel.GetAccel()),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorWAccel, Float.parseFloat(t))));
        Canvas.Register(wheelGroup,
                new GuiFormatedTextField(fontRendererObj, 25, 30, 30, 11, 0xffffff, 12,
                        () -> String.format("%7.1f", tile.wheel.GetSpeed()),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorWSpeed, Float.parseFloat(t))));
        Canvas.Register(wheelGroup,
                new GuiFormatedTextField(fontRendererObj, 25, 50, 30, 11, 0xffffff, 12,
                        () -> String.format("%7.1f", tile.wheel.rotAngle.get()),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorWLength, Float.parseFloat(t))));

        GUIFerrisWheel.Scale(Canvas, fontRendererObj, 0, 75,
                tile.wheel, wheelGroup, MessageFerrisMisc.GuiConnectorWScale,
                () -> String.format("%7.2f", tile.wheel.localScale),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorWScale, t));

        GUIFerrisWheel.Rotate(Canvas, fontRendererObj, 0, 100, tile.wheel,
                wheelGroup, MessageFerrisMisc.GuiConnectorWRotateX, MessageFerrisMisc.GuiConnectorWRotateY,
                () -> String.format("%7.1f", tile.wheel.pitch),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorWRotateX, t),
                () -> String.format("%7.1f", tile.wheel.yaw),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorWRotateY, t));

        GUIFerrisWheel.Offset(Canvas, fontRendererObj, 0, 152, tile.wheel,
                wheelGroup,
                MessageFerrisMisc.GuiConnectorWOffsetX, MessageFerrisMisc.GuiConnectorWOffsetY, MessageFerrisMisc.GuiConnectorWOffsetZ,
                () -> String.format("%7.2f", tile.wheel.offset.x),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorWOffsetX, t),
                () -> String.format("%7.2f", tile.wheel.offset.y),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorWOffsetY, t),
                () -> String.format("%7.2f", tile.wheel.offset.z),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorWOffsetZ, t));

        Canvas.Register(wheelGroup, new GuiButtonWrapper(-1, -100, 5, 70, 15, "StoryBoard",
                () -> SendMessageToSetParam(GUIOpenStoryBoard, 0)));

        /////////////////// elevator

        Canvas.MoveGroup(elevatorGroup, width - 80, 0, false);

        Canvas.Register(elevatorGroup, new GuiLabel("Accel:", fontRendererObj, -5, 10, 0xffffff));
        Canvas.Register(elevatorGroup, new GuiLabel("Speed:", fontRendererObj, -5, 30, 0xffffff));
        Canvas.Register(elevatorGroup, new GuiLabel("Length:", fontRendererObj, -5, 50, 0xffffff));

        Canvas.Register(elevatorGroup,
                new GuiFormatedTextField(fontRendererObj, 25, 10, 30, 11, 0xffffff, 12,
                        () -> String.format("%7.1f", tile.elevator.GetAccel()),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorEAccel, Float.parseFloat(t))));
        Canvas.Register(elevatorGroup,
                new GuiFormatedTextField(fontRendererObj, 25, 30, 30, 11, 0xffffff, 12,
                        () -> String.format("%7.1f", tile.elevator.GetSpeed()),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorESpeed, Float.parseFloat(t))));
        Canvas.Register(elevatorGroup,
                new GuiFormatedTextField(fontRendererObj, 25, 50, 30, 11, 0xffffff, 12,
                        () -> String.format("%7.1f", tile.elevator.length.get()),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorELength, Float.parseFloat(t))));


        GUIFerrisWheel.Scale(Canvas, fontRendererObj, 0, 75, tile.elevator,
                elevatorGroup, MessageFerrisMisc.GuiConnectorEScale,
                () -> String.format("%7.2f", tile.elevator.localScale),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorEScale, t));

        GUIFerrisWheel.Rotate(Canvas, fontRendererObj, 0, 100, tile.elevator,
                elevatorGroup, MessageFerrisMisc.GuiConnectorERotateX, MessageFerrisMisc.GuiConnectorERotateY,
                () -> String.format("%7.1f", tile.elevator.pitch),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorERotateX, t),
                () -> String.format("%7.1f", tile.elevator.yaw),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorERotateY, t));

        GUIFerrisWheel.Offset(Canvas, fontRendererObj, 0, 152, tile.elevator,
                elevatorGroup,
                MessageFerrisMisc.GuiConnectorEOffsetX, MessageFerrisMisc.GuiConnectorEOffsetY, MessageFerrisMisc.GuiConnectorEOffsetZ,
                () -> String.format("%7.2f", tile.elevator.offset.x),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorEOffsetX, t),
                () -> String.format("%7.2f", tile.elevator.offset.y),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorEOffsetY, t),
                () -> String.format("%7.2f", tile.elevator.offset.z),
                t -> SendMessageToSetParam(MessageFerrisMisc.GuiConnectorEOffsetZ, t));

        Canvas.Register(elevatorGroup, new GuiButtonWrapper(-1, -100, 5, 70, 15, "StoryBoard",
                () -> SendMessageToSetParam(GUIOpenStoryBoard, 0)));

        ////////

        Canvas.ActiveGroup(-1);
        Canvas.DisableGroup(wheelGroup);
        Canvas.DisableGroup(elevatorGroup);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float tick, int mouseX, int mouseY)
    {
        Canvas.DrawContents(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        Canvas.Update();

        if(slotActiveMode != tile.slotMode)
        {
            slotActiveMode = tile.slotMode;
            switch(slotActiveMode)
            {
                case 1 :
                    Canvas.ActiveGroup(wheelGroup);
                    Canvas.DisableGroup(elevatorGroup);
                    break;
                case 2 :
                    Canvas.ActiveGroup(elevatorGroup);
                    Canvas.DisableGroup(wheelGroup);
                    break;
                default:
                    Canvas.DisableGroup(wheelGroup);
                    Canvas.DisableGroup(elevatorGroup);
                    break;
            }
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int id)
    {
        Canvas.MouseClicked(x, y, id);
        super.mouseClicked(x, y, id);
    }

    @Override
    protected void keyTyped(char c, int keycode)
    {
        if(!Canvas.KeyTyped(c, keycode)) super.keyTyped(c, keycode);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        MessageFerrisMisc packet = new MessageFerrisMisc(posX ,posY, posZ,
                MessageFerrisMisc.GUIConnectorRename, 0, 0, tile.GetName().getBytes(Charsets.UTF_8));
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }

    @Override
    public void drawWorldBackground(int p_146270_1_)
    {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        int centerW = this.width / 2;
        int centerH = this.height / 2;
        int width = 83;
        int height = 10;

        drawString(fontRendererObj, "Connector Name",
                centerW-width, 67, 0xffffff);

        this.drawGradientRect(centerW-width, centerH + 2,
                centerW + width, centerH + 60,
                0x88000000, 0x88000000);
        this.drawGradientRect(centerW-width, centerH + 62,
                centerW + width, centerH + 82,
                0x88000000, 0x88000000);

        this.drawGradientRect(centerW-width, centerH + 62,
                centerW + width, centerH + 82,
                0x88000000, 0x88000000);
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
//        if(!textField.getText().equals(tile.GetName())) textField.setText(tile.GetName());
//        this.textField.drawTextBox();

//        if(tile.isInserted())for(GuiTextField f : textFields) ChangeTextPos(f, 50);
//        else for(GuiTextField f : textFields) ChangeTextPos(f, -1000);
//
//        for(GuiTextField f : textFields) f.drawTextBox();
    }

    @Override
    public void onGuiClosed()
    {
//        writeValue();
//        tile.SetName(textField.getText());
        super.onGuiClosed();
    }


    private void SendMessageToSetParam(int flag, float param)
    {
        IMessage packet = new MessageFerrisMisc(tile.xCoord, tile.yCoord, tile.zCoord, flag, 0, param);
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }

}
