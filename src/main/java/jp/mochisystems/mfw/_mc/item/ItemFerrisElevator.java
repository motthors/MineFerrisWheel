package jp.mochisystems.mfw._mc.item;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw.ferriswheel.FerrisElevator;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemFerrisElevator extends ItemBlock implements IItemBlockModelHolder {

    @Override
    public IModel GetBlockModel(IModelController controller)
    {
        return new FerrisElevator(controller);
    }


    public ItemFerrisElevator(Block block)
    {
        super(block);
        setContainerItem(this);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)){
            ItemStack itemStack = new ItemStack(this, 1, 0);
            NBTTagCompound model = new NBTTagCompound();
            NBTTagCompound nbt = new NBTTagCompound();
            model.setInteger("connectornum", 1);
            model.setString("connectorName0", "Connector");
            model.setFloat("amplitude", 1f);
            model.setBoolean("isDrawCore", true);
            model.setBoolean("stopflag", true);
            nbt.setFloat("wsize", 1f);
            model.setFloat("accel", 0.1f);
            model.setFloat("resist", 0.1f);
            nbt.setTag("model", model);
            itemStack.setTagCompound(nbt);

            items.add(itemStack);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
//        if(playerIn.isSneaking())
//        {
//            playerIn.openGui(MFW.INSTANCE, MFW.GUIID_FerrisCore, worldIn, 0, 0, 0);
//            return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
//        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        if(stack.getTagCompound()!=null && stack.getTagCompound().getCompoundTag("model")!=null){
            return _Core.I18n(this.getUnlocalizedNameInefficiently(stack) + ".name").trim()
                    + " : " + stack.getTagCompound().getCompoundTag("model").getString("ModelName");
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
}
