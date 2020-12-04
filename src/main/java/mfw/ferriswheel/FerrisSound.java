package mfw.ferriswheel;

import cpw.mods.fml.common.FMLCommonHandler;
import mfw._mc._1_7_10._core.MFW_Core;
import mfw.sound.FerrisFrameSound;
import mfw.sound.SoundManager;

public class FerrisSound {

    FerrisPartBase part;

    public FerrisSound(FerrisPartBase part)
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
                String domain = SoundManager.getSoundDomain(idx);
                if(idx >= SoundManager.sounds.size())soundidx = 0;
                FerrisFrameSound sound = new FerrisFrameSound(part,
                        part.controller.CorePosX(),
                        part.controller.CorePosY(),
                        part.controller.CorePosZ(),
                        MFW_Core.proxy.getClientPlayer(), SoundManager.soundDomain+":"+domain);
                this.sound = sound;
                mochisystems._mc._1_7_10._core._Core.proxy.PlayContinuousSound(sound);
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
