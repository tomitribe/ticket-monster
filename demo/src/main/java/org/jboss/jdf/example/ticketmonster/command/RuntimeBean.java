/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.jdf.example.ticketmonster.command;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.connector.api.CrestListener;
import org.tomitribe.util.Size;
import org.tomitribe.util.SizeUnit;

import javax.ejb.MessageDriven;

@MessageDriven(name = "Runtime")
public class RuntimeBean implements CrestListener {

    @Command
    public int processors() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Command
    public void gc() {
        Runtime.getRuntime().gc();
    }

    @Command
    public String freeMemory(@Option({"unit", "u"}) @Default("MEGABYTES") SizeUnit unit) {
        final long bytes = Runtime.getRuntime().freeMemory();
        final Size size = new Size(unit.convert(bytes, SizeUnit.BYTES), unit);
        return size.toString();
    }

    @Command
    public String maxMemory(@Option({"unit","u"}) @Default("MEGABYTES") SizeUnit unit) {
        final long bytes = Runtime.getRuntime().maxMemory();
        final Size size = new Size(unit.convert(bytes, SizeUnit.BYTES), unit);
        return size.toString();
    }

    @Command
    public String totalMemory(@Option({"unit","u"}) @Default("MEGABYTES") SizeUnit unit) {
        final long bytes = Runtime.getRuntime().totalMemory();
        final Size size = new Size(unit.convert(bytes, SizeUnit.BYTES), unit);
        return size.toString();
    }

}
