package org.jboss.jdf.ticketmonster.test.rest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
public class WeirdIssueTest {

    @Deployment(name = "orange.war")
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, "orange.war");
    }

    @ArquillianResource
    private URL webappUrl;

    @Test
    public void testSomething() {
        fail("This should fail");
    }
}
