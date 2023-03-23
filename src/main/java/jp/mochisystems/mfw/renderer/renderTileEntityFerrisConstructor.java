package jp.mochisystems.mfw.renderer;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.renderer.renderTileEntityLimitFrame;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class renderTileEntityFerrisConstructor extends renderTileEntityLimitFrame {

	RenderPlayer rp;

	@Override
	public void render(TileEntity t, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		super.render(t, x, y, z, partialTicks, destroyStage, alpha);
		TileEntityFerrisConstructor b = (TileEntityFerrisConstructor) t;

		if(_Core.CONFIG_ANNOTATIONS.isProGui)
		{
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
			GL11.glLineWidth(4f);
			double cx = x + 0.5 + b.modelOffset.x;
			double cy = y + 0.5 + b.modelOffset.y;
			double cz = z + 0.5 + b.modelOffset.z;
			double dx = b.faceDir.x * 13;
			double dy = b.faceDir.y * 13;
			double dz = b.faceDir.z * 13;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(cx+dx, cy+dy, cz+dz).color(20, 255, 100, 255).endVertex();
			bufferbuilder.pos(cx-dx, cy-dy, cz-dz).color(20, 255, 100, 255).endVertex();
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
		}

		if(b.BodyGuide == 0) return;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if(rp == null){
			float p = 0f;
			rp = new RenderPlayer(Minecraft.getMinecraft().getRenderManager());
//			rp.modelBipedMain.setRotationAngles(p, p, p, p, p, p, player);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(player.getLocationSkin());
		ModelRenderer renderer = null;
		switch(b.BodyGuide)
		{
			case 1: renderer = rp.getMainModel().bipedHead; break;
			case 2: renderer = rp.getMainModel().bipedLeftArm; break;
			case 3: renderer = rp.getMainModel().bipedRightArm; break;
			case 4: renderer = rp.getMainModel().bipedBody; break;
			case 5: renderer = rp.getMainModel().bipedLeftLeg; break;
			case 6: renderer = rp.getMainModel().bipedRightLeg; break;
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5f);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPushMatrix();
		GL11.glTranslated(x+0.5, y+0.5, z+0.5);
		GL11.glRotated(b.angleForRenderBody, b.AxisForRenderBody.x, b.AxisForRenderBody.y, b.AxisForRenderBody.z);
		GL11.glRotated(180, 0, 0, 1);
		GL11.glScaled(1/b.scale.x, 1/b.scale.y, 1/b.scale.z);
		GL11.glTranslated(-renderer.rotationPointX*0.0625, -renderer.rotationPointY*0.0625, -renderer.rotationPointZ*0.0625);
		renderer.rotateAngleX = renderer.rotateAngleY = renderer.rotateAngleZ = 0;
//		renderer.offsetX = (float)x + 0.5f;
//		renderer.offsetY = (float)y + 2.0f;
//		renderer.offsetZ = (float)z + 0.5f;
		renderer.render(0.0625F);

		GL11.glPopMatrix();
	}
}
