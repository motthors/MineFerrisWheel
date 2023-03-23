package jp.mochisystems.mfw.renderer;

import jp.mochisystems.core.bufferedRenderer.CachedBufferBase;
import jp.mochisystems.core.bufferedRenderer.RendererVbo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;

public class ElevatorCoreRenderer extends CachedBufferBase {


    @Override
    public int GetHash()
    {
        return "MFW.FerrisElevatorCore".hashCode();
    }

    @Override
    public int GetDrawMode(){return GL11.GL_TRIANGLES;}
    @Override
    protected VertexFormat GetVertexFormat(){return DefaultVertexFormats.POSITION_TEX_NORMAL;}
    @Override
    public void setupArrayPointers()
    {
        RendererVbo.SetupArrayPointersForPosTexNormal(GetVertexFormat());
    }


    protected void Compile()
    {
        double x = -0.5;
        double y = -0.5;
        double z = -0.5;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

//        GlStateManager.disableLighting();
        GlStateManager.color(1f, 1f, 1f);
        BlockModelShapes shapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
        TextureAtlasSprite icon = shapes.getTexture(Blocks.DIAMOND_BLOCK.getDefaultState());
        double mu = icon.getMinU();
        double mv = icon.getMinV();
        double xu = icon.getMaxU();
        double xv = icon.getMaxV();
        double cu = (mu+xu)/2f;
        double cv = (mv+xv)/2f;


        double _w1 = 0.88;
        double _w0 = 0.1;
        double _h0 = 0.28;

        {
            /*top*/buffer.pos(x+0.5, y+_w1, z+0.5).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*1*/buffer.pos(x+0.5, y+_h0, z+_w0).tex(mu, mv).normal(0, 0, -1).endVertex();
            /*2*/buffer.pos(x+_w0, y+_h0, z+0.5).tex(xu, mv).normal(-1, 0, 0).endVertex();
            /*top*/buffer.pos(x+0.5, y+_w1, z+0.5).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*2*/buffer.pos(x+_w0, y+_h0, z+0.5).tex(xu, mv).normal(-1, 0, 0).endVertex();
            /*3*/buffer.pos(x+0.5, y+_h0, z+_w1).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*top*/buffer.pos(x+0.5, y+_w1, z+0.5).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*3*/buffer.pos(x+0.5, y+_h0, z+_w1).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*4*/buffer.pos(x+_w1, y+_h0, z+0.5).tex(mu, xv).normal(+1, 0, 0).endVertex();
            /*top*/buffer.pos(x+0.5, y+_w1, z+0.5).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*4*/buffer.pos(x+_w1, y+_h0, z+0.5).tex(mu, xv).normal(+1, 0, 0).endVertex();
            /*1*/buffer.pos(x+0.5, y+_h0, z+_w0).tex(mu, mv).normal(0, 0, -1).endVertex();

            /*1*/buffer.pos(x+0.5, y+_h0, z+_w0).tex(mu, mv).normal(0, -1, 0).endVertex();
            /*2*/buffer.pos(x+_w1, y+_h0, z+0.5).tex(xu, mv).normal(0, -1, 0).endVertex();
            /*3*/buffer.pos(x+0.5, y+_h0, z+_w1).tex(xu, xv).normal(0, -1, 0).endVertex();
            /*1*/buffer.pos(x+0.5, y+_h0, z+_w0).tex(mu, mv).normal(0, -1, 0).endVertex();
            /*3*/buffer.pos(x+0.5, y+_h0, z+_w1).tex(xu, xv).normal(0, -1, 0).endVertex();
            /*4*/buffer.pos(x+_w0, y+_h0, z+0.5).tex(mu, xv).normal(0, -1, 0).endVertex();
        }

        icon = shapes.getTexture(Blocks.EMERALD_BLOCK.getDefaultState());
        mu = icon.getMinU();
        mv = icon.getMinV();
        xu = icon.getMaxU();
        xv = icon.getMaxV();
        cu = (mu+xu)/2f;
        cv = (mv+xv)/2f;
        _w1 = 0.95;
        _w0 = 0.0;
        _h0 = 0.24;
        {
            /*top*/buffer.pos(x+0.5, y+_w1, z+0.5).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*2*/buffer.pos(x+_w0, y+_h0, z+0.5).tex(xu, mv).normal(-1, 0, 0).endVertex();
            /*1*/buffer.pos(x+0.5, y+_h0, z+_w0).tex(mu, mv).normal(0, 0, -1).endVertex();
            /*top*/buffer.pos(x+0.5, y+_w1, z+0.5).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*3*/buffer.pos(x+0.5, y+_h0, z+_w1).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*2*/buffer.pos(x+_w0, y+_h0, z+0.5).tex(xu, mv).normal(-1, 0, 0).endVertex();
            /*top*/buffer.pos(x+0.5, y+_w1, z+0.5).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*4*/buffer.pos(x+_w1, y+_h0, z+0.5).tex(mu, xv).normal(+1, 0, 0).endVertex();
            /*3*/buffer.pos(x+0.5, y+_h0, z+_w1).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*top*/buffer.pos(x+0.5, y+_w1, z+0.5).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*1*/buffer.pos(x+0.5, y+_h0, z+_w0).tex(mu, mv).normal(0, 0, -1).endVertex();
            /*4*/buffer.pos(x+_w1, y+_h0, z+0.5).tex(mu, xv).normal(+1, 0, 0).endVertex();

            /*1*/buffer.pos(x+0.5, y+_h0, z+_w0).tex(mu, mv).normal(0, -1, 0).endVertex();
            /*3*/buffer.pos(x+0.5, y+_h0, z+_w1).tex(xu, xv).normal(0, -1, 0).endVertex();
            /*2*/buffer.pos(x+_w1, y+_h0, z+0.5).tex(xu, mv).normal(0, -1, 0).endVertex();
            /*1*/buffer.pos(x+0.5, y+_h0, z+_w0).tex(mu, mv).normal(0, -1, 0).endVertex();
            /*4*/buffer.pos(x+_w0, y+_h0, z+0.5).tex(mu, xv).normal(0, -1, 0).endVertex();
            /*3*/buffer.pos(x+0.5, y+_h0, z+_w1).tex(xu, xv).normal(0, -1, 0).endVertex();
        }

    }
}
