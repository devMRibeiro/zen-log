package com.github.devmribeiro.zenlog.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.github.devmribeiro.zenlog.enums.Level;

/**
 * @version 2.3.0
 * @author Michael Ribeiro 
 */
public class Logger {

    private static Level currentLevel = Level.TRACE;
    private final Class<?> clazz;
    private static BufferedWriter writer;
    private final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<Class<?>, Logger> instances = new HashMap<Class<?>, Logger>();
 
    public static Logger getLogger(Class<?> clazz) {
    	return instances.computeIfAbsent(clazz, Logger::new);
    }

    public Logger(Class<?> clazz) {
        this.clazz = clazz;

    	if (writer == null) {
    		synchronized (Logger.class) {
				if (writer == null) { // double-checked locking
					String timestampFormatFilename = LocalDateTime.now().format(timestampFormat).replaceAll("[:\\s]", "_");

			        File logFile = new File(new File("").getAbsolutePath() + "/logs", timestampFormatFilename + ".txt");

			        logFile.getParentFile().mkdirs();

			        try {
		            	writer = new BufferedWriter(new FileWriter(logFile, true));
		            } catch (IOException e) {
		            	throw new RuntimeException("Error creating log file", e);
		            }
		        }
			}
    	}
    }

    public static void setLevel(Level level) {
        currentLevel = level;
    }

    private void log(Level level, Object message, Throwable ex) {
        if (level.ordinal() < currentLevel.ordinal())
        	return;

        String log = formatLog(level, message);
        System.out.println(log);

        String logToFile = log.substring(log.indexOf("[" + level), log.indexOf("\u001B[0m"));
        writeLogToFile(logToFile, ex);
    }

    private void writeLogToFile(String log, Throwable ex) {
    	try {
            writer.write(log);
            writer.newLine();

            if (ex != null) {
                writer.write("Exception: " + ex.toString());
                writer.newLine();
                for (StackTraceElement element : ex.getStackTrace()) {
                    writer.write("\tat " + element);
                    writer.newLine();
                }
            }

            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to the log file:\n" + e.getMessage());
        }
    }
    
    private String formatLog(Level level, Object message) {
    	return String.format("%s[%s] [%s] [%s]: %s",
    			setColor(level),
    			level.name(),
    			LocalDateTime.now().format(timestampFormat),
    			clazz.getSimpleName(),
    			sanitize(message) + "\u001B[0m");
    }

    private void log(Level level, Object message) {
    	log(level, message, null);
    }

    private String formatMessage(Object msg) {
        if (msg == null) return "null";
        return msg.toString();
    }

    private static String sanitize(Object msg) {
        return msg.toString().replaceAll("(?i)(senha|password|token|apiKey|authToken|secret|accessToken|refreshToken)[:=]([^&\\s]+)", "$1=*****");
    }

    private String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String setColor(Level level) {
    	String color;

    	if (level == Level.TRACE)
        	color = "\u001B[37m";

    	else if (level == Level.DEBUG)
        	color = "\u001B[36m";

    	else if (level == Level.INFO)
        	color = "\u001B[32m";

    	else if (level == Level.WARN)
        	color = "\u001B[33m";

    	else if (level == Level.ERROR)
        	color = "\u001B[31m";

    	else if (level == Level.FATAL)
        	color = "\u001B[35m";

    	else
        	color = "\u001B[0m";

    	return color;
    }

    public void t(Object msg) {
    	log(Level.TRACE, msg);
    }

    public void d(Object msg) {
    	log(Level.DEBUG, msg);
    }

    public void i(Object msg) {
    	log(Level.INFO, msg);
    }

    public void w(Object msg) {
    	log(Level.WARN, msg);
    }

    public void e(Object msg) {
    	log(Level.ERROR, msg);
	}

    public void e(Object msg, Throwable t) { 
    	log(Level.ERROR, formatMessage(msg) + "\n" + getStackTrace(t), t);
	}

    public void f(Object msg) {
    	log(Level.FATAL, msg);
	}
}