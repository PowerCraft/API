package powercraft.api.version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import powercraft.api.xml.PC_XMLNode;
import powercraft.api.xml.PC_XMLProperty;
import cpw.mods.fml.common.Loader;


public class PC_UpdateInfo {

	public static PC_UpdateInfo pharse(PC_XMLNode load) {
		if(!load.getName().equals("Versions")){
			throw new IllegalArgumentException();
		}
		HashMap<String, List<PC_VersionInfo>> modules = new HashMap<String, List<PC_VersionInfo>>();
		PC_Version mcv;
		try{
			mcv = PC_Version.pharse(Loader.instance().getMCVersionString().substring(10));
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
		for(int i=0; i<load.getChildCount(); i++){
			PC_XMLNode child = load.getChild(i);
			if(!child.getName().equals("Minecraft")){
				continue;
			}
			PC_XMLProperty p = child.getProperty("mcv");
			if(p==null)
				continue;
			PC_Version mcvf;
			try{
				mcvf = PC_Version.pharse(p.getValue());
			}catch(Exception e){
				continue;
			}
			if(mcv.compareTo(mcvf)!=0){
				continue;
			}
			for(int k=0; k<child.getChildCount(); k++){
				PC_XMLNode c = child.getChild(i);
				if(!c.getName().equals("Module")){
					continue;
				}
				PC_XMLProperty property = c.getProperty("name");
				if(property==null){
					continue;
				}
				String name = property.getValue();
				if(modules.containsKey(name)){
					throw new IllegalArgumentException();
				}
				List<PC_VersionInfo> versions = new ArrayList<PC_VersionInfo>();
				modules.put(name, versions);
				for(int j=0; j<c.getChildCount(); j++){
					PC_XMLNode v = c.getChild(j);
					if(!v.getName().equals("Version")){
						continue;
					}
					PC_XMLProperty propertyVersion = v.getProperty("version");
					PC_XMLProperty propertyDownload = v.getProperty("download");
					if(propertyVersion==null){
						continue;
					}
					PC_Version version;
					try{
						version = PC_Version.pharse(propertyVersion.getValue());
					}catch(Exception e){
						e.printStackTrace();
						continue;
					}
					if(propertyDownload==null){
						versions.add(new PC_VersionInfo(version, ""));
					}else{
						versions.add(new PC_VersionInfo(version, propertyDownload.getValue()));
					}
				}
			}
		}
		return new PC_UpdateInfo(modules);
	}
	
	private HashMap<String, List<PC_VersionInfo>> modules;
	
	private PC_UpdateInfo(HashMap<String, List<PC_VersionInfo>> modules) {
		this.modules = modules;
	}
	
	public PC_VersionInfo getNewestVersion(String module, boolean preReleases){
		List<PC_VersionInfo> infos = this.modules.get(module);
		if(infos==null){
			return null;
		}
		PC_VersionInfo newest = null;
		for(PC_VersionInfo info:infos){
			if(preReleases || !info.isPreRelease()){
				if(newest==null){
					newest = info;
				}else if(newest.compareTo(info)<0){
					newest = info;
				}
			}
		}
		return newest;
	}
	
}
