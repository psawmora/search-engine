package se;

/**
 * @author: prabath
 */
public class Starter {

    public static void main(String[] args) {

        FileLoader fileLoader = new FileLoader();
        IndexBuilder indexBuilder = new IndexBuilder();
        System.out.println(" Format : java -jar <jar_name> <base_folder_path> <index_file_path>");
        if (args.length > 0) {
            fileLoader.setBaseFolder(args[0]);
        }
        if (args.length > 1) {
            indexBuilder.setIndexFile(args[1]);
        }
        indexBuilder.setFileLoader(fileLoader);
        try {
            fileLoader.init();
            indexBuilder.init();
            indexBuilder.buildIndex();
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
