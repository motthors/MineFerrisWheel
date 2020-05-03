package mfw.handler;

import mochisystems._core.Logger;
import mochisystems.blockcopier.message.MessageChangeLimitLine;
import mochisystems.blockcopier.message.PacketHandler;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import mfw.manager.ChangingLimitLineManager;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import mochisystems.blockcopier.ILimitLine;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.tileentity.TileEntity;

public class KeyHandlerClient {
	
	public static final KeyBinding keyEditX2 = new KeyBinding("Change Position-", Keyboard.KEY_NUMPAD2, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditX3 = new KeyBinding("Change Position+", Keyboard.KEY_NUMPAD3, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditY4 = new KeyBinding("Change height-", Keyboard.KEY_NUMPAD4, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditY7 = new KeyBinding("Change height+", Keyboard.KEY_NUMPAD7, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditZ5 = new KeyBinding("Change Depth-", Keyboard.KEY_NUMPAD5, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditZ9 = new KeyBinding("Change Depth+", Keyboard.KEY_NUMPAD9, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyReset = new KeyBinding("LimitLine Reset", Keyboard.KEY_NUMPAD0, "mfw : Change Frame Lnegth");
	public static final KeyBinding[] keyarray = new KeyBinding[]{keyEditX2,keyEditX3,keyEditY4,keyEditY7,keyEditZ5,keyEditZ9, keyReset};
	
	public static void init() {
		for(KeyBinding k : keyarray)
			ClientRegistry.registerKeyBinding(k);

	}

	@SubscribeEvent
	public void keyDown(InputEvent.KeyInputEvent event) 
	{
		int value = 0;
		int idx = -1;
		for(int i=0;i<keyarray.length;++i)
		{
			if(keyarray[i].getIsKeyPressed())
			{
				idx = i;
				break;
			}
		}
		ILimitLine limitLine = ChangingLimitLineManager.INSTANCE.getSaveTile();
		if(limitLine == null)return;
		TileEntity TILE = (TileEntity)limitLine;
		int FLAG;
		int len = 0;
		switch(idx)
		{
		case 0 : case 1 : FLAG =  MessageChangeLimitLine.Width; len = limitLine.getFrameWidth(); break;
		case 2 : case 3 : FLAG =  MessageChangeLimitLine.Height; len = limitLine.getFrameHeight(); break;
		case 4 : case 5 : FLAG =  MessageChangeLimitLine.Length; len = limitLine.getFrameLength(); break;
		case 6 : FLAG = MessageChangeLimitLine.Reset; break;
		default : return;
		}
		int sign = idx%2 == 0 ? -2 : 2;
		boolean alt = Keyboard.isKeyDown(Keyboard.KEY_LMENU);
		boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
		if(alt && ctrl) sign *= 100;
		else if(alt) sign *= 10;
		len += sign;
		Logger.debugInfo(len+":"+sign);
		MessageChangeLimitLine m = new MessageChangeLimitLine(TILE.xCoord, TILE.yCoord, TILE.zCoord, FLAG, len);
		PacketHandler.INSTANCE.sendToServer(m);
	}
}
