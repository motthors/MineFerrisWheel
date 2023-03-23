package jp.mochisystems.mfw._mc.gui;

import jp.mochisystems.core.util.gui.IGuiElement;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

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

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
	{
		if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            Minecraft.getMinecraft().getTextureManager().bindTexture(customtexture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            DrawTexture(x, y, width,height,0,-0.5f+0.5f*k,  1f,  0.5f);
            this.mouseDragged(mc, mouseX, mouseY);
            int color = 0xE0E0E0;

            if (packedFGColour != 0)
            {
                color = packedFGColour;
            }
            else if (!this.enabled)
            {
                color = 10526880;
            }
            else if (this.hovered)
            {
                color = 16777120;
            }

            this.drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
        }
    }
	
	private static void DrawTexture(int xpos, int ypos, int lenx, int leny, float upos, float vpos, float pixlenx, float pixleny)
    {
//        float f4 = 1.0F / pixlenx;
//        float f5 = 1.0F / pixleny;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(xpos, ypos + leny, 0.0D).tex(upos, vpos + pixleny).endVertex();
        bufferbuilder.pos(xpos + lenx, ypos + leny, 0.0D).tex(upos + pixlenx, vpos + pixleny).endVertex();
        bufferbuilder.pos(xpos + lenx, ypos, 0.0D).tex(upos + pixlenx, vpos).endVertex();
        bufferbuilder.pos(xpos, ypos, 0.0D).tex(upos, vpos).endVertex();
        tessellator.draw();
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
        this.x = x;
        this.y = y;
    }
    @Override
    public int GetPositionX()
    {
        return x;
    }
    @Override
    public int GetPositionY()
    {
        return y;
    }
    @Override
    public int GetWidth(){ return width; }
    @Override
    public int GetHeight(){return height;}
    @Override
    public void SetPositionY(int y)
    {
        y = y;
    }
    @Override
    public void Update()
    {

    }
    @Override
    public void Draw(int mouseX, int mouseY)
    {
        drawButton(Minecraft.getMinecraft(), mouseX, mouseY, 0);
    }
    @Override
    public void ClickReleased(){}
}
