package org.jboss.jdf.example.ticketmonster.command;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.interceptor.Interceptors;
import javax.validation.constraints.Min;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.jdf.example.ticketmonster.rest.ShowEndpoint;
import org.jboss.jdf.example.ticketmonster.rest.dto.NestedEventDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.NestedVenueDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.ShowDTO;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Required;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.connector.api.CrestListener;

@Interceptors({ CountInterceptor.class })
@MessageDriven(name = "Show")
@Command("show")
public class ShowCommand implements CrestListener {

    @EJB
    private ShowEndpoint service;

    @Command
    public StreamingOutput list(@Option({"first", "f"}) @Default("0") @Min(0) Integer first, @Option({"max", "m"}) @Default("10") @Min(1) Integer max) {

        final List<ShowDTO> results = service.listAll(first, max);

        return new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException {
                final PrintWriter pw = new PrintWriter(os);

                List<DisplayField> fieldNames = Arrays.asList(new DisplayField[]{
                        new DisplayField("id", "ID"),
                        new DisplayField("event.id", "Event ID"),
                        new DisplayField("event.name", "Event Name"),
                        new DisplayField("venue.id", "Venue ID"),
                        new DisplayField("venue.name", "Venue Name")
                });

                final AlignedTablePrinter tablePrinter = new AlignedTablePrinter(fieldNames, pw);
                tablePrinter.printRows(results, true);
                tablePrinter.finish();
            }
        };
    }
    
    @Command
    public String insert(
            final @Option({"eventid"}) @Required Long eventId,
            final @Option({"venueid"}) @Required Long venueId) {
        
        final ShowDTO show = new ShowDTO();
        final NestedEventDTO event = new NestedEventDTO();
        event.setId(eventId);
        
        final NestedVenueDTO venue = new NestedVenueDTO();
        venue.setId(venueId);
        
        show.setEvent(event);
        show.setVenue(venue);
        
        final Response response = service.create(show);
        if (Status.CREATED.getStatusCode() == response.getStatus()) {
            return "New show created.";
        } else {
            return "Error creating show.";
        }
    }

    @Command
    public String update(
            final @Option({"id"}) @Required Long id,
            final @Option({"eventid"}) Long eventId,
            final @Option({"venueid"}) Long venueId) {
        
        final Response response = service.findById(id);
        if (Status.OK.getStatusCode() != response.getStatus()) {
            return "Show with ID " + id + " not found.";
        }
        
        final ShowDTO show = (ShowDTO) response.getEntity();

        if (eventId != null) {
            NestedEventDTO event = new NestedEventDTO();
            event.setId(eventId);
            show.setEvent(event);
        }

        if (venueId != null) {
            NestedVenueDTO venue = new NestedVenueDTO();
            venue.setId(venueId);
            show.setVenue(venue);
        }
        
        service.update(id, show);
        return "Show with ID " + id + " updated.";
    }

}
