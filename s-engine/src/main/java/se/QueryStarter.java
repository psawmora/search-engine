package se;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <p>
 * <code>QueryStarter</code> - Test query class
 * </p>
 *
 * @author: prabath
 */
public class QueryStarter {

    private static final String QUERY_FILE_PATH =
            "/home/prabath/Ebooks-And-Docs/Msc-Moratuwa-CSE/semester-3/performance-eng/lab-4/data_files/queries.txt";

    public static void main(String[] args) {
        System.out.println(" Format : java -jar <jar_name> <query_file_path> <index_file_path> <answer_file_path>");
        QueryProcessor queryProcessor = new QueryProcessor();
        String queryPath = QUERY_FILE_PATH;
        if (args.length > 0) {
            queryPath = args[0];
        }

        if (args.length > 1) {
            queryProcessor.setIndexFile(args[1]);
        }

        if (args.length > 2) {
            queryProcessor.setAnswerFilePath(args[2]);
        }

        Path indexFilePath = FileSystems.getDefault().getPath(queryPath);
        try {
            indexFilePath = Files.createFile(indexFilePath);
        } catch (IOException e) {

        }
        queryProcessor.init();
        queryProcessor.getUrlsForQuery(indexFilePath.toFile());
        //        queryProcessor.getUrlsForQuery("Random Number Generation");
    }
}
