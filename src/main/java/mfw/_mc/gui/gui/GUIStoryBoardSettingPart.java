package mfw._mc.gui.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import mochisystems.util.gui.GuiButtonWrapper;
import mochisystems.util.gui.GuiFormatedTextField;
import mfw.sound.SoundManager;
import mfw.storyboard.programpanel.IProgramPanel;
import mochisystems.util.gui.GuiLabel;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import static mfw.storyboard.programpanel.IProgramPanel.*;

public class GUIStoryBoardSettingPart {

	Type type;
	int apiIndex;
	int buttonid;
	int textid;
	GuiButtonWrapper button;
	GuiFormatedTextField textField;
	GuiLabel label;
	
	String description;
	
	int drawX;
	int drawY;
	
	public GUIStoryBoardSettingPart(Type type, int apiIndex, String desc)
	{
		this.type = type;
		this.apiIndex = apiIndex;
		description = desc;
	}
	public void SetLabel(GuiLabel label)
	{
		this.label = label;
	}

	public void SetButton(GuiButtonWrapper button)
	{
		this.button = button;
	}

	public void SetTextField(GuiFormatedTextField textField)
	{
		this.textField = textField;
	}
	

	public void LoadCurrentValueFromPanel(IProgramPanel panel)
	{
		switch(type)
		{
		case change : 
			button.displayString = panel.getValue(apiIndex);
			break;
		case inputValue:
			textField.setText(panel.getValue(apiIndex));
			break;
		case soundSelector:
			button.displayString = "";
			description = SoundManager.getSoundDomain(Integer.parseInt(panel.getValue(apiIndex)));
		default : break;
		}
	}
	
	public String getValue()
	{
		switch(type)
		{
		case change : 
			return button.displayString;
		case inputValue:
			return textField.getText();
		case soundSelector:
			return "";
		default : return "";
		}
	}

}
