package mfw.storyboard.programpanel;

import java.util.List;

import mfw.ferriswheel.FerrisPartBase;

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

	public enum Type{
		change,
		inputValue,
		soundSelector,
	}

	public class DataPack{
		DataPack(Type t, String s){type=t; description=s;}
		Type type;
		String description;
	}


//	int index;
//	public void SetIndex(int idx){ index = idx;}
//	public int GetIndex(){ return index; }

	public abstract boolean CanUseWith(FerrisPartBase part); // return can use for arg type of FerrisPartBase
	public abstract int ApiNum();
	public abstract Mode getMode();
	public abstract Type getType(int apiIndex);
	public abstract String getDescription(int apiIndex);
//	public abstract void insertSubPanelToList(List<IProgramPanel> inout_panel);
	public abstract int[] Clicked(int apiIndex);
	public abstract String getValue(int apiIndex);
	public abstract int[] setValue(int apiIndex, Object value);
	public abstract void setSelection(int apiIndex, int value);
	public abstract String[] GetSelectionLabels(int apiIndex);
	public abstract void start();
	public abstract boolean CanDoNext();
	public abstract boolean run(); //retval : true=�I��
	public abstract void RSHandler();
	public abstract void NotifyHandler();
	public abstract void fromString(String source);
	public abstract String displayDescription();
}

