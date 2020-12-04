package mfw._mc._1_7_10.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mfw._mc._1_7_10._core.MFW_Core;
import mfw.ferriswheel.FerrisPartBase;
import mfw._mc._1_7_10.tileEntity.TileEntityFerrisCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageMoveInGui implements IMessage, IMessageHandler<MessageMoveInGui, IMessage>{

	private int x, y, z;
	private int mode, index;

	private static final int OpenChild = 0;
	private static final int Back = 1;

	@SuppressWarnings("unused")
	public MessageMoveInGui(){}

	public MessageMoveInGui(int x, int y, int z)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
  	}

  	public MessageMoveInGui Open(int index)
	{
		this.mode = OpenChild;
		this.index = index;
		return this;
	}

	public MessageMoveInGui Back()
	{
		this.mode = Back;
		return this;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.mode);
		buf.writeInt(this.index);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
		this.z = buf.readInt();
		this.mode = buf.readInt();
		this.index = buf.readInt();
    }
	
	@Override
    public IMessage onMessage(MessageMoveInGui m, MessageContext ctx)
    {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(m.x, m.y, m.z);
		TileEntityFerrisCore tileWheel = (tile instanceof TileEntityFerrisCore) ? (TileEntityFerrisCore) tile : null;
		FerrisPartBase ferrisPart = (tileWheel != null) ? ((FerrisPartBase)tileWheel.blockModel).GetSelectedPartInGUI() : null;
		if(ferrisPart == null) return null;

		switch(m.mode)
		{
			case OpenChild :
				if(!ferrisPart.canOpenChildGUI(m.index)) return null;
				ferrisPart.SetSelectedPartInGUI(ferrisPart.GetChildren()[m.index]);
				player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj,
						m.x, m.y, m.z);
				return null;

			case Back :
				if(ferrisPart.isRoot()) player.closeScreen();
				else
				{
					((FerrisPartBase)tileWheel.blockModel).BackSelectedPart();
					player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj,
							m.x, m.y, m.z);
				}
				return null;
		}

		return null;
    }
}
