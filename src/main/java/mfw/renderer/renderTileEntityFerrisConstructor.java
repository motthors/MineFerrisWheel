package mfw.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mochisystems._core._Core;
import mochisystems._mc.renderer.renderTileEntityLimitFrame;
import mochisystems._mc.tileentity.TileEntityBlocksScannerBase;
import mochisystems.blockcopier.ILimitLine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class renderTileEntityFerrisConstructor extends renderTileEntityLimitFrame {

	RenderPlayer rp;

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		super.renderTileEntityAt(t, x, y, z, f);
		TileEntityBlocksScannerBase b = (TileEntityBlocksScannerBase) t;
		if(b.BodyGuide == 0) return;
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		if(rp == null){
			float p = 0f;
			rp = new RenderPlayer();
//			rp.modelBipedMain.setRotationAngles(p, p, p, p, p, p, player);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(player.getLocationSkin());
		ModelRenderer renderer = null;
		switch(b.BodyGuide)
		{
			case 1: renderer = rp.modelBipedMain.bipedHead; break;
			case 2: renderer = rp.modelBipedMain.bipedLeftArm; break;
			case 3: renderer = rp.modelBipedMain.bipedRightArm; break;
			case 4: renderer = rp.modelBipedMain.bipedBody; break;
			case 5: renderer = rp.modelBipedMain.bipedLeftLeg; break;
			case 6: renderer = rp.modelBipedMain.bipedRightLeg; break;
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5f);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPushMatrix();
		GL11.glTranslated(x+0.5, y+0.5, z+0.5);
		GL11.glRotated(b.rotMeta2_side, b.rotvecMeta2_side.x, b.rotvecMeta2_side.y, b.rotvecMeta2_side.z);
		GL11.glRotated(180, 0, 0, 1);
		GL11.glScalef(b.guideScale, b.guideScale, b.guideScale);

		renderer.rotateAngleX = renderer.rotateAngleY = renderer.rotateAngleZ = 0;
//		renderer.offsetX = (float)x + 0.5f;
//		renderer.offsetY = (float)y + 2.0f;
//		renderer.offsetZ = (float)z + 0.5f;
		renderer.render(0.0625F);

		GL11.glPopMatrix();
	}
}
