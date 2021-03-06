package stni.languager.crawl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public abstract class AbstractContentReadingCrawlAction implements CrawlAction {

    private Integer[] newlines;

    public void action(File basedir, File file, CrawlPattern pattern) throws IOException {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(file), pattern.getEncoding());
            char[] ch = new char[(int) file.length()];
            int read = in.read(ch);
            String s = new String(ch, 0, read);
            findNewlines(s);
            doAction(basedir, file, s, pattern);
        } finally {
            Util.closeSilently(in);
        }
    }

    protected abstract void doAction(File basedir, File file, String content, CrawlPattern pattern) throws IOException;

    protected void findNewlines(String content) {
        List<Integer> newlineList = new ArrayList<Integer>();
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                newlineList.add(i);
            }
        }
        newlines = newlineList.toArray(new Integer[newlineList.size()]);
    }

    protected int lineOfPosition(int pos) {
        return -Arrays.binarySearch(newlines, pos);
    }

    protected int columnOfPosition(int pos) {
        final int line = lineOfPosition(pos);
        return line == 1 ? pos : pos - newlines[line - 2];
    }

    protected File target(File source, File sourceBaseDir, File targetDir) {
        String relativeSource = source.getParentFile().getAbsolutePath().substring(sourceBaseDir.getAbsolutePath().length());
        File target = new File(targetDir, relativeSource);
        target.mkdirs();
        return target;
    }

}
