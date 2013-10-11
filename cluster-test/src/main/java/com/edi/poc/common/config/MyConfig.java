package com.edi.poc.common.config;

import java.util.ResourceBundle;

public class MyConfig {

	private final static String CONFIG_PATH = "config";
	
	public static int port;
	
	public static String ip;
	
	private static boolean isLoaded = false;
	
	private MyConfig(){}
	
	static
	{
		isLoaded = loadConfig();
	}
	
	private static boolean loadConfig()
	{
		ResourceBundle bundle = ResourceBundle.getBundle(CONFIG_PATH);
		String portInString = bundle.getString("port").trim();
		ip = bundle.getString("IP").trim();
		if(portInString==null)
			return false;
		if(ip==null)
			ip="localhost";
		try {
			port = Integer.valueOf(portInString);
		} catch (NumberFormatException e) {
			return false;
		}
		isLoaded = true;
		return true;
	}

	public static boolean isLoaded() {
		return isLoaded;
	}
	
}
