package stni.languager;

import java.io.File;
import java.io.IOException;
import java.util.*;

import stni.languager.crawl.CrawlPattern;
import stni.languager.crawl.FileCrawler;
import stni.languager.crawl.FindRegexAction;
import stni.languager.crawl.FindResult;

/**
 *
 */
public class KeyExtractor {
    private static final Comparator<FindResult> FIND_RESULT_SORTER = new Comparator<FindResult>() {
        public int compare(FindResult result1, FindResult result2) {
            int res = result1.getSource().compareTo(result2.getSource());
            if (res == 0) {
                res = result1.getLine() - result2.getLine();
            }
            return res;
        }
    };

    public static class FindResultPair {
        private final FindResult result1;
        private final FindResult result2;

        FindResultPair(FindResult result1, FindResult result2) {
            this.result1 = result1;
            this.result2 = result2;
        }

        public FindResult getResult1() {
            return result1;
        }

        public FindResult getResult2() {
            return result2;
        }
    }

    private final SortedMap<String, Message> messages = new TreeMap<String, Message>();
    private final Map<String, FindResult> negatives = new HashMap<String, FindResult>();
    private final Map<String, FindResult> resultsByKey = new HashMap<String, FindResult>();
    private final Map<String, FindResult> resultsByValue = new HashMap<String, FindResult>();
    private final List<FindResultPair> sameKeyResults = new ArrayList<FindResultPair>();
    private final List<FindResultPair> sameValueResults = new ArrayList<FindResultPair>();
    private boolean cleanedNegatives = true;

    public void extractFromFiles(CrawlPattern crawlPattern, String regex, EnumSet<FindRegexAction.Flag> flags) throws IOException {
        cleanedNegatives = false;
        FileCrawler crawler = createCrawler(crawlPattern);
        for (FindResult result : crawler.crawl(new FindRegexAction(regex, flags)).getResults()) {
            checkSameKey(result);
            checkSameValue(result);
            messages.put(keyOf(result), new Message(keyOf(result), true, valueOf(result)));
        }
    }

    public void extractNegativesFromFiles(CrawlPattern crawlPattern, String regex, EnumSet<FindRegexAction.Flag> flags) throws IOException {
        cleanedNegatives = false;
        FileCrawler crawler = createCrawler(crawlPattern);
        for (FindResult result : crawler.crawl(new FindRegexAction(regex, flags)).getResults()) {
            negatives.put(keyOf(result), result);
        }
    }

    protected FileCrawler createCrawler(CrawlPattern crawlPattern) {
        return new FileCrawler(crawlPattern);
    }

    private void checkSameKey(FindResult result) {
        String value = valueOf(result);
        String key = keyOf(result);
        final FindResult sameKey = resultsByKey.get(key);
        if (sameKey != null && !nullSafeEquals(value, valueOf(sameKey))) {
            sameKeyResults.add(new FindResultPair(sameKey, result));
        }
        resultsByKey.put(key, result);
    }

    private void checkSameValue(FindResult result) {
        String value = valueOf(result);
        String key = keyOf(result);
        final FindResult sameValue = resultsByValue.get(value);
        if (sameValue != null && !key.equals(keyOf(sameValue))) {
            sameValueResults.add(new FindResultPair(sameValue, result));
        }
        resultsByValue.put(value, result);
    }

    public List<FindResultPair> getSameKeyResults() {
        return sameKeyResults;
    }

    public List<FindResultPair> getSameValueResults() {
        return sameValueResults;
    }

    public Collection<FindResult> getNegatives() {
        cleanNegatives();
        final ArrayList<FindResult> findResults = new ArrayList<FindResult>(negatives.values());
        Collections.sort(findResults, FIND_RESULT_SORTER);
        return findResults;
    }

    private void cleanNegatives() {
        if (!cleanedNegatives) {
            cleanedNegatives = true;
            for (Message message : messages.values()) {
                negatives.remove(message.getDefaultValue());
            }
        }
    }

    public SortedMap<String, Message> getMessages() {
        return messages;
    }

    public String location(FindResult result) {
        return result.getSource() + ":" + result.getLine() + ":" + result.getColumn();
    }

    public String valueOf(FindResult result) {
        return result.getFindings().size() > 1 ? result.getFindings().get(1) : null;
    }

    public String keyOf(FindResult result) {
        return result.getFindings().get(0);
    }

    private boolean nullSafeEquals(String a, String b) {
        return a == b || (a != null && a.equals(b));
    }

    public void extractFromClasspath(List<String> propertyLocations) throws IOException {
        PropertiesFinder finder = new PropertiesFinder();
        for (String propertyLocation : propertyLocations) {
            finder.addPropertyLocation(propertyLocation);
        }
        messages.putAll(finder.findProperties());
    }


    public void removeNewlines() {
        for (Map.Entry<String, Message> message : messages.entrySet()) {
            messages.put(message.getKey(), message.getValue().transformed(new NewlineRemover()));
        }
    }

    public void writeCsv(File file, String encoding, char separator) throws IOException {
        file.getParentFile().mkdirs();

        MessagesWriter writer = new MessagesWriter(encoding, separator);
        writer.write(file, messages);
    }
}