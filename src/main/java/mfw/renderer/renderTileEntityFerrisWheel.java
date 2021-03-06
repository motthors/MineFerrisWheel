package mfw.renderer;

import mfw._mc._1_7_10.tileEntity.TileEntityFerrisCore;
import org.lwjgl.opengl.GL11;

import mfw.asm.renderPass1Hook;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class renderTileEntityFerrisWheel extends TileEntitySpecialRenderer {
	
	public void renderTileEntityAt(TileEntityFerrisCore t, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		t.blockModel.RenderModel(0, f);
      	renderPass1Hook.add(t);
      	GL11.glPopMatrix();
	}
	
	public void p_bindTexture(ResourceLocation texture){this.bindTexture(texture);}
	
	private void DrawArrow(Tessellator tess, Vec3 vec)
	{
      	tess.startDrawing(GL11.GL_TRIANGLES);
      	tess.addVertexWithUV(0.2d, 0d, 0.2d, 0.0d, 0.0d);
      	tess.addVertexWithUV(vec.xCoord*3d, vec.yCoord*3d, vec.zCoord*3d, 0.0d, 0.0d);
      	tess.addVertexWithUV(-0.2d, 0d, -0.2d, 0.0d, 0.0d);
      	tess.draw();
	}

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		renderTileEntityAt((TileEntityFerrisCore)t,x,y,z,f);
	}
}
