package mfw.storyboard.programpanel;

import mfw.ferriswheel.FerrisPartBase;
import mfw.ferriswheel.IFerrisParamGetter;
import net.minecraft.util.MathHelper;

import java.util.Arrays;

public class SetValuePanel implements IProgramPanel {
	
	private static String[] targets = {
			"Position",
			"Accel",
			"Weight",
			"Size",
			"Amp",
			"Phase",
	};
	private static String[] modes = {
			"To",
			"Add",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Mode"),
			new DataPack(Type.change, "Target"),
			new DataPack(Type.inputValue, "Value"),
	};
	
	private final int id_Mode = 0;
	private final int id_Target = 1;
	private final int id_Value = 2;
	
	private IFerrisParamGetter part;
	private int modeIndex = 0;
	private int targetIndex = 0;
	private float Value = 0.0f;

	public SetValuePanel(){}

	public SetValuePanel(String target, String mode, float to) {
		targetIndex = Arrays.asList(targets).indexOf(target);
		modeIndex = Arrays.asList(modes).indexOf(mode);
		if(targetIndex < 0 || modeIndex < 0) throw new IllegalArgumentException("KeyFramePanel has no argument param.");
		Value = to;
	}

	@Override
	public boolean CanUseWith(FerrisPartBase part)
	{
		if(!(part instanceof IFerrisParamGetter)) return false;
		this.part = (IFerrisParamGetter)part;
		return true;
	}

	@Override
	public Mode getMode() {
		return Mode.set;
	}
	
	@Override
	public int ApiNum(){ return 3; }

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
		switch(apiIndex){
		case id_Mode :
			modeIndex = (modeIndex + 1) % modes.length;
			break;
		case id_Target :
			targetIndex = ( targetIndex + 1 ) % targets.length;
			break;
		}
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		switch(apiIndex)
		{
		case id_Mode : return modes[modeIndex];
		case id_Target : return targets[targetIndex];
		case id_Value : return String.format("%.2f",Value);
		default : return "";
		}
	}

	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			switch(apiIndex)
			{
			case id_Mode : 
				modeIndex = Integer.parseInt((String) value);
				modeIndex = MathHelper.clamp_int(modeIndex, 0, modes.length-1);
				break;
			case id_Target : 
				targetIndex = Integer.parseInt((String) value);
				targetIndex = MathHelper.clamp_int(targetIndex, 0, targets.length-1);
				break;
			case id_Value : 
				Value = ClampWithTarget(Float.parseFloat((String) value), 0);
				break;
			default : return new int []{};
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
		}
	}

	@Override
	public String[] GetSelectionLabels(int apiIndex)
	{
		switch(apiIndex)
		{
			case id_Mode : return modes;
			case id_Target: return targets;
			default: return null;
		}
	}


	private float ClampWithTarget(float value, int targetindex)
	{
		switch(targetindex)
		{
		case 0/*Rotate*/ : return value;
		case 1/*Accel */ : return value;
		case 2/*Resist*/ : return MathHelper.clamp_float(value, 0.001f, 0.99f);
		case 3/*Size  */ : return (value < 0) ? 0f : value;
		case 4/*Amp   */ : return value;
		case 5/*Phase */ : return MathHelper.clamp_float(value, -180f, 180f);
		}
		return 1;
	}
	
	@Override
	public void start()
	{
		//MFW_Logger.debugInfo("***S_"+modeIndex+"_"+Value);
		if(modeIndex == 0) //set
		{
			switch(targetIndex)
			{
			case 0/*Rotate*/ : part.Position().Init(Value); /*part.speedTemp=Value;*/ break;
			case 1/*Accel */ : part.SetAccel(Value); break;
			case 2/*Resist*/ : part.SetResist(1f/Value); break;
			case 3/*Size  */ : part.SetLocalScale(Value); break;
			case 4/*Amp   */ : part.Amp().Init(Value); break;
			case 5/*Phase */ : part.Phase().Init(Value); break;
			}
			return;
		}
		else // add
		{
			switch(targetIndex)
			{
			case 0/*Rotate*/ : part.Position().addAll(Value); /*part.speedTemp+=Value;*/ break;
			case 1/*Accel */ : part.SetAccel(part.GetAccel() + Value); break;
			case 2/*Resist*/ : part.SetResist(part.GetRegist() + 1f/Value); break;
			case 3/*Size  */ : part.SetLocalScale(part.GetLocalScale() + Value); break;
			case 4/*Amp   */ : part.Amp().addAll(Value); break;
			case 5/*Phase */ : part.Phase().addAll(Value); break;
			}
			return;
		}
	}
	
	@Override
	public boolean CanDoNext()
	{
		return true;
	}
	
	@Override
	public boolean run() {
		//MFW_Logger.debugInfo("S_"+modeIndex+"_"+Value);
		return true;
	}

	@Override
	public void RSHandler(){}
	@Override
	public void NotifyHandler(){}
	
	@Override
	public String toString()
	{
		return "Sx"+ modeIndex +"x"+ targetIndex +"x"+Value+";";
	}
	
	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Mode, p[1]);
		setValue(id_Target, p[2]);
		setValue(id_Value, p[3]);
	}
	
	@Override
	public String displayDescription()
	{
		return targets[targetIndex]
				+ (modeIndex ==1 ? " += " : " << ")
				+ Value;
	}
}
