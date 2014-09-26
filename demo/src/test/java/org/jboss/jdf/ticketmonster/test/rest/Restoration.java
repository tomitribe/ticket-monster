/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.jboss.jdf.ticketmonster.test.rest;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.openejb.loader.JarLocation;
import org.tomitribe.util.IO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public enum Restoration {
    CONFIG;

    private File record;

    public static void assertService(Method m, final URI baseURI) throws URISyntaxException, IOException {
        final Path path = m.getAnnotation(Path.class);

        for (final Assertion assertion : getAssertions(m)) {
            WebClient client = WebClient.create(baseURI);
            client.accept(assertion.accept());

            client = client.path(path.value(), assertion.params());

            final String actual;

            if (m.isAnnotationPresent(GET.class)) {
                actual = client.get(String.class);
            } else {
                throw new IllegalArgumentException("HTTP Method Required");
            }

            if (CONFIG.shouldRecord()) {

                final File file = new File(CONFIG.record, assertion.content().replaceFirst("^/", ""));
                final File parent = file.getParentFile();

                if (!parent.exists()) assertTrue(parent.mkdirs());

                IO.copy(IO.read(actual), IO.write(file));

            } else {

                final String expected = expected(assertion);
                assertEquals(assertion.code(), client.getResponse().getStatus());
                assertEquals(expected, actual);
            }
        }
    }

    private static List<Assertion> getAssertions(Method m) {
        final List<Assertion> list = new ArrayList<Assertion>();

        final Assertions assertions = m.getAnnotation(Assertions.class);

        if (assertions != null) {
            addAll(list, assertions.value());
        }

        {
            final Assertion assertion;
            assertion = m.getAnnotation(Assertion.class);
            if (assertion != null) {
                list.add(assertion);
            }
        }
        return list;
    }

    private static String expected(Assertion assertion) throws IOException {
        final URL resource = RestLibraryTest.class.getResource(assertion.content());
        if (resource == null) return null;
        return IO.slurp(resource);
    }

    public boolean shouldRecord() {
        return record != null;
    }

    public void setRecord(final File record) {
        this.record = record;
    }
}
