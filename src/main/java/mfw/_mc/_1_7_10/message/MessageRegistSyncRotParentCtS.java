package mfw._mc._1_7_10.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mfw.ferriswheel.FerrisPartAddress;
import mfw.ferriswheel.FerrisPartBase;
import mfw._mc._1_7_10.tileEntity.TileEntityFerrisCore;
import net.minecraft.world.World;

public class MessageRegistSyncRotParentCtS implements IMessage, IMessageHandler<MessageRegistSyncRotParentCtS, IMessage>{

    private FerrisPartAddress parent;
    private FerrisPartAddress child;

	public MessageRegistSyncRotParentCtS(){}
	

	public MessageRegistSyncRotParentCtS(FerrisPartAddress parent, FerrisPartAddress child)
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
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
        parent = new FerrisPartAddress();
        child = new FerrisPartAddress();
	    parent.x = buf.readInt();
	    parent.y = buf.readInt();
	    parent.z = buf.readInt();
	    child.x = buf.readInt();
	    child.y = buf.readInt();
	    child.z = buf.readInt();
        parent.TreeListIndex = buf.readInt();
        child.TreeListIndex = buf.readInt();
    }
	
	@Override
    public IMessage onMessage(MessageRegistSyncRotParentCtS m, MessageContext ctx)
    {
		World world = ctx.getServerHandler().playerEntity.worldObj;
		TileEntityFerrisCore childTile = (TileEntityFerrisCore) world.getTileEntity(m.child.x, m.child.y, m.child.z);
		FerrisPartBase childPart = m.child.GetInstance(childTile);
        childPart.SetNewSyncParent(m.parent);
		return null;
    }

}
