package jp.mochisystems.mfw._mc.gui.gui;

import jp.mochisystems.core._mc.gui.container.DefContainer;
import jp.mochisystems.core.util.gui.GuiButtonWrapper;
import jp.mochisystems.core.util.gui.GuiFormattedTextField;
import jp.mochisystems.core.util.gui.GuiGroupCanvas;
import jp.mochisystems.core.util.gui.GuiLabel;
import jp.mochisystems.mfw._mc.message.MFW_PacketHandler;
import jp.mochisystems.mfw._mc.message.MessageFerrisMisc;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.io.IOException;


public class GUIGarlandInit extends GuiContainer {

    protected GuiGroupCanvas Canvas = new GuiGroupCanvas();
    String garlandId = "";

    public GUIGarlandInit() {
        super(new DefContainer());
    }


    @Override
    public void initGui()
    {
        super.initGui();
        Canvas.Init();

        Canvas.Register(-1, new GuiLabel("ID", fontRenderer, width/2-4, 80, -1));
        Canvas.Register(-1, new GuiFormattedTextField(0, fontRenderer, width/2-40, 92, 80, 12, -1, 100,
                () -> garlandId,
                t -> true,
                t -> garlandId = t));
        Canvas.Register(-1, new GuiButtonWrapper(0, width/2+42, 92, 30, 12, "Snap",
                () -> {
                    MFW_PacketHandler.INSTANCE.sendToServer(new MessageFerrisMisc(0, 0, 0, MessageFerrisMisc.GarlandSnap, 0, 0, garlandId.getBytes()));
                    this.mc.player.closeScreen();
                }));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void drawWorldBackground(int tint)
    {
        drawRect(width/2-42, 102, width/2+42, 90, 0xD0101010);
        drawRect(0, 0, width, height, 0xD0101010);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Canvas.Update();
        Canvas.DrawContents(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char c, int keyCode) throws IOException {
        if (Canvas.KeyTyped(c, keyCode)) return;
        super.keyTyped(c, keyCode);
    }
    @Override
    public void mouseClicked(int x, int y, int buttonId) throws IOException
    {
        if(!Canvas.MouseClicked(x, y, buttonId)) {
            super.mouseClicked(x, y, buttonId);
        }
    }
    @Override
    protected void mouseReleased(int x, int y, int state)
    {
        super.mouseReleased(x, y, state);
        Canvas.mouseReleased(x, y, state);
    }
}
