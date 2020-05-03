package mfw.storyboard.programpanel;

import mfw.ferriswheel.FerrisPartBase;
import net.minecraft.util.MathHelper;

public class WaitPanel implements IProgramPanel {
	
	private static String[] modes = {
			"RsInput",
			"Notify",
			"NonStop",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Mode"),
	};

	private final int id_Mode = 0;
	
	private int modeIndex = 0;

	private boolean canDispose = false;
	
	@Override
	public boolean CanUseWith(FerrisPartBase part)
	{
		return true;
	}

	@Override
	public Mode getMode() {
		return Mode.wait;
	}
	
	@Override
	public int ApiNum(){ return 1; }

	@Override
	public Type getType(int apiIndex){
		return datapacks[apiIndex].type;
	}
	
	@Override
	public String getDescription(int apiIndex) {
		return datapacks[apiIndex].description;
	}
	
	@Override
	public int[] Clicked(int apiIndex)
	{
		modeIndex = (modeIndex + 1) % modes.length;
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		return modes[modeIndex];
	}
	
	/**
	 * retval : Value�̍X�V���K�v�ȍ��ڂ�APIIndex�̔z��
	 */
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			modeIndex = Integer.parseInt((String) value);
			return new int[]{apiIndex};
		}catch(NumberFormatException e){
			return new int[]{};
		}
	}

	@Override
	public void setSelection(int apiIndex, int value)
	{
		if(datapacks[apiIndex].type != Type.change) return;
		switch(apiIndex) {
			case id_Mode:
				modeIndex = value;
				modeIndex = MathHelper.clamp_int(modeIndex, 0, modes.length - 1);
				break;
		}
	}

	@Override
	public String[] GetSelectionLabels(int apiIndex)
	{
		switch(apiIndex)
		{
			case id_Mode : return modes;
			default: return null;
		}
	}

	@Override
	public void start()
	{
		if(modeIndex == 2) // nonstop
		{
			canDispose = true;
		}
		else canDispose = false;
	}
	
	
	@Override
	public boolean CanDoNext()
	{
		return canDispose;
	}
	
	@Override
	public boolean run() {
		return canDispose;
	}

	@Override
	public void RSHandler(){if(modeIndex ==0)canDispose=true;}
	@Override
	public void NotifyHandler(){if(modeIndex ==1)canDispose=true;}
	
	@Override
	public String toString()
	{
		return "Wx"+ modeIndex +";";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Mode, p[1]);
	}
	
	@Override
	public String displayDescription()
	{
		return "wait "+modes[modeIndex];
	}
}
