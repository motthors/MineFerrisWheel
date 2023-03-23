package jp.mochisystems.mfw._mc.gui.gui;

import jp.mochisystems.core.util.gui.GuiButtonWrapper;
import jp.mochisystems.core.util.gui.GuiFormattedTextField;
import jp.mochisystems.core.util.gui.GuiLabel;
import jp.mochisystems.mfw.storyboard.programpanel.IProgramPanel;

import static jp.mochisystems.mfw.storyboard.programpanel.IProgramPanel.Type;

public class GUIStoryBoardSettingPart {

	Type type;
	int apiIndex;
	int buttonId;
	GuiButtonWrapper button;
	GuiFormattedTextField textField;
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

	public void SetTextField(GuiFormattedTextField textField)
	{
		this.textField = textField;
	}
	

	public void LoadCurrentValueFromPanel(IProgramPanel panel)
	{
		switch(type)
		{
		case change : 
			button.displayString = panel.getValueString(apiIndex);
			break;
		case inputValue:
			textField.setText(panel.getValueString(apiIndex));
			break;
//		case soundSelector:
//			button.displayString = "";
//			description = SoundManager.Instance.getSoundDomain(Integer.parseInt(panel.getValueString(apiIndex)));
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
//		case soundSelector:
//			return "";
		default : return "";
		}
	}

}
