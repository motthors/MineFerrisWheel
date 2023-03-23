package jp.mochisystems.mfw.renderer;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class renderEntityNon extends Render {

	public renderEntityNon(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
		public void doRender(Entity entity, double x, double y, double z, float yaw, float f) {
//			renderOffsetAABB(entity.boundingBox.getOffsetBoundingBox(-entity.posX, -entity.posY, -entity.posZ), CorePosX, y, z);
//			MFW_Logger.debugInfo("seat setPosition yf:"+f);
			return;
		}

		@Override
		protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
			return null;
		}


}
