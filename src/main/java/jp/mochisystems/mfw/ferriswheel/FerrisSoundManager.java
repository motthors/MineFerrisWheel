package jp.mochisystems.mfw.ferriswheel;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw.sound.FerrisFrameSound;
import jp.mochisystems.mfw.sound.SoundLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class FerrisSoundManager {

    FerrisPartBase part;

    public FerrisSoundManager(FerrisPartBase part)
    {
        this.part = part;
    }

    private int soundidx = 0;

    public FerrisFrameSound sound;

    public void SetSoundIndex(int idx){
        if(soundidx != idx){
            soundidx = idx;
            if(sound!=null) Invalid();
            if(idx==0) return;
            if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            {
                String domain = SoundLoader.Instance.getSoundDomain(idx);
                if(idx >= SoundLoader.Instance.sounds.size())soundidx = 0;
                FerrisFrameSound sound = new FerrisFrameSound(part,
                        Math.floor(part.controller.CorePosX()),
                        Math.floor(part.controller.CorePosY()),
                        Math.floor(part.controller.CorePosZ()),
                        MFW.proxy.getClientPlayer(), SoundLoader.soundDomain+":"+domain);
                this.sound = sound;
                _Core.proxy.PlayContinuousSound(sound);
//				MFW_Logger.debugInfo("soundManager register "+domain);
            }
        }
    }
    public int GetSoundIndex(){ return soundidx; }

    public void Invalid()
    {
        if(sound != null) sound.Invalid();
    }
}
