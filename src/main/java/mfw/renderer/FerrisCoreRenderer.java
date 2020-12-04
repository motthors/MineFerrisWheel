package mfw.renderer;

import mochisystems._mc._1_7_10.bufferedrenderer.IBufferedRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

public class FerrisCoreRenderer extends IBufferedRenderer {

    public FerrisCoreRenderer(){}

    @Override
    public int GetHash()
    {
        return "MFW.FerrisCore".hashCode();
    }

    protected void Draw()
    {
        double x = -0.5;
        double y = -0.5;
        double z = -0.5;
        Tessellator tessellator = Tessellator.instance;

//        GL11.GL_ENABLE_BIT;GL11.GL_QUADS
        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        IIcon iicon = Blocks.diamond_block.getIcon(0,0);
        double mu = iicon.getMinU();
        double mv = iicon.getMinV();
        double xu = iicon.getMaxU();
        double xv = iicon.getMaxV();
        double cu = (mu+xu)/2f;
        double cv = (mv+xv)/2f;

        double _w1 = 0.92;
        double _w2 = 0.08;

        {
            tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
            tessellator.setNormal(0f, 1f, 0);
            tessellator.addVertexWithUV(x+0.5, y+0.5, z+_w1, cu, cv);
            tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
            tessellator.addVertexWithUV(x+_w1, y+0.5, z+0.5, xu, mv);
            tessellator.addVertexWithUV(x+0.5, y+_w1, z+0.5, xu, xv);
            tessellator.addVertexWithUV(x+_w2, y+0.5, z+0.5, mu, xv);
            tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
            tessellator.draw();
            tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
            tessellator.setNormal(0f, -1f, 0);
            tessellator.addVertexWithUV(x+0.5, y+0.5, z+_w2, cu, cv);
            tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
            tessellator.addVertexWithUV(x+_w2, y+0.5, z+0.5, mu, xv);
            tessellator.addVertexWithUV(x+0.5, y+_w1, z+0.5, xu, xv);
            tessellator.addVertexWithUV(x+_w1, y+0.5, z+0.5, xu, mv);
            tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
            tessellator.draw();
        }

        iicon = Blocks.emerald_block.getIcon(0, 0);
        mu = iicon.getMinU();
        mv = iicon.getMinV();
        xu = iicon.getMaxU();
        xv = iicon.getMaxV();
        cu = (mu+xu)/2f;
        cv = (mv+xv)/2f;
        {
            tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
            tessellator.addVertexWithUV(x+0.5, y+0.5, z+0.0, cu, cv);
            tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
            tessellator.addVertexWithUV(x+1.0, y+0.5, z+0.5, xu, mv);
            tessellator.addVertexWithUV(x+0.5, y+1.0, z+0.5, xu, xv);
            tessellator.addVertexWithUV(x+0.0, y+0.5, z+0.5, mu, xv);
            tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
            tessellator.draw();

            tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
            tessellator.addVertexWithUV(x+0.5, y+0.5, z+1.0, cu, cv);
            tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
            tessellator.addVertexWithUV(x+0.0, y+0.5, z+0.5, mu, xv);
            tessellator.addVertexWithUV(x+0.5, y+1.0, z+0.5, xu, xv);
            tessellator.addVertexWithUV(x+1.0, y+0.5, z+0.5, xu, mv);
            tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
            tessellator.draw();
        }
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
