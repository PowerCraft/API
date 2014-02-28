package powercraft.api.gres.autoadd;

public class PC_StringWithInfo implements Comparable<PC_StringWithInfo>{

	private String string;
	private String tooltip;
	private String info;
	
	public PC_StringWithInfo(String string, String tooltip) {
		this.string = string;
		this.tooltip = tooltip;
	}
	
	public PC_StringWithInfo(String string, String tooltip, String info) {
		this.string = string;
		this.tooltip = tooltip;
		this.info = info;
	}

	@Override
	public int compareTo(PC_StringWithInfo o) {
		return string.compareTo(o.string);
	}

	public boolean startsWith(String prefix) {
		return string.startsWith(prefix);
	}

	public String getString() {
		return string;
	}

	public String getTooltip() {
		return tooltip;
	}
	
	public String getInfo() {
		return info;
	}
	
}
