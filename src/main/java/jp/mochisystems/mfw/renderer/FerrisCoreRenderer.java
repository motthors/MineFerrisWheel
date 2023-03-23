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

public class FerrisCoreRenderer extends CachedBufferBase {

    public FerrisCoreRenderer(){}

    @Override
    public int GetDrawMode(){return GL11.GL_TRIANGLES;}
    @Override
    protected VertexFormat GetVertexFormat(){return DefaultVertexFormats.POSITION_TEX_NORMAL;}
    @Override
    public void setupArrayPointers()
    {
        RendererVbo.SetupArrayPointersForPosTexNormal(GetVertexFormat());
    }


    @Override
    public int GetHash()
    {
        return "MFW.FerrisCore".hashCode();
    }

    @Override
    public void PreRender(){
//        GlStateManager.translate(-20, -20, -20);
    }

    protected void Compile() {
        double x = - 0.5;
        double y = - 0.5;
        double z = - 0.5;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        GlStateManager.color(1f, 1f, 1f);
        BlockModelShapes shapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
        TextureAtlasSprite icon = shapes.getTexture(Blocks.DIAMOND_BLOCK.getDefaultState());
        double mu = icon.getMinU();
        double mv = icon.getMinV();
        double xu = icon.getMaxU();
        double xv = icon.getMaxV();
        double cu = (mu + xu) / 2f;
        double cv = (mv + xv) / 2f;

        double _w1 = 0.92;
        double _w2 = 0.08;

        {
//            builder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_NORMAL);
            /*t*/builder.pos(x + 0.5, y + 0.5, z + _w1).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*1*/builder.pos(x + 0.5, y + _w2, z + 0.5).tex(mu, mv).normal(0, 0, -1).endVertex();
            /*2*/builder.pos(x + _w1, y + 0.5, z + 0.5).tex(xu, mv).normal(-1, 0, 0).endVertex();
            /*t*/builder.pos(x + 0.5, y + 0.5, z + _w1).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*2*/builder.pos(x + _w1, y + 0.5, z + 0.5).tex(xu, mv).normal(-1, 0, 0).endVertex();
            /*3*/builder.pos(x + 0.5, y + _w1, z + 0.5).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*t*/builder.pos(x + 0.5, y + 0.5, z + _w1).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*3*/builder.pos(x + 0.5, y + _w1, z + 0.5).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*4*/builder.pos(x + _w2, y + 0.5, z + 0.5).tex(mu, xv).normal(+1, 0, 0).endVertex();
            /*t*/builder.pos(x + 0.5, y + 0.5, z + _w1).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*4*/builder.pos(x + _w2, y + 0.5, z + 0.5).tex(mu, xv).normal(+1, 0, 0).endVertex();
            /*1*/builder.pos(x + 0.5, y + _w2, z + 0.5).tex(mu, mv).normal(0, 0, -1).endVertex();
//            tessellator.draw();
//            builder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_NORMAL);
            /*b*/builder.pos(x + 0.5, y + 0.5, z + _w2).tex(cu, cv).normal(0, -1, 0).endVertex();
            /*1*/builder.pos(x + 0.5, y + _w2, z + 0.5).tex(mu, mv).normal(0, 0, -1).endVertex();
            /*2*/builder.pos(x + _w2, y + 0.5, z + 0.5).tex(mu, xv).normal(-1, 0, 0).endVertex();
            /*b*/builder.pos(x + 0.5, y + 0.5, z + _w2).tex(cu, cv).normal(0, -1, 0).endVertex();
            /*2*/builder.pos(x + _w2, y + 0.5, z + 0.5).tex(mu, xv).normal(-1, 0, 0).endVertex();
            /*3*/builder.pos(x + 0.5, y + _w1, z + 0.5).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*b*/builder.pos(x + 0.5, y + 0.5, z + _w2).tex(cu, cv).normal(0, -1, 0).endVertex();
            /*3*/builder.pos(x + 0.5, y + _w1, z + 0.5).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*4*/builder.pos(x + _w1, y + 0.5, z + 0.5).tex(xu, mv).normal(+1, 0, 0).endVertex();
            /*b*/builder.pos(x + 0.5, y + 0.5, z + _w2).tex(cu, cv).normal(0, -1, 0).endVertex();
            /*4*/builder.pos(x + _w1, y + 0.5, z + 0.5).tex(xu, mv).normal(+1, 0, 0).endVertex();
            /*1*/builder.pos(x + 0.5, y + _w2, z + 0.5).tex(mu, mv).normal(0, 0, -1).endVertex();
//            tessellator.draw();
        }

        icon = shapes.getTexture(Blocks.EMERALD_BLOCK.getDefaultState());
        mu = icon.getMinU();
        mv = icon.getMinV();
        xu = icon.getMaxU();
        xv = icon.getMaxV();
        cu = (mu + xu) / 2f;
        cv = (mv + xv) / 2f;
        {
//            builder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_NORMAL);
            /*t*/builder.pos(x + 0.5, y + 0.5, z + 0.0).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*1*/builder.pos(x + 0.5, y + 0.0, z + 0.5).tex(mu, mv).normal(0, 0, -1).endVertex();
            /*2*/builder.pos(x + 1.0, y + 0.5, z + 0.5).tex(xu, mv).normal(-1, 0, 0).endVertex();
            /*t*/builder.pos(x + 0.5, y + 0.5, z + 0.0).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*2*/builder.pos(x + 1.0, y + 0.5, z + 0.5).tex(xu, mv).normal(-1, 0, 0).endVertex();
            /*3*/builder.pos(x + 0.5, y + 1.0, z + 0.5).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*t*/builder.pos(x + 0.5, y + 0.5, z + 0.0).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*3*/builder.pos(x + 0.5, y + 1.0, z + 0.5).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*4*/builder.pos(x + 0.0, y + 0.5, z + 0.5).tex(mu, xv).normal(+1, 0, 0).endVertex();
            /*t*/builder.pos(x + 0.5, y + 0.5, z + 0.0).tex(cu, cv).normal(0, +1, 0).endVertex();
            /*4*/builder.pos(x + 0.0, y + 0.5, z + 0.5).tex(mu, xv).normal(+1, 0, 0).endVertex();
            /*1*/builder.pos(x + 0.5, y + 0.0, z + 0.5).tex(mu, mv).normal(0, 0, -1).endVertex();
//            tessellator.draw();

//            builder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_NORMAL);
            /*b*/builder.pos(x + 0.5, y + 0.5, z + 1.0).tex(cu, cv).normal(0, -1, 0).endVertex();
            /*1*/builder.pos(x + 0.5, y + 0.0, z + 0.5).tex(mu, mv).normal(0, 0, -1).endVertex();
            /*2*/builder.pos(x + 0.0, y + 0.5, z + 0.5).tex(mu, xv).normal(-1, 0, 0).endVertex();
            /*b*/builder.pos(x + 0.5, y + 0.5, z + 1.0).tex(cu, cv).normal(0, -1, 0).endVertex();
            /*2*/builder.pos(x + 0.0, y + 0.5, z + 0.5).tex(mu, xv).normal(-1, 0, 0).endVertex();
            /*3*/builder.pos(x + 0.5, y + 1.0, z + 0.5).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*b*/builder.pos(x + 0.5, y + 0.5, z + 1.0).tex(cu, cv).normal(0, -1, 0).endVertex();
            /*3*/builder.pos(x + 0.5, y + 1.0, z + 0.5).tex(xu, xv).normal(0, 0, +1).endVertex();
            /*4*/builder.pos(x + 1.0, y + 0.5, z + 0.5).tex(xu, mv).normal(+1, 0, 0).endVertex();
            /*b*/builder.pos(x + 0.5, y + 0.5, z + 1.0).tex(cu, cv).normal(0, -1, 0).endVertex();
            /*4*/builder.pos(x + 1.0, y + 0.5, z + 0.5).tex(xu, mv).normal(+1, 0, 0).endVertex();
            /*1*/builder.pos(x + 0.5, y + 0.0, z + 0.5).tex(mu, mv).normal(0, 0, -1).endVertex();
//            tessellator.draw();
        }

//        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
