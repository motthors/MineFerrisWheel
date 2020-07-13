package mfw.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mfw._core.MFW_Core;
import mfw.ferriswheel.*;
import mfw._mc.gui.container.ContainerFerrisCore;
import mfw._mc.tileEntity.*;
import mfw._mc.tileEntity.TileEntityFerrisCore;
import mfw.storyboard.StoryBoardManager;
import mochisystems._core.Logger;
import mochisystems._mc.tileentity.TileEntityBlocksScannerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageFerrisMisc implements IMessage, IMessageHandler<MessageFerrisMisc, IMessage>{

	
	public static final int GUIConstruct = 1;
	public static final int GUIAddLength = 2;
	public static final int GUIAddWidth  = 3;
	public static final int GUIAddHeight = 4;
	public static final int GUIAddCopyNum  = 5;
	public static final int GUIDrawCoreFlag = 6;
	public static final int GUIDrawEntityFlag = 7;
    public static final int GUICopyModeChange = 8;
    public static final int GUIToggleCoreIsConnector = 9;
	public static final int GUIOddFlag = 10;
	public static final int GUIBodyGuide = 11;
	public static final int GUIConstructScale = 12;
	public static final int GUITrueCopy = 13;

	public static final int GUIBConstruct = 50;
//	public static final int GUIBConstructSendTagArray = 54;

//	public static final int GUICoreAddSpeed = 100;
	public static final int GUICoreTurn = 101;
	public static final int GUICoreStop = 102;
//	public static final int GUICoreResist = 103;


	public static final int GUICoreSetAngle = 106;
	public static final int GUICoreSetSpeed = 107;
	public static final int GUICoreSetAccel = 108;
	public static final int GUICoreSetResist = 109;
	public static final int GUICoreSetSetYaw = 110;
	public static final int GUICoreSetSetPitch = 111;
	public static final int GUICoreSetAmp = 112;
	public static final int GUICoreSetPhase = 113;
	public static final int GUICoreSetSize = 114;

	public static final int GUICoreSizes = 200;
	public static final int GUICoreRot1 = 201;
	public static final int GUICoreRot2 = 202;
	public static final int GUICoreRotReset = 203;
	public static final int GUICoreSlotPage = 204;
	public static final int GUICoreSyncSaveParent = 205;
	public static final int GUICoreSyncRegistParent = 206;
	public static final int GUICoreSyncClear = 207;
	public static final int GUICoreRSFlagRotate = 208;
	public static final int GUICoreLock = 209;
	public static final int GUICoreSetSound = 210;
	public static final int GUICoreToggleDrawCore = 211;
	public static final int GUICoreToggleForrowTransform = 212;
	public static final int GUICoreToggleEnableCollider = 213;
	public static final int GUICoreOffsetX = 220;
	public static final int GUICoreOffsetY = 221;
	public static final int GUICoreOffsetZ = 222;

	public static final int GUICoreSyncTargetChange = 295;
	public static final int GUICoreStoryBoardChange = 296;
	public static final int GUICoreSinConvertChange = 297;
	public static final int GUICoreSyncMode = 298;
//	public static final int GUICoreModeChange = 299;
	
	public static final int GUIFileWrite = 300;
	public static final int GUIFileRead = 301;
	public static final int GUIFileRename = 302;
	public static final int GUIFileSendTagArray = 350; // 350~352 reserved
	
	public static final int GUIFerrisCutterX = 400;
	public static final int GUIFerrisCutterY = 401;
	public static final int GUIFerrisCutterZ = 402;
	
	public static final int ChangeFrameLengthWithKeyX = 500;
	public static final int ChangeFrameLengthWithKeyY = 501;
    public static final int ChangeFrameLengthWithKeyZ = 502;

	public static final int GUIOpenStoryBoard = 600;
	public static final int GUICloseStoryBoard = 601;
	public static final int GUIStoryBoardSendData = 602;
	public static final int GUIStoryBoardStop = 603;
	
	public static final int GUICoreSoundSelectUp = 700;
	public static final int GUICoreSoundSelectDown = 701;

	public static final int GUIConnectorRename = 800;

	public static final int GUIGarlandChangeCoreNum = 900;

	public static final int GuiConnectorWScale = 1000;
	public static final int GuiConnectorWOffsetX = 1001;
	public static final int GuiConnectorWOffsetY = 1002;
	public static final int GuiConnectorWOffsetZ = 1003;
	public static final int GuiConnectorWRotateX = 1004;
	public static final int GuiConnectorWRotateY = 1005;
	public static final int GuiConnectorWLength = 1006;
	public static final int GuiConnectorWAccel = 1007;
	public static final int GuiConnectorWSpeed = 1008;

	public static final int GuiConnectorEScale = 1100;
	public static final int GuiConnectorEOffsetX = 1101;
	public static final int GuiConnectorEOffsetY = 1102;
	public static final int GuiConnectorEOffsetZ = 1103;
	public static final int GuiConnectorERotateX = 1104;
	public static final int GuiConnectorERotateY = 1105;
	public static final int GuiConnectorELength = 1106;
	public static final int GuiConnectorEAccel = 1107;
	public static final int GuiConnectorESpeed = 1108;


//	public static final int GUIBackPartGUIOpen = 9999;
//	public static final int GUISubPartGUIOpenBase = 10000; // +��
	
	// GUI���瑗�肽�����
	public int x, y, z;
	public int FLAG;
	public int MiscInt;
	public float MiscFloat;
	public byte[] arrayByte;
	
	public MessageFerrisMisc(){}
	
	public MessageFerrisMisc(int x, int y, int z, int flag, int imisc, float fMics)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.FLAG = flag;
	    this.MiscInt = imisc;
	    this.MiscFloat = fMics;
  	}
	public MessageFerrisMisc(int x, int y, int z, int flag, int imisc, float fMics, byte[] abyte)
	{
		this(x,y,z,flag,imisc, fMics);
		this.arrayByte = abyte;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
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
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    this.FLAG = buf.readInt();
	    this.MiscInt = buf.readInt();
	    this.MiscFloat = buf.readFloat();
	    int arraylen = buf.readInt();
	    if(arraylen<=0)return;
	    arrayByte = new byte[arraylen];
	    if(arrayByte.length>0)buf.readBytes(this.arrayByte);
    }
	
	@Override
    public IMessage onMessage(MessageFerrisMisc message, MessageContext ctx)
    {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
		if (tile==null)return null;
		TileEntityFerrisCore tileWheel = (tile instanceof TileEntityFerrisCore) ? (TileEntityFerrisCore) tile : null;
		FerrisPartBase ferrisPart = (tileWheel != null) ? ((FerrisPartBase)tileWheel.blockModel).GetSelectedPartInGUI() : null;
		IFerrisParamGetter ferrisParam = (ferrisPart instanceof IFerrisParamGetter) ? (IFerrisParamGetter) ferrisPart : null;
		StoryBoardManager storyBoard = null;
		if(tile instanceof TileEntityFerrisCore) storyBoard = ferrisPart.storyboardManager;
		else if(tile instanceof TileEntityConnector) storyBoard = ((TileEntityConnector)tile).GetCurrentPart().storyboardManager;

//		if(GUISubPartGUIOpenBase <= message.FLAG)
//		{
//			int absoluteSlotIndex = (message.FLAG - GUISubPartGUIOpenBase) + 25 * message.MiscInt;
//			if(!ferrisPart.canOpenChildGUI(absoluteSlotIndex)) return null;
//            ferrisPart.SetSelectedPartInGUI(ferrisPart.GetChildren()[absoluteSlotIndex]);
//            player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj,
//                    message.x, message.y, message.z);
//			return null;
//		}
//		else if(GUIBackPartGUIOpen == message.FLAG)
//		{
//            if(ferrisPart.isRoot()) player.closeScreen();
//            else
//            {
//				((FerrisPartBase)tileWheel.blockModel).BackSelectedPart();
//                player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj,
//                        message.x, message.y, message.z);
//            }
//			return null;
//		}

		switch(message.FLAG)
    	{
		////////////////////////////////GUI Constructer ////////////////////////////////
//    	case GUIAddLength:
//			((TileEntityFerrisConstructor) tile).setFrameLength(message.MiscInt);
//			break;
//    	case GUIAddWidth:
//			((TileEntityFerrisConstructor) tile).setFrameWidth(message.MiscInt);
//			break;
//		case GUIAddHeight:
//			((TileEntityFerrisConstructor) tile).setFrameHeight(message.MiscInt);
//			break;
    	case GUIDrawCoreFlag:
			((TileEntityBlocksScannerBase) tile).toggleFlagDrawCore();
			break;
    	case GUIDrawEntityFlag:
    		((TileEntityBlocksScannerBase) tile).toggleFlagDrawEntity();
			break;
    	case GUIAddCopyNum:
    		((TileEntityBlocksScannerBase) tile).setCopyNum(message.MiscInt);
    		break;
//    	case GUIConstructSendTagArray :
//    		((TileEntityFerrisConstructor) tile).SetItemFerrisWheel(message.MiscInt, message.arrayByte);
//    		break;wwwww
    	case GUICopyModeChange:
    		((TileEntityBlocksScannerBase) tile).rotateCopyMode();
    		break;
		case GUIOddFlag:
			((TileEntityBlocksScannerBase) tile).toggleFlagFrameLenIsOdd();
			break;
        case GUIToggleCoreIsConnector:
            ((TileEntityBlocksScannerBase) tile).toggleFlagCoreIsConnector();
            break;
		case GUIBodyGuide:
			((TileEntityBlocksScannerBase) tile).setBodyGuide(message.MiscInt);
			break;
		case GUIConstructScale:
			((TileEntityBlocksScannerBase) tile).setScale(message.MiscFloat);
			break;
		case GUITrueCopy:
			((TileEntityBlocksScannerBase) tile).toggleFlagTrueCopy();
			break;

            //////////////////////////////// GUI BasketConstruct ////////////////////////////////
//    	case GUIBConstruct: ��������Ȃ�
//    		if(!((FerrisBasketConstructor) tile).isExistBasket(player))break;
//			player = ctx.getServerHandler().playerEntity;
//			if(message.arrayByte!=null)((FerrisBasketConstructor) tile).modelName = new String(message.arrayByte);
//			((FerrisBasketConstructor) tile).startConstructBasket(player);
//			break;
//    	case GUIBAddOutLength:
//			((FerrisBasketConstructor) tile).setFrameLength(message.MiscInt);
//			break;
//    	case GUIBAddOutWidth:
//			((FerrisBasketConstructor) tile).setFrameWidth(message.MiscInt);
//			break;
//    	case GUIBAddOutHeight:
//			((FerrisBasketConstructor) tile).setFrameHeight(message.MiscInt);
//			break;
//    	case GUIBConstructSendTagArray :
//    		((FerrisBasketConstructor) tile).SetItemFerrisBasket(message.arrayByte, message.MiscInt);
//    		break;
			//////////////////////////////// GUI FileManager ////////////////////////////////
    	case GUIFileRename:
    		((TileEntityFileManager) tile).ReNameItemStack(new String(message.arrayByte));
			return null;
    	case GUIFileSendTagArray: return null;
    	case GUIFileSendTagArray+1:
    		((TileEntityFileManager) tile).FileRead_server(message.arrayByte, message.MiscInt, player, 1);
    		break;
    	case GUIFileSendTagArray+2:
    		((TileEntityFileManager) tile).FileRead_server(message.arrayByte, message.MiscInt, player, 2);
			break;
            //////////////////////////////// GUI Connector ////////////////////////////////
            case GUIConnectorRename:
                if(message.arrayByte == null) return null;
                ((TileEntityConnector) tile).SetName(new String(message.arrayByte));
                break;

            //////////////////////////////// GUI core ////////////////////////////////
//    	case GUICoreAddSpeed:
//    		float add = 0;
//			switch(message.MiscInt)
//			{
//				case 0 : add =-10.0f; break;
//				case 1 : add = -1.0f; break;
//				case 2 : add = -0.1f; break;
//				case 3 : add =  0.1f; break;
//				case 4 : add =  1.0f; break;
//				case 5 : add = 10.0f; break;
//			}
//			ferrisParam.SetAccel(ferrisParam.GetAccel()+add);
//			break;
    	case GUICoreTurn:
			if(ferrisPart instanceof FerrisWheel)((FerrisWheel) ferrisPart).turnSpeed();
			else if(ferrisPart instanceof FerrisElevator)((FerrisElevator) ferrisPart).turnSpeed();
			break;
//    	case GUICoreResist:
//    	    float regist = ((FerrisWheel) ferrisPart).rotResist;
//            switch(message.MiscInt)
//            {
//                case 0 : regist *= 1.1f; break;
//                case 1 : regist *= 1.01f; break;
//                case 2 : regist /= 1.01f; break;
//                case 3 : regist /= 1.1f; break;
//            }
//			((FerrisWheel) ferrisPart).setResist(regist);
//			break;

		case GUICoreToggleForrowTransform:
			ferrisPart.ToggleIsIndependentTransform();
			break;
//    	case GUICoreSizes:
//			add = 0;
//			switch(message.MiscInt)
//			{
//				case 0 : add = -0.1f; break;
//				case 1 : add = -0.01f; break;
//				case 2 : add =  0.01f; break;
//				case 3 : add =  0.1f; break;
//			}
//			ferrisPart.setSize(ferrisPart.Scale.get() + add);
//			break;
//    	case GUICoreRot1:
//    	case GUICoreRot2:
//			((FerrisWheel) ferrisPart).setRot(message.MiscInt, message.FLAG);
//            ((FerrisWheel) ferrisPart).RotateConnectorByAttitude();
//            break;
		case GUICoreOffsetX:
			ferrisPart.offset.x = message.MiscFloat;
			break;
		case GUICoreOffsetY:
			ferrisPart.offset.y = message.MiscFloat;
			break;
		case GUICoreOffsetZ:
			ferrisPart.offset.z = message.MiscFloat;
			break;


    	case GUICoreRotReset:
    		if(ferrisPart instanceof FerrisWheel) {
				((FerrisWheel) ferrisPart).resetRot();
				((FerrisWheel) ferrisPart).RotateConnectorByAttitude();
			}
    		ferrisPart.Reset();
            break;
    	case GUICoreSlotPage:
    		((ContainerFerrisCore)player.openContainer).changePage(message.MiscInt);
    		break;
    	case GUICoreSyncClear:
			ferrisPart.ReleaseSyncParent();
    		return null;
    	case GUICoreLock :
			ferrisPart.toggleLock();
    		break;
			
    	case GUICoreSetSound :
    	case GUICoreSoundSelectUp :
    	case GUICoreSoundSelectDown :
			ferrisPart.soundManager.SetSoundIndex(message.MiscInt);
    		break;

		case GUICoreToggleDrawCore:
			ferrisPart.ToggleVisibleCore();
			break;

		case GUICoreToggleEnableCollider:
			((FerrisWheel)ferrisPart).toggleEnableCollider();
			break;

    	case GUICoreStop:
			ferrisPart.toggleStopFlag();
			break;	
//    	case GUICoreModeChange : 
//    		((FerrisWheel) blockModel).changeRotFlag();
//    		break;
    	case GUICoreStoryBoardChange:
			ferrisPart.toggleStoryBoardFlag();
    		break;
    	case GUICoreSinConvertChange :
			((FerrisWheel) ferrisPart).toggleSinConvertFlag();
    		break;
    	case GUICoreSyncTargetChange:
			if(ferrisPart instanceof FerrisWheel) ((FerrisWheel) ferrisPart).toggleSyncFlag();
			else if(ferrisPart instanceof FerrisElevator) ((FerrisElevator) ferrisPart).toggleSyncFlag();
			break;
    	case GUICoreSyncMode :
			if(ferrisPart instanceof FerrisWheel) ((FerrisWheel) ferrisPart).toggleSyncMode();
			else if(ferrisPart instanceof FerrisElevator) ((FerrisElevator) ferrisPart).toggleSyncMode();
    		break;
    	case GUICoreRSFlagRotate :
			ferrisPart.rotateRSFlag();
    		break;

			case GUICoreSetAngle : ((IFerrisParamGetter) ferrisPart).Position().Init(message.MiscFloat); break;
			case GUICoreSetSpeed : ((IFerrisParamGetter) ferrisPart).SetSpeed(message.MiscFloat); break;
			case GUICoreSetAccel : ((IFerrisParamGetter) ferrisPart).SetAccel(message.MiscFloat); break;
			case GUICoreSetResist : ((IFerrisParamGetter) ferrisPart).SetResist(message.MiscFloat); break;
			case GUICoreSetSetYaw : ((IFerrisParamGetter) ferrisPart).SetYaw(message.MiscFloat); break;
			case GUICoreSetSetPitch : ((IFerrisParamGetter) ferrisPart).SetPitch(message.MiscFloat); break;
			case GUICoreSetAmp: ((IFerrisParamGetter)ferrisPart).Amp().Init(message.MiscFloat); break;
			case GUICoreSetPhase: ((IFerrisParamGetter)ferrisPart).Phase().Init(message.MiscFloat); break;
			case GUICoreSetSize : ((IFerrisParamGetter) ferrisPart).SetLocalScale(message.MiscFloat); break;



            ////////////////////////////////////StoryBoard/////////////////////////////

            case GUIOpenStoryBoard:
                player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisStoryBoard, player.worldObj, message.x, message.y, message.z);
                return null;
            case GUICloseStoryBoard :
                player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj, message.x, message.y, message.z);
                return null;
            case GUIStoryBoardSendData :
            	if(storyBoard == null) return null;
                String serial = (message.arrayByte != null) ? new String(message.arrayByte) : "";
				storyBoard.createFromSerialCode(serial);
                break;
            case GUIStoryBoardStop :
				if(storyBoard == null) return null;
				storyBoard.clear();
                break;

            ////////////////////////////////////GUI Connector/////////////////////////////

			case GuiConnectorWScale: ((TileEntityConnector)tile).wheel.setSize(message.MiscFloat); break;
			case GuiConnectorWOffsetX: ((TileEntityConnector)tile).wheel.offset.x = message.MiscFloat; break;
			case GuiConnectorWOffsetY: ((TileEntityConnector)tile).wheel.offset.y = message.MiscFloat; break;
			case GuiConnectorWOffsetZ: ((TileEntityConnector)tile).wheel.offset.z = message.MiscFloat; break;
			case GuiConnectorWRotateX: ((TileEntityConnector)tile).wheel.pitch = message.MiscFloat; break;
			case GuiConnectorWRotateY: ((TileEntityConnector)tile).wheel.yaw = message.MiscFloat; break;
			case GuiConnectorWLength: ((TileEntityConnector)tile).wheel.rotAngle.Init(message.MiscFloat); break;
			case GuiConnectorWAccel: ((TileEntityConnector)tile).wheel.SetAccel(message.MiscFloat); break;
			case GuiConnectorWSpeed: ((TileEntityConnector)tile).wheel.SetSpeed(message.MiscFloat); break;

			case GuiConnectorEScale: ((TileEntityConnector)tile).elevator.setSize(message.MiscFloat); break;
			case GuiConnectorEOffsetX: ((TileEntityConnector)tile).elevator.offset.x = message.MiscFloat; break;
			case GuiConnectorEOffsetY: ((TileEntityConnector)tile).elevator.offset.y = message.MiscFloat; break;
			case GuiConnectorEOffsetZ: ((TileEntityConnector)tile).elevator.offset.z = message.MiscFloat; break;
			case GuiConnectorERotateX: ((TileEntityConnector)tile).elevator.pitch = message.MiscFloat; break;
			case GuiConnectorERotateY: ((TileEntityConnector)tile).elevator.yaw = message.MiscFloat; break;
			case GuiConnectorELength: ((TileEntityConnector)tile).elevator.length.Init(message.MiscFloat); break;
			case GuiConnectorEAccel: ((TileEntityConnector)tile).elevator.SetAccel(message.MiscFloat); break;
			case GuiConnectorESpeed: ((TileEntityConnector)tile).elevator.SetSpeed(message.MiscFloat); break;

			////////////////////////////////////Garland/////////////////////////////
            case GUIGarlandChangeCoreNum:
                int num = ((FerrisGarland)ferrisPart).GetCoreNum() + message.MiscInt;
                ((FerrisGarland)ferrisPart).ChangeCore(num);
                player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj,
                        message.x, message.y, message.z);
                break;

    	}
		player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
        return null;
    }
    
}
