package powercraft.api;


import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * 
 * the PowerCraft Logger
 * 
 * @author XOR
 *
 */
public final class PC_Logger {
	
	private static final Logger logger = Logger.getLogger("PowerCraft");

	private static boolean loggingEnabled = true;
	
	private static boolean inited;
	
	static boolean printToStdout = false;

	/**
	 * inits the logger, called from PC_Api
	 * @param file the PowerCraft .log file
	 */
	static void init(File file) {
		if(inited)
			return;
		inited=true;
		try {
			FileHandler handler = new FileHandler(file.getPath());
			handler.setFormatter(new PC_LogFormatter());
			logger.addHandler(handler);
			loggingEnabled = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.setLevel(Level.ALL);
		logger.info("PowerCraft logger initialized.");
		logger.info((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	}

	/**
	 * sets the logging enabled/disabled
	 * @param flag the state
	 */
	public static void enableLogging(boolean flag) {

		loggingEnabled = flag;
	}

	/**
	 * sets the print to the console enabled/disabled
	 * @param flag the state
	 */
	public static void setPrintToStdout(boolean printToStdout) {

		PC_Logger.printToStdout = printToStdout;
	}

	/**
	 * makes a log message
	 * @param level the message level
	 * @param msg the message
	 * @param param the format parameters
	 */
	public static void log(Level level, String msg, Object... param) {

		if (!loggingEnabled) {
			return;
		}
		logger.log(level, String.format(msg, param));
	}

	/**
	 * makes a info message
	 * @param msg the info message
	 * @param param the format parameters
	 */
	public static void info(String msg, Object... param) {

		log(Level.INFO, msg, param);
	}

	/**
	 * makes a fine message
	 * @param msg the fine message
	 * @param param the format parameters
	 */
	public static void fine(String msg, Object... param) {

		log(Level.FINE, msg, param);
	}

	/**
	 * makes a finer message
	 * @param msg the finer message
	 * @param param the format parameters
	 */
	public static void finer(String msg, Object... param) {

		log(Level.FINER, msg, param);
	}

	/**
	 * makes a finest message
	 * @param msg the finest message
	 * @param param the format parameters
	 */
	public static void finest(String msg, Object... param) {

		log(Level.FINEST, msg, param);
	}

	/**
	 * makes a warning message
	 * @param msg the warning message
	 * @param param the format parameters
	 */
	public static void warning(String msg, Object... param) {

		log(Level.WARNING, msg, param);
	}

	/**
	 * makes a severe message
	 * @param msg the severe message
	 * @param param the format parameters
	 */
	public static void severe(String msg, Object... param) {

		log(Level.SEVERE, msg, param);
	}

	/**
	 * makes a throw
	 * @param sourceClass the source class name
	 * @param sourceMethod the method name
	 * @param thrown the thrown object
	 */
	public static void throwing(String sourceClass, String sourceMethod, Throwable thrown) {

		if (!loggingEnabled) {
			return;
		}

		logger.throwing(sourceClass, sourceMethod, thrown);
	}

	/**
	 *
	 * the log formatter
	 *
	 * @author MightyPork
	 *
	 */
	private static class PC_LogFormatter extends Formatter {

		private static final String nl = System.getProperty("line.separator");

		public PC_LogFormatter() {}

		@Override
		public String format(LogRecord record) {

			StringBuffer buf = new StringBuffer(180);

			if (record.getMessage().equals("\n")) {
				return nl;
			}

			if (record.getMessage().charAt(0) == '\n') {
				buf.append(nl);
				record.setMessage(record.getMessage().substring(1));
			}

			Level level = record.getLevel();
			String trail = "";

			if (level == Level.CONFIG) {
				trail = "CONFIG: ";
			}

			if (level == Level.FINE) {
				trail = "";
			}

			if (level == Level.FINER) {
				trail = "\t";
			}

			if (level == Level.FINEST) {
				trail = "\t\t";
			}

			if (level == Level.INFO) {
				trail = "INFO: ";
			}

			if (level == Level.SEVERE) {
				trail = "SEVERE: ";
			}

			if (level == Level.WARNING) {
				trail = "WARNING: ";
			}

			buf.append(trail);
			buf.append(formatMessage(record));
			buf.append(nl);
			Throwable throwable = record.getThrown();

			if (throwable != null) {
				buf.append("at ");
				buf.append(record.getSourceClassName());
				buf.append('.');
				buf.append(record.getSourceMethodName());
				buf.append(nl);
				StringWriter sink = new StringWriter();
				throwable.printStackTrace(new PrintWriter(sink, true));
				buf.append(sink.toString());
				buf.append(nl);
			}

			if (PC_Logger.printToStdout) {
				System.out.print(buf.toString());
			}

			return buf.toString();
		}
	}
}
