package jp.mochisystems.mfw._mc.gui.gui;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.GUIBlockScannerBase;
import jp.mochisystems.core._mc.gui.GuiToggleButton;
import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.NbtParamsShadow;
import jp.mochisystems.core.util.gui.*;
import jp.mochisystems.mfw._mc.message.MFW_PacketHandler;
import jp.mochisystems.mfw._mc.message.MessageFerrisMisc;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityFerrisConstructor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import net.minecraft.entity.player.InventoryPlayer;

public class GUIFerrisConstructor extends GUIBlockScannerBase {

    private final NbtParamsShadow.Param<Integer> copyMode;
    private final NbtParamsShadow.Param<Integer> BodyGuide;
    private final NbtParamsShadow.Param<Integer> copyNum;
    private final NbtParamsShadow.Param<Boolean> isCoreConnector;
    private final NbtParamsShadow.Class<Vec3d> scale;
    private final NbtParamsShadow.Class<Vec3d> rotAxis;

    public GUIFerrisConstructor(InventoryPlayer playerInventory, TileEntityBlocksScannerBase tile)
    {
        super(playerInventory, tile);
        copyMode = shadow.Create("copyMode", n -> n::setInteger, NBTTagCompound::getInteger);
        BodyGuide = shadow.Create("BodyGuide", n -> n::setInteger, NBTTagCompound::getInteger);
        copyNum = shadow.Create("copyNum", n -> n::setInteger, NBTTagCompound::getInteger);
        isCoreConnector = shadow.Create("isCoreConnector", n -> n::setBoolean, NBTTagCompound::getBoolean);
        scale = shadow.Create("srcScale", tile.scale, v -> v::WriteToNBT);

        if(tile instanceof TileEntityFerrisConstructor){
            Vec3d _rotAxis = ((TileEntityFerrisConstructor)tile).modelOffset;
            rotAxis = shadow.Create("modelOffset", _rotAxis, v -> v::WriteToNBT);
        }
        else rotAxis = shadow.Create("", new Vec3d(), v -> v::WriteToNBT); //Dummy
    }


	@Override
	public void initGui()
    {
        super.initGui();

        int gDef = -1;

        if(_Core.CONFIG_ANNOTATIONS.isProGui) {

            if(tile instanceof TileEntityFerrisConstructor) {
                // copy num
                Canvas.Register(gDef, new GuiLabel(_Core.I18n("gui.scan.text.sync.rotate_copy"), fontRenderer, 170, height - 55, 0xffffff));
                GuiUtil.addButton2(Canvas, -1, 175, height - 42,
                        () -> { if(copyNum.Get()>1) copyNum.Set(copyNum.Get() - 1); },
                        () -> { if(copyNum.Get()<100) copyNum.Set(copyNum.Get() + 1); });
                Canvas.Register(gDef,
                        new GuiFormattedTextField(0, fontRenderer, 188, height - 40, 22, 10, 0xffffff, 12,
                                () -> String.format("%2d", copyNum.Get()),
                                s -> s.matches(GuiFormattedTextField.regexInteger),
                                v -> copyNum.Set(Integer.parseInt(v))));

                // copy mode
//                Canvas.Register(gDef, new GuiLabel("Copy Mode", fontRenderer, 240, height - 55, 0xffffff));
//                Canvas.Register(gDef,
//                        new GuiToggleButton(0, 240, height - 45, 40, 16,
//                                "Clone", "Add",
//                                () -> copyMode.Get() != 0,
//                                isOn -> copyMode.Set(isOn ? 1 : 0)));


                // body parts guide
                Canvas.Register(1, new GuiLabel(_Core.I18n("gui.scan.text.body_guide"), fontRenderer, -5, 30, 0xffffff));
                Canvas.Register(1,
                        new GuiToggleButton(0, 30, 40, 10, 10,
                                "", "",
                                () -> BodyGuide.Get() == 1,
                                isOn -> ChangeBodyGuide(isOn, 1)));
                Canvas.Register(1,
                        new GuiToggleButton(0, 30 - 10, 50, 8, 15,
                                "", "",
                                () -> BodyGuide.Get() == 2,
                                isOn -> ChangeBodyGuide(isOn, 2)));
                Canvas.Register(1,
                        new GuiToggleButton(0, 30 + 12, 50, 8, 15,
                                "", "",
                                () -> BodyGuide.Get() == 3,
                                isOn -> ChangeBodyGuide(isOn, 3)));
                Canvas.Register(1,
                        new GuiToggleButton(0, 29, 50, 12, 14,
                                "", "",
                                () -> BodyGuide.Get() == 4,
                                isOn -> ChangeBodyGuide(isOn, 4)));
                Canvas.Register(1,
                        new GuiToggleButton(0, 27, 64, 8, 15,
                                "", "",
                                () -> BodyGuide.Get() == 5,
                                isOn -> ChangeBodyGuide(isOn, 5)));
                Canvas.Register(1,
                        new GuiToggleButton(0, 35, 64, 8, 15,
                                "", "",
                                () -> BodyGuide.Get() == 6,
                                isOn -> ChangeBodyGuide(isOn, 6)));

                // body model scale
                GuiUtil.Vec3(_Core.I18n("gui.core.text.scale"), scale, Canvas, fontRenderer, -2, -9, 1,
                        0.05f, this::SyncToServer);


                // Rotate Center
                GuiUtil.Vec3(_Core.I18n("gui.scan.text.sync.axis"), rotAxis, Canvas, fontRenderer,
                        width-78, 150, gDef, 0.05f,
                        this::SyncToServer);
            }

            // core with connector
            GuiUtil.addCheckButton(Canvas, fontRenderer, -1, width - 20, 112,
                    isCoreConnector::Get,
                    _Core.I18n("gui.constructor.switch.coreconnector"),
                    isCoreConnector::Set);

        }

        Canvas.MoveGroup(1, 7, 100, false);
        Canvas.ActiveGroup(1);
    }



    private void ChangeBodyGuide(boolean isOn, int idx)
    {
        int i = isOn ? idx : 0;
        BodyGuide.Set(i);
        scale.Get().CopyFrom(Vec3d.One).mul(0.25f);
        switch(idx)
        {
            case 2:
            case 5: ModelName.Set("L"); break;
            case 3:
            case 6: ModelName.Set("R"); break;
            case 1:
            case 4: ModelName.Set("Body"); break;
        }
        SyncClient();
    }

    private void SendMessageToSetParam(int flag, float param)
    {
        IMessage packet = new MessageFerrisMisc(tile.getPos(), flag, 0, param);
        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }



}
