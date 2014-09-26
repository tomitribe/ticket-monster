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
import org.jboss.jdf.example.ticketmonster.rest.VenueEndpoint;
import org.jboss.jdf.example.ticketmonster.rest.dto.AddressDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.NestedMediaItemDTO;
import org.jboss.jdf.example.ticketmonster.rest.dto.VenueDTO;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Required;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.connector.api.CrestListener;

@Interceptors({ CountInterceptor.class })
@MessageDriven(name = "Venue")
@Command("venue")
public class VenueCommand implements CrestListener {

    @EJB
    private VenueEndpoint service;

    @Command
    public StreamingOutput list(@Option({"first", "f"}) @Default("0") @Min(0) Integer first, @Option({"max", "m"}) @Default("10") @Min(1) Integer max) {

        final List<VenueDTO> results = service.listAll(first, max);

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
    
    @Command
    public String insert(
            final @Option({"name"}) @Required String name, 
            final @Option({"capacity"}) @Required Integer capacity, 
            final @Option({"description"}) String description, 
            final @Option({"url"}) @Required String url, 
            final @Option({"street"}) @Required String street, 
            final @Option({"city"}) @Required String city, 
            final @Option({"country"}) @Required String country) {
        
        final VenueDTO venue = new VenueDTO();
        final AddressDTO address = new AddressDTO();
        address.setCity(city);
        address.setCountry(country);
        address.setStreet(street);
        final NestedMediaItemDTO mediaItem = new NestedMediaItemDTO();
        mediaItem.setMediaType(MediaType.IMAGE);
        mediaItem.setUrl(url);
        venue.setMediaItem(mediaItem);
        venue.setAddress(address);
        venue.setCapacity(capacity);
        venue.setDescription(description);
        venue.setName(name);
        
        final Response response = service.create(venue);
        if (Status.CREATED.getStatusCode() == response.getStatus()) {
            return "New venue created.";
        } else {
            return "Error creating venue";
        }
    }

    @Command
    public String update(
            final @Option({"id"}) @Required Long id,
            final @Option({"name"}) String name, 
            final @Option({"capacity"}) Integer capacity, 
            final @Option({"description"}) String description, 
            final @Option({"url"}) String url, 
            final @Option({"street"}) String street, 
            final @Option({"city"}) String city, 
            final @Option({"country"}) String country) {
        
        final Response response = service.findById(id);
        if (Status.OK.getStatusCode() != response.getStatus()) {
            return "Venue with ID " + id + " not found.";
        }
        
        final VenueDTO venue = (VenueDTO) response.getEntity();

        if (city != null) {
            venue.getAddress().setCity(city);
        }
        
        if (country != null) {
            venue.getAddress().setCountry(country);
        }
        
        if (street != null) {
            venue.getAddress().setStreet(street);
        }
        
        if (url != null) {
            venue.getMediaItem().setUrl(url);
        }

        if (capacity != null) {
            venue.setCapacity(capacity);
        }
        
        if (description != null) {
            venue.setDescription(description);
        }
        
        if (name != null) {
            venue.setName(name);
        }
        
        service.update(id, venue);
        return "Venue with ID " + id + " updated.";
    }

}
