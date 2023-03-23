package jp.mochisystems.mfw._mc.gui.gui;

import jp.mochisystems.core._mc.gui.GUICanvasGroupControl;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.message.MessageSyncNbtCtS;
import jp.mochisystems.core._mc.message.PacketHandler;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.core.util.NbtParamsShadow;
import jp.mochisystems.core.util.gui.*;
import jp.mochisystems.mfw._mc.message.MFW_PacketHandler;
import jp.mochisystems.mfw._mc.message.MessageFerrisMisc;
import jp.mochisystems.mfw._mc.message.MessageOpenModelStoryBoardGui;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityConnector;
import jp.mochisystems.mfw._mc.gui.container.ContainerConnector;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.lwjgl.opengl.GL11;

public class GUIConnector extends GUICanvasGroupControl {

    private final TileEntityConnector tile;

    private TileEntityConnector.Mode slotActiveMode = TileEntityConnector.Mode.none;

    private final int paramGroup = 1;

    private final NbtParamsShadow shadow;
    private final NbtParamsShadow.Param<String> connectorName;

    private final NbtParamsShadow modelShadow;
    private final NbtParamsShadow.Param<Float> modelAccel;
    private final NbtParamsShadow.Param<Float> modelSpeed;
    private final NbtParamsShadow.Param<Float> modelPosition;
    private final NbtParamsShadow.Class<Vec3d> modelScale;
    private final NbtParamsShadow.Class<Vec3d> modelOffset;
    private final NbtParamsShadow.Class<Quaternion> modelTilt;
    private final NbtParamsShadow.Param<Boolean> modelStop;


    public GUIConnector(InventoryPlayer invPlayer, TileEntityConnector tile)
    {
        super(new ContainerConnector(invPlayer, tile));
        this.tile = tile;
        xSize = 420;
        ySize = 270;

        NBTTagCompound src = tile.writeToNBT(new NBTTagCompound());
        shadow = new NbtParamsShadow(src);
        connectorName = shadow.Create("connectorName", n->n::setString, NBTTagCompound::getString);

        //SelfMover, PartBase
        modelShadow = new NbtParamsShadow(tile.modelNbt);
        modelAccel = modelShadow.Create("accel", n->n::setFloat, NBTTagCompound::getFloat);
        modelSpeed = modelShadow.Create("speed", n->n::setFloat, NBTTagCompound::getFloat);
        modelPosition = modelShadow.Create("rot", n->n::setFloat, NBTTagCompound::getFloat);
        modelScale = modelShadow.Create("scale", new Vec3d(), v->v::WriteToNBT);
        modelOffset = modelShadow.Create("offset", new Vec3d(), v->v::WriteToNBT);
        modelTilt = modelShadow.Create("tilt", new Quaternion(), v->v::WriteToNBT);
        modelStop = modelShadow.Create("stopflag", n->n::setBoolean, NBTTagCompound::getBoolean);

    }

