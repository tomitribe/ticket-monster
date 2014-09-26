/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.jdf.example.ticketmonster.command;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.repeat;
import static java.lang.Math.max;
import static java.lang.String.format;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.fusesource.jansi.AnsiString;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

public class AlignedTablePrinter {
    private static final Splitter LINE_SPLITTER = Splitter.on('\n');

    private final List<DisplayField> fieldNames;
    private final Writer writer;

    private boolean headerOutput;
    private long rowCount;

    public AlignedTablePrinter(List<DisplayField> fieldNames, Writer writer) {
        this.fieldNames = ImmutableList.copyOf(checkNotNull(fieldNames, "fieldNames is null"));
        this.writer = checkNotNull(writer, "writer is null");
    }

    public void finish()
            throws IOException {
        printRows(ImmutableList.<Object>of(), true);
        writer.append(format("(%s row%s)%n", rowCount, (rowCount != 1) ? "s" : ""));
        writer.flush();
    }

    public void printRows(List<? extends Object> rows, boolean complete)
            throws IOException {
        rowCount += rows.size();
        int columns = fieldNames.size();

        int[] maxWidth = new int[columns];
        for (int i = 0; i < columns; i++) {
            maxWidth[i] = max(1, fieldNames.get(i).getDescription().length());
        }
        for (Object object : rows) {
            for (int i = 0; i < fieldNames.size(); i++) {
                
                final String fieldName = fieldNames.get(i).getName();
                String fieldValue = "";
               
                try {
                    fieldValue = BeanUtils.getNestedProperty(object, fieldName);
                } catch (Exception e) {
                }

                final String s = formatValue(fieldValue);
                maxWidth[i] = max(maxWidth[i], maxLineLength(s));
            }
        }

        if (!headerOutput) {
            headerOutput = true;

            for (int i = 0; i < columns; i++) {
                if (i > 0) {
                    writer.append('|');
                }
                final String name = fieldNames.get(i).getDescription();
                writer.append(center(name, maxWidth[i], 1));
            }
            writer.append('\n');

            for (int i = 0; i < columns; i++) {
                if (i > 0) {
                    writer.append('+');
                }
                writer.append(repeat("-", maxWidth[i] + 2));
            }
            writer.append('\n');
        }

        for (Object object : rows) {
            final List<List<String>> columnLines = new ArrayList<List<String>>(columns);
            int maxLines = 1;
            for (int i = 0; i < columns; i++) {

                final String fieldName = fieldNames.get(i).getName();
                String fieldValue = "";
                
                try {
                    fieldValue = BeanUtils.getNestedProperty(object, fieldName);
                } catch (Exception e) {
                }

                final String s = formatValue(fieldValue);
                final ImmutableList<String> lines = ImmutableList.copyOf(LINE_SPLITTER.split(s));
                columnLines.add(lines);
                maxLines = max(maxLines, lines.size());
            }

            for (int line = 0; line < maxLines; line++) {
                for (int column = 0; column < columns; column++) {
                    if (column > 0) {
                        writer.append('|');
                    }
                    final List<String> lines = columnLines.get(column);
                    final String s = (line < lines.size()) ? lines.get(line) : "";

                    final String fieldName = fieldNames.get(column).getName();
                    String fieldValue = "";
                    
                    try {
                        fieldValue = BeanUtils.getNestedProperty(object, fieldName);
                    } catch (Exception e) {
                    }

                    boolean numeric = false;
                    try {
                        Double.parseDouble(fieldValue);
                        numeric = true;
                    } catch (NumberFormatException e) {
                    }

                    String out = align(s, maxWidth[column], 1, numeric);
                    if ((!complete || (rowCount > 1)) && ((line + 1) < lines.size())) {
                        out = out.substring(0, out.length() - 1) + "+";
                    }
                    writer.append(out);
                }
                writer.append('\n');
            }
        }

        writer.flush();
    }

    static String formatValue(Object o) {
        return (o == null) ? "NULL" : o.toString();
    }

    private static String center(String s, int maxWidth, int padding) {
        final AnsiString ansiString = new AnsiString(s);

        checkState(ansiString.length() <= maxWidth, "string length is greater than max width");
        int left = (maxWidth - ansiString.length()) / 2;
        int right = maxWidth - (left + ansiString.length());
        return repeat(" ", left + padding) + s + repeat(" ", right + padding);
    }

    private static String align(String s, int maxWidth, int padding, boolean right) {
        final AnsiString ansiString = new AnsiString(s);
        checkState(ansiString.length() <= maxWidth, "string length is greater than max width");
        final String large = repeat(" ", (maxWidth - ansiString.length()) + padding);
        final String small = repeat(" ", padding);
        return right ? (large + s + small) : (small + s + large);
    }

    static int maxLineLength(String s) {
        int n = 0;
        for (String line : LINE_SPLITTER.split(s)) {
            n = max(n, new AnsiString(line).length());
        }
        return n;
    }
}
