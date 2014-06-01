package powercraft.api.version;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import powercraft.api.PC_Logger;
import powercraft.api.xml.PC_XMLLoader;


public final class PC_UpdateChecker extends Thread {
	
	private static final String URL = "https://www.dropbox.com/s/h8s3y2iuwlg487j/Versions.xml?dl=1";
	private static final int TIMEOUT = 30000;
	
	private static PC_UpdateChecker running = null;
	
	private static PC_UpdateInfo updateInfo;
	
	public static synchronized void check(){
		if(running==null){
			running = new PC_UpdateChecker();
		}
	}
	
	public static PC_UpdateInfo getUpdateInfo(){
		return updateInfo;
	}
	
	private PC_UpdateChecker(){
		setDaemon(true);
		setName("PowerCraft Update Checker");
		start();
	}
	
	private static void onUpdateInfoDownloaded(String page) {
		PC_Logger.fine("Update information received from server.");
        updateInfo = PC_UpdateInfo.pharse(PC_XMLLoader.load(page));
	}
	
	@Override
    public void run(){
        try{
        	PC_Logger.info("Request version");
        	URL url = new URL(PC_UpdateChecker.URL);
            URLConnection urlC = url.openConnection();
            urlC.setReadTimeout(TIMEOUT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlC.getInputStream()));
            String page = "";
            String line;

            while ((line = reader.readLine()) != null){
                page += line + "\n";
            }

            reader.close();
            onUpdateInfoDownloaded(page);
        }catch (Exception e){
        	e.printStackTrace();
            PC_Logger.warning("Error while downloading update info");
        }
        running = null;
    }
	
}
