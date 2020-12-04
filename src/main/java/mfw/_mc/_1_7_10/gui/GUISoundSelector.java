//package mfw._mc._1_7_10.gui;
//
//import java.util.ArrayList;
//
//import mfw._mc._1_7_10._core.MFW_Core;
//import mfw.sound.SoundManager;
//import mfw._mc._1_7_10.tileEntity.TileEntityFerrisCore;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.audio.SoundList;
//import net.minecraft.client.gui.GuiLanguage;
//import net.minecraft.client.gui.GuiSlot;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.resources.Language;
//
//public class GUISoundSelector extends GuiSlot {
//
//	GUIFerrisCoreBase loopHead;
//	TileEntityFerrisCore tile;
//	private ArrayList<String> soundlist = SoundManager.sounds;
//
//	public GUISoundSelector(GUIFerrisCoreBase loopHead, TileEntityFerrisCore tile)
//	{
//		super(Minecraft.getMinecraft(), 
//				loopHead.width/3, //width
//				loopHead.height, 			//height
//				0,		//top
//				loopHead.height,		 	//bottom
//				16);							//slot height
//		this.left = loopHead.width/3*2-100;
//		this.right = loopHead.width;
//		this.loopHead = loopHead;
//		this.tile = tile;
//	}
//
//	public void updateList() 
//	{
//		this.soundlist = SoundManager.sounds;
//	}
//
//	protected int getSize() 
//	{
//		return this.soundlist.size();
//	}
//
//	protected void elementClicked(int par1, boolean isDoubleClick, int par3, int par4) 
//	{
//		if(isDoubleClick){
//			tile.SetSoundIndex(par1);
//		}
//		else{
//			String domain = soundlist.get(par1);
//			tile.getWorldObj().playSoundEffect(tile.xCoord+0.5, tile.yCoord+0.5, tile.zCoord+0.5, domain, 1.0F, 0.9F);
//		}
//	}
//
//	protected boolean isSelected(int par1) {
//		return tile.GetSoundIndex() == par1;
//	}
//
//
//	protected void drawBackground() {
//	}
//
//	protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5, int par6, int par7) 
//	{
//		loopHead.drawCenteredString(loopHead.GetFontRenderer(),
//				this.soundlist.get(par1),
//				this.width / 2, par3 + 1, 
//				16777215);
//	}
//}