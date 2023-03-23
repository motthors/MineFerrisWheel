package jp.mochisystems.mfw._mc.message;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisCore;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncRSPowerStC implements IMessage {
	
	// GUI���瑗�肽�����
	private int x, y, z;
	private	int rs;
	
	public MessageSyncRSPowerStC(){}
	
	public MessageSyncRSPowerStC(int x, int y, int z, int rs)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.rs = rs;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.rs);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    this.rs = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageSyncRSPowerStC, IMessage> {
		@Override
		public IMessage onMessage(MessageSyncRSPowerStC m, MessageContext ctx) {
			TileEntityFerrisCore tile = (TileEntityFerrisCore) FMLClientHandler.instance().getClient().world.getTileEntity(new BlockPos(m.x, m.y, m.z));
			if (tile == null) return null;
			tile.blockModel.setRSPower(m.rs);
//		MFW_Logger.debugInfo("rspower:"+m.rs);
			return null;
		}
	}
}
