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

import org.jboss.jdf.example.ticketmonster.rest.SectionEndpoint;
import org.jboss.jdf.example.ticketmonster.rest.dto.NestedVenueDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.SectionDTO;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Required;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.connector.api.CrestListener;

@Interceptors({ CountInterceptor.class })
@MessageDriven(name = "Section")
@Command("section")
public class SectionCommand implements CrestListener {

    @EJB
    private SectionEndpoint service;

    @Command
    public StreamingOutput list(@Option({"first", "f"}) @Default("0") @Min(0) Integer first, @Option({"max", "m"}) @Default("10") @Min(1) Integer max) {

        final List<SectionDTO> results = service.listAll(first, max);

        return new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException {
                final PrintWriter pw = new PrintWriter(os);

                List<DisplayField> fieldNames = Arrays.asList(new DisplayField[]{
                        new DisplayField("id", "ID"),
                        new DisplayField("name", "Section Name"),
                        new DisplayField("capacity", "Capacity"),
                        new DisplayField("numberOfRows", "No Of Rows"),
                        new DisplayField("rowCapacity", "Row Capacity"),
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
            final @Option({"name"}) @Required String name, 
            final @Option({"capacity"}) @Required Integer capacity, 
            final @Option({"description"}) String description, 
            final @Option({"numberofrows"}) @Required Integer numberOfRows, 
            final @Option({"rowcapacity"}) @Required Integer rowCapacity, 
            final @Option({"venueid"}) @Required Long venueId) {
        
        final SectionDTO section = new SectionDTO();
        final NestedVenueDTO venue = new NestedVenueDTO();
        venue.setId(venueId);
        section.setVenue(venue);
        section.setCapacity(capacity);
        section.setDescription(description);
        section.setName(name);
        section.setNumberOfRows(numberOfRows);
        section.setRowCapacity(rowCapacity);
        
        final Response response = service.create(section);
        if (Status.CREATED.getStatusCode() == response.getStatus()) {
            return "New section created.";
        } else {
            return "Error creating section";
        }
    }

    @Command
    public String update(
            final @Option({"id"}) @Required Long id,
            final @Option({"name"}) String name, 
            final @Option({"capacity"}) Integer capacity, 
            final @Option({"description"}) String description, 
            final @Option({"numberofrows"}) Integer numberOfRows, 
            final @Option({"rowcapacity"}) Integer rowCapacity, 
            final @Option({"venueid"}) Long venueId) {
        
        final Response response = service.findById(id);
        if (Status.OK.getStatusCode() != response.getStatus()) {
            return "Section with ID " + id + " not found.";
        }
        
        final SectionDTO section = (SectionDTO) response.getEntity();

        if (capacity != null) {
            section.setCapacity(capacity);
        }
        
        if (description != null) {
            section.setDescription(description);
        }
        
        if (numberOfRows != null) {
            section.setNumberOfRows(numberOfRows);
        }
        
        if (rowCapacity != null) {
            section.setRowCapacity(rowCapacity);
        }
        
        if (venueId != null) {
            section.getVenue().setId(venueId);
        }

        service.update(id, section);
        return "Section with ID " + id + " updated.";
    }

}