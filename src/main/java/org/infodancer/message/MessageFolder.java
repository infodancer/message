package org.infodancer.message;

import java.util.List;

/**
 * Represents a collection of messages.
 * @author matthew
 */

public interface MessageFolder
{
	public void open();
	public void close();
	public List<Message> list();
	public List<MessageFolder> listFolders();
}
