package mfw.storyboard.programpanel;

import java.util.ArrayList;
import java.util.Arrays;

import mfw.ferriswheel.FerrisPartBase;
import mfw.ferriswheel.IFerrisParamGetter;
import net.minecraft.util.MathHelper;

public class KeyFramePanel implements IProgramPanel {
	
	private static String[] targets = {
			"Position",
			"Accel",
			//"Weight",
			"Size",
			"Amp",
			"Phase",
	};
	private static String[] modes = {
			"Set",
			"Add",
	};
	private static String[] interpolations = {
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
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "parallel"),
			new DataPack(Type.change, "Mode"),
			new DataPack(Type.change, "Target"),
			new DataPack(Type.inputValue, "To"),
			new DataPack(Type.inputValue, "Second"),
			new DataPack(Type.inputValue, "Tick"),
			new DataPack(Type.change, "Interpolation"),
	};
	private IFerrisParamGetter part;
	
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
	private float From;
	private float To;
	private float targetValue;
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
	public boolean CanUseWith(FerrisPartBase part)
	{
		if(!(part instanceof IFerrisParamGetter)) return false;
		this.part = (IFerrisParamGetter) part;
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
		return datapacks[apiIndex].type;
	}
	
	@Override
	public String getDescription(int apiIndex) {
		return datapacks[apiIndex].description;
	}
	
	@Override
	public int[] Clicked(int apiIndex)
	{
		switch(apiIndex)
		{
		case id_Parallel : isParallel = !isParallel; break;
		case id_Mode : modeIndex = (modeIndex + 1) % modes.length; break;
		case id_Target : targetIndex = ( targetIndex + 1 ) % targets.length; break;
		case id_Interpolation : interpolationIndex = ( interpolationIndex + 1 ) % interpolations.length; break;
		}
		
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		switch(apiIndex)
		{
		case id_Parallel : return isParallel?"Enable":"Disable";
		case id_Mode : return modes[modeIndex];
		case id_Target : return targets[targetIndex];
		case id_To : return String.format("%.4f",To);
		case id_Tick : return ""+tickTimeTarget;
		case id_Second : return String.format("%.2f",tickTimeTarget/20.0);
		case id_Interpolation : return interpolations[interpolationIndex];
		}
		return "";
	}
	
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			switch(apiIndex)
			{
			case id_Parallel : isParallel = "Enable".equals(value); break;
			case id_Mode : 
				modeIndex = Integer.parseInt((String) value); 
				modeIndex = MathHelper.clamp_int(modeIndex, 0, modes.length-1);
				break;
			case id_Target : 
				targetIndex = Integer.parseInt((String) value);
				targetIndex = MathHelper.clamp_int(targetIndex, 0, targets.length-1);
				break;
			case id_To : 
				To = Float.parseFloat((String)value);
				break;
			case id_Second : 
				tickTimeTarget = (int)((Double.parseDouble((String)value))*20); 
				if(tickTimeTarget < 1)tickTimeTarget = 1;
				return new int[]{id_Second,id_Tick};
			case id_Tick : 
				tickTimeTarget = (int)(Double.parseDouble((String)value)); 
				return new int[]{id_Tick,id_Second};
			case id_Interpolation : 
				interpolationIndex = Integer.parseInt((String) value); 
				interpolationIndex = MathHelper.clamp_int(interpolationIndex, 0, interpolations.length-1);
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
			case id_Target:
				targetIndex = value;
				targetIndex = MathHelper.clamp_int(targetIndex, 0, targets.length - 1);
				break;
			case id_Parallel:
				isParallel = value==1;
				break;
			case id_Interpolation:
				interpolationIndex = value;
				interpolationIndex = MathHelper.clamp_int(interpolationIndex, 0, interpolations.length - 1);
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
		From = getTargetValue();
		if(modeIndex == 0)/*set*/
			targetValue = To;
		else
			targetValue = To + From;
		canDispose = false;
		//frame calc
		KeyFrameValue.clear();
		float distance = targetValue - From;
		for(int i=0; i<=tickTimeTarget; ++i){
			float d = distance * calcInterpolation( (float)i / (float)tickTimeTarget);
			KeyFrameValue.add(d + From);
		}
//		MFW_Logger.debugInfo("****K_");
	}
	
	private boolean canDispose = false;
	@Override
	public boolean CanDoNext()
	{
		return canDispose || isParallel;
	}
	
	private ArrayList<Float> KeyFrameValue = new ArrayList<>();
	@Override
	public boolean run() {
//		Logger.debugInfo(""+targetIndex + " : " + tickTimeCount);
		if(tickTimeCount >= tickTimeTarget)
		{
			if(!canDispose) canDispose = true;
			return true;
		}
		tickTimeCount ++ ;
		if(KeyFrameValue.size() <= tickTimeCount) return true;
		float partial = KeyFrameValue.get(tickTimeCount);
//		float prevpart = KeyFrameValue.get(tickTimeCount - 1);
		SetTargetValue(partial);
//		MFW_Logger.debugInfo("K_"+tickTimeCount+"_"+tickTimeTarget+"_"+partial+"_"+From);
		return canDispose;
	}
	
	private float calcInterpolation(float base)
	{
		switch(interpolationIndex)
		{
		case 0 : //linear
			return base;
		case 1 : // in sine
			return (float) - Math.cos(base * Math.PI * 0.5) + 1.0f;
		case 2 : // out sine
			return (float) Math.sin(base * Math.PI * 0.5);
		case 3 : // in out sine
			return (float) -Math.cos(base * Math.PI) * 0.5f + 0.5f;
		case 4 : // in bounce
			return (float) (Math.abs(Math.sin(Math.pow(base, 0.5)*3.5*Math.PI))*base);
		case 5 : // out bounce
			base = 1-base;
			return (float) (1f-(Math.abs(Math.sin(Math.pow(base, 0.5)*3.5*Math.PI))*base));
		case 6 : // in back
			return (float)( -(Math.cos(1.2*Math.PI*base)-1f)*(-0.7+1.25*base));
		case 7 : // out back
			base -= 1f;
			return (float)( 1+(Math.cos(1.2*Math.PI*base)-1f)*(-0.7-1.25*base));
		case 8 : // in spring
			return (float)(Math.sin(4.5*Math.PI*base)*base*base);
		case 9 : // out spring
			return (float)( 1 - Math.sin(4.5*Math.PI*(base-1f))*(Math.pow(base, 0.4)-1));
		}
		return base;
	}
	
	private float getTargetValue()
	{
		switch(targetIndex)
		{
		case 0 : return part.Position().get(); //"Rotation",
		case 1 : return part.GetAccel(); //"Accel",
		case 2 : return part.GetLocalScale(); //"Size",
		case 3 : return part.Amp().get(); //"Amp",
		case 4 : return part.Phase().get(); //"Phase",
		}
		return 0;
	}
	
	private void SetTargetValue(float Value)
	{
		switch(targetIndex)
		{
		case 0/*Rotate*/ : part.Position().set(Value); break;
		case 1/*Accel */ : part.SetAccel(Value); break;
		case 2/*Size  */ : part.SetLocalScale(Value); break;
		case 3/*Amp   */ : part.Amp().set(Value); break;
		case 4/*Phase */ : part.Phase().set(Value); break;
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
		setValue(id_Parallel, (Character.isUpperCase(p[0].charAt(0))?"Disable":"Enable"));
		setValue(id_Mode, p[1]);
		setValue(id_Target, p[2]);
		setValue(id_To, p[3]);
		setValue(id_Tick, p[4]);
		setValue(id_Interpolation, p[5]);
	}
	
	@Override
	public String displayDescription()
	{
		return targets[targetIndex]
				+ (modeIndex==0 ? (" set " + To) : (" add "+(To<0?"":"+")+To))
				+ " in "+tickTimeTarget+" tick";
	}
}
