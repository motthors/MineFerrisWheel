package mfw._mc.gui.gui;

import mfw._mc.gui.CustomTexButton;
import mfw.storyboard.programpanel.IProgramPanel;
import mfw.storyboard.programpanel.LoopPanel;
import mochisystems.util.gui.GuiGroupCanvas;
import mochisystems.util.gui.IGuiDraggable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;


public class GuiStoryPartPanel extends CustomTexButton implements IGuiDraggable {

    private GUIStoryBoard parent;
    IProgramPanel panel;
    private boolean isLoop;
    private int startPosX;
    private GuiGroupCanvas.Group group;

    public GuiStoryPartPanel(ResourceLocation tex, GUIStoryBoard parent, IProgramPanel panel,
                             GuiGroupCanvas.Group group, boolean isLoop,
                             int xPos, int yPos, int width, int height, Consumer<GuiStoryPartPanel> action)
    {
        super(tex, xPos, yPos, width, height, null);
        this.panel = panel;
        this.group = group;
        this.parent = parent;
        this.isLoop = isLoop;
        super.action  = () -> action.accept(this);
    }

    @Override
    public void SetStartPosX(int x)
    {
        startPosX = x;
    }

    @Override
    public int GetStartPosX()
    {
        return startPosX;
    }

    @Override
    public void Clicked()
    { }

    @Override
    public void ClickReleased()
    {
        super.Clicked();
    }

    @Override
    public void DragReleased()
    {
        parent.PanelDragEnd(this);
    }

    private GuiStoryPartPanel endPanel;
    private int layerIdx;
    private int[] colors = {0xff00ffff, 0xffffff00, 0xffff00ff, 0xffff8800};

    public void SetEndPanel(GuiStoryPartPanel endPanel, int layerIdx)
    {
        this.endPanel = endPanel;
        this.layerIdx = layerIdx;
        ((LoopPanel)panel).Reconnect((LoopPanel.LoopEndPanel) endPanel.panel);
    }

    @Override
    public void Update()
    {

    }

    @Override
    public void Draw(int mouseX, int mouseY)
    {
        super.Draw(mouseX, mouseY);
        if(isLoop)
        {
            int h = 2;
            drawRect(xPosition+20, yPosition, endPanel.xPosition, yPosition+h, colors[layerIdx%colors.length]);
        }
    }
}
