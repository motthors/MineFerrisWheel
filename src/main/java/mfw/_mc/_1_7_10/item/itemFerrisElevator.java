package mfw._mc._1_7_10.item;

import mfw.ferriswheel.FerrisElevator;
import mochisystems.util.IModel;
import mochisystems.blockcopier.IItemBlockModelHolder;
import mochisystems.util.IModelController;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class itemFerrisElevator extends ItemBlock implements IItemBlockModelHolder {

    @Override
    public IModel GetBlockModel(IModelController controller)
    {
        return new FerrisElevator(controller);
    }

    @Override
    public void OnSetInventory(IModel part, int slotidx, ItemStack itemStack, EntityPlayer player){}


    public itemFerrisElevator(Block block)
    {
        super(block);
        setContainerItem(this);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List itemList) {
        ItemStack itemStack = new ItemStack(this, 1, 0);
        NBTTagCompound model = new NBTTagCompound();
        NBTTagCompound nbt = new NBTTagCompound();
        model.setInteger("connectornum", 1);
        model.setString("connectorName0", "Connector");
        model.setFloat("syncAmplitude", 1f);
        model.setBoolean("isdrawingcore", true);
        model.setBoolean("stopflag", true);
        nbt.setFloat("wsize", 1f);
        model.setFloat("accel", 1);
        model.setFloat("resist", 0.01f);
        nbt.setTag("model", model);
        itemStack.setTagCompound(nbt);

        itemList.add(itemStack);
    }


}
