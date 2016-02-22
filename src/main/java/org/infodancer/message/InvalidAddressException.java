package org.infodancer.message;

/** 
 * Thrown when the address provided to an EmailAddress object is 
 * invalid.   
 **/
 
public class InvalidAddressException extends Exception
{
	String address;
	
	public InvalidAddressException(String address)
	{
		this.address = address;
	}
	
	public String getMessage()
	{
		if (address != null)
		{
			return address + " is not a valid email address.";
		}
		else return "The address provided is not valid!";
	}
} 

