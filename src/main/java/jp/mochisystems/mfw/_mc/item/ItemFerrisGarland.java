package jp.mochisystems.mfw._mc.item;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw.ferriswheel.FerrisGarland;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemFerrisGarland extends ItemBlock implements IItemBlockModelHolder {

    @Override
    public IModel GetBlockModel(IModelController controller)
    {
        return new FerrisGarland(controller);
    }


    public ItemFerrisGarland(Block block)
    {
        super(block);
//        setContainerItem(this);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        String[] usages = _Core.I18n("usage.garland").split("\\\\ ");
        for(String s : usages){
            tooltip.add(TextFormatting.AQUA+s);
        }
    }

    public static ItemStack Default(int meta){
        ItemStack itemStack = new ItemStack(MFW.ItemFerrisGarland, 1, meta);
        NBTTagCompound base = new NBTTagCompound();
        NBTTagCompound nbt = new NBTTagCompound();
        base.setInteger("connectornum", 3);
        base.setString("connectorName0", "0");
        base.setString("connectorName1", "1");
        base.setString("connectorName2", "2");
        base.setBoolean("isDrawCore", true);
        nbt.setTag("model", base);
        itemStack.setTagCompound(nbt);
        return itemStack;
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        if(stack.getTagCompound()!=null && stack.getTagCompound().getCompoundTag("model")!=null){
            return _Core.I18n(this.getUnlocalizedNameInefficiently(stack) + ".name").trim()
                    + " : " + stack.getTagCompound().getCompoundTag("model").getString(FerrisGarland.garlandIdKey);
        }
        return super.getItemStackDisplayName(stack);
    }


    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if(!stack.hasTagCompound()) return EnumActionResult.FAIL;
        stack.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
        stack.shrink(1);

        return EnumActionResult.SUCCESS;
    }

//    @Override
//    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
//    {
//        return EnumActionResult.FAIL;
//    }

    public static class End extends ItemFerrisGarland {
        public End(Block block) {
            super(block);
        }
    }
}
