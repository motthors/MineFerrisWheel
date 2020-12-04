package mfw._mc._1_7_10.block;

import mfw._mc._1_7_10._core.MFW_Core;
import mfw.ferriswheel.FerrisGarland;
import mfw.ferriswheel.FerrisPartAddress;
import mfw.ferriswheel.FerrisPartBase;
import mfw._mc._1_7_10.message.MFW_PacketHandler;
import mfw._mc._1_7_10.message.MessageSyncRSPowerStC;
import mfw._mc._1_7_10.tileEntity.TileEntityFerrisCore;
import mochisystems.blockcopier.IItemBlockModelHolder;
import mochisystems.math.Math;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public abstract class blockFerrisCore extends BlockContainer {

	public static class Wheel extends blockFerrisCore{
		@Override
		public TileEntity createNewTileEntity(World world, int meta)
		{
			return new TileEntityFerrisCore.Wheel();
		}
	}

	public static class Elevator extends blockFerrisCore{
        @Override
        public TileEntity createNewTileEntity(World world, int meta)
        {
            return new TileEntityFerrisCore.Elevator();
        }

		@Override
		public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack)
		{
			if(!(stack.getItem() instanceof IItemBlockModelHolder)) return;
			if(!stack.hasTagCompound())return;
			NBTTagCompound nbt = stack.getTagCompound();
			TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(x, y, z);
			int direction = Math.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			int meta=2;
			switch(direction)
			{
				case 0: meta = 2; break;
				case 1: meta = 5; break;
				case 2: meta = 3; break;
				case 3: meta = 4; break;
			}
			tile.SetSide(meta);
			tile.blockModel.Reset();
			tile.blockModel.readFromNBT(nbt);
			tile.blockModel.setRSPower(world.getStrongestIndirectPower(x, y, z));
		}
	}

	public static class Garland extends blockFerrisCore{
		@Override
		public TileEntity createNewTileEntity(World world, int meta)
		{
			return new TileEntityFerrisCore.Garland();
		}
	}

	public static class GarlandEnd extends blockFerrisCore{
		@Override
		public TileEntity createNewTileEntity(World world, int meta)
		{
			return new TileEntityFerrisCore.GarlandEnd();
		}
		@Override
		public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
			super.onBlockPlacedBy(world, x, y, z, player, itemStack);

			TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(x, y ,z);

			if(tile == null) return;
			FerrisGarland.End garland = (FerrisGarland.End) tile.blockModel;
			if(garland == null) return;
			NBTTagCompound nbt = itemStack.getTagCompound();
			if(nbt == null) return;
			FerrisPartAddress address = new FerrisPartAddress();
			address.readFromNBT((NBTTagCompound) nbt.getTag("model"),"garlandMain_");
			garland.InitEnd(address);
		}
	}

	public blockFerrisCore()
	{
		super(Material.ground);
		this.setHardness(1.0f);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setLightLevel(0.0F);
	}

	@Override
	public boolean isOpaqueCube() {return false;}
	
	@Override
	public int getRenderType()
	{
		return MFW_Core.blockCoreRenderId;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int itemMeta)
	{
		return side; //register constructSide as coreSide
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack)
	{
		if(!(stack.getItem() instanceof IItemBlockModelHolder)) return;
		if(!stack.hasTagCompound())return;
		NBTTagCompound nbt = stack.getTagCompound();
		TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(x, y, z);
		tile.SetSide(world.getBlockMetadata(x, y, z));
		tile.blockModel.Reset();
		tile.blockModel.readFromNBT(nbt);
		tile.blockModel.setRSPower(world.getStrongestIndirectPower(x, y, z));
	}

//	@Override
//	public TileEntity createNewTileEntity(World world, int meta)
//	{
//		return new TileEntityFerrisCore();
//	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(x, y, z);
		if(player.isSneaking()) ((FerrisPartBase)tile.blockModel).ResetSelectedPartIngGUI();
		
		//OPEN GUI
		if(!world.isRemote)
			player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj, x, y, z);
		
        return true;
    }
	


	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        //　super()を呼ばないことでTileEntityを延命させるテスト
    }


    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            ItemStack itemstack = this.createStackedBlock(meta);
            NBTTagCompound nbt = new NBTTagCompound();
            TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(x, y, z);
            tile.blockModel.writeToNBT(nbt);
            itemstack.setTagCompound(nbt);
            world.removeTileEntity(x, y, z);
            this.dropBlockAsItem(world, x, y, z, itemstack);
        }
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
    {
        TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(x, y, z);
        if(tile!=null && tile.blockModel.IsLock())return false;
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
	public boolean canProvidePower() 
	{
		return false;
	}
	
	public void onBlockAdded(World world, int x, int y, int z)
    {
        if (world.isRemote)return;
    	boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z);
    	if (flag)
    	{
//    		world.setBlockMetadataWithNotify(CorePosX, y, z, 8^world.getBlockMetadata(CorePosX, y, z), 2);
        } 
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
//        if (!world.isRemote)
        {
            boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z);
            
            if (flag || block.canProvidePower())
            {
            	TileEntityFerrisCore tile = (TileEntityFerrisCore)world.getTileEntity(x, y, z);
            	if(tile==null)return;
            	int rs = world.getStrongestIndirectPower(x, y, z);
        		tile.blockModel.setRSPower(rs);
        		MessageSyncRSPowerStC packet = new MessageSyncRSPowerStC(x, y, z, rs);
        		MFW_PacketHandler.INSTANCE.sendToAll(packet);
//        		MFW_Logger.debugInfo(""+world.getStrongestIndirectPower(CorePosX, y, z));
            }
        }
    }

    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
    	if(!player.capabilities.isCreativeMode)return null;
    	
		ItemStack stack = super.getPickBlock(target, world, x, y, z, player);
    	TileEntityFerrisCore tile = (TileEntityFerrisCore) world.getTileEntity(x, y, z);
    	NBTTagCompound nbt = new NBTTagCompound();
    	tile.blockModel.writeToNBT(nbt);
    	stack.setTagCompound(nbt);
    	return stack;
    }
}
