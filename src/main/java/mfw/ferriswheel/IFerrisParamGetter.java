package mfw.ferriswheel;

import mochisystems.blockcopier.MovingModel;
import mochisystems.math.Vec3d;
import mochisystems.util.InterpolationTick;

public interface IFerrisParamGetter extends MovingModel {
    InterpolationTick Scale();
    InterpolationTick Amp();
    InterpolationTick Phase();
    float GetLocalScale();
    void SetLocalScale(float value);
    float GetRegist();
    void SetResist(float value);
    float GetYaw();
    void SetYaw(float value);
    float GetPitch();
    void SetPitch(float value);
    Vec3d GetOffset();
    void SetOffset(Vec3d source);
}
