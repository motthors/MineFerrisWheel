package mfw._mc.gui;

import mochisystems.util.gui.IGuiElement;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw.storyboard.programpanel.IProgramPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class CustomTexButton extends GuiButtonExt implements IGuiElement {

    protected Runnable action;
    private ResourceLocation customtexture;

	public CustomTexButton(ResourceLocation tex, int xPos, int yPos, int width, int height, Runnable action) {
		super(0, xPos, yPos, width, height, "");
        customtexture = tex;
        this.action = action;
    }

    @Override
    public void Clicked()
    {
        action.run();
    }

	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(customtexture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            DrawTexture(this.xPosition, 			this.yPosition, this.width,  this.height, 	0, 		-0.5f+0.5f*k,  1f,  0.5f);
//            this.DrawTexture(this.xPosition+20, 		this.yPosition, 100, this.height, 	0.5f,   -0.5f+0.5f*k,  0.1f,  0.5f);
//            this.DrawTexture(this.xPosition+width-20, 	this.yPosition, 20,  this.height, 	0.8f,	-0.5f+0.5f*k,  0.2f,  0.5f);
            this.mouseDragged(mc, mouseX, mouseY);
            int l = 14737632;

            if (packedFGColour != 0)
            {
                l = packedFGColour;
            }
            else if (!this.enabled)
            {
                l = 10526880;
            }
            else if (this.field_146123_n)
            {
                l = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
        }
    }
	
	private static void DrawTexture(int xpos, int ypos, int lenx, int leny, float upos, float vpos, float pixlenx, float pixleny)
    {
//        float f4 = 1.0F / pixlenx;
//        float f5 = 1.0F / pixleny;
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)xpos, (double)(ypos + leny), 0.0D, (double)(upos), (double)((vpos + pixleny)));
        tess.addVertexWithUV((double)(xpos + lenx), (double)(ypos + leny), 0.0D, (double)((upos + pixlenx)), (double)((vpos + (float)pixleny)));
        tess.addVertexWithUV((double)(xpos + lenx), (double)ypos, 0.0D, (double)((upos + pixlenx)), (double)(vpos));
        tess.addVertexWithUV((double)xpos, (double)ypos, 0.0D, (double)(upos), (double)(vpos));
        tess.draw();
    }

    @Override
    public void SetId(int id)
    {
        this.id = id;
    }
    @Override
    public int GetId()
    {
        return id;
    }
    @Override
    public void SetPosition(int x, int y)
    {
        xPosition = x;
        yPosition = y;
    }
    @Override
    public int GetPositionX()
    {
        return xPosition;
    }
    @Override
    public int GetPositionY()
    {
        return yPosition;
    }
    @Override
    public int GetWidth(){ return width; }
    @Override
    public int GetHeight(){return height;}
    @Override
    public void SetPositionY(int y)
    {
        yPosition = y;
    }
    @Override
    public void MouseClicked(int x, int y, int buttonId){}
    @Override
    public void Draw(int mouseX, int mouseY)
    {
        drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
    }
    @Override
    public void ClickReleased(){}
}
