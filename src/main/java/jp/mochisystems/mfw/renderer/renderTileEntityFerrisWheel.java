package jp.mochisystems.mfw.renderer;

import jp.mochisystems.core.manager.RenderTranslucentBlockModelManager;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisCore;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class renderTileEntityFerrisWheel extends TileEntitySpecialRenderer<TileEntityFerrisCore> {

	public void render(TileEntityFerrisCore t, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GL11.glPushMatrix();
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.color(1f, 1f, 1f, 1f);
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		t.blockModel.RenderModel(0, partialTicks);
		RenderTranslucentBlockModelManager.add(t.blockModel);
		GL11.glPopMatrix();
	}
	
//	private void DrawArrow(Tessellator tess, Vec3d vec)
//	{
//      	tess.startDrawing(GL11.GL_TRIANGLES);
//      	tess.addVertexWithUV(0.2d, 0d, 0.2d, 0.0d, 0.0d);
//      	tess.addVertexWithUV(vec.x*3d, vec.y*3d, vec.z*3d, 0.0d, 0.0d);
//      	tess.addVertexWithUV(-0.2d, 0d, -0.2d, 0.0d, 0.0d);
//      	tess.draw();
//	}

}
