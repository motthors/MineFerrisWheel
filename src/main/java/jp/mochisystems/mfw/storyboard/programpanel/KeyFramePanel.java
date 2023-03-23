package jp.mochisystems.mfw.storyboard.programpanel;

import java.util.Arrays;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;

import javax.annotation.Nonnull;

import static java.lang.Math.PI;
import static java.lang.Math.*;

public class KeyFramePanel implements IProgramPanel {
	
	private static final String[] targets = {
			"Position",
			"Accel",
			//"Weight",
			"Size",
			"Amp",
			"Phase",
			"Size_x",
			"Size_y",
			"Size_z",
	};
	private static final String[] modes = {
			"Set",
			"Add",
	};
	private static final String[] interpolations = {
			"Linear",
			"InSine",
		    "OutSine",
		    "InOutSine",
		    "InBounce",
		    "OutBounce",
		    "InBack",
		    "OutBack",
		    "InSpring",
		    "OutSpring",
	};
	
	private static final DataPack[] dataPacks = {
			new DataPack(Type.change, "parallel"),
			new DataPack(Type.change, "Mode"),
			new DataPack(Type.change, "Target"),
			new DataPack(Type.inputValue, "To"),
			new DataPack(Type.inputValue, "Second"),
			new DataPack(Type.inputValue, "Tick"),
			new DataPack(Type.change, "Interpolation"),
	};
	private FerrisSelfMover part;
	
	private final int id_Parallel = 0;
	private final int id_Mode = 1;
	private final int id_Target = 2;
	private final int id_To = 3;
	private final int id_Second = 4;
	private final int id_Tick = 5;
	private final int id_Interpolation = 6;
	
	private boolean isParallel;
	private int modeIndex = 0;
	private int targetIndex = 0;
	private int interpolationIndex = 0;
	private float To;
	private int tickTimeCount;
	private int tickTimeTarget;

	public KeyFramePanel(){}

	public KeyFramePanel(String target, String mode, String interpo, float to, int tick, boolean parallel) {
		targetIndex = Arrays.asList(targets).indexOf(target);
		modeIndex = Arrays.asList(modes).indexOf(mode);
		interpolationIndex = Arrays.asList(interpolations).indexOf(interpo);
		if(targetIndex < 0 || modeIndex < 0 || interpolationIndex < 0) throw new IllegalArgumentException("KeyFramePanel has no argument param.");
		To = to;
		tickTimeTarget = tick;
		isParallel = parallel;
	}

	@Override
	public boolean CanUseWith(@Nonnull FerrisSelfMover part)
	{
		this.part = part;
		return true;
	}

	@Override
	public Mode getMode() {
		return Mode.keyframe;
	}
	
