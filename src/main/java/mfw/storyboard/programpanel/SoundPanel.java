package mfw.storyboard.programpanel;

import mfw._core.MFW_Core;
import mfw.ferriswheel.FerrisPartBase;
import mfw.ferriswheel.FerrisWheel;
import mfw.sound.SoundManager;
import mochisystems.math.Vec3d;
import net.minecraft.util.MathHelper;

public class SoundPanel implements IProgramPanel {
	
	private static String[] modes = {
			"Once",
			"Continue",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Mode"),
			new DataPack(Type.change, "��"),
			new DataPack(Type.change, "��"),
	};
	private FerrisPartBase part;
	
	private final int id_Mode = 0;
	private final int id_SoundIdx = 1;

	private int modeIndex = 0;
	private int soundIndex = 0;

	@Override
	public boolean CanUseWith(FerrisPartBase part)
	{
		this.part = part;
		return true;
	}

	@Override
	public Mode getMode() {
		return Mode.sound;
	}
	
	@Override
	public int ApiNum(){ return 3; }

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
		switch(apiIndex){
		case id_Mode : 
			modeIndex = (modeIndex + 1) % modes.length;
			break;
		case id_SoundIdx :
			soundIndex = (soundIndex + 1) % SoundManager.sounds.size();
			return new int[]{apiIndex,apiIndex+1};
		case id_SoundIdx+1 :
			soundIndex--;
			if(soundIndex < 0) soundIndex = SoundManager.sounds.size() - 1;
			return new int[]{apiIndex,apiIndex-1};
		}
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		switch(apiIndex){
		case id_Mode : 
			return modes[modeIndex];
		case id_SoundIdx :
		case id_SoundIdx+1 :
//			return soundIndex+"";
			return SoundManager.sounds.get(soundIndex);
		default : return "";
		}
	}
	
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			switch(apiIndex){
			case id_Mode : 
				modeIndex = Integer.parseInt((String) value);
				break;
			case id_SoundIdx :
				soundIndex = Integer.parseInt((String) value);
				break;
			}
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
			case id_SoundIdx:
				soundIndex = value;
				soundIndex = MathHelper.clamp_int(soundIndex, 0, SoundManager.sounds.size() - 1);
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
	}
	
	@Override
	public boolean CanDoNext()
	{
		return true;
	}
	
	@Override
	public boolean run() {
		switch (modeIndex) {
		case 0: //once
            Vec3d p = part.connectorFromParent.Current();
			String domain = MFW_Core.MODID+":"+SoundManager.getSoundDomain(soundIndex);
			mochisystems._core._Core.proxy.PlaySoundOnce(p.x+0.5, p.y+0.5, p.z+0.5, domain, 1.0F, 0.9F);
			break;
		case 1: // continue
			part.soundManager.SetSoundIndex(soundIndex);
			break;
		default:
			break;
		}
		
		return true;
	}

	@Override
	public void RSHandler(){}
	@Override
	public void NotifyHandler(){}
	
	@Override
	public String toString()
	{
		return "Mx"+modeIndex+"x"+soundIndex+";";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Mode, p[1]);
		setValue(id_SoundIdx, p[2]);
	}
	
	@Override
	public String displayDescription()
	{
		switch (modeIndex) {
		case 0: //once
			return "FerrisSound "+"\" "+SoundManager.getSoundDomain(soundIndex)+" \"";
		case 1: // continue
			return "Frame.FerrisSound <= "+"\" "+SoundManager.getSoundDomain(soundIndex)+" \"";
		default:
			return "error";
		}
	}
}
