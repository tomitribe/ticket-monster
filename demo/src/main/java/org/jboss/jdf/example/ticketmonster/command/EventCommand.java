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

import org.jboss.jdf.example.ticketmonster.model.MediaType;
import org.jboss.jdf.example.ticketmonster.rest.EventEndpoint;
import org.jboss.jdf.example.ticketmonster.rest.dto.EventDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.NestedEventCategoryDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.NestedMediaItemDTO;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Required;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.connector.api.CrestListener;

@Interceptors({ CountInterceptor.class })
@MessageDriven(name = "Event")
@Command("event")
public class EventCommand implements CrestListener {

    @EJB
    private EventEndpoint service;

    @Command
    public StreamingOutput list(@Option({"first", "f"}) @Default("0") @Min(0) Integer first, @Option({"max", "m"}) @Default("10") @Min(1) Integer max) {

        final List<EventDTO> results = service.listAll(first, max);

        return new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException {
                final PrintWriter pw = new PrintWriter(os);

                List<DisplayField> fieldNames = Arrays.asList(new DisplayField[]{
                        new DisplayField("id", "ID"),
                        new DisplayField("description", "Description")
                });

                final AlignedTablePrinter tablePrinter = new AlignedTablePrinter(fieldNames, pw);
                tablePrinter.printRows(results, true);
                tablePrinter.finish();
            }
        };
    }
    
    @Command
    public String insert(
            final @Option({"categoryid"}) @Required Long categoryId,
            final @Option({"description"}) @Required String description,
            final @Option({"name"}) @Required String name,
            final @Option({"url"}) String url) {
        
        final EventDTO event = new EventDTO();
        final NestedEventCategoryDTO category = new NestedEventCategoryDTO();
        category.setId(categoryId);
        
        if (url != null) {
            NestedMediaItemDTO mediaItem = new NestedMediaItemDTO();
            mediaItem.setMediaType(MediaType.IMAGE);
            mediaItem.setUrl(url);
        }
        
        event.setCategory(category);
        event.setName(name);
        event.setDescription(description);
        
        final Response response = service.create(event);
        if (Status.CREATED.getStatusCode() == response.getStatus()) {
            return "New event created.";
        } else {
            return "Error creating event.";
        }
    }

    @Command
    public String update(
            final @Option({"id"}) @Required Long id,
            final @Option({"categoryid"}) Long categoryId,
            final @Option({"description"}) String description,
            final @Option({"name"}) String name,
            final @Option({"url"}) String url) {
        
        final Response response = service.findById(id);
        if (Status.OK.getStatusCode() != response.getStatus()) {
            return "Event with ID " + id + " not found.";
        }
        
        final EventDTO event = (EventDTO) response.getEntity();

        if (name != null) {
            event.setName(name);
        }
        
        if (description != null) {
            event.setDescription(description);
        }
        
        if (url != null) {
            NestedMediaItemDTO mediaItem = new NestedMediaItemDTO();
            mediaItem.setMediaType(MediaType.IMAGE);
            mediaItem.setUrl(url);
        }
        
        if (categoryId != null) {
            final NestedEventCategoryDTO category = new NestedEventCategoryDTO();
            category.setId(categoryId);
           
            event.setCategory(category);
        }
        
        service.update(id, event);
        return "Event with ID " + id + " updated.";
    }

}
