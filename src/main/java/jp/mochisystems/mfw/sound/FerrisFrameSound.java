package jp.mochisystems.mfw.sound;

import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FerrisFrameSound extends MovingSound {
	private final FerrisPartBase part;
	private final EntityPlayer player;
	private final Vec3d rootCorePos;

	public FerrisFrameSound(FerrisPartBase wheel, double rootCoreX, double rootCoreY, double rootCoreZ, EntityPlayer player, String domain)
	{
		super(SoundEvents.WEATHER_RAIN, SoundCategory.AMBIENT); //todo
	    this.part = wheel;
	    this.player = player;
	    this.rootCorePos = new Vec3d(rootCoreX, rootCoreY, rootCoreZ);
	    this.repeat = true;
	    this.repeatDelay = 0;
	    this.volume = 0.01f;
	}

	public void update() 
	{
		if (!part.IsInvalid())
		{
			Vec3d corePos = part.connectorFromParent.Current();
			float distance = 0.05f * (float) player.getDistanceSq(
					corePos.x + rootCorePos.x,
					corePos.y + rootCorePos.y,
					corePos.z + rootCorePos.z);
			float v = java.lang.Math.abs(part.GetSoundSourceValue()) * 0.1f;
			float f = v/ (distance + v);
			if (f >= 0.00001D) 
			{
				this.volume = (Math.Clamp(f, 0.0F, 1.0F));
//				this.field_147663_c = 0.86f + java.lang.Math.min(v*0.1f, 5.5f);
//				MFW_Logger.debugInfo(field_147663_c+" : "+ v);
			} 
			else 
			{
				this.volume = 0.0F;
			}
		} 
		else
		{
			this.donePlaying = true;
			//MFW_Logger.debugInfo("soundManager invalid 1");
		}
	}
	
	public void Invalid()
	{
		this.donePlaying = true;
		//MFW_Logger.debugInfo("soundManager invalid 2");
	}
}