package mfw.blocksReplication;

import mfw._mc.block.*;
import mfw._mc.tileEntity.TileEntityFerrisConstructor;
import mfw._mc.tileEntity.TileEntityConnector;
import mochisystems._mc.block.BlockRemoteController;
import mochisystems._mc.tileentity.TileEntityBlocksScannerBase;
import mochisystems.blockcopier.BlocksScanner;
import mochisystems.blockcopier.IBLockCopyHandler;
import mochisystems.blockcopier.IItemBlockModelHolder;
import mochisystems.math.Vec3d;
import mochisystems.util.IModel;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

public class MFWBlocksScanner extends BlocksScanner {

    int slotIdx;
    int layer;

//	private int ConnectorNum;
    private class Connector{
        Vec3d pos;
        String name;
        TileEntityConnector tile;
    }
	public ArrayList<Connector> connectors = new ArrayList<>();

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

    private boolean isExcludedBlock(Block block)
    {
        if(block instanceof BlockRemoteController)return true;
        if(block instanceof blockFerrisSupporter)return true;
        if(block instanceof blockFerrisCutter)return true;
        if(block instanceof blockChunkLoader)return true;
        if(block instanceof blockFileManager)return true;
        if(block instanceof blockFerrisCore)return true;

        return false;
    }

	protected void setBlock(Block block, int meta, int x, int y, int z, TileEntity tile)
	{
		if(isExcludedBlock(block)) return;

		if(block instanceof blockFerrisConnector)
		{
            Connector c = new Connector();
            c.tile = (TileEntityConnector)tile;
            c.pos = new Vec3d(x-(srcPosMaxX+srcPosMinX)/2, y-(srcPosMaxY+srcPosMinY)/2, z-(srcPosMaxZ+srcPosMinZ)/2);
            c.name =  c.tile.GetName();
			connectors.add(c);
			return;
		}
		else if (block instanceof blockFerrisConstructor)
        {
            if(((TileEntityFerrisConstructor)tile).isCoreConnector) {
                Connector c = new Connector();
                c.pos = new Vec3d(0, 0, 0);
                c.name = "_Core_";
                connectors.add(c);
                return;
            }
        }
        super.setBlock(block, meta, x, y, z, tile);
    }

    public void makeTag(NBTTagCompound nbt, IBLockCopyHandler handler)
    {
        super.makeTag(nbt, handler);
        NBTTagCompound modelTag = (NBTTagCompound) nbt.getTag("model");
        int connectorNum = connectors.size();
        modelTag.setInteger("connectornum", connectorNum);
        int i = 0;
        for( Connector connector : connectors)
        {
            connector.pos.WriteToNBT("connector"+i, modelTag);
            modelTag.setString("connectorName"+i, connector.name);

            TileEntityBlocksScannerBase childConstructor = connector.tile.GetRegisteredConstructor();
            if(childConstructor != null)
            {
                int cx = childConstructor.xCoord;
                int cy = childConstructor.yCoord;
                int cz = childConstructor.zCoord;
                BlocksScanner copier = new MFWBlocksScanner();
                IModel model = connector.tile.GetCurrentPart();
                copier.Init(childConstructor, cx, cy, cz, childConstructor.GetLimitFrame(), childConstructor.FlagDrawEntity);
                NBTTagCompound childNbt = copier.GetNbt();
                model.writeToNBT(childNbt);
                copier.Register((NBTTagCompound) childNbt.getTag("model"));
                nbt.setTag("PartSlot"+i, childNbt);
                handler.InstantiateModelItem().writeToNBT(childNbt);
            }
            i++;
        }
    }

}
