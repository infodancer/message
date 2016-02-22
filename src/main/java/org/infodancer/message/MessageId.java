package org.infodancer.message;

/**
 * A unique identifier for a given message.
 * The message id consists of a unique string identifier, followed by the '@' 
 * symbol followed by the hostname of the MX hosting or sending the message.  
 * The exact nature of the unique string is up to the MX to determine, but 
 * encoded timestamps (with sufficient precision), counters, or a one-way hash 
 * of the message contents could all be used singly or in combination.
 * 
 * @author matthew
 */

public class MessageId
{

}
