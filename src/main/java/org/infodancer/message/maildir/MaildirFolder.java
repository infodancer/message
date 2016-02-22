package org.infodancer.message.maildir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.infodancer.message.DeliveryException;
import org.infodancer.message.Message;

public class MaildirFolder
{
	// extends javax.mail.Folder
	MaildirFolder parent;
	java.io.File maildir;
	java.io.File newDirectory;
	java.io.File tmpDirectory;
	java.io.File curDirectory;
	List<MaildirMessageFile> messages;
	
	public MaildirFolder(java.io.File directory)
	{
		this.maildir = directory;
		this.newDirectory = new java.io.File(maildir + "/new/");
		this.tmpDirectory = new java.io.File(maildir + "/tmp/");
		this.curDirectory = new java.io.File(maildir + "/cur/");
	}
	
	public MaildirFolder(MaildirFolder parent, java.io.File directory)
	{
		this.parent = parent;
		this.maildir = directory;
		this.newDirectory = new java.io.File(maildir + "/new/");
		this.tmpDirectory = new java.io.File(maildir + "/tmp/");
		this.curDirectory = new java.io.File(maildir + "/cur/");
	}
	
	/** 
	 * This is a <em>very</em> lightweight method that checks to see
	 * if new messages have been delivered to the /new/ directory, 
	 * instead of explicitly checking the "RECENT" flag.
	 **/
	 
	public boolean hasNewMessages()
	{
		String[] files = newDirectory.list();
		if (files.length > 0) return true;
		else return false;
	}

	public void close() 
	{
		
	}

	public void open() 
	{ 
		
		// First, copy the new messages into the current directory
		File[] msgfiles = newDirectory.listFiles(new MaildirMessageFileFilter());
		for (int i = 0; i < msgfiles.length; i++)
		{
			File newname = new File(curDirectory + File.separator + msgfiles[i].getName());
			msgfiles[i].renameTo(newname);
		}
		
		// Second, list all messages from the current directory
		
		msgfiles = curDirectory.listFiles(new MaildirMessageFileFilter());
		messages = new java.util.ArrayList<MaildirMessageFile>(size());
		for (int i = 0; i < msgfiles.length; i++)
		{
			messages.add(new MaildirMessageFile(this, msgfiles[i]));
		}
	}

	public java.io.File getMaildirFile()
	{
		return maildir;
	}
	
	public void deliver(Message message) throws DeliveryException
	{
		try
		{
			java.io.File tmpfile = createTempFile();
			WritableByteChannel channel = java.nio.channels.Channels.newChannel(new FileOutputStream(tmpfile));
			// message.writeTo(channel);
			java.io.File msgfile = createMessageFile(tmpfile);
			tmpfile.renameTo(msgfile);
		}
		
		catch (Exception e)
		{
			DeliveryException ee = new DeliveryException(e);
			throw ee;
		}
	}

	public int size()
	{
		int size = 0;
		MaildirMessageFileFilter filter = new MaildirMessageFileFilter(); 
		size += curDirectory.listFiles(filter).length;
		size += newDirectory.listFiles(filter).length;
		return size;
	}
	
	/** 
	 * Creates a temporary file into which a message can be delivered, using the 
	 * JVM's createTempFile() method.  This does not exactly conform to the maildir
	 * specification, but it appears to be as close as can be achieved without 
	 * native code.
	 **/
	 
	protected File createTempFile() throws IOException, java.net.UnknownHostException
	{
		String prefix = System.currentTimeMillis() + ".";
		String suffix = "." + java.net.InetAddress.getLocalHost().getHostName();
		return File.createTempFile(prefix, suffix, tmpDirectory);		
	}
	
	/** 
	 * Creates a message filename from a temporary filename.  Note that the actual 
	 * file is not created; the temporary file should be renamed to the resulting
	 * filename when delivery is complete.
	 **/
	protected File createMessageFile(java.io.File tmpfile) throws IOException
	{
		return new File(newDirectory + File.separator + tmpfile.getName());		
	}
	
	/** 
	 * Determines whether the folder represented by this object actually exists on disk.
	 * @return true if the Maildir structure exists; false otherwise.
	 */
	public boolean exists()
	{
		if ((maildir.exists() && maildir.isDirectory()))
		{
			if ((tmpDirectory.exists() && tmpDirectory.isDirectory()) && 
			    (newDirectory.exists() && newDirectory.isDirectory()) &&
			    (curDirectory.exists() && curDirectory.isDirectory()))
			{
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * Creates the maildir if it does not already exist.
	 */
	public void create() throws IOException
	{
		if (!maildir.exists()) maildir.mkdirs();
		if (!tmpDirectory.exists()) tmpDirectory.mkdirs();
		if (!curDirectory.exists()) curDirectory.mkdirs();
		if (!newDirectory.exists()) newDirectory.mkdirs();
	}

	public List<Message> list()
	{
		List<Message> result = new ArrayList<Message>(messages);
		return result;
	}

}
