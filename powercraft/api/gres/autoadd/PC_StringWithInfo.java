package powercraft.api.gres.autoadd;

public class PC_StringWithInfo implements Comparable<PC_StringWithInfo>{

	private String string;
	private String info;
	
	public PC_StringWithInfo(String string, String info) {
		this.string = string;
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

	public String getInfo() {
		return info;
	}
	
}
