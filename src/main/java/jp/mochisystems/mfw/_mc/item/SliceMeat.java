package jp.mochisystems.mfw._mc.item;

import jp.mochisystems.mfw._mc._core.MFW;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

public class SliceMeat{
	
	public static interface SyabuMeat{
		public Item cooked();
	}

	public static Item FactoryCreateBeaf(){return new itemSliceBeef();}
	public static Item FactoryCreatePork(){return new itemSlicePork();}
	public static Item FactoryCreateChicken(){return new itemSliceChicken();}
	public static Item FactoryCreateShabuBeaf(){return new itemSyabuBeef();}
	public static Item FactoryCreateShabuPork(){return new itemSyabuPork();}
	public static Item FactoryCreateShabuChicken(){return new itemSyabuChicken();}
	
	public static class itemSliceBeef extends Item implements SyabuMeat
	{
		public Item cooked() { return MFW.ItemShabuBeef; }
	}
	public static class itemSlicePork extends Item implements SyabuMeat
	{
		public Item cooked() { return MFW.ItemShabuPork; }
	}
	public static class itemSliceChicken extends Item implements SyabuMeat
	{
		public Item cooked() { return MFW.ItemShabuChicken; }
	}
	
	public static class itemSyabuBeef extends ItemFood{
		public itemSyabuBeef()
		{
			super(3, 0.3f, true);
			this.setAlwaysEdible();
		}
	}
	public static class itemSyabuPork extends ItemFood{
		public itemSyabuPork()
		{
			super(3, 0.3f, true);
			this.setAlwaysEdible();
		}
	}
	public static class itemSyabuChicken extends ItemFood{
		public itemSyabuChicken()
		{
			super(3, 0.3f, true);
			this.setAlwaysEdible();
		}
	}
}
