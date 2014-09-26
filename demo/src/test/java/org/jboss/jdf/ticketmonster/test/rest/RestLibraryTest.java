package org.jboss.jdf.ticketmonster.test.rest;

import org.apache.cxf.jaxrs.client.WebClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tomitribe.util.IO;

import javax.ws.rs.core.MediaType;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class RestLibraryTest {

    @Deployment
    public static WebArchive deployment() {
        return RESTDeployment.deployment();
    }

    @ArquillianResource
    private URL webappUrl;


    @Test
    public void testSomething() throws Exception {
        final WebClient client = WebClient.create(webappUrl.toURI());
        client.accept(MediaType.APPLICATION_JSON_TYPE);

        final URL resource = this.getClass().getResource("/api/rest/shows/1.json");

        final String expected = IO.slurp(resource);

        final String show = client.path("rest/shows/1").get(String.class);

        assertEquals(expected, show);
    }
}
