package com.edi.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
	//public static Logger LOGGER = LoggerFactory.getLogger("test");
	public static Logger LOGGER_D = LoggerFactory.getLogger(App.class);
	
	
    public static void main( String[] args )
    {
        //LOGGER.debug("test");
        LOGGER_D.debug("aaa");
    }
}
