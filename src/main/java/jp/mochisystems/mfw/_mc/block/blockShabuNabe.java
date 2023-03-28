package jp.mochisystems.mfw._mc.block;

import java.util.Random;

import jp.mochisystems.mfw._mc.item.SliceMeat.SyabuMeat;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class blockShabuNabe extends Block{

    public static final PropertyBool IS_FILL = PropertyBool.create("is_fill");
    public static final PropertyBool IS_HOT = PropertyBool.create("is_hot");

//    public static final AxisAlignedBB AABB =
//            new AxisAlignedBB(
//                    1.0f / 8.0f, 2f / 8f, 1.0f / 8.0f,
//                    7.0f / 8.0f, 5.0f / 8.0f, 7.0f / 8.0f);


    public blockShabuNabe()
    {
        super(Material.GROUND);
        this.setHardness(0.3F);
        this.setResistance(2000.0F);
        setDefaultState(blockState.getBaseState().withProperty(IS_FILL, false).withProperty(IS_HOT, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, IS_FILL, IS_HOT);
    }

//    @Override
//    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
//    {
//        return AABB;
//    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }



    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return true;
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack.isEmpty())
        {
            return true;
        }
        else
        {
            if (itemstack.getItem() == Items.WATER_BUCKET && !state.getValue(IS_FILL))
            {
                if (!player.capabilities.isCreativeMode)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.BUCKET));
                }
                state = state.withProperty(IS_FILL, true);
                world.setBlockState(pos, state);
                neighborChanged(state, world, pos, Blocks.AIR, pos.down());
                return true;
            }

            else if(state.getValue(IS_HOT) && itemstack.getItem() instanceof SyabuMeat)
            {
                if(!itemstack.hasTagCompound())
                {
                    itemstack.setTagCompound(new NBTTagCompound());
                }

                NBTTagCompound tag = itemstack.getTagCompound();
                int count = 1 + tag.getInteger("count");
                if(count >= 3)
                {
                    Item cooked = ((SyabuMeat)itemstack.getItem()).cooked();
                    itemstack.shrink(1);
                    if (itemstack.isEmpty())
                    {
                        player.setHeldItem(hand, new ItemStack(cooked));
                    }
                    else {
                        tag.setInteger("count", 0);
                        itemstack.setTagCompound(tag);
                        if (!player.inventory.addItemStackToInventory(new ItemStack(cooked)))
                        {
                            player.dropItem(new ItemStack(cooked), false);
                        }
                    }
                }
                else tag.setInteger("count", count);
                return true;
            }
        }
        return false;

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand)
    {
        if(stateIn.getValue(IS_HOT))
    	{
    		world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
                    pos.getX()+0.5* world.rand.nextDouble(), pos.getY()+0.8, pos.getZ()+0.5*world.rand.nextDouble(),
                    0.01*world.rand.nextDouble(), 0.1, 0.01*world.rand.nextDouble());
//    		world.spawnParticle("cloud", CorePosX+random.nextDouble(), y+0.5, z+random.nextDouble(), 0, 0.1, 0);
    	}
    }
    @Deprecated
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if(!fromPos.equals(pos.down())) return;
        if(!state.getValue(IS_FILL)) return;
        Block block = worldIn.getBlockState(fromPos).getBlock();
        if(block == Blocks.FIRE
        || block == Blocks.MAGMA
        || block == Blocks.LAVA){
            worldIn.setBlockState(pos, state.withProperty(IS_HOT, true));
        }else{
            worldIn.setBlockState(pos, state.withProperty(IS_HOT, false));
        }
    }

    @Override
    public void fillWithRain(World worldIn, BlockPos pos)
    {
        if (worldIn.rand.nextInt(20) == 1)
        {
            float f = worldIn.getBiome(pos).getTemperature(pos);

            if (worldIn.getBiomeProvider().getTemperatureAtHeight(f, pos.getY()) >= 0.15F)
            {
                IBlockState iblockstate = worldIn.getBlockState(pos);

                if (!iblockstate.getValue(IS_FILL))
                {
                    worldIn.setBlockState(pos, iblockstate.cycleProperty(IS_FILL), 2);
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer(){
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

//    @SideOnly(Side.CLIENT)
//    public static int colorMultiplier(int tintIndex)
//    {
////        return 0xabcbff;
//        return -1;
//    }



    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState()
                .withProperty(IS_FILL, (meta&1)>0)
                .withProperty(IS_HOT, (meta&2)>0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(IS_FILL) ? 1 : 0)
                | (state.getValue(IS_HOT) ? 2 : 0);
    }


}
