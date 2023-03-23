package jp.mochisystems.mfw.blocksReplication;

import jp.mochisystems.core._mc.block.*;
import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core.blockcopier.BlocksScanner;
import jp.mochisystems.core.blockcopier.IBLockCopyHandler;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.mfw._mc.block.*;
import jp.mochisystems.mfw._mc.block.blockFerrisSupporter;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisConstructor;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityConnector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class MFWBlocksScanner extends BlocksScanner {

    int slotIdx;
    int layer;

    public MFWBlocksScanner(IBLockCopyHandler handler) {
        super(handler);
    }

    private class Connector{
        Vec3d pos;
        String name;
        TileEntityConnector tile;
    }
	public ArrayList<Connector> connectors = new ArrayList<>();
    public BlockPos constructorPos;

    public void SetIndex(int slotIdx, int layer)
    {
        this.slotIdx = slotIdx;
        this.layer = layer;
    }

	protected void allocBlockArray(int x, int y, int z)
	{
        connectors.clear();
		super.allocBlockArray(x, y, z);
    }

    protected boolean isExcludedBlock(Block block)
    {
        if(block instanceof BlockFerrisConstructor) return false; //Connectorの位置ずらしのため
        if(block instanceof BlockRemoteController)return true;
        if(block instanceof blockFerrisSupporter)return true;
//        if(block instanceof blockChunkLoader)return true;
        if(block instanceof blockFerrisCore)return true;

        return super.isExcludedBlock(block);
    }

    protected void setBlock(IBlockState state, BlockPos pos, TileEntity tile)
	{
		if(state.getBlock() instanceof blockFerrisConnector)
		{
            Connector c = new Connector();
            c.tile = (TileEntityConnector)tile;
            c.pos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            c.name =  c.tile.GetName();
			connectors.add(c);
			return;
		}
		else if (state.getBlock() instanceof BlockFerrisConstructor)
        {
            constructorPos = pos;
            if(((TileEntityFerrisConstructor)tile).isCoreConnector) {
                Connector c = new Connector();
                c.pos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                c.name = "_Core_";
                connectors.add(c);
            }
            return;
        }
        super.setBlock(state, pos, tile);
    }

    public void makeTag(NBTTagCompound nbt, IBLockCopyHandler handler)
    {
        super.makeTag(nbt, handler);
        NBTTagCompound modelTag = (NBTTagCompound) nbt.getTag("model");
        int connectorNum = connectors.size();
        int copyNum = modelTag.getInteger("copyNum");
        double rotOffset = Math.PI*2 / copyNum;
        Vec3d vecForCopyRotAxis = new Vec3d();
        switch(handler.GetSide().getIndex())
        {
            case 0 : vecForCopyRotAxis.y = 1; break;
            case 1 : vecForCopyRotAxis.y =-1; break;
            case 2 : vecForCopyRotAxis.z = 1; break;
            case 3 : vecForCopyRotAxis.z =-1; break;
            case 4 : vecForCopyRotAxis.x = 1; break;
            case 5 : vecForCopyRotAxis.x =-1; break;
        }
        modelTag.setInteger("connectornum", connectorNum * copyNum);
        int i = 0;
        for(Connector connector : connectors)
        {
            connector.pos.sub(constructorPos.getX(), constructorPos.getY(), constructorPos.getZ());
            connector.pos.WriteToNBT("connector"+i, modelTag);
            modelTag.setString("connectorName"+i, connector.name);

            for(int c = 1; c < copyNum; ++c){
                int idx = connectorNum * c + i;
                Connector newC = new Connector();
                newC.pos = connector.pos.New();
                newC.name = connector.name + ":" + c;
                newC.pos.Rotate(vecForCopyRotAxis, rotOffset*c);
//                connector.pos.sub(constructorPos.getX(), constructorPos.getY(), constructorPos.getZ());
                newC.pos.WriteToNBT("connector"+idx, modelTag);
                modelTag.setString("connectorName"+idx, newC.name);
            }

            TileEntityBlocksScannerBase childConstructor = connector.tile.GetRegisteredConstructor();
            if(childConstructor != null)
            {
//                Logger.debugInfo("next scan["+connector.name+"], "+childConstructor.modelName+", pos:"+childConstructor.GetHandlerPos().toString());
                int cx = childConstructor.GetHandlerPos().getX();
                int cy = childConstructor.GetHandlerPos().getY();
                int cz = childConstructor.GetHandlerPos().getZ();
                BlocksScanner scanner = new MFWBlocksScanner(childConstructor);
                scanner.RegisterSettings(cx, cy, cz, childConstructor.GetLimitFrame(), childConstructor.isDrawEntity, childConstructor.TrueCopy);
                scanner.Register();

                NBTTagCompound childNbt = scanner.GetTag();
                childNbt.getCompoundTag("model").merge(connector.tile.modelNbt);
                childConstructor.InstantiateModelItem().writeToNBT(childNbt);

                nbt.setTag("PartSlot"+i, childNbt);
//                handler.InstantiateModelItem().writeToNBT(childNbt);
            }
            i++;
        }
//        Logger.debugInfo("END");
    }
}
