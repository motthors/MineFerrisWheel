package mfw.renderer;

import mfw._mc._1_7_10.entity.EntityFerrisCollider;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class renderEntityFerrisCollider extends RenderEntity{
	public void doRender(EntityFerrisCollider collider, double x, double y, double z, float f, float partialtick)
	{
		Tessellator.instance.setBrightness(15<<20|15<<4);
//		renderOffsetAABB(collider.boundingBox, x-collider.posX, y-collider.posY, z-collider.posZ);
		Entity[] ea = collider.getParts();
		for(int i=0; i<ea.length; i++)
		{
			renderOffsetAABB(ea[i].boundingBox,  x-collider.posX, y-collider.posY, z-collider.posZ);
		}
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float partialtick) {
		doRender((EntityFerrisCollider)entity, x, y, z, f, partialtick);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}
}
