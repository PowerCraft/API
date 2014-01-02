package powercraft.api.logger;

import cpw.mods.fml.common.FMLLog;

/**
 * @author James
 * The logger
 */
public class Logger {

	java.util.logging.Logger _l_ = java.util.logging.Logger.getLogger("PowerCraft");
	java.util.logging.Logger _l = java.util.logging.Logger.getGlobal();
	
	java.util.logging.Logger l_ = FMLLog.getLogger();
	
	/**
	 * Logs something
	 * @param l The log level
	 * @param message The message to log
	 */
	public void log(LogLevel l, String message){
		if(l != null){
			if(l == LogLevel.fine){
				this._l_.fine(message);
				this._l.fine(message);
				this.l_.fine(message);
			}
			
			if(l == LogLevel.FINE){
				this._l_.fine(message);
				this._l.fine(message);
				this.l_.fine(message);
			}
			
			if(l == LogLevel.info){
				this._l_.info(message);
				this._l.info(message);
				this.l_.info(message);
			}
			
			if(l == LogLevel.INFO){
				this._l_.info(message);
				this._l.info(message);
				this.l_.info(message);
			}
			
			if(l == LogLevel.warning){
				this._l_.warning(message);
				this._l.warning(message);
				this.l_.warning(message);
			}
			
			if(l == LogLevel.WARNING){
				this._l_.warning(message);
				this._l.warning(message);
				this.l_.warning(message);
			}
			
			if(l == LogLevel.severe){
				this._l_.severe(message);
				this._l.severe(message);
				this.l_.severe(message);
			}
			
			if(l == LogLevel.SEVERE){
				this._l_.severe(message);
				this._l.severe(message);
				this.l_.severe(message);
			}
			
			if(l == LogLevel.finer){
				this._l_.finer(message);
				this._l.finer(message);
				this.l_.finer(message);
			}
				
			if(l == LogLevel.FINER){
				this._l_.finer(message);
				this._l.finer(message);
				this.l_.finer(message);
			}
			
			if(l == LogLevel.finest){
				this._l_.finest(message);
				this._l.finest(message);
				this.l_.finest(message);
			}
			
			if(l == LogLevel.FINEST){
				this._l_.finest(message);
				this._l.finest(message);
				this.l_.finest(message);
			}
		}
	}
}
