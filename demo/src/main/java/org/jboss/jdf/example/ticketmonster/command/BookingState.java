package org.jboss.jdf.example.ticketmonster.command;

import org.jboss.jdf.example.ticketmonster.rest.BookingRequest;
import org.tomitribe.crest.connector.api.TerminalSessionScoped;

@TerminalSessionScoped
public class BookingState {
    
    private Long selectedVenue;
    
    private Long selectedEvent;
    
    private Long selectedShow;
    
    private BookingRequest bookingRequest;

    public Long getSelectedVenue() {
        return selectedVenue;
    }

    public void setSelectedVenue(Long selectedVenue) {
        this.selectedVenue = selectedVenue;
    }

    public Long getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Long selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public Long getSelectedShow() {
        return selectedShow;
    }

    public void setSelectedShow(Long selectedShow) {
        this.selectedShow = selectedShow;
    }

    public BookingRequest getBookingRequest() {
        return bookingRequest;
    }

    public void setBookingRequest(BookingRequest bookingRequest) {
        this.bookingRequest = bookingRequest;
    }
    
    
}
