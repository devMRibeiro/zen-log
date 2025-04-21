package com.github.devmribeiro.zenlog.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.github.devmribeiro.zenlog.enums.Level;

public class Logger {

    private static Level currentLevel = Level.TRACE;
    private final String className;

    public Logger(Class<?> clazz) {
        this.className = clazz.getSimpleName();
    }

    public static void setLevel(Level level) {
        currentLevel = level;
    }

    private void log(Level level, Object message) {
        if (level.ordinal() < currentLevel.ordinal()) return;

        System.out.println(String.format("%s[%s] [%s] [%s]: %s",
    			setColor(level),
    			level.name(),
    			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()),
    			className,
    			sanitize(message) + "\u001B[0m"));
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
        log(Level.ERROR, formatMessage(msg) + "\n" + getStackTrace(t));
    }

    public void f(Object msg) {
    	log(Level.FATAL, msg);
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
        	color = "\u001B[46m";

    	else if (level == Level.INFO)
        	color = "\u001B[42m";

    	else if (level == Level.WARN)
        	color = "\u001B[43m";

    	else if (level == Level.ERROR)
        	color = "\u001B[41m";

    	else if (level == Level.FATAL)
        	color = "\u001B[45m";

    	else
        	color = "\u001B[0m";

    	return color;
    }
}