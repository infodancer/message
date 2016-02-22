package org.infodancer.message.maildir;

import org.infodancer.message.FileMessage;

public class MaildirMessageFile extends FileMessage
{
	MaildirFolder folder; 
	
	public MaildirMessageFile(MaildirFolder folder, java.io.File file)
	{
		super(file);
		this.folder = folder;
	}
	
	/** 
	 * Provides a java.io.InputStream containing the message.
	 **/
	 
	public java.io.InputStream getInputStream() throws java.io.IOException
	{
		return new java.io.FileInputStream(msgFile); 
	}
	
	/** 
	 * Provides an outputstream which will replace the message.
	 **/
	 
	 public java.io.OutputStream getOutputStream() throws java.io.IOException
	 {
		return new java.io.FileOutputStream(msgFile);
	 }	 
}
