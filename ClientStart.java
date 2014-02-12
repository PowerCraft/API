import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;

/**
 * 
 * Run this in case that Minecraft is another project
 * 
 * @author XOR
 *
 */
public class ClientStart {

	/**
	 * 
	 * Start the client
	 * 
	 * @param args only allowed --username yourName
	 */
	public static void main(String args[]){
		String userName = System.getProperty("user.name", "PowerCraftPlayer"+(Minecraft.getSystemTime()%1000));
		for(int i=0; i<args.length; i++){
			if(args[i].equals("--username")){
				userName = args[i+1];
				break;
			}
		}
		Launch.main(new String[]{"--version", "1.6", "--tweakClass", "cpw.mods.fml.common.launcher.FMLTweaker", "--accessToken", "0", "--username", userName});
	}
	
}
