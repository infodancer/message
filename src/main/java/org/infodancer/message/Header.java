package org.infodancer.message;

public class Header
{
	protected String name;
	protected String value;
	
	public Header(String name)
	{
		this.name = name;
	}
	
	public Header(String name, String value)
	{
		this.name = name;
		this.value = value;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public String toString()
	{
		return name + ": " + value;
	}
}
