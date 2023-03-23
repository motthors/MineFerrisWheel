package jp.mochisystems.mfw._mc.message;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.manager.EntityWearingModelManager;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.mfw.ferriswheel.FerrisGarland;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MessageSyncNbtForMFWCtS implements IMessage {
    private NBTTagCompound nbt;
    private FerrisPartBase part;
    private CommonAddress address;
    private int action;

    public enum Action{
        ClearSync, ToggleSyncMode, RsMode, Lock, ToggleCol, Reset,
        GarlandCoreUp, GarlandCoreDown, ToggleSyncCopyMode,
    }

    public MessageSyncNbtForMFWCtS() {
    }

    public MessageSyncNbtForMFWCtS(FerrisPartBase part, NBTTagCompound nbt)
    {
        this.nbt = nbt;
        this.part = part;
    }
    public MessageSyncNbtForMFWCtS(FerrisPartBase part, Action action)
    {
        this.part = part;
        this.action = action.ordinal();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(action);
        CommonAddress ad = this.part.GetCommonAddress();
        buf.writeInt(ad.entityId);
        buf.writeInt(ad.x);
        buf.writeInt(ad.y);
        buf.writeInt(ad.z);
        buf.writeInt(ad.TreeListIndex);
        if(nbt == null) {
            buf.writeInt(0);
            return;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            CompressedStreamTools.writeCompressed(nbt, os);
            buf.writeInt(os.size());
            buf.writeBytes(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.address = new CommonAddress();
        this.action = buf.readInt();
        this.address.entityId = buf.readInt();
        this.address.x = buf.readInt();
        this.address.y = buf.readInt();
        this.address.z = buf.readInt();
        this.address.TreeListIndex = buf.readInt();

        int len = buf.readInt();
        if(len <= 0) return;
        byte[] arrayByte = new byte[len];
        buf.readBytes(arrayByte);
        try {
            this.nbt = CompressedStreamTools.readCompressed(new ByteArrayInputStream(arrayByte));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Handler implements IMessageHandler<MessageSyncNbtForMFWCtS, IMessage> {

        @Override
        public IMessage onMessage(MessageSyncNbtForMFWCtS message, MessageContext ctx) {
            EntityPlayer player = null;
            if(ctx.side.isClient()) player = _Core.proxy.GetPlayer(ctx);
            else player = ctx.getServerHandler().player;
            World world = player.world;

            FerrisPartBase model = (FerrisPartBase) message.address.GetInstance(world);
            if(model != null) {
                if(message.nbt != null){
                    NBTTagCompound nbt = new NBTTagCompound();
                    model.writeMineToNBT(nbt);
                    nbt.merge(message.nbt);
                    model.readMineFromNBT(nbt);
                    if(message.address.entityId > -1 && message.address.y > -1){
                        ItemStack stack = player.inventory.armorInventory.get(message.address.y);
                        NBTTagCompound toStack = new NBTTagCompound();
                        EntityWearingModelManager.GetCurrentModel(player, message.address.y).writeToNBT(toStack);
                        stack.setTagCompound(toStack);
                    }
                    if(ctx.side.isServer()) MFW_PacketHandler.INSTANCE.sendToAll(new MessageSyncNbtForMFWCtS(model, message.nbt));
                }
                else {
                    switch(Action.values()[message.action]){
                        case ClearSync:
                            ((FerrisSelfMover)model).ReleaseSyncParent();
                            break;
                        case ToggleSyncMode:
                            ((FerrisSelfMover)model).toggleSyncMode();
                            model.markDirty();
                            return null;
                        case ToggleSyncCopyMode:
                            ((FerrisSelfMover)model).toggleSyncCopyMode();
                            model.markDirty();
                            return null;
                        case RsMode:
                            model.rotateRSFlag();
                            model.markDirty();
                            return null;
                        case Lock:
                            model.toggleLock();
                            model.markDirty();
                            return null;
                        case ToggleCol:
                            ((FerrisSelfMover)model).ToggleEnableCollider();
                            model.markDirty();
                            return null;
                        case Reset:
                            model.Reset();
                            break;
                        case GarlandCoreDown:
                            int num = ((FerrisGarland) model).GetCoreNum() - 1;
                            ((FerrisGarland) model).ChangeCore(num);
                            model.markDirty();
                            return null;
                        case GarlandCoreUp:
                            num = ((FerrisGarland) model).GetCoreNum() + 1;
                            ((FerrisGarland) model).ChangeCore(num);
                            model.markDirty();
                            return null;
                    }
                    if(ctx.side.isServer()) {
                        NBTTagCompound nbt = new NBTTagCompound();
                        model.writeMineToNBT(nbt);
                        MFW_PacketHandler.INSTANCE.sendToAll(new MessageSyncNbtForMFWCtS(model, Action.values()[message.action]));
                    }

                }
            }
            return null;
        }
    }
}
