package jp.mochisystems.mfw.sound;

import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.mfw.ferriswheel.FerrisPartBase;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FerrisFrameSound extends MovingSound {
	private final FerrisSelfMover part;
	private final EntityPlayer player;
	private final Vec3d rootCorePos;
	private final int soundIdx;

	public FerrisFrameSound(SoundEvent event, FerrisSelfMover wheel, double rootCoreX, double rootCoreY, double rootCoreZ, EntityPlayer player, int soundIdx)
	{
		super(event, SoundCategory.AMBIENT); //todo
	    this.part = wheel;
	    this.player = player;
	    this.rootCorePos = new Vec3d(rootCoreX, rootCoreY, rootCoreZ);
	    this.repeat = true;
	    this.repeatDelay = 0;
	    this.volume = 0.01f;
		this.soundIdx = soundIdx;
	}

	public void update() 
	{
		if (part.soundManager.GetSoundIndex() != soundIdx)
		{
			this.donePlaying = true;
			return;
		}
		if (part.IsInvalid())
		{
			this.donePlaying = true;
			return;
		}

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
		}
		else
		{
			this.volume = 0.0F;
		}
	}
}