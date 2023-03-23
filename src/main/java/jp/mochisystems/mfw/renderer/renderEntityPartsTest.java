//package jp.mochisystems.mfw.renderer;
//
//import net.minecraft.client.renderer.entity.Render;
//import net.minecraft.client.renderer.entity.RenderManager;
//import net.minecraft.entity.Entity;
//import net.minecraft.util.ResourceLocation;
//
//public class renderEntityPartsTest extends Render{
//
//	public renderEntityPartsTest(RenderManager renderManager) {
//		super(renderManager);
//	}
//
//	@Override
//	public void doRender(Entity e, double x, double y, double z, float f, float p)
//	{
//		renderOffsetAABB(e.getEntityBoundingBox(),  x-e.posX, y-e.posY, z-e.posZ);
//	}
//
//	@Override
//	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
//		return null;
//	}
//
//}
