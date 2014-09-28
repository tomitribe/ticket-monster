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

import org.jboss.jdf.example.ticketmonster.rest.TicketPriceEndpoint;
import org.jboss.jdf.example.ticketmonster.rest.dto.NestedSectionDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.NestedShowDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.NestedTicketCategoryDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.TicketPriceDTO;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Required;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.connector.api.CrestListener;

@Interceptors({ CountInterceptor.class })
@MessageDriven(name = "TicketPrice")
@Command("ticketprice")
public class TicketPriceCommand implements CrestListener {

    @EJB
    private TicketPriceEndpoint service;

    @Command
    public StreamingOutput list(@Option({"first", "f"}) @Default("0") @Min(0) Integer first, @Option({"max", "m"}) @Default("10") @Min(1) Integer max) {

        final List<TicketPriceDTO> results = service.listAll(first, max);

        return new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException {
                final PrintWriter pw = new PrintWriter(os);

                List<DisplayField> fieldNames = Arrays.asList(new DisplayField[]{
                        new DisplayField("id", "ID"),
                        new DisplayField("displayTitle", "Description"),
                        new DisplayField("section.name", "Section"),
                        new DisplayField("ticketCategory.description", "Category"),
                        new DisplayField("price", "Price"),
                });

                final AlignedTablePrinter tablePrinter = new AlignedTablePrinter(fieldNames, pw);
                tablePrinter.printRows(results, true);
                tablePrinter.finish();
            }
        };
    }
    
    @Command
    public String insert(
            final @Option({"price"}) @Required Float price,
            final @Option({"sectionid"}) @Required Long sectionId,
            final @Option({"showid"}) @Required Long showId,
            final @Option({"ticketcategoryid"}) @Required Long ticketCategoryId) {
        
        final TicketPriceDTO ticketPrice = new TicketPriceDTO();
        final NestedShowDTO show = new NestedShowDTO();
        show.setId(showId);
        
        final NestedSectionDTO section = new NestedSectionDTO();
        section.setId(sectionId);
        
        final NestedTicketCategoryDTO ticketCategory = new NestedTicketCategoryDTO();
        ticketCategory.setId(ticketCategoryId);
        
        ticketPrice.setShow(show);
        ticketPrice.setSection(section);
        ticketPrice.setTicketCategory(ticketCategory);
        ticketPrice.setPrice(price);
        
        final Response response = service.create(ticketPrice);
        if (Status.CREATED.getStatusCode() == response.getStatus()) {
            return "New ticket price created.";
        } else {
            return "Error creating ticket price.";
        }
    }

    @Command
    public String update(
            final @Option({"id"}) @Required Long id,
            final @Option({"price"}) Float price,
            final @Option({"sectionid"}) Long sectionId,
            final @Option({"showid"}) Long showId,
            final @Option({"ticketcategoryid"}) Long ticketCategoryId) {
        
        final Response response = service.findById(id);
        if (Status.OK.getStatusCode() != response.getStatus()) {
            return "Ticket price with ID " + id + " not found.";
        }
        
        final TicketPriceDTO ticketPrice = (TicketPriceDTO) response.getEntity();

        if (price != null) {
            ticketPrice.setPrice(price);
        }
        
        if (sectionId != null) {
            ticketPrice.getSection().setId(sectionId);
        }
        
        if (showId != null) {
            ticketPrice.getShow().setId(showId);
        }
        
        if (ticketCategoryId != null) {
            ticketPrice.getTicketCategory().setId(ticketCategoryId);
        }

        service.update(id, ticketPrice);
        return "Ticket price with ID " + id + " updated.";
    }

}
