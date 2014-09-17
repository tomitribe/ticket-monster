package org.jboss.jdf.example.ticketmonster.command;

import javax.ejb.MessageDriven;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.connector.api.CrestListener;

@MessageDriven(name = "About")
public class AboutCommand implements CrestListener {

	@Command
	public String about() {
		return "Ticket Monster Sample Application, version 2.6.1-SNAPSHOT";
	}
	
	
}
