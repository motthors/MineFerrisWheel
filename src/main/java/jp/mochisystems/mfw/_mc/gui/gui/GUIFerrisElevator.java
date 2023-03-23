package jp.mochisystems.mfw._mc.gui.gui;

import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.mfw.ferriswheel.FerrisElevator;
import net.minecraft.entity.player.InventoryPlayer;

public class GUIFerrisElevator extends GUIFerrisSelfMover {

	private final FerrisElevator elevator;

	public GUIFerrisElevator(InventoryPlayer playerInventory, IModel elevator)
	{
		super(playerInventory, elevator);
		this.elevator = (FerrisElevator) elevator;

	}

    @Override
    protected void UpdateCameraPosFromCore(Vec3d dest, float tick)
    {
        Vec3d.Lerp(dest, tick, elevator.prevLocalPosition, elevator.currentLocalPosition);
        dest.add(elevator.connectorFromParent.Current());
        dest.add(part.controller.CorePosX(), part.controller.CorePosY(), part.controller.CorePosZ());
    }

}