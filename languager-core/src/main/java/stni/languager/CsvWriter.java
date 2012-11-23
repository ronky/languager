package stni.languager;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stni
 * Date: 17.09.12
 * Time: 12:16
 * To change this template use File | Settings | File Templates.
 */
class CsvWriter {
    private final Writer out;
    private final char separator;
    private boolean start;

    public CsvWriter(Writer out, char separator) {
        this.out = out;
        this.separator = separator;
        start = true;
    }

    public void writeField(String value) throws IOException {
        writeSeparator();
        if (value == null) {
            return;
        }
        if (value.contains("\"") || value.contains(",") || value.contains("\n") || value.contains("\r")) {
            out.write("\"" + value.replace("\"", "\"\"") + "\"");
        } else {
            out.write(value);
        }
    }


    public void writeLine(List<String> values) throws IOException {
        for (String value : values) {
            writeField(value);
        }
        writeEndOfLine();
    }

    public void writeEndOfLine() throws IOException {
        out.write("\r\n");
        start = true;
    }

    public void close() throws IOException {
        out.close();
    }

    private void writeSeparator() throws IOException {
        if (start) {
            start = false;
        } else {
            out.write(separator);
        }
    }
}