package org.infodancer.message;

/** 
 * Provides a lightweight implementation of an email address intended for 
 * server-side and network use (as opposed to the javax.mail.internet.InernetAddress
 * implementation, which is intended for client-side use within messages.
 **/

public class EmailAddress
{
	public String name;
	public String user;
	public String host;
	
	/** 
	 * Parses the provided string into an EmailAddress object.  Full RFC-2822 
	 * support is intended.
	 **/
	
	public EmailAddress(String address) throws InvalidAddressException
	{
		parseAddress(address);	
	}
	
	private void parseAddress(String address) throws InvalidAddressException
	{
		try
		{
			user = null;
			host = null;
			name = null;
			boolean escaped = false;
			char[] buffer = address.toCharArray();
			StringBuilder current = new StringBuilder();
			for (int count = 0; count < buffer.length; count++)
			{
				if (escaped)
				{
					escaped = false;
					current.append(buffer[count]);
				}
				else
				{
					if (buffer[count] == '\\')
					{
						escaped = true;
						current.append(buffer[count]);
					}
					else if (buffer[count] == '@')
					{
						user = current.toString().trim();
						current = new StringBuilder();
					}
					else if (buffer[count] == '<')
					{
						name = current.toString().trim();
						current = new StringBuilder();
					}
					else if (buffer[count] == '>')
					{
						host = current.toString().trim();
						current = new StringBuilder();
					}
					else current.append(buffer[count]);
				}
			}
			
			if (current.length() > 0)
			{
				if (user == null) 
				{
					host = null;
					user = current.toString().trim();
				}
				else if (host == null)
				{
					host = current.toString().trim();
				}
			}
		}
		
		catch (Exception e)
		{
			throw new InvalidAddressException(address + " is not a valid email address!");
		}
	}

	/** 
	 * Provides this email address in RFC822 format.
	 **/
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		if (name != null)
		{
			buffer.append(name);
			buffer.append(' ');
			buffer.append('<');
		}
		buffer.append(user);
		if (host != null)
		{
			buffer.append("@");
			buffer.append(host);
		}
		if (name != null)
		{
			buffer.append('>');
		}
		return buffer.toString();
	}
	
	public boolean equals(EmailAddress address)
	{
		if (address != null)
		{
			if (host == null)
			{
				if (address.host == null) 
				{
					if (user != null) 
					{
						if (user.equals(address.user)) return true;
						else return false;
					}
					else if (address.user == null) return true;
					else return false;					
				}
				else return false;
			}
			else
			{
				if (host.equals(address.host))
				{
					if (user != null) 
					{
						if (user.equals(address.user)) return true;
						else return false;
					}
					else if (address.user == null) return true;
					else return false;
				}
				else return false;
			}
		}
		else return false;
	}
	
	/** 
	 * Validates the provided email address.  Validation consists of checking for the 
	 * presences of an "@" character followed by a "." character. 
	 **/

	public static boolean validate(String email)
	{
		if (email == null) return false;
		else
		{
			int index = email.indexOf("@");
			if (index == -1) return false;
			else
			{
				index = email.indexOf(".", index);
				if (index == -1) return false;
				else return true;
			}
		}
	}
	
	/** 
	 * Provides both the user and host portions of the address (eg, user@host),
	 * but not any additional information that might be encoded.
	 **/
	 
	public String getAddress()
	{
		if (host != null) return user + "@" + host;
		else return user;
	}

	/** 
	 * Provides only the domain/host portions of the address (eg, following the @ symbol).
	 * @return the domain/host portion of the address, or null if there is no such component. 
	 **/
	 
	public String getDomain()
	{
		return host;
	}
	
	/** 
	 * Provides only the user portion of the address.
	 **/
	 
	public String getUser()
	{
		return user;
	}

	/** 
	 * Provides only the user-readable name portion of the address.
	 **/
	 
	public String getName()
	{
		return name;
	}
		
	/** 
	 * For debugging purposes.  Takes any number of arguments, each of which is parsed as an email address,
	 * and the results of the parsing displayed to standard out.
	 **/
	
	public static final void main(String[] args)
	{
		try
		{
			String line = null;
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(args[0]));
			while ((line = reader.readLine()) != null)
			{
				EmailAddress address = new EmailAddress(line);
				System.out.println(address.toString());
				System.out.println("\tUser:" + address.user);
				System.out.println("\tHost:" + address.host);
				System.out.println("\tName:" + address.name);
			}
			reader.close();
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
