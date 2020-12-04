package mfw.renderer;

import mochisystems._mc._1_7_10.bufferedrenderer.IBufferedRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

public class ElevatorCoreRenderer extends IBufferedRenderer {


    @Override
    public int GetHash()
    {
        return "MFW.FerrisElevatorCore".hashCode();
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

        double _w1 = 0.82;
        double _w0 = 0.18;
        double _h0 = 0.28;

        {
            tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
            tessellator.setNormal(0f, 1f, 0);
            tessellator.addVertexWithUV(x+0.5, y+_w1, z+0.5, cu, cv);
            tessellator.addVertexWithUV(x+0.5, y+_h0, z+_w0, mu, mv);
            tessellator.addVertexWithUV(x+_w0, y+_h0, z+0.5, xu, mv);
            tessellator.addVertexWithUV(x+0.5, y+_h0, z+_w1, xu, xv);
            tessellator.addVertexWithUV(x+_w1, y+_h0, z+0.5, mu, xv);
            tessellator.addVertexWithUV(x+0.5, y+_h0, z+_w0, mu, mv);
            tessellator.draw();
            tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
            tessellator.setNormal(0f, -1f, 0);
            tessellator.addVertexWithUV(x+0.5, y+_h0, z+_w0, mu, mv);
            tessellator.addVertexWithUV(x+_w1, y+_h0, z+0.5, xu, mv);
            tessellator.addVertexWithUV(x+0.5, y+_h0, z+_w1, xu, xv);
            tessellator.addVertexWithUV(x+_w0, y+_h0, z+0.5, mu, xv);
            tessellator.draw();
        }

        iicon = Blocks.emerald_block.getIcon(0, 0);
        mu = iicon.getMinU();
        mv = iicon.getMinV();
        xu = iicon.getMaxU();
        xv = iicon.getMaxV();
        cu = (mu+xu)/2f;
        cv = (mv+xv)/2f;
        _w1 = 1;
        _w0 = 0.0;
        _h0 = 0.24;
        {
            tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
            tessellator.setNormal(0f, -1f, 0);
            tessellator.addVertexWithUV(x+0.5, y+_w1, z+0.5, cu, cv);
            tessellator.addVertexWithUV(x+0.5, y+_h0, z+_w0, mu, mv);
            tessellator.addVertexWithUV(x+_w1, y+_h0, z+0.5, mu, xv);
            tessellator.addVertexWithUV(x+0.5, y+_h0, z+_w1, xu, xv);
            tessellator.addVertexWithUV(x+_w0, y+_h0, z+0.5, xu, mv);
            tessellator.addVertexWithUV(x+0.5, y+_h0, z+_w0, mu, mv);
            tessellator.draw();

            tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
            tessellator.setNormal(0f, 1f, 0);
            tessellator.addVertexWithUV(x+0.5, y+_h0, z+_w0, mu, mv);
            tessellator.addVertexWithUV(x+_w0, y+_h0, z+0.5, xu, mv);
            tessellator.addVertexWithUV(x+0.5, y+_h0, z+_w1, xu, xv);
            tessellator.addVertexWithUV(x+_w1, y+_h0, z+0.5, mu, xv);
            tessellator.draw();
        }
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
