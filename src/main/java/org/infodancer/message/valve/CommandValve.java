package org.infodancer.message.valve;

import java.util.logging.Logger;

/** 
 * This valve simply passes the incoming messages through an external command.
 **/

public class CommandValve
{
	private static Logger log = Logger.getLogger(CommandValve.class.getName());
	String name;
	String command;
	Runtime runtime;
		
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
}
