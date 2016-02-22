 package org.infodancer.message.maildir;

/** 
 * Provides a wrapping around an Iterator of java.io.File, transforming
 * each java.io.File into a MessageFile.  Optionally marks messages read
 * after processing.
 **/

public class MaildirMessageIterator implements java.util.Iterator
{
	boolean markMessagesRead;
	private MaildirFolder folder;
	private java.io.File maildir;
	private java.util.Iterator iterator;
	
	public MaildirMessageIterator(MaildirFolder folder, java.io.File maildir, java.util.Iterator iterator)
	{
		this.folder = folder;
		this.maildir = maildir;
		this.iterator = iterator;
		this.markMessagesRead = false;
	}

	public MaildirMessageIterator(MaildirFolder folder, java.io.File maildir, java.util.Iterator iterator, boolean markMessagesRead)
	{
		this.folder = folder;
		this.maildir = maildir;
		this.iterator = iterator;
		this.markMessagesRead = markMessagesRead;
	}
	
	public void setMarkMessagesRead(boolean value)
	{
		this.markMessagesRead = value;
	}
	
	public boolean hasNext()
	{
		return iterator.hasNext();
	}
	
	public void remove()
	{
		iterator.remove();
	}
	
	public void markCurrentMessageRead()
	{
		
	}
	
	public Object next()
	{
		if (markMessagesRead) markCurrentMessageRead();
		return new MaildirMessageFile(folder, (java.io.File) iterator.next());
	}
}
