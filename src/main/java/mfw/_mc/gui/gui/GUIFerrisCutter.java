package mfw._mc.gui.gui;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.client.config.GuiButtonExt;
import mochisystems._mc.gui.DefContainer;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import mfw._mc.tileEntity.TileEntityFerrisCutter;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class GUIFerrisCutter extends GuiContainer{
	
//	private static final ResourceLocation TEXTURE = new ResourceLocation(MFW_Core.MODID, "textures/gui/ferriscore.png");
	TileEntityFerrisCutter tile;
	private int buttonid;
	class GUIName{
		String name;
		int x;
		int y;
		int flag;
		int baseID;
		GUIName(String str,int x, int y, int flag, int base){name=str; this.x=x; this.y=y; this.flag = flag; this.baseID = base;}
	}
    Map<Integer, GUIName> GUINameMap = new HashMap<Integer, GUIName>();
    
    public GUIFerrisCutter(int x, int y, int z, InventoryPlayer invPlayer, TileEntityFerrisCutter tile)
    {
        super(new DefContainer());
        this.tile = tile;
    }
    
    // GUI���J�����ьĂ΂�鏉�����֐�
	@Override
	public void initGui()
    {
		super.initGui();
		buttonid = 0;
//		this.guiTop = 0;//this.height*7/8 - ySize/2;
		// �{�^���o�^
//		offsetx = this.guiLeft + 20;
//		offsety = this.guiTop;
//		int offset = 4;
//        int length = 27;

		addButton6(width/5, height*9/10, "", MessageFerrisMisc.GUIFerrisCutterX);
		addButton6(width/2, height*9/10, "", MessageFerrisMisc.GUIFerrisCutterY);
		addButton6(width*4/5, height*9/10, "", MessageFerrisMisc.GUIFerrisCutterZ);
    }
	
    @SuppressWarnings("unchecked")
    public void addButton6(int posx, int posy, String str, int flag)
    {
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx-41, posy, 14, 10, ""));
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx-26, posy, 12, 10, ""));
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx-13, posy, 10, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx   , posy, 10, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+11, posy, 12, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+24, posy, 14, 10, ""));
		GUIName data = new GUIName(str,posx-53,posy+2,flag,buttonid-6);
		GUINameMap.put(buttonid-6, data);
    	GUINameMap.put(buttonid-5, data);
		GUINameMap.put(buttonid-4, data);
    	GUINameMap.put(buttonid-3, data);
    	GUINameMap.put(buttonid-2, data);
    	GUINameMap.put(buttonid-1, data);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
    	super.drawGuiContainerForegroundLayer(mouseX, mouseZ);
//    	this.fontRendererObj.drawString("drawScreen",width/5,0,0xffffff);
    	drawString(this.fontRendererObj, "Position X",         xSize/2-25-width*3/10, ySize/2+height*3/10, 0xffffff);
    	drawString(this.fontRendererObj, "Position Y(Height)", xSize/2-40,            ySize/2+height*3/10, 0xffffff);
    	drawString(this.fontRendererObj, "Lenght Z",         xSize/2-25+width*3/10, ySize/2+height*3/10, 0xffffff);
    	drawString(this.fontRendererObj, ""+tile.getMaxX(), xSize/2-4-width*3/10, ySize/2+height*5/14, 0xffffff);
    	drawString(this.fontRendererObj, ""+tile.getMaxY(), xSize/2-4,            ySize/2+height*5/14, 0xffffff);
    	drawString(this.fontRendererObj, ""+tile.getMaxZ(), xSize/2-4+width*3/10, ySize/2+height*5/14, 0xffffff);
    }
    
    @Override
    public void drawWorldBackground(int p_146270_1_)
    {
    	this.drawGradientRect(0, this.height*7/10, this.width, this.height, -1072689136, -804253680);
    }
	
    public boolean doesGuiPauseGame()
    {
    	return false;
    }
    
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) 
	{
	}
//	/*GUI�̕������̕`�揈��*/
//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
//    {
//        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);   
//        
//        this.fontRendererObj.drawString(tile.,10,147,0x0000A);
//        for(GUIName g :  GUINameMap.values())
//        {
//        	this.fontRendererObj.drawString(g.name,g.CorePosX,g.y,0x404040);
//        }
//        
//        drawString(this.fontRendererObj, "Regist :"+String.format("% 4.2f",tile.rotAccel), 270, 5, 0xffffAFF);
//        drawString(this.fontRendererObj, "Speed :"+String.format("% 4.2f",tile.rotSpeed), 270, 15, 0xffffff);
//        drawString(this.fontRendererObj, "Resist :"+String.format("% 4.1f",tile.rotResist*100f), 270, 32, 0xffffff);
//        drawString(this.fontRendererObj, "Size :"+String.format("% 4.2f",tile.wheelSize), 270, 48, 0xffffff);
//        
//        drawString(this.fontRendererObj, "rot1 :"+String.format("% 4.2f",tile.pitch), 270, 68, 0xffffff);
//        drawString(this.fontRendererObj, "rot2 :"+String.format("% 4.2f",tile.yaw), 270, 80, 0xffffff);
//        this.fontRendererObj.drawString(""+(container.getPageNum()),70,5,0x404040);
////        drawString(this.fontRendererObj, String.format("% 2.1f",ERC_BlockRailManager.clickedTileForGUI.BaseRail.Power), 37, 56, 0xffffff);
////        drawString(this.fontRendererObj, ERC_BlockRailManager.clickedTileForGUI.SpecialGUIDrawString(), 42, 199, 0xffffff);
//    }
	@Override
	protected void actionPerformed(GuiButton button)
	{
		GUIName obj = GUINameMap.get(button.id);
		int data = (button.id - obj.baseID);
		int flag = this.GUINameMap.get(button.id).flag;

		MessageFerrisMisc packet = new MessageFerrisMisc(tile.xCoord,tile.yCoord,tile.zCoord, flag, data, 0, new byte[1]);
	    MFW_PacketHandler.INSTANCE.sendToServer(packet);
	}


}
