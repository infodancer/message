package org.infodancer.message;

/** 
 * Encapsulates one or more email addresses.
 **/

public class AddressHeader extends Header
{
	public AddressHeader(String name, String value)
	{
		super(name, value);
	}
	
	public AddressHeader(String name, EmailAddress address)
	{
		super(name);
		value = address.toString();
	}
	
	public AddressHeader(String name, java.util.List<EmailAddress> addresses)
	{
		super(name);
		setAddresses(addresses);
	}
	
	public void setAddresses(java.util.List<EmailAddress> addresses)
	{
		boolean first = true;
		StringBuilder buffer = new StringBuilder();
		for (EmailAddress address : addresses)
		{
			if (first) first = false;
			else buffer.append(", ");
			buffer.append(address.toString());
		}
		value = buffer.toString();		
	}
	
	public void setAddress(EmailAddress address)
	{
		value = address.toString();
	}
	
	public void addAddress(EmailAddress address)
	{
		value = value + "," + address;
	}
	
	public EmailAddress getAddress() throws InvalidAddressException
	{
		return getAddresses().getFirst();
	}
	
	public java.util.LinkedList<EmailAddress> getAddresses() throws InvalidAddressException
	{
		boolean comment = false;
		boolean escaped = false;
		StringBuilder current = new StringBuilder();
		java.util.LinkedList<EmailAddress> result = new java.util.LinkedList<EmailAddress>();
				
		char last = 0;
		char[] buffer = value.toCharArray();
		for (int count = 0; count < buffer.length; count++)
		{
			if (comment)
			{
				if ((last != '\\') && (buffer[count] == ')')) comment = false;
			}
			else
			{
				if (!escaped)
				{
					if (buffer[count] == ',') 
					{
						result.add(new EmailAddress(current.toString()));
						current = new StringBuilder();
					}
					else if ((buffer[count] == '@') || (Character.isLetterOrDigit(buffer[count])))
					{
						current.append(buffer[count]);
					}
				}
				else current.append(buffer[count]);
			}
			
			if (current.length() != 0) 
			{
				result.add(new EmailAddress(current.toString()));
			}
			last = buffer[count];
		}
		return result;
	}
}
