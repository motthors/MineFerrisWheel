package jp.mochisystems.mfw._mc.message;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.mfw._mc._core.MFW_Command;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCommandSendToPlayerStC implements IMessage{
	
	// GUI���瑗�肽�����
	public byte flag;
	public float f;
	
	public static final byte flag_sync = 0;
	public static final byte flag_dist = 1;
	
	public MessageCommandSendToPlayerStC(){}
	
	public MessageCommandSendToPlayerStC(byte flag, float data)
	{
		this.flag = flag;
	    this.f = data;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(flag);
		buf.writeFloat(this.f);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		this.flag = buf.readByte();
	    this.f = buf.readFloat();
    }

    public static class Handler implements IMessageHandler<MessageCommandSendToPlayerStC, IMessage> {
		@Override
		public IMessage onMessage(MessageCommandSendToPlayerStC m, MessageContext ctx) {
//		EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
			switch (m.flag) {
				case flag_sync:
					FMLClientHandler.instance().getClient().player.sendChatMessage(
						_Core.I18n("message.command.sync." + (m.f >= 0 ? "true" : "false"))
					);
					MFW_Command.doSync = (m.f >= 0);
					break;
				case flag_dist:
					MFW_Command.renderDistRatio = m.f;
					FMLClientHandler.instance().getClient().player.sendChatMessage(
						_Core.I18n("message.command.renderdist") + " : CorePosX" + m.f
					);
					break;
			}
			return null;
		}
	}
}
