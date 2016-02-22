package org.infodancer.message.transport.smtp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import junit.framework.TestCase;

import org.infodancer.message.DeliveryException;
import org.infodancer.message.EmailAddress;
import org.infodancer.message.InvalidAddressException;

public class SMTPTransportTest extends TestCase
{
	SMTPTransport transport; 
	SMTPConnection connection;
	EmailAddress sender;
	EmailAddress recipient;
	Properties properties;

	/** 
	 * Loads values for test messages from PROPERTIES_FILE or environment variables.
	 */
	public void setUp() throws Exception
	{
		properties = new Properties();
		transport = new SMTPTransport();
		transport.setSmarthost(properties.getProperty("smarthost", "localhost"));
		sender = new EmailAddress(properties.getProperty("sender", "sender@example.com"));
		recipient = new EmailAddress(properties.getProperty("recipient", "recipient@example.com"));
	}

	public void testHELO() throws IOException
	{
		StringBuilder s = new StringBuilder();
		s.append("220 mail.example.com ESMTP\r\n");
		s.append("250 OK\r\n");
		ByteArrayInputStream input = new ByteArrayInputStream(s.toString().getBytes());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		connection = new SMTPConnection(transport, input, output);
		connection.sendHelo();
		BufferedReader reader = new BufferedReader(new StringReader(new String(output.toByteArray())));
		assertEquals("HELO localhost", reader.readLine());
	}

	public void testRCPTTO() throws IOException, InvalidAddressException
	{
		StringBuilder s = new StringBuilder();
		s.append("220 mail.example.com ESMTP\r\n");
		s.append("250 OK\r\n");
		s.append("250 OK\r\n");
		s.append("250 OK\r\n");
		ByteArrayInputStream input = new ByteArrayInputStream(s.toString().getBytes());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		connection = new SMTPConnection(transport, input, output);
		connection.sendHelo();
		connection.sendSender(sender);
		connection.sendRecipient(recipient);
		BufferedReader reader = new BufferedReader(new StringReader(new String(output.toByteArray())));
		assertEquals("HELO localhost", reader.readLine());
		assertEquals("MAIL FROM:<" + sender + ">", reader.readLine());
		assertEquals("RCPT TO:<" + recipient + ">", reader.readLine());
		
	}

	public void testDATA() throws IOException, InvalidAddressException, DeliveryException
	{
		StringBuilder s = new StringBuilder();
		s.append("220 mail.example.com ESMTP\r\n");
		s.append("250 OK\r\n");
		s.append("250 OK\r\n");
		s.append("250 OK\r\n");
		s.append("354 go ahead\r\n");
		s.append("250 OK\r\n");
		s.append("250 OK\r\n");
		StringBuilder data = new StringBuilder();
		data.append("From: sender@example.com\r\n");
		data.append("To: recipient@example.com\r\n");
		data.append("\r\n");
		data.append("Message body\r\n");
		ByteArrayInputStream input = new ByteArrayInputStream(s.toString().getBytes());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		connection = new SMTPConnection(transport, input, output);
		connection.sendHelo();
		connection.sendSender(sender);
		connection.sendRecipient(recipient);
		connection.sendData(data.toString().getBytes());
		connection.sendQuit();
		BufferedReader reader = new BufferedReader(new StringReader(new String(output.toByteArray())));
		assertEquals("HELO localhost", reader.readLine());
		assertEquals("MAIL FROM:<" + sender + ">", reader.readLine());
		assertEquals("RCPT TO:<" + recipient + ">", reader.readLine());
		assertEquals("DATA", reader.readLine());
		// Should validate the contents of the DATA sent, but that's tricky
		// assertEquals("QUIT", reader.readLine());
	}

	public void testParseEHLOLine() throws InvalidAddressException, IOException
	{
		StringBuilder s = new StringBuilder();
		s.append("220 mail.example.com ESMTP\r\n");
		ByteArrayInputStream input = new ByteArrayInputStream(s.toString().getBytes());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		connection = new SMTPConnection(transport, input, output);
		connection.parseEHLOLine("250-STARTTLS");
		assertTrue(connection.isStartTLSSupported());
		connection.parseEHLOLine("250-AUTH PLAIN LOGIN CRAM-MD5");
		assertTrue(connection.isAuthSupported());
		assertTrue(connection.isAuthPlainSupported());
		assertTrue(connection.isAuthLoginSupported());
		assertTrue(connection.isAuthCramSupported());
		connection.parseEHLOLine("250-PIPELINING");
		assertTrue(connection.isPipeliningSupported());
		connection.parseEHLOLine("250 SIZE 0");		
	}

	public void testMAILFROM() throws IOException, InvalidAddressException
	{
		StringBuilder s = new StringBuilder();
		s.append("220 mail.example.com ESMTP\r\n");
		s.append("250 OK\r\n");
		s.append("250 OK\r\n");
		ByteArrayInputStream input = new ByteArrayInputStream(s.toString().getBytes());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		connection = new SMTPConnection(transport, input, output);
		connection.sendHelo();
		connection.sendSender(sender);
		BufferedReader reader = new BufferedReader(new StringReader(new String(output.toByteArray())));
		assertEquals("HELO localhost", reader.readLine());
		assertEquals("MAIL FROM:<" + sender + ">", reader.readLine());	
	}
}
