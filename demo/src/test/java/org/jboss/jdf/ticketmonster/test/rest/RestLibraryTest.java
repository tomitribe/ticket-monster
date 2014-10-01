package org.jboss.jdf.ticketmonster.test.rest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

@RunWith(Arquillian.class)
public class RestLibraryTest {

    @Deployment
    public static WebArchive deployment() {
        return RESTDeployment.deployment();
    }

    @ArquillianResource
    private URL webappUrl;

    @BeforeClass
    public static void before() {
//        Restoration.CONFIG.setRecord(new File("/Users/dblevins/work/tomitribe/ticket-monster/demo/src/test/resources"));
    }

    @Test
    @GET
    @Path("rest/bookings/")
    @Assertion(content = "/api/rest/bookings/.json")
    public void getBookings() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getBookings");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/bookings/count")
    @Assertion(content = "/api/rest/bookings/count.json")
    public void getBookingsCount() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getBookingsCount");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Ignore
    @Test
    @GET
    @Path("rest/bookings/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/bookings/1.json")
    @Assertion(params = "2", content = "/api/rest/bookings/2.json")
    @Assertion(params = "3", content = "/api/rest/bookings/3.json")
    public void getBookings1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getBookings1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/eventcategories/")
    @Assertion(content = "/api/rest/eventcategories/.json")
    public void getEventcategories() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getEventcategories");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/eventcategories/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/eventcategories/1.json")
    @Assertion(params = "2", content = "/api/rest/eventcategories/2.json")
    @Assertion(params = "3", content = "/api/rest/eventcategories/3.json")
    public void getEventcategories1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getEventcategories1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/events/")
    @Assertion(content = "/api/rest/events/.json")
    public void getEvents() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getEvents");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/events/count")
    @Assertion(content = "/api/rest/events/count.json")
    public void getEventsCount() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getEventsCount");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/events/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/events/1.json")
    @Assertion(params = "2", content = "/api/rest/events/2.json")
    @Assertion(params = "3", content = "/api/rest/events/3.json")
    public void getEvents1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getEvents1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/forge/bookings/")
    @Assertion(content = "/api/rest/forge/bookings/.json")
    public void getForgeBookings() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getForgeBookings");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Ignore
    @Test
    @GET
    @Path("rest/forge/bookings/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/forge/bookings/1.json")
    @Assertion(params = "2", content = "/api/rest/forge/bookings/2.json")
    @Assertion(params = "3", content = "/api/rest/forge/bookings/3.json")
    public void getForgeBookings1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getForgeBookings1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/forge/events/")
    @Assertion(content = "/api/rest/forge/events/.json")
    public void getForgeEvents() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getForgeEvents");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/forge/events/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/forge/events/1.json")
    @Assertion(params = "2", content = "/api/rest/forge/events/2.json")
    @Assertion(params = "3", content = "/api/rest/forge/events/3.json")
    public void getForgeEvents1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getForgeEvents1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Ignore
    @Test
    @GET
    @Path("rest/forge/shows/")
    @Assertion(content = "/api/rest/forge/shows/.json")
    public void getForgeShows() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getForgeShows");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Ignore
    @Test
    @GET
    @Path("rest/forge/shows/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/forge/shows/1.json")
    @Assertion(params = "2", content = "/api/rest/forge/shows/2.json")
    @Assertion(params = "3", content = "/api/rest/forge/shows/3.json")
    public void getForgeShows1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getForgeShows1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Ignore
    @Test
    @GET
    @Path("rest/forge/venues/")
    @Assertion(content = "/api/rest/forge/venues/.json")
    public void getForgeVenues() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getForgeVenues");
        Restoration.assertService(m, webappUrl.toURI());
    }

    @Ignore
    @Test
    @GET
    @Path("rest/forge/venues/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/forge/venues/1.json")
    @Assertion(params = "2", content = "/api/rest/forge/venues/2.json")
    @Assertion(params = "3", content = "/api/rest/forge/venues/3.json")
    public void getForgeVenues1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getForgeVenues1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/mediaitems/")
    @Assertion(content = "/api/rest/mediaitems/.json")
    public void getMediaitems() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getMediaitems");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/mediaitems/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/mediaitems/1.json")
    @Assertion(params = "2", content = "/api/rest/mediaitems/2.json")
    @Assertion(params = "3", content = "/api/rest/mediaitems/3.json")
    public void getMediaitems1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getMediaitems1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Ignore
    @Test
    @GET
    @Path("rest/metrics/")
    @Assertion(content = "/api/rest/metrics/.json")
    public void getMetrics() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getMetrics");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/performances/")
    @Assertion(content = "/api/rest/performances/.json")
    public void getPerformances() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getPerformances");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/performances/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/performances/1.json")
    @Assertion(params = "2", content = "/api/rest/performances/2.json")
    @Assertion(params = "3", content = "/api/rest/performances/3.json")
    public void getPerformances1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getPerformances1");
        Restoration.assertService(m, webappUrl.toURI());
    }

    @Ignore
    @Test
    @GET
    @Path("rest/sectionallocations/")
    @Assertion(content = "/api/rest/sectionallocations/.json")
    public void getSectionallocations() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getSectionallocations");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/sectionallocations/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/sectionallocations/1.json")
    @Assertion(params = "2", content = "/api/rest/sectionallocations/2.json")
    @Assertion(params = "3", content = "/api/rest/sectionallocations/3.json")
    public void getSectionallocations1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getSectionallocations1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/sections/")
    @Assertion(content = "/api/rest/sections/.json")
    public void getSections() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getSections");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/sections/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/sections/1.json")
    @Assertion(params = "2", content = "/api/rest/sections/2.json")
    @Assertion(params = "3", content = "/api/rest/sections/3.json")
    public void getSections1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getSections1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/shows/")
    @Assertion(content = "/api/rest/shows/.json")
    public void getShows() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getShows");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/shows/count")
    @Assertion(content = "/api/rest/shows/count.json")
    public void getShowsCount() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getShowsCount");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/shows/performance/{performanceId:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/shows/performance/1.json")
    @Assertion(params = "2", content = "/api/rest/shows/performance/2.json")
    @Assertion(params = "3", content = "/api/rest/shows/performance/3.json")
    public void getShowsPerformance() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getShowsPerformance");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/shows/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/shows/1.json")
    @Assertion(params = "2", content = "/api/rest/shows/2.json")
    @Assertion(params = "3", content = "/api/rest/shows/3.json")
    public void getShows1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getShows1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/ticketcategories/")
    @Assertion(content = "/api/rest/ticketcategories/.json")
    public void getTicketcategories() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getTicketcategories");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Ignore
    @Test
    @GET
    @Path("rest/ticketcategories/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/ticketcategories/1.json")
    @Assertion(params = "2", content = "/api/rest/ticketcategories/2.json")
    @Assertion(params = "3", content = "/api/rest/ticketcategories/3.json")
    public void getTicketcategories1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getTicketcategories1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/ticketprices/")
    @Assertion(content = "/api/rest/ticketprices/.json")
    public void getTicketprices() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getTicketprices");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/ticketprices/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/ticketprices/1.json")
    @Assertion(params = "2", content = "/api/rest/ticketprices/2.json")
    @Assertion(params = "3", content = "/api/rest/ticketprices/3.json")
    public void getTicketprices1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getTicketprices1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/tickets/")
    @Assertion(content = "/api/rest/tickets/.json")
    public void getTickets() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getTickets");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Ignore
    @Test
    @GET
    @Path("rest/tickets/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/tickets/1.json")
    @Assertion(params = "2", content = "/api/rest/tickets/2.json")
    @Assertion(params = "3", content = "/api/rest/tickets/3.json")
    public void getTickets1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getTickets1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/venues/")
    @Assertion(content = "/api/rest/venues/.json")
    public void getVenues() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getVenues");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/venues/count")
    @Assertion(content = "/api/rest/venues/count.json")
    public void getVenuesCount() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getVenuesCount");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/venues/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/venues/1.json")
    @Assertion(params = "2", content = "/api/rest/venues/2.json")
    @Assertion(params = "3", content = "/api/rest/venues/3.json")
    public void getVenues1() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getVenues1");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/bot/messages")
    @Assertion(content = "/api/rest/bot/messages.json")
    public void getBotMessages() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getBotMessages");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/bot/status")
    @Assertion(content = "/api/rest/bot/status.json")
    public void getBotStatus() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getBotStatus");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/media/cache/{cachedFileName:\\S*}")
    @Assertion(params = "1", content = "/api/rest/media/cache/1.json")
    @Assertion(params = "2", content = "/api/rest/media/cache/2.json")
    @Assertion(params = "3", content = "/api/rest/media/cache/3.json")
    public void getMediaCache() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getMediaCache");
        Restoration.assertService(m, webappUrl.toURI());
    }


    @Test
    @GET
    @Path("rest/media/{id:\\d*}")
    @Assertion(params = "1", content = "/api/rest/media/1.json")
    @Assertion(params = "2", content = "/api/rest/media/2.json")
    @Assertion(params = "3", content = "/api/rest/media/3.json")
    public void getMedia() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getMedia");
        Restoration.assertService(m, webappUrl.toURI());
    }


}
