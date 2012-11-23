package stni.languager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by IntelliJ IDEA.
 * User: nidi
 * Date: 09.03.12
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
 */
class Util {
    final static String UTF8 = "utf-8";
    final static String ISO = "iso-8859-1";

    static BufferedReader reader(File file, String encoding) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
    }

    static BufferedWriter writer(File file, String encoding) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
    }

    static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }
}