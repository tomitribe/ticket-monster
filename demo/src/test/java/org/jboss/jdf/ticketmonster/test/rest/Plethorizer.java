/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.jboss.jdf.ticketmonster.test.rest;

import org.tomitribe.util.IO;
import org.tomitribe.util.PrintString;
import org.tomitribe.util.Strings;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Plethorizer {


    public static void main(String[] args) throws IOException {

        final Set<String> used = new HashSet<String>();

        for (final String line : iterate("api.txt")) {
            final String[] split = line.split(" +");

            final String fullUri = split[3];
            final String httpMethod = split[2];

            if (!httpMethod.equals("GET")) continue;

            final String path = fullUri.replaceAll(".*ticket-monster/rest/", "");

            final String shortPath = path.replaceAll("\\{.*", "");

            String name = Strings.lcfirst(Strings.camelCase(httpMethod.toLowerCase() + "/" + shortPath, "/"));
            name = unique(used, name, 1);

            final PrintString out = new PrintString();

            out.printf("" +
                    "    @Test\n" +
                    "    @%s\n" +
                    "    @Path(\"rest/%s\")\n",
                    httpMethod, path.replace("\\", "\\\\")
            );
            if (path.contains("{")) {
                out.printf("    @Assertion(params = \"1\", content = \"/api/rest/%s1.json\")\n", shortPath);
                out.printf("    @Assertion(params = \"2\", content = \"/api/rest/%s2.json\")\n", shortPath);
                out.printf("    @Assertion(params = \"3\", content = \"/api/rest/%s3.json\")\n", shortPath);
            } else {
                out.printf("    @Assertion(content = \"/api/rest/%s.json\")\n", shortPath);
            }

            out.printf("" +
                    "    public void %s() throws Exception {\n" +
                    "        final Method m = RestLibraryTest.class.getMethod(\"%s\");\n" +
                    "        Restoration.assertService(m, webappUrl.toURI());\n" +
                    "    }\n" +
                    "\n", name, name);

//            System.out.println(Join.join("\t", path, name));
            System.out.println(out.toString());


        }
    }

    private static String unique(Set<String> used, String name, int i) {
        if (used.add(name)) {return name;}

        if (used.add(name + i)) {return name + i;}

        return unique(used, name, i + 1);
    }

    private static Iterable<String> iterate(String name) throws IOException {
        if (!name.startsWith("/")) {
            name = "/" + name;
        }


        final URL resource = Plethorizer.class.getResource(name);

        if (resource == null) throw new IllegalStateException(String.format("Not found '%s'", name));

        return IO.readLines(IO.read(resource));
    }
}
