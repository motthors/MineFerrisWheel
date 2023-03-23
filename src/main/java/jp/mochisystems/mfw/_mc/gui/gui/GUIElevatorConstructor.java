package jp.mochisystems.mfw._mc.gui.gui;

import jp.mochisystems.core._mc.gui.GUIBlockScannerBase;
import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import net.minecraft.entity.player.InventoryPlayer;

public class GUIElevatorConstructor extends GUIBlockScannerBase {

    public GUIElevatorConstructor(InventoryPlayer playerInventory, TileEntityBlocksScannerBase tile) {
        super(playerInventory, tile);
    }

    protected int FrameLengthHeight() {
        return tile.GetLimitFrame().lenZ();
    }
    protected void AddFrameHeight(int add){
        frame.AddLengths(0, 0, 0, 0, 0, add);
    }
}
