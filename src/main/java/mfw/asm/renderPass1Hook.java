package mfw.asm;

import java.util.ArrayDeque;

import mfw._mc.tileEntity.TileEntityFerrisCore;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class renderPass1Hook {
	
	public static ArrayDeque<TileEntityFerrisCore> deque = new ArrayDeque<TileEntityFerrisCore>();
	
	public static void add(TileEntityFerrisCore tile)
	{
		deque.add(tile);
	}
	
	// from renderEntities
	public static void draw(float f)
    {
		Minecraft mc = Minecraft.getMinecraft();
        {
//        	�Ƃ肠���������ăe�X�g
//            double d0 = p_147589_1_.prevPosX + (p_147589_1_.posX - p_147589_1_.prevPosX) * (double)p_147589_3_;
//            double d1 = p_147589_1_.prevPosY + (p_147589_1_.posY - p_147589_1_.prevPosY) * (double)p_147589_3_;
//            double d2 = p_147589_1_.prevPosZ + (p_147589_1_.posZ - p_147589_1_.prevPosZ) * (double)p_147589_3_;
//            mc.theWorld.theProfiler.startSection("prepare");
//            TileEntityRendererDispatcher.instance.cacheActiveRenderInfo(mc.theWorld, mc.getTextureManager(), mc.fontRenderer, mc.renderViewEntity, p_147589_3_);
//            RenderManager.instance.cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRenderer, this.mc.renderViewEntity, this.mc.pointedEntity, this.mc.gameSettings, p_147589_3_);
//
//            EntityLivingBase entitylivingbase1 = this.mc.renderViewEntity;
//            double d3 = entitylivingbase1.lastTickPosX + (entitylivingbase1.posX - entitylivingbase1.lastTickPosX) * (double)p_147589_3_;
//            double d4 = entitylivingbase1.lastTickPosY + (entitylivingbase1.posY - entitylivingbase1.lastTickPosY) * (double)p_147589_3_;
//            double d5 = entitylivingbase1.lastTickPosZ + (entitylivingbase1.posZ - entitylivingbase1.lastTickPosZ) * (double)p_147589_3_;
//            TileEntityRendererDispatcher.staticPlayerX = d3;
//            TileEntityRendererDispatcher.staticPlayerY = d4;
//            TileEntityRendererDispatcher.staticPlayerZ = d5;

//            if (renderglobal.displayListEntitiesDirty)
//            {
//                RenderManager.renderPosX = 0.0D;
//                RenderManager.renderPosY = 0.0D;
//                RenderManager.renderPosZ = 0.0D;
//                renderglobal.rebuildDisplayListEntities();
//            }

            GL11.glMatrixMode(GL11.GL_MODELVIEW);

            mc.entityRenderer.enableLightmap((double)f);


            RenderHelper.enableStandardItemLighting();

            
            for(TileEntityFerrisCore tile : deque)
            {
            	// same TileEntityRenderCispatcher
            	TileEntityRendererDispatcher terd = TileEntityRendererDispatcher.instance;
            	int i = terd.field_147550_f.getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);
            	int j = i % 65536;
            	int k = i / 65536;
            	OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            	
            	// rendererTileEntity
            	Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            	GL11.glPushMatrix();
            	double x = (double)tile.xCoord - TileEntityRendererDispatcher.staticPlayerX;
            	double y = (double)tile.yCoord - TileEntityRendererDispatcher.staticPlayerY;
            	double z = (double)tile.zCoord - TileEntityRendererDispatcher.staticPlayerZ;
            	GL11.glTranslated(x, y, z);
            	tile.blockModel.RenderModel(1, f);
            	GL11.glPopMatrix();
            }		
            deque.clear();
            
            mc.entityRenderer.disableLightmap((double)f);

            
        }
        
    }
}
