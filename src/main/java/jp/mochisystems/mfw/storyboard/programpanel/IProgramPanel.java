package jp.mochisystems.mfw.storyboard.programpanel;


import jp.mochisystems.mfw.ferriswheel.FerrisSelfMover;

public interface IProgramPanel {

	public enum Mode{
		set(1),		// 値即反映
		timer(2),		// 指定時間待機
		keyframe(3),	// 指定時間内補間動作
		loop(4),		// 指定回数繰り返し
		wait(5),		// 通知まで待機
		notify(6),		// 通知発行
		sound(7),		// 発音
		loopend(8),
		;
		int idx;
		Mode(int idx){this.idx = idx;}
		public static Mode getType(final String src) {
			Mode[] Modes = Mode.values();
	        for (Mode m : Modes) {if (m.toString() == src) { return m;}}
	        return null;
	    }
	    public int GetIdx(){return idx;}
	}

	enum Type{
		change,
		inputValue,
//		soundSelector,
	}

	public class DataPack{
		DataPack(Type t, String s){type=t; description=s;}
		Type type;
		String description;
	}


//	int index;
//	public void SetIndex(int idx){ index = idx;}
//	public int GetIndex(){ return index; }

	boolean CanUseWith(FerrisSelfMover part); // return can use for arg type of FerrisPartBase
	int ApiNum();
	Mode getMode();
	Type getType(int apiIndex);
	String getDescription(int apiIndex);
	String getValueString(int apiIndex);
	void addValue(int apiIndex, Number value);
	void setValue(int apiIndex, Number value);
	void setSelection(int apiIndex, int value);
	String[] GetSelectionLabels(int apiIndex);
	void start();
	boolean CanDoNext();
	boolean run();
	void RSHandler();
	void NotifyHandler();
	void fromString(String source);
	String displayDescription();
}

