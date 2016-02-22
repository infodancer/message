package org.infodancer.message.transport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.infodancer.message.EmailAddress;

public class MX
{
    public int priority;
    public String host;
    
    public MX(String mx)
    {
        int index = mx.indexOf(" ");
        this.priority = Integer.parseInt(mx.substring(0, index));
        this.host = mx.substring(index + 1, mx.length());
    }
    
    public String toString()
    {
        return host + ":" + priority;
    }
    
    
    /**
     * Looks up the default resolver in /etc/resolv.conf.
     * @return
     * @throws IOException 
     * @throws NamingException 
     */
    public static String lookupResolver() throws IOException, NamingException
    {
    	File file = new File("/etc/resolv.conf");
		BufferedReader reader = null;
		
    	try
    	{
    		reader = new BufferedReader(new FileReader(file));
    		String line;
    		while ((line = reader.readLine()) != null)
    		{
    			if (line.startsWith("nameserver"))
    			{
    				String[] s = line.split(" ");
    				if (s.length > 1)
    				{
    					return s[1].trim();
    				}
    			}
    		}
    		throw new NamingException("Unable to identify DNS resolver in /etc/resolv.conf!");
    	}
    	
    	finally
    	{
    		try { if (reader != null) reader.close(); } catch (Exception e) { } 
    	}
    }
    
    /** 
     * Looks up the MX record for a given EmailAddress using the given resolver.
     **/
    
    public static MX[] mxlookup(EmailAddress recipient, String resolver) throws javax.naming.NamingException
    {
        return mxlookup(recipient.host, resolver);
    }

    /** 
     * Looks up the MX record for a given EmailAddress.
     * @throws IOException 
     **/
    
    public static MX[] mxlookup(EmailAddress recipient) throws javax.naming.NamingException, IOException
    {
        return mxlookup(recipient.host, lookupResolver());
    }
    
    /** 
     * Looks up the MX record for a given domain. This method assumes a caching DNS
     * server on localhost!  
     **/
     
    public static MX[] mxlookup(String domain, String resolver) throws javax.naming.NamingException
    {
        java.util.ArrayList<MX> results = new java.util.ArrayList<MX>();
        DirContext ictx = new InitialDirContext();
        Attributes attributes = ictx.getAttributes("dns://" + resolver + "/" + domain, new String[] {"MX"});
        NamingEnumeration e = attributes.getAll();
        while (e.hasMore())
        {
            Attribute mx = (Attribute) e.next();
            String value = (String) mx.get();
            results.add(new MX(value));
        }
        
        MX[] result = new MX[results.size()];
        result = (MX[]) results.toArray(result);
        java.util.Arrays.sort(result, new MXComparator());
        return result;
    }    
}
