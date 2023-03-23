package jp.mochisystems.mfw._mc.message;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core.util.IBlockModel;
import jp.mochisystems.core.util.IModelController;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw._mc.gui.container.ContainerFerrisCore;
import jp.mochisystems.mfw._mc.item.ItemFerrisGarlandSeed;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityConnector;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisCore;
import jp.mochisystems.mfw.ferriswheel.*;
import jp.mochisystems.mfw.storyboard.StoryBoardManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Deprecated
public class MessageFerrisMisc implements IMessage {

	public static final int OpenModelGui = 1;

	//	public static final int GUICoreAddSpeed = 100;
//	public static final int GUICoreTurn = 101;
//	public static final int GUICoreStop = 102;
//	public static final int GUICoreResist = 103;


//	public static final int GUICoreSetAngle = 106;
//	public static final int GUICoreSetSpeed = 107;
//	public static final int GUICoreSetAccel = 108;
//	public static final int GUICoreSetResist = 109;
//	public static final int GUICoreSetSetYaw = 110;
//	public static final int GUICoreSetSetPitch = 111;
//	public static final int GUICoreSetAmp = 112;
//	public static final int GUICoreSetPhase = 113;
//	public static final int GUICoreSetSize = 114;

//	public static final int GUICoreSizes = 200;
//	public static final int GUICoreRot1 = 201;
//	public static final int GUICoreRot2 = 202;
//	public static final int GUICoreRotReset = 203;
//	public static final int GUICoreSlotPage = 204;
//	public static final int GUICoreSyncSaveParent = 205;
//	public static final int GUICoreSyncRegistParent = 206;
//	public static final int GUICoreSyncClear = 207;
//	public static final int GUICoreRSFlagRotate = 208;
//	public static final int GUICoreLock = 209;
	public static final int GUICoreSetSound = 210;
	public static final int GUICoreToggleDrawCore = 211;
//	public static final int GUICoreToggleForrowTransform = 212;
//	public static final int GUICoreToggleEnableCollider = 213;
//	public static final int GUICoreOffsetX = 220;
//	public static final int GUICoreOffsetY = 221;
//	public static final int GUICoreOffsetZ = 222;

	public static final int GUICoreSyncTargetChange = 295;
	public static final int GUICoreStoryBoardChange = 296;
	public static final int GUICoreSinConvertChange = 297;
//	public static final int GUICoreSyncMode = 298;
//	public static final int GUICoreModeChange = 299;
	
//	public static final int GUIFileWrite = 300;
//	public static final int GUIFileRead = 301;
//	public static final int GUIFileRename = 302;
//	public static final int GUIFileSendTagArray = 350; // 350~352 reserved
//
//	public static final int GUIFerrisCutterX = 400;
//	public static final int GUIFerrisCutterY = 401;
//	public static final int GUIFerrisCutterZ = 402;
//
//	public static final int ChangeFrameLengthWithKeyX = 500;
//	public static final int ChangeFrameLengthWithKeyY = 501;
//    public static final int ChangeFrameLengthWithKeyZ = 502;

//	public static final int GUIOpenStoryBoard = 600;
//	public static final int GUICloseStoryBoard = 601;
//	public static final int GUIStoryBoardSendData = 602;
//	public static final int GUIStoryBoardStop = 603;
	
	public static final int GUICoreSoundSelectUp = 700;
	public static final int GUICoreSoundSelectDown = 701;

//	public static final int GUIConnectorRename = 800;

//	public static final int GUIGarlandChangeCoreNum = 900;

//	public static final int GuiConnectorWScale = 1000;
//	public static final int GuiConnectorWOffsetX = 1001;
//	public static final int GuiConnectorWOffsetY = 1002;
//	public static final int GuiConnectorWOffsetZ = 1003;
//	public static final int GuiConnectorWRotateX = 1004;
//	public static final int GuiConnectorWRotateY = 1005;
//	public static final int GuiConnectorWLength = 1006;
//	public static final int GuiConnectorWAccel = 1007;
//	public static final int GuiConnectorWSpeed = 1008;
//
//	public static final int GuiConnectorEScale = 1100;
//	public static final int GuiConnectorEOffsetX = 1101;
//	public static final int GuiConnectorEOffsetY = 1102;
//	public static final int GuiConnectorEOffsetZ = 1103;
//	public static final int GuiConnectorERotateX = 1104;
//	public static final int GuiConnectorERotateY = 1105;
//	public static final int GuiConnectorELength = 1106;
//	public static final int GuiConnectorEAccel = 1107;
//	public static final int GuiConnectorESpeed = 1108;

