package mfw.renderer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._mc.item.itemBlockFerrisCore;
import mochisystems.manager.EntityWearingModelManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

@SideOnly(Side.CLIENT)
public class FerrisArmorRenderer {

    @SubscribeEvent
    public void onArmorRender(RenderPlayerEvent.SetArmorModel event)
    {
        if ( event.stack == null || !( event.stack.getItem() instanceof itemBlockFerrisCore) )
        {
            return;
        }
        event.result = 1;
//		itemBlockFerrisCore item = (itemBlockFerrisCore) event.stack.getItem();

        mochisystems.util.ModelBiped model = EntityWearingModelManager.GetModelBiped(event.entityLiving, event.stack, event.slot);
        model.isSneak = event.entityLiving.isSneaking();
        if ( event.entityLiving instanceof EntityPlayer)
        {
            ItemStack held = event.entityPlayer.inventory.getCurrentItem();
            model.heldItemRight = ( held != null ) ? 0 : 1;
            if ( held != null )
            {
                model.aimedBow = event.entityPlayer.getItemInUseCount() > 0 && held.getItemUseAction().equals( EnumAction.bow );
            }
        }
        event.renderer.setRenderPassModel( model );
    }

}
