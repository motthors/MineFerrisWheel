package jp.mochisystems.mfw.storyboard.programpanel;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;
import jp.mochisystems.mfw.sound.SoundLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundPanel implements IProgramPanel {
	
	private static final String[] modes = {
			"Once",
			"Continue",
	};
	
	private static final DataPack[] dataPacks = {
			new DataPack(Type.change, "Mode"),
			new DataPack(Type.change, "Sound"),
	};
	private FerrisSelfMover part;
	
	private final int id_Mode = 0;
	private final int id_SoundIdx = 1;

	private int modeIndex = 0;
	private int soundIndex = 0;

	@Override
	public boolean CanUseWith(FerrisSelfMover part)
	{
		this.part = part;
		return true;
	}

	@Override
	public Mode getMode() {
		return Mode.sound;
	}
	
	@Override
	public int ApiNum(){ return 2; }

	@Override
	public Type getType(int apiIndex){
		return dataPacks[apiIndex].type;
	}
	
	@Override
	public String getDescription(int apiIndex) {
        return dataPacks[apiIndex].description;
	}
	

	@Override
	public String getValueString(int apiIndex){
		switch(apiIndex){
		case id_Mode : 
			return modes[modeIndex];
		case id_SoundIdx :
			return SoundLoader.Instance.sounds.get(soundIndex);
		default : return "";
		}
	}

	@Override
	public void addValue(int apiIndex, Number value) {

	}
	@Override
	public void setValue(int apiIndex, Number value) {
		switch(apiIndex){
		case id_Mode :
			modeIndex = value.intValue();
			break;
		case id_SoundIdx :
			soundIndex = value.intValue();
			break;
		}
	}

	@Override
	public void setSelection(int apiIndex, int value)
	{
		if(dataPacks[apiIndex].type != Type.change) return;
		switch(apiIndex) {
			case id_Mode:
				modeIndex = value;
				modeIndex = Math.Clamp(modeIndex, 0, modes.length - 1);
				break;
			case id_SoundIdx:
				soundIndex = value;
				soundIndex = Math.Clamp(soundIndex, 0, SoundLoader.Instance.sounds.size() - 1);
				break;
		}
	}

	@Override
	public String[] GetSelectionLabels(int apiIndex)
	{
		switch(apiIndex)
		{
			case id_Mode : return modes;
			case id_SoundIdx: return SoundLoader.Instance.sounds.toArray(new String[0]);
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
		if(soundIndex <= 0) {
			part.soundManager.Invalid();
			return true;
		}
		switch (modeIndex) {
		case 0: //once

            Vec3d p = part.connectorFromParent.Current();
			SoundEvent domain = SoundLoader.Instance.getSoundEvent(soundIndex);
			double x = p.x + part.controller.CorePosX();
			double y = p.y + part.controller.CorePosY();
			double z = p.z + part.controller.CorePosZ();
			part.controller.World().playSound(null, x, y, z, domain, SoundCategory.BLOCKS, 1f, 1f);
//			_Core.proxy.PlaySoundOnce(part.controller.World(), p.x+0.5, p.y+0.5, p.z+0.5, domain, 1.0F, 0.9F);
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
		setValue(id_Mode, Integer.parseInt(p[1]));
		setValue(id_SoundIdx, Integer.parseInt(p[2]));
	}
	
	@Override
	public String displayDescription()
	{
		switch (modeIndex) {
		case 0: //once
			return "FerrisSound "+"\" "+ SoundLoader.Instance.getSoundDomain(soundIndex)+" \"";
		case 1: // continue
			return "Frame.FerrisSound <= "+"\" "+ SoundLoader.Instance.getSoundDomain(soundIndex)+" \"";
		default:
			return "error";
		}
	}
}
