
package mfw._mc.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import mfw._core.MFW_Core;
import mfw.ferriswheel.FerrisPartBase;
import mfw._mc.gui.container.*;
import mfw._mc.gui.gui.*;
import mfw.manager.ChangingLimitLineManager;
import mfw._mc.tileEntity.*;
import mfw._mc.tileEntity.TileEntityFerrisCore;
import mochisystems._mc.gui.DefContainer;
import mochisystems._mc.tileentity.TileEntityBlocksScannerBase;
import mochisystems.blockcopier.ILimitLine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public class MFW_GUIHandler implements IGuiHandler {
	
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile == null)return null;
		switch(ID) {
			case MFW_Core.GUIID_FerrisConstructor:
			case MFW_Core.GUIID_FerrisElevatorConstructor:
				return new ContainerFerrisConstructor(player.inventory, (TileEntityBlocksScannerBase) tile);
			case MFW_Core.GUIID_FerrisCore:
				return new ContainerFerrisCore(player.inventory, ((FerrisPartBase) ((TileEntityFerrisCore) tile).blockModel).GetSelectedPartInGUI());
			case MFW_Core.GUIID_FerrisFileManager:
				return new ContainerFileManager(player.inventory, (TileEntityFileManager) tile);
			case MFW_Core.GUIID_FerrisCutter:
			case MFW_Core.GUIID_FerrisStoryBoard:
				return new DefContainer();
			case MFW_Core.GUIID_FerrisConnector:
				return new ContainerConnector(player.inventory, (TileEntityConnector) tile);
		}
		return null;
	}
    
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile==null)return null;
		if(tile instanceof ILimitLine) ChangingLimitLineManager.INSTANCE.saveTile((ILimitLine) tile);
		else ChangingLimitLineManager.INSTANCE.reset();
		switch(ID) {
			case MFW_Core.GUIID_FerrisConstructor:
			case MFW_Core.GUIID_FerrisElevatorConstructor:
				return new GUIFerrisConstructor(x, y, z, player.inventory, (TileEntityBlocksScannerBase) tile);
			case MFW_Core.GUIID_FerrisCore:
				FerrisPartBase part = ((FerrisPartBase) ((TileEntityFerrisCore) tile).blockModel).GetSelectedPartInGUI();
				return part.GetGUIInstance(x, y, z, player.inventory, part);
			case MFW_Core.GUIID_FerrisFileManager:
				return new GUIFileManager(x, y, z, player.inventory, (TileEntityFileManager) tile);
			case MFW_Core.GUIID_FerrisCutter:
				return new GUIFerrisCutter(x, y, z, player.inventory, (TileEntityFerrisCutter) tile);
			case MFW_Core.GUIID_FerrisStoryBoard:
				part = null;
				if(tile instanceof TileEntityFerrisCore)
					part = (FerrisPartBase) ((TileEntityFerrisCore) tile).blockModel;
				if(tile instanceof TileEntityConnector)
					part = ((TileEntityConnector) tile).GetCurrentPart();
				if(part == null) return null;
				return new GUIStoryBoard(x, y, z, part.GetSelectedPartInGUI());
			case MFW_Core.GUIID_FerrisConnector:
				return new GUIConnector(x, y, z, player.inventory, (TileEntityConnector) tile);
		}
		return null;
	}
}
