package jp.mochisystems.mfw._mc.message;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisCore;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncMoverStC implements IMessage {
	
	public int x, y, z;
	public float[] afloat;
	
	public MessageSyncMoverStC(){}
	
	public MessageSyncMoverStC(int x, int y, int z, float[] fa)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.afloat = fa;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(afloat.length);
		for(int i=0;i<afloat.length;++i)buf.writeFloat(afloat[i]);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    afloat = new float[buf.readInt()];
	    for(int i=0;i<afloat.length;++i)afloat[i] = buf.readFloat();
    }

    public static class Handler implements IMessageHandler<MessageSyncMoverStC, IMessage> {
		@Override
		public IMessage onMessage(MessageSyncMoverStC m, MessageContext ctx) {
			TileEntityFerrisCore tile = (TileEntityFerrisCore) FMLClientHandler.instance().getClient().world.getTileEntity(new BlockPos(m.x, m.y, m.z));
//		if(tile!=null)tile.blockModel.syncRot_recieve(m.afloat);
			return null;
		}
	}
}
