package org.infodancer.message;

public interface MessageStore
{
	public static final String CONTEXT_MESSAGE_STORE = "MessageStore";
	/**
	 * Provides the mailbox for the specified user.
	 * @param username
	 */
	public Mailbox getMailbox(String username);
}
