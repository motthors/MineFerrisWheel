package mfw._mc._1_7_10.block;

import java.util.Random;

import mfw._mc._1_7_10._core.MFW_Core;
import mfw._mc._1_7_10.tileEntity.TileEntityConnector;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class blockFerrisConnector extends BlockContainer {
	
	public blockFerrisConnector() {
		super(Material.glass);
		this.setHardness(0.3f);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
//		this.setBlockBounds(0.3F, 0.3F, 0.3F, 0.7F, 0.7F, 0.7F);
		this.setLightLevel(0.0F);
	}
	
	@Override
	public boolean isOpaqueCube() 
	{
		return false;
	}

	public int quantityDropped(Random p_149745_1_)
    {
        return 1;
    }
	
	public boolean renderAsNormalBlock()
    {
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityConnector();
	}

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        //OPEN GUI
        player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisConnector, player.worldObj, x, y, z);
        return true;
    }
}
