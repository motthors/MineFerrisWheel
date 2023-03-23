package jp.mochisystems.mfw._mc.message;

import jp.mochisystems.mfw._mc._core.MFW;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MFW_PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MFW.MODID);
  
  	public static void init()
  	{
  		int i=0;
  		INSTANCE.registerMessage(MessageFerrisMisc.Handler.class, MessageFerrisMisc.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSyncMoverStC.Handler.class, MessageSyncMoverStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(MessageRegisterSyncRotParentCtS.Handler.class, MessageRegisterSyncRotParentCtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSyncRSPowerStC.Handler.class, MessageSyncRSPowerStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(MessageCommandSendToPlayerStC.Handler.class, MessageCommandSendToPlayerStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(MessageSyncNbtForMFWCtS.Handler.class, MessageSyncNbtForMFWCtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSyncNbtForMFWCtS.Handler.class, MessageSyncNbtForMFWCtS.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(MessageOpenModelStoryBoardGui.Handler.class, MessageOpenModelStoryBoardGui.class, i++, Side.SERVER);
  	}
}
