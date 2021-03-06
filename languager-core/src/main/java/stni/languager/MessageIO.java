package stni.languager;

import java.util.Arrays;
import java.util.List;

import static stni.languager.Message.Status.NOT_FOUND;
import static stni.languager.Message.Status.ofSymbol;

/**
 *
 */
public class MessageIO {
    private MessageIO() {
    }

    static final int KEY_COLUMN = 0;
    static final int STATUS_COLUMN = 1;
    static final int OCCURRENCE_COLUMN = 2;
    static final int DEFAULT_COLUMN = 3;
    static final int FIRST_LANG_COLUMN = 4;
    static final List<String> MINIMAL_FIRST_LINE = Arrays.asList("key", "status", "occurs", "default value");

    static Message.Status statusOfLine(List<String> line) {
        return line.get(STATUS_COLUMN).length() == 0 ? NOT_FOUND : ofSymbol(line.get(STATUS_COLUMN).charAt(0));
    }
}
