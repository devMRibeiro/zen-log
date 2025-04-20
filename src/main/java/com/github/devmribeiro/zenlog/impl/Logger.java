package com.github.devmribeiro.zenlog.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import com.github.devmribeiro.zenlog.enums.Level;

public class Logger {

	private String className = null;

	public Logger(Class<?> clazz) {
		this.className = clazz.getSimpleName();
	}

	private void log(Level level, String message) {
		System.out.println(String.format("[%s] [%s] [%s]: %s", level.name(), LocalDateTime.now(), className, message));
	}

	public void i(String message) {
		log(Level.INFO, message);
	}

	public void w(String message) {
		log(Level.WARN, message);
	}

	public void e(String message) {
		log(Level.ERROR, message);
	}

	public void e(String message, Throwable t) {
		log(Level.ERROR, message + "\n" + getStackTrace(t));
	}

	private String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}