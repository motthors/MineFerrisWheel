package mfw.sound;

import mfw.ferriswheel.FerrisPartBase;
import mochisystems.math.Vec3d;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class FerrisFrameSound extends MovingSound {
	private final FerrisPartBase part;
	private final EntityPlayer player;
	private Vec3d rootCorePos;

	public FerrisFrameSound(FerrisPartBase wheel, int rootCoreX, int rootCoreY, int rootCoreZ, EntityPlayer player, String domain)
	{
	    super(new ResourceLocation(domain));
	    this.part = wheel;
	    this.player = player;
	    this.rootCorePos = new Vec3d(rootCoreX, rootCoreY, rootCoreZ);
	    this.field_147666_i = ISound.AttenuationType.NONE;
	    this.repeat = true;
	    this.field_147665_h = 0;
	    this.field_147663_c = 10.7f;
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
			float v = Math.abs(part.GetSoundSourceValue()) * 0.1f;
			float f = v/ (distance + v);
			if (f >= 0.00001D) 
			{
				this.volume = (MathHelper.clamp_float(f, 0.0F, 1.0F));
				this.field_147663_c = 0.86f + Math.min(v*0.1f, 5.5f);
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