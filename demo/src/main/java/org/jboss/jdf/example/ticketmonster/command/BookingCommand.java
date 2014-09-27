package org.jboss.jdf.example.ticketmonster.command;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.jdf.example.ticketmonster.model.Event;
import org.jboss.jdf.example.ticketmonster.model.Performance;
import org.jboss.jdf.example.ticketmonster.model.Show;
import org.jboss.jdf.example.ticketmonster.model.TicketPrice;
import org.jboss.jdf.example.ticketmonster.model.Venue;
import org.jboss.jdf.example.ticketmonster.rest.BookingRequest;
import org.jboss.jdf.example.ticketmonster.rest.BookingService;
import org.jboss.jdf.example.ticketmonster.rest.EventService;
import org.jboss.jdf.example.ticketmonster.rest.ShowService;
import org.jboss.jdf.example.ticketmonster.rest.TicketRequest;
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
    
    @Inject
    private BookingService bookingService;

    
    @Command
    public StreamingOutput venues() {
        
        final List<Venue> venues = new ArrayList<Venue>();;
        // if event selected, show venues for the event
        if (state.getSelectedEvent() != null) {
            venues.addAll(getEventVenues(state.getSelectedEvent()));
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

        // reset performance
        state.setBookingRequest(null);
        
        // if venue is selected, but not an venue for this event, reset venue
        SortedSet<Event> venueEvents = getVenueEvents(id);
        for (Event event : venueEvents) {
            if (event.getId().equals(state.getSelectedEvent())) {
                return "Venue selected.";
            }
        }
        
        state.setSelectedEvent(null);
        return "Venue selected, please select an event";
    }
    
    @Command
    public StreamingOutput events() {
        final List<Event> venues = new ArrayList<Event>();;
        // if event selected, show venues for the event
        if (state.getSelectedVenue() != null) {
            venues.addAll(getVenueEvents(state.getSelectedVenue()));
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
        
        // reset performance
        state.setBookingRequest(null);
        
        // if the venue is selected but not a venue for this event, reset venue
        SortedSet<Venue> eventVenues = getEventVenues(id);
        for (Venue venue : eventVenues) {
            if (venue.getId().equals(state.getSelectedVenue())) {
                return "Event selected.";
            }
        }
        
        state.setSelectedVenue(null);
        return "Event selected, please select a venue";
    }
    
    @Command
    public StreamingOutput performances() {
        
        return new StreamingOutput() {
            
            @Override
            public void write(OutputStream os) throws IOException {
                PrintWriter pw = new PrintWriter(os);
                // event and venue must be selected
                if (state.getSelectedEvent() == null && state.getSelectedVenue() == null) {
                    pw.println("Please select venue and an event.");
                    return;
                }
                
                if (state.getSelectedEvent() == null) {
                    pw.println("Please select an event.");
                    return;
                }
                
                if (state.getSelectedVenue() == null) {
                    pw.println("Please select a venue.");
                    return;
                }

                List<Performance> performances = getPerformances(state.getSelectedEvent(), state.getSelectedVenue());
                
                // show shows/performances for the event/venue
                List<DisplayField> fieldNames = Arrays.asList(new DisplayField[]{
                        new DisplayField("id", "ID"),
                        new DisplayField("date", "Date")
                });

                final AlignedTablePrinter tablePrinter = new AlignedTablePrinter(fieldNames, pw);
                tablePrinter.printRows(performances, true);
                tablePrinter.finish();
            }
        };
    }
    
    @Command
    public String performance(@Option("id") Long id) {
        if (state.getSelectedEvent() == null && state.getSelectedVenue() == null) {
            return "Please select venue and an event.";
        }
        
        if (state.getSelectedEvent() == null) {
            return "Please select an event.";
        }
        
        if (state.getSelectedVenue() == null) {
            return "Please select a venue.";
        }

        List<Performance> performances = getPerformances(state.getSelectedEvent(), state.getSelectedVenue());
         
        for (Performance performance : performances) {
            if (performance.getId().equals(id)) {
                BookingRequest bookingRequest = new BookingRequest();
                bookingRequest.setPerformance(id);
                state.setBookingRequest(bookingRequest);
                
                return "Performance selected, please choose your tickets";
            }
        }
        
        return "Selected performance ID not found for selected venue/event";
    }
    
    @Command
    public StreamingOutput tickets() {
        return new StreamingOutput() {
            
            @Override
            public void write(OutputStream os) throws IOException {
                PrintWriter pw = new PrintWriter(os);

                // event, venue and performance must be selected
                if (state.getBookingRequest() == null) {
                    pw.println("Please select a performance.");
                    return;
                }
                
                // show sections and ticket prices
                Show show = showService.getShowByPerformance(state.getBookingRequest().getPerformance());
                if (show == null) {
                    pw.println("Please select a performance.");
                    return;
                }
                
                Set<TicketPrice> ticketPrices = show.getTicketPrices();
                List<DisplayField> fieldNames = Arrays.asList(new DisplayField[]{
                        new DisplayField("id", "ID"),
                        new DisplayField("section.name", "Section"),
                        new DisplayField("ticketCategory.description", "Category"),
                        new DisplayField("price", "Price")
                });

                final AlignedTablePrinter tablePrinter = new AlignedTablePrinter(fieldNames, pw);
                tablePrinter.printRows(ticketPrices, true);
                tablePrinter.finish();
            }
        };
    }
    
    @Command
    public String addticket(@Option("id") Long id, @Option("quantity") Integer quantity) {
        // event, venue and performance must be selected
        if (state.getBookingRequest() == null) {
            return "Please select a performance.";
        }

        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setTicketPrice(id);
        ticketRequest.setQuantity(quantity);
        state.getBookingRequest().addTicketRequest(ticketRequest);

        return "Added " + quantity + " tickets";
    }
    
    @Command
    public String book(String email) {
        // event, venue and performance must be selected
        if (state.getBookingRequest() == null) {
            return "Please select a performance.";
        }
        
        // tickets must be added
        if (state.getBookingRequest().getTicketRequests() == null
                || state.getBookingRequest().getTicketRequests().size() == 0) {
            return "Please add some tickets.";
        }
        
        state.getBookingRequest().setEmail(email);
        
        // book
        Response createBooking = bookingService.createBooking(state.getBookingRequest());
        if (Status.OK.getStatusCode() == createBooking.getStatus()) {
            clear();
            return "Booking created succesfully, enjoy the performance.";
        } else {
            return "An error occurred submitting the booking request. Please review and try again.";
        }
    }
    
    @Command
    public String reset() {
        // reset everything
        clear();
        
        return "Booking cleared.";
    }

    private void clear() {
        state.setBookingRequest(null);
        state.setSelectedEvent(null);
        state.setSelectedShow(null);
        state.setSelectedVenue(null);
    }

    private SortedSet<Venue> getEventVenues(Long eventId) {
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
        return eventVenues;
    }

    private SortedSet<Event> getVenueEvents(Long venueId) {
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
        return venueEvents;
    }

    private List<Performance> getPerformances(Long eventId, Long venueId) {
        final MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<String, String>();
        queryParameters.add("event", eventId.toString());
        queryParameters.add("venue", venueId.toString());
        List<Show> shows = showService.getAll(queryParameters);
        
        List<Performance> performances = new ArrayList<Performance>();
        for (Show show : shows) {
            performances.addAll(show.getPerformances());
        }
        return performances;
    }
    
}
