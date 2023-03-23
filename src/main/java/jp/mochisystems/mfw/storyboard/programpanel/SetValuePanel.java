package jp.mochisystems.mfw.storyboard.programpanel;

import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;

import java.util.Arrays;

public class SetValuePanel implements IProgramPanel {
	
	private static final String[] targets = {
			"Position",
			"Accel",
			"Weight",
			"Size",
			"Amp",
			"Phase",
			"Size_x",
			"Size_y",
			"Size_z",
	};
	private static final String[] modes = {
			"To",
			"Add",
	};
	
	private static final DataPack[] dataPacks = {
			new DataPack(Type.change, "Mode"),
			new DataPack(Type.change, "Target"),
			new DataPack(Type.inputValue, "Value"),
	};
	
	private final int id_Mode = 0;
	private final int id_Target = 1;
	private final int id_Value = 2;
	
	private FerrisSelfMover part;
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
	public boolean CanUseWith(FerrisSelfMover part)
	{
		this.part = part;
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
		case id_Mode : return modes[modeIndex];
		case id_Target : return targets[targetIndex];
		case id_Value : return String.format("%.2f",Value);
		default : return "";
		}
	}

	@Override
	public void addValue(int apiIndex, Number value) {
		if(apiIndex == id_Value) {
			Value = ClampWithTarget(Value+value.intValue()*0.05f, 0);
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
		case id_Value :
			Value = ClampWithTarget(value.floatValue(), 0);
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
		case 2/*Resist*/ : return Math.Clamp(value, 0.001f, 0.99f);
		case 3/*Size  */ : return (value < 0) ? 0f : value;
		case 4/*Amp   */ : return value;
		case 5/*Phase */ : return Math.Clamp(value, -180f, 180f);
		case 6/*Size x*/ : return value;
		case 7/*Size y*/ : return value;
		case 8/*Size z*/ : return value;
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
			case 3/*Size  */ : part.GetLocalScale().SetFrom(Value, Value, Value); break;
			case 4/*Amp   */ : part.Amp().Init(Value); break;
			case 5/*Phase */ : part.Phase().Init(Value); break;
			case 6/*Size x*/ : part.GetLocalScale().x = Value; break;
			case 7/*Size y*/ : part.GetLocalScale().y = Value; break;
			case 8/*Size z*/ : part.GetLocalScale().z = Value; break;
			}
		}
		else // add
		{
			switch(targetIndex)
			{
			case 0/*Rotate*/ : part.Position().addAll(Value); /*part.speedTemp+=Value;*/ break;
			case 1/*Accel */ : part.SetAccel(part.GetAccel() + Value); break;
			case 2/*Resist*/ : part.SetResist(part.GetResist() + 1f/Value); break;
			case 3/*Size  */ : part.GetLocalScale().add(Value); break;
			case 4/*Amp   */ : part.Amp().addAll(Value); break;
			case 5/*Phase */ : part.Phase().addAll(Value); break;
			case 6/*Size x*/ : part.GetLocalScale().x += Value; break;
			case 7/*Size y*/ : part.GetLocalScale().y += Value; break;
			case 8/*Size z*/ : part.GetLocalScale().z += Value; break;
			}
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
		setSelection(id_Mode, Integer.parseInt(p[1]));
		setSelection(id_Target, Integer.parseInt(p[2]));
		setValue(id_Value, Float.parseFloat(p[3]));
	}
	
	@Override
	public String displayDescription()
	{
		return targets[targetIndex]
				+ (modeIndex ==1 ? " += " : " << ")
				+ Value;
	}
}
