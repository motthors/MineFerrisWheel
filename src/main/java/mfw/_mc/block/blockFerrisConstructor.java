package mfw._mc.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import mfw._mc.tileEntity.TileEntityFerrisConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class blockFerrisConstructor extends BlockContainer{
	
	private final Random random = new Random();
	@SideOnly(Side.CLIENT)
	private IIcon TopIcon;
	@SideOnly(Side.CLIENT)
	private IIcon SideIcon; 
	@SideOnly(Side.CLIENT)
	private IIcon BottomIcon;
	
	public blockFerrisConstructor()
	{
		super(Material.ground);
		this.setHardness(1.0F);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setLightLevel(0.0F);
	}
 
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if(side==meta)return TopIcon;
		if((side&14) == (meta&14))return BottomIcon;
		else return SideIcon;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.TopIcon = iconRegister.registerIcon(MFW_Core.MODID+":ferrisConstructor");
		this.SideIcon = iconRegister.registerIcon(MFW_Core.MODID+":ferrisConstructor_s");
		this.BottomIcon = iconRegister.registerIcon(MFW_Core.MODID+":ferrisConstructor_b");
	}

	@Override
	public int getRenderType()
	{
		return 31;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return true;
	}
 
	public boolean renderAsNormalBlock() 
	{
		return false;
	}
	
	public TileEntityFerrisConstructor getTileEntityInstance()
	{
		return new TileEntityFerrisConstructor();
	}
	
//	@Override
//	public boolean canPlaceBlockOnSide(World world, int CorePosX, int y, int z, int constructSide) {
//
//		return (constructSide==0)?false:true;
//	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		//OPEN GUI
		player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisConstructor, player.worldObj, x, y, z);
        return true;
    }
	
	// Args: World, X, Y, Z, constructSide, hitX, hitY, hitZ, block metadata
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		return side;
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack p_149689_6_)
	{
		super.onBlockPlacedBy(world, x, y, z, player, p_149689_6_); 

		TileEntityFerrisConstructor tile = getTileEntityInstance();
		world.setTileEntity(x, y, z, tile);
		tile.Init(world.getBlockMetadata(x, y, z));
	}
	
	public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_)
    {
        TileEntityFerrisConstructor tile = (TileEntityFerrisConstructor)world.getTileEntity(x, y, z);

        if (tile != null)
        {
            for (int i1 = 0; i1 < tile.getSizeInventory(); ++i1)
            {
                ItemStack itemstack = tile.getStackInSlot(i1);

                if (itemstack != null)
                {
                    float f = this.random.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.random.nextFloat() * 0.8F + 0.1F;
                    EntityItem entityitem;

                    for (float f2 = this.random.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem))
                    {
                        int j1 = this.random.nextInt(21) + 10;

                        if (j1 > itemstack.stackSize)
                        {
                            j1 = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j1;
                        entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
                        float f3 = 0.05F;
                        entityitem.motionX = (double)((float)this.random.nextGaussian() * f3);
                        entityitem.motionY = (double)((float)this.random.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double)((float)this.random.nextGaussian() * f3);

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                        }
                    }
                }
            }

            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, p_149749_6_);
    }

	public void setBlockBoundsForItemRender()
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
 
 
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
	{
		return AxisAlignedBB.getBoundingBox(
				((double)x)+this.minX,((double)y)+this.minY,((double)z)+this.minZ,
				((double)x)+this.maxX,((double)y)+this.maxY,((double)z)+this.maxZ
				);
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int x, int y, int z)
	{
		return AxisAlignedBB.getBoundingBox(
				((double)x)+this.minX,((double)y)+this.minY,((double)z)+this.minZ,
				((double)x)+this.maxX,((double)y)+this.maxY,((double)z)+this.maxZ
				);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return getTileEntityInstance();
	}
}