    @Override
    protected void UpdateCameraPosFromCore(Vec3d dest, float tick) {
        dest.SetFrom(0.5, 0.5, 0.5);
        dest.add(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int centerW = this.width / 2;
        int centerH = this.height / 2;
        int nameTextWidth = 80;
        int nameTextHeight = 10;
        int offset = 4;

        ((ContainerConnector) inventorySlots).SetWindowSize(width, height);

        if(modelScale.Get().length() < 0.001) modelScale.Get().CopyFrom(Vec3d.One);

        GuiFormattedTextField txt = new GuiFormattedTextField(0, fontRenderer,
                        centerW-nameTextWidth+offset, 80+offset,
                        (nameTextWidth-offset) * 2, (nameTextHeight-offset) * 2, 0xffffff, 22,
                        connectorName::Get,
                        s -> true,
                        t -> {
                            connectorName.Set(t);
                            Sync();
                        });
        txt.setEnableBackgroundDrawing(true);
        Canvas.Register(-1, txt);

        /////////////////// wheel
        modelStop.Set(false);

        Canvas.MoveGroup(paramGroup, width - 80, 0, false);

        GuiUtil.AddInspector(Canvas, fontRenderer, "Accel", paramGroup, -5, 10, true,
                modelAccel::Get,
                modelAccel::Set, 0.05f,
                this::Sync);
        GuiUtil.AddInspector(Canvas, fontRenderer, "Speed", paramGroup, -5, 30, true,
                modelSpeed::Get,
                modelSpeed::Set, 0.05f,
                this::Sync);
        GuiUtil.AddInspector(Canvas, fontRenderer, "Position", paramGroup, -5, 50, true,
                modelPosition::Get,
                modelPosition::Set, 0.05f,
                this::Sync);

//        Canvas.Register(paramGroup, new GuiLabel("Accel:", fontRenderer, -5, 10, 0xffffff));
//        Canvas.Register(paramGroup, new GuiLabel("Speed:", fontRenderer, -5, 30, 0xffffff));
//        Canvas.Register(paramGroup, new GuiLabel("Angle:", fontRenderer, -5, 50, 0xffffff));
//
//        Canvas.Register(paramGroup,
//                new GuiFormattedTextField(0, fontRenderer, 25, 10, 30, 11, 0xffffff, 12,
//                        () -> String.format("%7.2f", modelAccel.Get()),
//                        s -> s.matches(GuiFormattedTextField.regexNumber),
//                        t -> {
//                            modelAccel.Set(Float.parseFloat(t)); Sync(); }));
//        Canvas.Register(paramGroup,
//                new GuiFormattedTextField(0, fontRenderer, 25, 30, 30, 11, 0xffffff, 12,
//                        () -> String.format("%7.2f", modelSpeed.Get()),
//                        s -> s.matches(GuiFormattedTextField.regexNumber),
//                        t -> {
//                            modelSpeed.Set(Float.parseFloat(t)); Sync(); }));
//        Canvas.Register(paramGroup,
//                new GuiFormattedTextField(0, fontRenderer, 25, 50, 30, 11, 0xffffff, 12,
//                        () -> String.format("%7.2f", modelPosition.Get()),
//                        s -> s.matches(GuiFormattedTextField.regexNumber),
//                        t -> {
//                            modelPosition.Set(Float.parseFloat(t)); Sync(); }));

        GuiUtil.Vec3("Scale", modelScale, Canvas, fontRenderer, 0, 72, paramGroup,
                0.05f, this::Sync);


        GuiUtil.Quaternion("Rotate", modelTilt, Canvas, fontRenderer,
                0, 117, paramGroup, 0.01f,
                this::Sync);


        GuiUtil.Vec3("Offset", modelOffset, Canvas, fontRenderer,
                0, 162,
                paramGroup, 0.01f,
                this::Sync);

//            TODO いつか対応したい
//        Canvas.Register(paramGroup, new GuiButtonWrapper(-1, -100, 5, 70, 15, "StoryBoard",
//                () -> {
//                    CommonAddress address = new CommonAddress().Init(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 0);
//                    GUIHandler.OpenCustomGuiInClient(address);
//                    MFW_PacketHandler.INSTANCE.sendToServer(new MessageOpenModelStoryBoardGui(address));
//                }));

        GuiUtil.addCheckButton(Canvas, fontRenderer, paramGroup, 50, 216,
                modelStop::Get,
                "Stop",
                isOn -> {
                    modelStop.Set(isOn);
                    this.Sync();
                });



        slotActiveMode = TileEntityConnector.Mode.none;

        Canvas.ActiveGroup(-1);
        Canvas.DisableGroup(paramGroup);
    }

    private void Sync()
    {
        shadow.WriteAll();
        modelShadow.WriteAll();
        shadow.GetNbtTag().setTag("model", modelShadow.GetNbtTag());
        PacketHandler.INSTANCE.sendToServer(new MessageSyncNbtCtS(tile, shadow.GetNbtTag()));
    }



    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);

        if(slotActiveMode != tile.mode)
        {
            slotActiveMode = tile.mode;
            switch(slotActiveMode)
            {
                case wheel :
                    Canvas.ActiveGroup(paramGroup);
//                    Canvas.DisableGroup(elevatorGroup);
                    break;
                case elevator :
//                    Canvas.ActiveGroup(elevatorGroup);
                    Canvas.DisableGroup(paramGroup);
                    break;
                case none:
                    Canvas.DisableGroup(paramGroup);
//                    Canvas.DisableGroup(elevatorGroup);
                    break;
            }
        }
    }





    @Override
    public void drawWorldBackground(int p_146270_1_)
    {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        int centerW = this.width / 2;
        int centerH = this.height / 2;
        int width = 83;
        int height = 10;

        drawString(fontRenderer, "Connector Name",
                centerW-width, 67, 0xffffff);

        this.drawGradientRect(centerW-width, centerH + 2,
                centerW + width, centerH + 60,
                0x88000000, 0x88000000);
        this.drawGradientRect(centerW-width, centerH + 62,
                centerW + width, centerH + 82,
                0x88000000, 0x88000000);

        this.drawGradientRect(centerW+width+5, 63,
                centerW+width+25, 83,
                0x88000000, 0x88000000);

        this.drawGradientRect(this.width-80, 0,
                this.width, this.height,
                0x88000000, 0x88000000);
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
//        if(!textField.getText().equals(tile.GetName())) textField.setText(tile.GetName());
//        this.textField.drawTextBox();

//        if(tile.isInserted())for(GuiTextField f : textFields) ChangeTextPos(f, 50);
//        else for(GuiTextField f : textFields) ChangeTextPos(f, -1000);
//
//        for(GuiTextField f : textFields) f.drawTextBox();
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }


    private void SendMessageToSetParam(int flag, float param)
    {
        IMessage packet = new MessageFerrisMisc(tile.getPos(), flag, 0, param);
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }

}
