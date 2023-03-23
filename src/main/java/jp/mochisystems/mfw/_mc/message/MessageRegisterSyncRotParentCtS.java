package jp.mochisystems.mfw._mc.message;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisCore;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRegisterSyncRotParentCtS implements IMessage {

    private CommonAddress parent;
    private CommonAddress child;

	public MessageRegisterSyncRotParentCtS(){}
	

	public MessageRegisterSyncRotParentCtS(CommonAddress parent, CommonAddress child)
	{
	    this.parent = parent;
	    this.child = child;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.parent.x);
		buf.writeInt(this.parent.y);
		buf.writeInt(this.parent.z);
		buf.writeInt(this.child.x);
		buf.writeInt(this.child.y);
		buf.writeInt(this.child.z);
		buf.writeInt(this.parent.TreeListIndex);
		buf.writeInt(this.child.TreeListIndex);
		buf.writeInt(this.parent.entityId);
		buf.writeInt(this.child.entityId);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
        parent = new CommonAddress();
        child = new CommonAddress();
	    parent.x = buf.readInt();
	    parent.y = buf.readInt();
	    parent.z = buf.readInt();
	    child.x = buf.readInt();
	    child.y = buf.readInt();
	    child.z = buf.readInt();
        parent.TreeListIndex = buf.readInt();
        child.TreeListIndex = buf.readInt();
		parent.entityId = buf.readInt();
		child.entityId = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageRegisterSyncRotParentCtS, IMessage> {
		@Override
		public IMessage onMessage(MessageRegisterSyncRotParentCtS m, MessageContext ctx) {
			World world = ctx.getServerHandler().player.world;
			TileEntityFerrisCore childTile = (TileEntityFerrisCore) world.getTileEntity(new BlockPos(m.child.x, m.child.y, m.child.z));
			if(childTile == null) return null;
			IModel childPart = m.child.GetInstance(childTile);
			((FerrisSelfMover)childPart).SetNewSyncParent(m.parent);
			return null;
		}
	}
}
