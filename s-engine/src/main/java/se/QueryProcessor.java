package se;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

    public void init() {
        String path = INDEX_FILE_BASE_PATH + INDEX_FILE_BASE_NAME;
        if (indexFile != null && !indexFile.isEmpty()) {
            path = indexFile;
        }
        indexFilePath = FileSystems.getDefault().getPath(path);
    }

    public void getUrlsForQuery(File queryFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(queryFile))) {
            boolean isFileEnd = false;
            while (reader.ready() && !isFileEnd) {
                String query = reader.readLine();
                if (query == null) {
                    isFileEnd = true;
                } else if (!query.isEmpty()) {
                    getUrlsForQuery(query);
                }
            }

        } catch (Exception e) {
            System.out.println("Error in reading file content. " + e);
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
    public Map<String, Integer> getUrlsForQuery(String query) {
        Map<String, String> indexResults = readIndexFile(indexFilePath);
        System.out.println("------------ Start - " + query + " -------------");
        Map<String, Integer> resultMap = searchForQuery(indexResults, query);
        System.out.println("------------ End - " + query + " -------------");
        return resultMap;
    }

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

    private Map<String, String> readIndexFile(Path indexFilePath) {
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

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }
}
