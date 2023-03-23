package jp.mochisystems.mfw._mc._core;

import jp.mochisystems.mfw._mc.message.MFW_PacketHandler;
import jp.mochisystems.mfw._mc.message.MessageCommandSendToPlayerStC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class MFW_Command extends CommandBase{

	public static boolean doSync = true;
	public static float renderDistRatio = 1.0f;
	public static final int chunkRatio = 2;

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public String getName()
	{
		return "mfw";
	}

	@Override
	public String getUsage(ICommandSender par1)
	{
		return "commands.mfw.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if(args.length == 2)
		{
			byte flag = -1;
			float data = 0;
			if(args[0].equals("sync"))
			{
				if(args[1].equals("true")) data = 1;
				else if(args[1].equals("false")) data = -1;
				else
				{
					player.sendMessage(new TextComponentString("usage : /mfw sync [true/false]"));
					return;
				}
				flag = MessageCommandSendToPlayerStC.flag_sync;
			}
			else if(args[0].equals("renderdist"))
			{
				try{
					data = Float.parseFloat(args[1]);
					if(data < 1.0f || 10.0f < data)throw new Exception();
				}catch(Exception e){
					player.sendMessage(new TextComponentString("usage : /mfw renderdist [1~10]"));
					return;
				}
				flag = MessageCommandSendToPlayerStC.flag_dist;
			}
			else
			{
				player.sendMessage(new TextComponentString("usage : /mfw [sync/renderdist] (data)"));
				return;
			}

			MessageCommandSendToPlayerStC packet = new MessageCommandSendToPlayerStC(flag, data);
			MFW_PacketHandler.INSTANCE.sendTo(packet, player);
		}
		else
			player.sendMessage(new TextComponentString("usage : /mfw [sync/renderdist] (data)"));
	}
}