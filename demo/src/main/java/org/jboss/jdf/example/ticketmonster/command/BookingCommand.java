package org.jboss.jdf.example.ticketmonster.command;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.jdf.example.ticketmonster.model.Event;
import org.jboss.jdf.example.ticketmonster.model.Show;
import org.jboss.jdf.example.ticketmonster.model.Venue;
import org.jboss.jdf.example.ticketmonster.rest.EventService;
import org.jboss.jdf.example.ticketmonster.rest.ShowService;
import org.jboss.jdf.example.ticketmonster.rest.VenueService;
import org.jboss.jdf.example.ticketmonster.util.MultivaluedHashMap;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.connector.api.CrestListener;

@Interceptors({CountInterceptor.class})
@MessageDriven(name = "Booking")
@Command("book")
public class BookingCommand implements CrestListener {

    @Inject
    private BookingState state;
    
    @Inject 
    private VenueService venueService;

    @Inject 
    private EventService eventService;

    @Inject 
    private ShowService showService;

    
    @Command
    public StreamingOutput venues() {
        
        final List<Venue> venues = new ArrayList<Venue>();;
        // if event selected, show venues for the event
        if (state.getSelectedEvent() != null) {
            Long eventId = state.getSelectedEvent();
            
            final MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<String, String>();
            queryParameters.add("event", eventId.toString());
            
            List<Show> shows = showService.getAll(queryParameters);
            SortedSet<Venue> eventVenues = new TreeSet<Venue>(new Comparator<Venue>() {

                @Override
                public int compare(Venue o1, Venue o2) {
                    return o1.getId().compareTo(o2.getId());
                }
                
            });
            for (Show show : shows) {
                eventVenues.add(show.getVenue());
            }
            
            venues.addAll(eventVenues);
        } else {
            // else show all venues
            final MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<String, String>();
            venues.addAll(venueService.getAll(queryParameters));
        }

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
                tablePrinter.printRows(venues, true);
                tablePrinter.finish();
            }
        };
    }
    
    @Command
    public String venue(@Option("id") Long id) {
        // select the venue
        state.setSelectedVenue(id);
        // if event is selected, but not an event for this venue, reset event
        // reset performance
        return "Venue selected.";
    }
    
    @Command
    public StreamingOutput events() {
        final List<Event> venues = new ArrayList<Event>();;
        // if event selected, show venues for the event
        if (state.getSelectedVenue() != null) {
            Long venueId = state.getSelectedVenue();
            
            final MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<String, String>();
            queryParameters.add("venue", venueId.toString());
            
            List<Show> shows = showService.getAll(queryParameters);
            SortedSet<Event> venueEvents = new TreeSet<Event>(new Comparator<Event>() {

                @Override
                public int compare(Event o1, Event o2) {
                    return o1.getId().compareTo(o2.getId());
                }
                
            });
            for (Show show : shows) {
                venueEvents.add(show.getEvent());
            }
            
            venues.addAll(venueEvents);
        } else {
            // else show all events
            final MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<String, String>();
            venues.addAll(eventService.getAll(queryParameters));
        }

        return new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException {
                final PrintWriter pw = new PrintWriter(os);

                List<DisplayField> fieldNames = Arrays.asList(new DisplayField[]{
                        new DisplayField("id", "ID"),
                        new DisplayField("name", "Name"),
                        new DisplayField("description", "Description")
                });

                final AlignedTablePrinter tablePrinter = new AlignedTablePrinter(fieldNames, pw);
                tablePrinter.printRows(venues, true);
                tablePrinter.finish();
            }
        };
    }
    
    @Command
    public String event(@Option("id") Long id) {
        // select the event
        state.setSelectedEvent(id);
        // if the venue is selected but not a venue for this event, reset venue
        // reset performance
        return "Event selected.";
    }
    
    @Command
    public String performances() {
        // event and venue must be selected
        // show shows/performances for the event/venue
        throw new UnsupportedOperationException();
    }
    
    @Command
    public String performance(@Option("id") Long id) {
        // event, venue must be selected
        // select the performance
        throw new UnsupportedOperationException();
    }
    
    @Command
    public String sections() {
        // event, venue and performance must be selected
        // show sections and ticket prices
        throw new UnsupportedOperationException();
    }
    
    @Command
    public String addticket(@Option("id") Long id) {
        // event, venue and performance must be selected
        // add ticket request
        throw new UnsupportedOperationException();
    }
    
    @Command
    public String book(String email) {
        // event, venue and performance must be selected
        // tickets must be added
        // book
        // reset
        throw new UnsupportedOperationException();
    }
    
    @Command
    public String reset() {
        // reset everything
        state.setBookingRequest(null);
        state.setSelectedEvent(null);
        state.setSelectedShow(null);
        state.setSelectedVenue(null);
        
        return "Booking cleared.";
    }
    
}
