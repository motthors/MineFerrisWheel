
package jp.mochisystems.mfw._mc.gui;

import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core._mc.gui.container.DefContainer;
import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw._mc.gui.container.ContainerConnector;
import jp.mochisystems.core._mc.gui.container.ContainerBlockScanner;
import jp.mochisystems.mfw._mc.gui.container.ContainerFerrisCore;
import jp.mochisystems.mfw._mc.gui.gui.*;
import jp.mochisystems.mfw._mc.tileEntity.TileEntityConnector;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;


public class MFW_GUIHandler implements IGuiHandler {
	
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == MFW.GUIID_GarlandInit) return new DefContainer();
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile == null)return null;
		switch(ID) {
			case MFW.GUIID_FerrisConstructor:
			case MFW.GUIID_FerrisElevatorConstructor:
				return new ContainerBlockScanner(player.inventory, (TileEntityBlocksScannerBase) tile);
			case MFW.GUIID_FerrisConnector:
				return new ContainerConnector(player.inventory, (TileEntityConnector) tile);
		}

		IModel model = GUIHandler.GetModel(player, world, x, y, z);
		if(model == null) return null;
		switch(ID) {
			case MFW.GUIID_FerrisStoryBoard:
				return new DefContainer();
		}

		return null;
	}
    
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

		switch(ID) {
			case MFW.GUIID_GarlandInit:
				return new GUIGarlandInit();
			case MFW.GUIID_FerrisElevatorConstructor:
			case MFW.GUIID_FerrisConstructor:
				return new GUIFerrisConstructor(player.inventory, (TileEntityBlocksScannerBase) tile);
			case MFW.GUIID_FerrisConnector:
				return new GUIConnector(player.inventory, (TileEntityConnector) tile);
		}


		IModel model = GUIHandler.GetModel(player, world, x, y, z);
		if(model == null) return null;

		switch(ID) {
			case MFW.GUIID_FerrisStoryBoard:
				if(!(model instanceof FerrisSelfMover)) return null;
				return new GUIStoryBoard((FerrisSelfMover) model);
		}
		return null;
	}


}
