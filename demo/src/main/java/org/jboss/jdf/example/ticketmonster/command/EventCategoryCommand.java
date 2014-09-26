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

import org.jboss.jdf.example.ticketmonster.rest.EventCategoryEndpoint;
import org.jboss.jdf.example.ticketmonster.rest.dto.EventCategoryDTO;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Required;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.connector.api.CrestListener;

@Interceptors({ CountInterceptor.class })
@MessageDriven(name = "EventCategory")
@Command("eventcategory")
public class EventCategoryCommand implements CrestListener {

    @EJB
    private EventCategoryEndpoint service;

    @Command
    public StreamingOutput list(@Option({"first", "f"}) @Default("0") @Min(0) Integer first, @Option({"max", "m"}) @Default("10") @Min(1) Integer max) {

        final List<EventCategoryDTO> results = service.listAll(first, max);

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
            final @Option({"description"}) String description) {
        
        final EventCategoryDTO eventCategory = new EventCategoryDTO();
        eventCategory.setDescription(description);
        
        final Response response = service.create(eventCategory);
        if (Status.CREATED.getStatusCode() == response.getStatus()) {
            return "New event category created.";
        } else {
            return "Error creating event category";
        }
    }

    @Command
    public String update(
            final @Option({"id"}) @Required Long id,
            final @Option({"description"}) String description) {
        
        final Response response = service.findById(id);
        if (Status.OK.getStatusCode() != response.getStatus()) {
            return "Ticket category with ID " + id + " not found.";
        }
        
        final EventCategoryDTO eventCategory = (EventCategoryDTO) response.getEntity();

        if (description != null) {
            eventCategory.setDescription(description);
        }
        
        service.update(id, eventCategory);
        return "Event category with ID " + id + " updated.";
    }

}
