package jp.mochisystems.mfw._mc.message;

import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.message.MessageOpenModelGui;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityConnector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenModelStoryBoardGui extends MessageOpenModelGui {

    public MessageOpenModelStoryBoardGui(){}
    public MessageOpenModelStoryBoardGui(CommonAddress address) {
        super(address);
    }

    public static class Handler implements IMessageHandler<MessageOpenModelStoryBoardGui, IMessage> {
        @Override
        public IMessage onMessage(MessageOpenModelStoryBoardGui m, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;

            IModel model = m.address.GetInstance(player.world);
            if (model != null) {
                GUIHandler.OpenForCustomGuiInServer(m.address);
                player.openGui(MFW.INSTANCE, MFW.GUIID_FerrisStoryBoard, player.world, m.address.x, m.address.y, m.address.z);
                return null;
            }

            TileEntityConnector c = (TileEntityConnector) player.world.getTileEntity(new BlockPos(m.address.x, m.address.y, m.address.z));
            if(c != null) {
                player.openGui(MFW.INSTANCE, MFW.GUIID_FerrisStoryBoard, player.world, m.address.x, m.address.y, m.address.z);
                return null;
            }
            return null;
        }
    }
}
