package powercraft.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class PC_Extractor {

	public static void extract(File source) {
		if(source.isFile()){
			try {
				ZipFile zip = new ZipFile(source);
				extractZip(zip);
				zip.close();
			} catch (Exception e) {
				PC_Logger.severe("I am not a jar nor zip");
			} 
			
		}else{
			extractDir(source);
		}
	}
	
	private static void extractZip(ZipFile zip) {
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while(entries.hasMoreElements()){
			ZipEntry entry = entries.nextElement();
			String name = entry.getName();
			if(name.startsWith("def\\") || name.startsWith("def/")){
				name = name.substring(4);
				File file = PC_Utils.getPowerCraftFile(null, name);
				file.getParentFile().mkdirs();
				try{
					InputStream fis = zip.getInputStream(entry);
					FileOutputStream fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int length;
					while((length=fis.read(buffer))!=-1){
						fos.write(buffer, 0, length);
					}
					fis.close();
					fos.close();
				}catch(Exception e){
					PC_Logger.severe("Error while copy def file");
				}
			}
		}
	}
	
	private static void extractDir(File source) {
		File def = new File(source, "def");
		copy(def, PC_Utils.getPowerCraftFile(null, "tmp").getParentFile());
	}
	
	public static boolean copy(File file, File to){
		if(file.isDirectory()){
			return copyDirectory(file, to);
		}else if(file.isFile()){
			return copyFile(file, to);
		}
		return false;
	}
	
	private static boolean copyDirectory(File file, File to){
		File[] files = file.listFiles();
		to.mkdirs();
		boolean ok=true;
		for(int i=0; i<files.length; i++){
			ok &= copy(files[i], new File(to, files[i].getName()));
		}
		return ok;
	}
	
	private static boolean copyFile(File file, File to){
		try {
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(to);
			byte[] buffer = new byte[1024];
			int length;
			while((length=fis.read(buffer))!=-1){
				fos.write(buffer, 0, length);
			}
			fis.close();
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