	public static final int GarlandSnap = 2000;


	public int entityId = -1;
	public int x, y, z;
	public int FLAG;
	public int MiscInt;
	public float MiscFloat;
	public byte[] arrayByte;
	
	public MessageFerrisMisc(){}

	public MessageFerrisMisc(IModelController controller, int flag, int imisc, float fMics){
		if (controller instanceof Entity) {
			entityId = ((Entity) controller).getEntityId();
		}
		else {
			TileEntity tile = (TileEntity) controller;
			this.x = tile.getPos().getX();
			this.y = tile.getPos().getY();
			this.z = tile.getPos().getZ();
		}
		this.FLAG = flag;
		this.MiscInt = imisc;
		this.MiscFloat = fMics;
	}
	public MessageFerrisMisc(BlockPos pos, int flag, int imisc, float fMics){
		this(pos.getX(), pos.getY(), pos.getZ(), flag, imisc, fMics);
	}
	public MessageFerrisMisc(int x, int y, int z, int flag, int imisc, float fMics)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.FLAG = flag;
	    this.MiscInt = imisc;
	    this.MiscFloat = fMics;
  	}

	public MessageFerrisMisc(BlockPos pos, int flag, int imisc, float fMics, byte[] abyte) {
		this(pos.getX(), pos.getY(), pos.getZ(), flag, imisc, fMics, abyte);
	}
	public MessageFerrisMisc(int x, int y, int z, int flag, int imisc, float fMics, byte[] abyte)
	{
		this(x,y,z,flag,imisc, fMics);
		this.arrayByte = abyte;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.FLAG);
		buf.writeInt(this.MiscInt);
		buf.writeFloat(this.MiscFloat);
		if(arrayByte!=null)buf.writeInt(arrayByte.length);
		else buf.writeInt(0);
		if(arrayByte!=null)buf.writeBytes(arrayByte);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		this.entityId = buf.readInt();
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    this.FLAG = buf.readInt();
	    this.MiscInt = buf.readInt();
	    this.MiscFloat = buf.readFloat();
	    int arraylen = buf.readInt();
	    if(arraylen<=0)return;
	    arrayByte = new byte[arraylen];
		buf.readBytes(this.arrayByte);
    }


    public static class Handler implements IMessageHandler<MessageFerrisMisc, IMessage> {
		@Override
		public IMessage onMessage(MessageFerrisMisc message, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().player;

			switch (message.FLAG) {
				case GarlandSnap:
					ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
					ItemFerrisGarlandSeed.Snap(stack, player, new String(message.arrayByte));
					return null;
			}
//			BlockPos pos = new BlockPos(message.x, message.y, message.z);
//			World world = player.world;

//			TileEntity tile = null;
//			Entity entity = null;
//			if(message.entityId >= 0) {
//				entity = world.getEntityByID(message.entityId);
//				if(entity == null) return null;
//			}
//			else{
//				tile = world.getTileEntity(pos);
//				if (tile == null) return null;
//			}
//			TileEntityFerrisCore tileWheel = (tile instanceof TileEntityFerrisCore) ? (TileEntityFerrisCore) tile : null;
//			FerrisPartBase ferrisPart = (tileWheel != null) ? tileWheel.blockModel.GetSelectedPartInGUI() : entity!=null ? (FerrisPartBase)((IModelController)entity).GetModel() : null;
//			FerrisSelfMover ferrisParam = (ferrisPart instanceof FerrisSelfMover) ? (FerrisSelfMover) ferrisPart : null;
//			StoryBoardManager storyBoard = null;
//			if (ferrisPart instanceof FerrisSelfMover) storyBoard = ((FerrisSelfMover)ferrisPart).storyboardManager;
//			else if (tile instanceof TileEntityConnector){
//				FerrisSelfMover part = (FerrisSelfMover)((IModelController) tile).GetModel();
//				storyBoard = part.storyboardManager;
//			}


			switch (message.FLAG) {
				case GUICoreSetSound:
				case GUICoreSoundSelectUp:
				case GUICoreSoundSelectDown: //TODO
//					((FerrisSelfMover)ferrisPart).soundManager.SetSoundIndex(message.MiscInt);
					break;
			}
//			if(tileWheel!=null) tileWheel.markBlockForUpdate();
//			if(entity!=null) ((IModelController)entity).markBlockForUpdate();
//			IBlockState state = player.world.getBlockState(pos);
//			player.world.notifyBlockUpdate(pos, state, state, 3);
			return null;
		}
	}
}
