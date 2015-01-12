package se;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * <code>IndexBuilder</code> - Builds and updates the index files periodically. At the moment, there would be only one index
 * file. But later the model would be extended to use multiple index files based on the hash or any other similar grouping
 * method.
 * </p>
 *
 * @author: prabath
 */
public class IndexBuilder {

    private static final String INDEX_FILE_BASE_NAME = "se_index.txt";

    private static final String INDEX_FILE_BASE_PATH =
            "/home/prabath/Ebooks-And-Docs/Msc-Moratuwa-CSE/semester-3/performance-eng/lab-4/";

    //    private String stopWords = "\\b(the|is|at|which|and|i\\.e\\.|in|a|that|to|or|of)\\b";
    private String stopWords =
            "(\\b(the|is|at|which|and|i\\.e\\.|in|a|that|to|or|of|&|\\.|>|<|\\?|#|@|~|!|\"|\\$|%|\\*|:)\\b)|(&|\\" +
                    ".|>|<|\\?|#|@|~|!|\"|\\$|%|\\*|,|\\||:$)";

    private FileLoader fileLoader;

    private Path indexFilePath;

    private String indexFile;

    public void init() {
        String path = INDEX_FILE_BASE_PATH + INDEX_FILE_BASE_NAME;
        try {
            if (indexFile != null && !indexFile.isEmpty()) {
                path = indexFile;
            }
            indexFilePath = FileSystems.getDefault().getPath(path);
            indexFilePath = Files.createFile(indexFilePath);
        } catch (IOException e) {

        }
    }

    public void buildIndex() throws FileNotFoundException {
        // Get new data files if there's any.
        // If new data files are available, mark the index file as being processed.
        // Read the words.
        List<File> updatedFileList = fileLoader.getUpdatedFileList();
        if (updatedFileList.size() > 0) {
            Map<String, String> indexLines = readIndexFile(indexFilePath);
            System.out.println(updatedFileList.size());
            for (File file : updatedFileList) {
                readFileWords(file, indexLines);
            }
            writeBackToIndexFile(indexFilePath, indexLines);
        }
    }

    private void writeBackToIndexFile(Path indexFilePath, Map<String, String> indexLines) {
        try (FileWriter writer = new FileWriter(indexFilePath.toString(), false)) {
            for (Map.Entry<String, String> entry : indexLines.entrySet()) {
                writer.write(entry.getKey() + ">>" + entry.getValue() + "\n");
            }
            writer.flush();
        } catch (Exception e) {
            System.out.println("Error in writing to index file. " + e);
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

    private void readFileWords(File file, Map<String, String> indexLines) throws FileNotFoundException {
        System.out.println("File name : " + file.getName());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String url = reader.readLine();
            boolean isFileEnd = false;
            while (reader.ready() && !isFileEnd) {
                byte[] lineBytes = reader.readLine().getBytes();
                if (lineBytes == null) isFileEnd = true;
                processLine(url, lineBytes, indexLines);
            }

        } catch (Exception e) {
            System.out.println("Error in reading file content. " + e);
        }
    }

    private void processLine(String url, byte[] lineBytes, Map<String, String> indexLines) {
        String line = new String(lineBytes).trim().replaceAll(stopWords, "");
        String[] words = line.split("\\s");
        for (String word : words) {
            if (!word.isEmpty()) {
                processWord(url, word, indexLines);
            }
        }
    }

    private void processWord(String url, String word, Map<String, String> indexLines) {
        if (indexLines.containsKey(word)) {
            StringBuffer content = new StringBuffer(indexLines.get(word));
            if (!content.toString().contains(url)) {
                content.append(",").append(url);
                indexLines.put(word, content.toString());
            }
        } else {
            indexLines.put(word, url);
        }
    }

    public void setFileLoader(FileLoader fileLoader) {
        this.fileLoader = fileLoader;
    }

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }
}
