package jp.mochisystems.mfw.ferriswheel;

import jp.mochisystems.mfw._mc._core.MFW;
import jp.mochisystems.mfw.sound.SoundLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class FerrisSoundManager {

    FerrisSelfMover part;

    public FerrisSoundManager(FerrisSelfMover part)
    {
        this.part = part;
    }

    private int soundIdx = 0;

    public void SetSoundIndex(int idx){
        if(soundIdx != idx){
            soundIdx = idx;
            if(idx==0) return;
            if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            {
//                String domain = SoundLoader.Instance.getSoundDomain(idx);
                if(idx >= SoundLoader.Instance.sounds.size()) soundIdx = 1;
                MFW.proxy.PlayMFWSound(part, idx);
            }
        }
    }
    public int GetSoundIndex(){ return soundIdx; }
    public void Invalid(){ soundIdx = -1; }
}
