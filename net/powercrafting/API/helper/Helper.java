package net.powercrafting.API.helper;

import java.util.logging.Level;
import java.util.logging.Logger;
import cpw.mods.fml.common.FMLLog;

public class Helper {
	public static Logger logger = FMLLog.getLogger();

	public static void log(String msg) {
		logger.log(Level.INFO, msg);
	}

	public static void logWarn(String msg) {
		logger.log(Level.WARNING, msg);
	}

	public static void logError(String msg) {
		logger.log(Level.SEVERE, msg);
	}

	public static void logDebug(String msg) {
		logger.log(Level.FINE, msg);
	}
}