	@Override
	public int ApiNum(){ return 7; }

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
		switch(apiIndex)
		{
		case id_Parallel : return isParallel?"Enable":"Disable";
		case id_Mode : return modes[modeIndex];
		case id_Target : return targets[targetIndex];
		case id_To : return String.format("%.3f",To);
		case id_Tick : return ""+tickTimeTarget;
		case id_Second : return String.format("%.2f",tickTimeTarget/20.0);
		case id_Interpolation : return interpolations[interpolationIndex];
		}
		return "";
	}

	@Override
	public void addValue(int apiIndex, Number value) {
		switch(apiIndex)
		{
			case id_To:
				To += value.intValue() * 0.05f;
				break;
			case id_Second :
			case id_Tick :
				tickTimeTarget += value.intValue();
				if(tickTimeTarget < 1)tickTimeTarget = 1;
				break;
		}
	}
	@Override
	public void setValue(int apiIndex, Number value) {
		switch(apiIndex)
		{
		case id_Mode :
			modeIndex = value.intValue();
			modeIndex = Math.Clamp(modeIndex, 0, modes.length-1);
			break;
		case id_Target :
			targetIndex = value.intValue();
			targetIndex = Math.Clamp(targetIndex, 0, targets.length-1);
			break;
		case id_To :
			To = value.floatValue();
			break;
		case id_Second :
			tickTimeTarget = (int)(value.doubleValue()*20);
			if(tickTimeTarget < 1)tickTimeTarget = 1;
			break;
		case id_Tick :
			tickTimeTarget = value.intValue();
			break;
		case id_Interpolation :
			interpolationIndex = value.intValue();
			interpolationIndex = Math.Clamp(interpolationIndex, 0, interpolations.length-1);
			break;
		default:
			Logger.error("yaba");
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
			case id_Target:
				targetIndex = value;
				targetIndex = Math.Clamp(targetIndex, 0, targets.length - 1);
				break;
			case id_Parallel:
				isParallel = value==1;
				break;
			case id_Interpolation:
				interpolationIndex = value;
				interpolationIndex = Math.Clamp(interpolationIndex, 0, interpolations.length - 1);
				break;
			default:
				Logger.error("yaba");
				break;
		}
	}

	@Override
	public String[] GetSelectionLabels(int apiIndex)
	{
		switch(apiIndex)
		{
			case id_Mode : return modes;
			case id_Target: return targets;
			case id_Parallel: return new String[]{"Disable","Enable"};
			case id_Interpolation: return interpolations;
			default: return null;
		}
	}

	@Override
	public void start()
	{
		tickTimeCount = 0;
		float from = getTargetValue();
		float targetValue;
		if(modeIndex == 0)/*set*/
			targetValue = To;
		else
			targetValue = To + from;
		isFinished = false;
		//frame calc
		frameDiffs = new float[tickTimeTarget+1];
		float distance = targetValue - from;
		for(int i=0; i<=tickTimeTarget; ++i){
			float t = calcInterpolation( i / (float)tickTimeTarget);
			float t0 = i==0 ? 0 : calcInterpolation( (i-1) / (float)tickTimeTarget);
			frameDiffs[i] = distance * (t - t0);
		}
	}
	
	private boolean isFinished = false;
	@Override
	public boolean CanDoNext()
	{
		return isFinished || isParallel;
	}
	
	private float[] frameDiffs;
	@Override
	public boolean run() {
		if(tickTimeCount >= tickTimeTarget)
		{
			isFinished = true;
			return true;
		}
		tickTimeCount ++ ;
		if(frameDiffs.length <= tickTimeCount) return true;
		float diff = frameDiffs[tickTimeCount];
		AddTargetValue(diff);
		return isFinished;
	}
	
	private float calcInterpolation(float base)
	{
		switch(interpolationIndex)
		{
		case 0 : //linear
			return base;
		case 1 : // in sine
			return (float) - cos(base * PI * 0.5) + 1.0f;
		case 2 : // out sine
			return (float) sin(base * PI * 0.5);
		case 3 : // in out sine
			return (float) -cos(base * PI) * 0.5f + 0.5f;
		case 4 : // in bounce
			return (float) (abs(sin(pow(base, 0.5)*3.5*PI))*base);
		case 5 : // out bounce
			base = 1-base;
			return (float) (1f-(abs(sin(pow(base, 0.5)*3.5*PI))*base));
		case 6 : // in back
			return (float)( -(cos(1.2*PI*base)-1f)*(-0.7+1.25*base));
		case 7 : // out back
			base -= 1f;
			return (float)( 1+(cos(1.2*PI*base)-1f)*(-0.7-1.25*base));
		case 8 : // in spring
			return (float)(sin(4.5*PI*base)*base*base);
		case 9 : // out spring
			return (float)( 1 - sin(4.5*PI*(base-1f))*(pow(base, 0.4)-1));
		}
		return base;
	}
	
	private float getTargetValue()
	{
		switch(targetIndex)
		{
		case 0 : return part.Position().get(); //"Rotation",
		case 1 : return part.GetAccel(); //"Accel",
		case 2 : return (float) part.GetLocalScale().x; //"Size",
		case 3 : return part.Amp().get(); //"Amp",
		case 4 : return part.Phase().get(); //"Phase",
		case 5 : return (float)part.GetLocalScale().x;
		case 6 : return (float)part.GetLocalScale().y;
		case 7 : return (float)part.GetLocalScale().z;
		}
		return 0;
	}
	
	private void AddTargetValue(float diff)
	{
		switch(targetIndex)
		{
		case 0/*Rotate*/ : part.Position().add(diff); break;
		case 1/*Accel */ : part.SetAccel(diff + part.GetAccel()); break;
		case 2/*Size  */ : part.GetLocalScale().add(diff); break;
		case 3/*Amp   */ : part.Amp().add(diff); break;
		case 4/*Phase */ : part.Phase().add(diff); break;
		case 5/*Size x*/ : part.GetLocalScale().x += diff; break;
		case 6/*Size y*/ : part.GetLocalScale().y += diff; break;
		case 7/*Size z*/ : part.GetLocalScale().z += diff; break;
		}
	}
	
	@Override
	public void RSHandler(){}
	@Override
	public void NotifyHandler(){}

	@Override
	public String toString()
	{
		return (isParallel?"k":"K") +"x"
				+modeIndex +"x"
				+targetIndex +"x"
				+To +"x"
				+tickTimeTarget +"x"
				+interpolationIndex+";";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setSelection(id_Parallel, (Character.isUpperCase(p[0].charAt(0))?0:1));
		setSelection(id_Mode, Integer.parseInt(p[1]));
		setSelection(id_Target, Integer.parseInt(p[2]));
		setValue(id_To, Float.parseFloat(p[3]));
		setValue(id_Tick, Integer.parseInt(p[4]));
		setSelection(id_Interpolation, Integer.parseInt(p[5]));
	}
	
	@Override
	public String displayDescription()
	{
		return targets[targetIndex]
				+ (modeIndex==0 ? (" set " + To) : (" add "+(To<0?"":"+")+To))
				+ " in "+tickTimeTarget+" tick";
	}
}
