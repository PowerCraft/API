package powercraft.api.logging;

import cpw.mods.fml.common.FMLLog;

/**
 * @author James
 * The logger
 */
public class Logger {
	
	/**
	 * Logs something
	 * @param l The log level
	 * @param message The message to log
	 */
	public static final void log(LogLevel l, String message){
		java.util.logging.Logger _l_ = java.util.logging.Logger.getLogger("PowerCraft");
		java.util.logging.Logger _l = java.util.logging.Logger.getGlobal();
		
		java.util.logging.Logger l_ = FMLLog.getLogger();
		if(l != null){
			if(l == LogLevel.fine){
				_l_.fine(message);
				_l.fine(message);
				l_.fine(message);
			}
			
			if(l == LogLevel.FINE){
				_l_.fine(message);
				_l.fine(message);
				l_.fine(message);
			}
			
			if(l == LogLevel.info){
				_l_.info(message);
				_l.info(message);
				l_.info(message);
			}
			
			if(l == LogLevel.INFO){
				_l_.info(message);
				_l.info(message);
				l_.info(message);
			}
			
			if(l == LogLevel.warning){
				_l_.warning(message);
				_l.warning(message);
				l_.warning(message);
			}
			
			if(l == LogLevel.WARNING){
				_l_.warning(message);
				_l.warning(message);
				l_.warning(message);
			}
			
			if(l == LogLevel.severe){
				_l_.severe(message);
				_l.severe(message);
				l_.severe(message);
			}
			
			if(l == LogLevel.SEVERE){
				_l_.severe(message);
				_l.severe(message);
				l_.severe(message);
			}
			
			if(l == LogLevel.finer){
				_l_.finer(message);
				_l.finer(message);
				l_.finer(message);
			}
				
			if(l == LogLevel.FINER){
				_l_.finer(message);
				_l.finer(message);
				l_.finer(message);
			}
			
			if(l == LogLevel.finest){
				_l_.finest(message);
				_l.finest(message);
				l_.finest(message);
			}
			
			if(l == LogLevel.FINEST){
				_l_.finest(message);
				_l.finest(message);
				l_.finest(message);
			}
		}
	}
}
