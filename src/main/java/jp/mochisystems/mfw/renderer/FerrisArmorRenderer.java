//package jp.mochisystems.mfw.renderer;
//
//import jp.mochisystems.core.manager.EntityWearingModelManager;
//import jp.mochisystems.core.util.ModelBiped;
//import jp.mochisystems.mfw._mc.item.ItemBlockFerrisCore;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.EnumAction;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.client.event.RenderPlayerEvent;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//@SideOnly(Side.CLIENT)
//public class FerrisArmorRenderer {
//
//    @SubscribeEvent
//    public void onArmorRender(RenderPlayerEvent.SetArmorModel event)
//    {
//        ItemStack stack = event.getStack();
//        if (stack == null || !( stack.getItem() instanceof ItemBlockFerrisCore) )
//        {
//            return;
//        }
//        event.setResult(1);
////		itemBlockFerrisCore item = (itemBlockFerrisCore) event.stack.getItem();
//
//        ModelBiped model = EntityWearingModelManager.GetModelBiped(event.getEntityLiving(), stack, event.getSlot());
//        model.isSneak = event.getEntityLiving().isSneaking();
//        if ( event.getEntityLiving()  instanceof EntityPlayer)
//        {
//            ItemStack held = event.getEntityPlayer().inventory.getCurrentItem();
////            model.heldItemRight = ( held != null ) ? 0 : 1;
//            if ( held != null )
//            {
////                model.aimedBow = event.entityPlayer.getItemInUseCount() > 0 && held.getItemUseAction().equals( EnumAction.bow );
//            }
//        }
////        event.getRenderer().setRenderPassModel( model );
//    }
//
//}
