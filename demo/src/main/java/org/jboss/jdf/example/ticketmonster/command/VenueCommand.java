package org.jboss.jdf.example.ticketmonster.command;

import org.jboss.jdf.example.ticketmonster.model.Venue;
import org.jboss.jdf.example.ticketmonster.rest.VenueService;
import org.jboss.jdf.example.ticketmonster.util.MultivaluedHashMap;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.connector.api.CrestListener;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.validation.constraints.Min;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@MessageDriven(name = "Venue")
@Command("venue")
public class VenueCommand implements CrestListener {

    @EJB
    private VenueService service;

    @Command("all")
    public StreamingOutput getAll(@Option({"first", "f"}) @Default("1") @Min(1) Integer first, @Option({"max", "m"}) @Default("10") @Min(1) Integer max) {

        final MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();

        // apply pagination
        params.put("first", Collections.singletonList(String.valueOf(first)));
        params.put("maxResults", Collections.singletonList(String.valueOf(max)));

        final List<Venue> results = service.getAll(params);

        return new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException {
                final PrintWriter pw = new PrintWriter(os);

                List<DisplayField> fieldNames = Arrays.asList(new DisplayField[]{
                        new DisplayField("id", "ID"),
                        new DisplayField("name", "Venue Name"),
                        new DisplayField("capacity", "Capacity"),
                        new DisplayField("address", "Address")
                });

                final AlignedTablePrinter tablePrinter = new AlignedTablePrinter(fieldNames, pw);
                tablePrinter.printRows(results, true);
                tablePrinter.finish();
            }
        };
    }

}
