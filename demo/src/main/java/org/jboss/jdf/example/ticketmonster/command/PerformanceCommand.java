package org.jboss.jdf.example.ticketmonster.command;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.interceptor.Interceptors;
import javax.validation.constraints.Min;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.jdf.example.ticketmonster.rest.PerformanceEndpoint;
import org.jboss.jdf.example.ticketmonster.rest.dto.NestedShowDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.PerformanceDTO;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Required;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.connector.api.CrestListener;

@Interceptors({ CountInterceptor.class })
@MessageDriven(name = "Performance")
@Command("performance")
public class PerformanceCommand implements CrestListener {

    @EJB
    private PerformanceEndpoint service;

    @Command
    public StreamingOutput list(@Option({"first", "f"}) @Default("0") @Min(0) Integer first, @Option({"max", "m"}) @Default("10") @Min(1) Integer max) {

        final List<PerformanceDTO> results = service.listAll(first, max);

        return new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException {
                final PrintWriter pw = new PrintWriter(os);

                List<DisplayField> fieldNames = Arrays.asList(new DisplayField[]{
                        new DisplayField("id", "ID"),
                        new DisplayField("date", "Date"),
                        new DisplayField("displayTitle", "Title"),
                        new DisplayField("show.id", "Show ID"),
                        new DisplayField("show.event.name", "Event"),
                        new DisplayField("show.venue.name", "Venue"),
                });

                final AlignedTablePrinter tablePrinter = new AlignedTablePrinter(fieldNames, pw);
                tablePrinter.printRows(results, true);
                tablePrinter.finish();
            }
        };
    }
    
    @Command
    public String insert(
            final @Option({"date"}) @Required Date date,
            final @Option({"showid"}) @Required Long showid) {
        
        final PerformanceDTO performance = new PerformanceDTO();
        final NestedShowDTO show = new NestedShowDTO();
        show.setId(showid);
        performance.setDate(date);

        final Response response = service.create(performance);
        if (Status.CREATED.getStatusCode() == response.getStatus()) {
            return "New performance created.";
        } else {
            return "Error creating performance.";
        }
    }

    @Command
    public String update(
            final @Option({"id"}) @Required Long id,
            final @Option({"date"}) Date date,
            final @Option({"showid"}) Long showid) {
        
        final Response response = service.findById(id);
        if (Status.OK.getStatusCode() != response.getStatus()) {
            return "Performance with ID " + id + " not found.";
        }
        
        final PerformanceDTO performance = (PerformanceDTO) response.getEntity();

        if (date != null) {
            performance.setDate(date);
        }
        
        if (showid != null) {
            performance.getShow().setId(showid);
        }
        
        service.update(id, performance);
        return "Performance with ID " + id + " updated.";
    }

}
