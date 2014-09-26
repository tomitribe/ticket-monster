package org.jboss.jdf.ticketmonster.test.rest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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


    @Test
    @GET
    @Path("rest/shows/{id:[0-9][0-9]*}")
    @Assertion(params = "1", content = "/api/rest/shows/1.json")
    @Assertion(params = "2", content = "/api/rest/shows/2.json")
    @Assertion(params = "3", content = "/api/rest/shows/3.json")
    @Assertion(params = "4", content = "/api/rest/shows/4.json")
    @Assertion(params = "5", content = "/api/rest/shows/5.json")
    @Assertion(params = "6", content = "/api/rest/shows/6.json")
    public void getShows() throws Exception {
        final Method m = RestLibraryTest.class.getMethod("getShows");
        Restoration.assertService(m, webappUrl.toURI());
    }

}
