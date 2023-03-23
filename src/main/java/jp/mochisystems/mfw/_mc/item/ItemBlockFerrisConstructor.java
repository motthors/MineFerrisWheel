package jp.mochisystems.mfw._mc.item;

import jp.mochisystems.core._mc._core._Core;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockFerrisConstructor extends ItemBlock {

    public ItemBlockFerrisConstructor(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        String[] usages = _Core.I18n("usage.ferrisconstruct").split("\\\\ ");
        for(String s : usages){
            tooltip.add(TextFormatting.AQUA+s);
        }
    }
}
