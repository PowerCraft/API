package powercraft.api;

public final class PC_Sound {

	public static boolean isSoundEnabled(){
		if (PC_Utils.isServer()) {
			return false;
		}
		return true;//TODO
	}
	
	public static void playSound(double x, double y, double z, String sound, float soundVolume, float pitch) {
		if (isSoundEnabled()) {
			PC_Registry.playSound(x, y, z, sound, soundVolume, pitch);
		}
	}
	
	private PC_Sound(){
		PC_Utils.staticClassConstructor();
	}
	
}
