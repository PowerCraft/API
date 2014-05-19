package powercraft.api.version;


public class PC_VersionInfo implements Comparable<PC_VersionInfo>{

	private PC_Version version;
	
	private String download;
	
	public PC_VersionInfo(PC_Version version, String download) {
		this.version = version;
		this.download = download;
	}

	@Override
	public int compareTo(PC_VersionInfo o) {
		return this.version.compareTo(o.version);
	}
	
	public PC_Version getVersion(){
		return this.version;
	}
	
	public String getDownloadLink(){
		return this.download;
	}
	
	public boolean isPreRelease(){
		return this.version.isPreRelease();
	}
	
}