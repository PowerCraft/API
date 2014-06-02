package powercraft.api.version;

import powercraft.api.xml.PC_Pharser;


public class PC_Version implements Comparable<PC_Version> {
	
	private int[] version;
	
	private String type;
	
	public PC_Version(String type, int...version){
		this.version = version;
		if(this.version.length==0)
			throw new IllegalArgumentException("No version");
		for(int i=0; i<this.version.length; i++){
			if(this.version[i]<0)
				throw new IllegalArgumentException("Negative version");
		}
		this.type = type;
	}
	
	public PC_Version(int...version){
		this(null, version);
	}

	@Override
	public int compareTo(PC_Version o) {
		int c = this.version.length>o.version.length?this.version.length:o.version.length;
		for(int i=0; i<c; i++){
			int v1 = this.version.length>i?this.version[i]:0;
			int v2 = o.version.length>i?o.version[i]:0;
			if(v1>v2){
				return i+1;
			}else if(v1<v2){
				return -i-1;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		String sVersion = PC_Pharser.toString(this.version, ".");
		if(this.type==null)
			return sVersion;
		return sVersion+this.type;
	}
	
	public String getType(){
		return this.type;
	}
	
	public int getNumCount(){
		return this.version.length;
	}
	
	public int getVerstion(int i){
		return this.version[i];
	}
	
	private static final String[] prereleases = {"testbuild", "snapshot", "alpha", "a", "beta", "b"};
	
	public boolean isPreRelease(){
		if(this.type!=null){
			String t = this.type.toLowerCase();
			for(String prerelease:prereleases){
				if(prerelease.equals(t)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	public static PC_Version pharse(String s){
		String[] nums = s.split("\\.");
		int[] version = new int[nums.length];
		for(int i=0; i<version.length-1; i++){
			version[i] = Integer.parseInt(nums[i].trim());
		}
		int i = version.length-1;
		String n = nums[i].trim();
		int k=-1;
		for(int j=0; j<n.length(); j++){
			if(!Character.isDigit(n.charAt(j))){
				k = j;
				break;
			}
		}
		if(k==-1){
			version[i] = Integer.parseInt(n);
			return new PC_Version(version);
		}
		version[i] = Integer.parseInt(n.substring(0, k));
		return new PC_Version(n.substring(k).trim(), version);
	}
	
}
