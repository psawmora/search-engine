package se;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

/**
 * <p>
 * <code>QueryProcessor</code> - Process queries from a given source file against a given index file and outputs the results.
 * </p>
 *
 * @author: prabath
 */
public class QueryProcessor {

    private static final String INDEX_FILE_BASE_NAME = "se_index.txt";

    private static final String INDEX_FILE_BASE_PATH =
            "/home/prabath/Ebooks-And-Docs/Msc-Moratuwa-CSE/semester-3/performance-eng/lab-4/";

    private String indexFile;

    private Path indexFilePath;

    private String answerFilePath;

    public void init() {
        String path = INDEX_FILE_BASE_PATH + INDEX_FILE_BASE_NAME;
        if (indexFile != null && !indexFile.isEmpty()) {
            path = indexFile;
        }
        indexFilePath = FileSystems.getDefault().getPath(path);
    }

    public void getUrlsForQuery(File queryFile) {
        long tStart = System.currentTimeMillis();
        Map<String, String> indexResults = readIndexFile(indexFilePath);
        Map<String, Map<String, Integer>> queryResults = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(queryFile))) {
            boolean isFileEnd = false;
            while (reader.ready() && !isFileEnd) {
                String query = reader.readLine();
                if (query == null) {
                    isFileEnd = true;
                } else if (!query.isEmpty()) {
                    Map<String, Integer> urlsForQuery = getUrlsForQuery(query, indexResults);
                    queryResults.put(query, urlsForQuery);
                }
            }
            writeToAnswerFile(FileSystems.getDefault().getPath(answerFilePath), queryResults);
        } catch (Exception e) {
            System.out.println("Error in reading file content. " + e);
        } finally {
            System.out.println("Time take for querying(ms) -> " + (System.currentTimeMillis() - tStart));
        }
    }

    /**
     * <p>
     * Used to run single queries.
     * </p>
     *
     * @param query
     * @return
     */
    private Map<String, Integer> getUrlsForQuery(String query, Map<String, String> indexResults) {
        if (indexResults == null) {
            indexResults = readIndexFile(indexFilePath);
        }
        System.out.println("------------ Start - " + query + " -------------");
        Map<String, Integer> resultMap = searchForQuery(indexResults, query);
        System.out.println("------------ End - " + query + " -------------");
        return resultMap;
    }

    /**
     * <p>
     * Writes the query results to the answer file.
     * </p>
     *
     * @param indexFilePath
     * @param queryResults
     */
    private void writeToAnswerFile(Path indexFilePath, Map<String, Map<String, Integer>> queryResults) {
        try (FileWriter writer = new FileWriter(indexFilePath.toString(), false)) {
            for (Map.Entry<String, Map<String, Integer>> resultEntry : queryResults.entrySet()) {

                writer.write("------------ Start - " + resultEntry.getKey() + " -------------");
                for (Map.Entry<String, Integer> entry : resultEntry.getValue().entrySet()) {
                    writer.write(entry.getKey() + " : Priority - " + entry.getValue() + "\n");
                }
                writer.write("\n\n");
            }
            writer.flush();
        } catch (Exception e) {
            System.out.println("Error in writing to index file. " + e);
        }

    }

    /**
     * <p>
     * Search for a particular query and return the results.
     * </p>
     *
     * @param indexResults
     * @param query
     * @return
     */
    private Map<String, Integer> searchForQuery(Map<String, String> indexResults, String query) {
        String[] split = query.split(" ");
        List<String> contests = new ArrayList<>();
        if (split.length < 0) return null;
        for (String queryPart : split) {
            fillWithContestents(indexResults, queryPart, contests);
        }
        Map<String, Integer> resultMap = new HashMap<>();
        for (String value : contests) {
            int frequency = Collections.frequency(contests, value);
            resultMap.put(value, frequency);
        }

        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        return resultMap;
    }

    private void fillWithContestents(Map<String, String> indexResults, String queryPart, List<String> contests) {
        for (Map.Entry<String, String> entry : indexResults.entrySet()) {
            if (entry.getKey().equals(queryPart)) {
                String value = entry.getValue();
                String[] split = value.split(",");
                for (String splitPart : split) {
                    contests.add(splitPart.trim());
                }
            }
        }
    }

/*
    private Map<String, String> readIndexFile2(Path indexFilePath) {
        Map<String, String> indexLines = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(indexFilePath.toUri())))) {
            boolean isFileEnd = false;
            while (reader.ready() && !isFileEnd) {
                String line = reader.readLine();
                if (line == null) {
                    isFileEnd = true;
                } else if (!line.isEmpty()) {
                    String[] split = line.split(">>");
                    if (split.length > 1) indexLines.put(split[0], split[1]);
                }
            }

        } catch (Exception e) {
            System.out.println("Error in reading file content. " + e);
        }
        return indexLines;
    }
*/

    private Map<String, String> readIndexFile(Path indexFilePath) {
        Map<String, String> indexLines = new HashMap<>();
        final URI path = indexFilePath.toUri();
        try (BufferedReader reader = new BufferedReader(new RandomFileReader(new File(path)))) {
            boolean isFileEnd = false;
            while (reader.ready() && !isFileEnd) {
                String line = reader.readLine();
                if (line == null) {
                    isFileEnd = true;
                } else if (!line.isEmpty()) {
                    String[] split = line.split(">>");
                    if (split.length > 1) indexLines.put(split[0], split[1]);
                }
            }

        } catch (Exception e) {
            System.out.println("Error in reading file content. " + e);
        }
        return indexLines;
    }


    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }

    public void setAnswerFilePath(String answerFilePath) {
        this.answerFilePath = answerFilePath;
    }

    /**
     * <p>
     * Custom defined FileReader with RandomAccessFile as the underline file stream.
     * </p>
     */
    private static class RandomFileReader extends FileReader {

        RandomAccessFile randomAccessFile;

        public RandomFileReader(File file) throws FileNotFoundException {
            super(file);
            randomAccessFile = new RandomAccessFile(file, "r");
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            byte[] buff = new byte[cbuf.length];
            int nRead = randomAccessFile.read(buff, off, len);
            for (int i = 0; i < nRead; i++) {
                cbuf[i] = (char) buff[i]; // The character encoding is assumed to be an 8-bit encoding format.
            }
            return nRead;
        }

        @Override
        public void close() throws IOException {
            try {
                randomAccessFile.close();
            } finally {

            }
        }
    }
}
