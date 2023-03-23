package jp.mochisystems.mfw._mc.item;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw.ferriswheel.FerrisGarland;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFerrisGarlandSeed extends Item {


    public ItemFerrisGarlandSeed()
    {
        setContainerItem(this);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        String[] usages = _Core.I18n("usage.garland.seed").split("\\\\ ");
        for(String s : usages){
            tooltip.add(TextFormatting.AQUA+s);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(handIn != EnumHand.MAIN_HAND) return new ActionResult<>(EnumActionResult.FAIL, stack);
        if (!worldIn.isRemote) playerIn.openGui(MFW.INSTANCE, MFW.GUIID_GarlandInit, worldIn, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    public static void Snap(ItemStack stack, EntityPlayer player, String id)
    {
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.8F, 1F);

        stack.shrink(1);
        ItemStack garland = ItemFerrisGarland.Default(0);
        ItemStack end = new ItemStack(MFW.ItemFerrisGarlandEnd, 1);
//        ItemStack end = ItemFerrisGarland.Default();

        garland.getTagCompound().getCompoundTag("model").setString(FerrisGarland.garlandIdKey, id);
        garland.getTagCompound().getCompoundTag("model").setBoolean("lead", true);
        end.setTagCompound(new NBTTagCompound());
        end.getTagCompound().setTag("model", new NBTTagCompound());
        end.getTagCompound().getCompoundTag("model").setString(FerrisGarland.garlandIdKey, id);

        if (!player.inventory.addItemStackToInventory(garland))
        {
            player.dropItem(garland, false);
        }
        if (!player.inventory.addItemStackToInventory(end))
        {
            player.dropItem(end, false);
        }
    }

}
