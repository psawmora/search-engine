package se;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * <p>
 * <code>FileLoader</code> - Reads a specific folder and reads all the files with a specific name format.
 * The main functionalities are identifying newly added files dynamically and automatically and then build creating
 * FileContentReader objects for each file.
 * </p>
 *
 * @author: prabath
 */
public class FileLoader {

    private String baseFolder = "/home/prabath/Ebooks-And-Docs/Msc-Moratuwa-CSE/semester-3/performance-eng/lab-4/data_files";

    private String fileNamePattern = ".*url.*";

    private Pattern pattern;

    private AtomicLong lastUpdatedTime;

    public void init() {
        lastUpdatedTime = new AtomicLong();
        pattern = Pattern.compile(fileNamePattern);
        Path folder = FileSystems.getDefault().getPath(baseFolder);
        if (!Files.exists(folder)) {
            System.out.println("The data directory \n [" + baseFolder + "] \n does not exist. Exiting the system.");
            return;
        }
    }

    public List<File> getUpdatedFileList() {
        List<File> dataFiles = new ArrayList<>();
        Path baseFolderPath = FileSystems.getDefault().getPath(baseFolder);
        File folder = baseFolderPath.toFile();
        long lastUpdatedTime = this.lastUpdatedTime.get();
        try {
            for (File file : folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains("url");
                }
            })) {
                if (file.isFile() && file.lastModified() > lastUpdatedTime) {
                    dataFiles.add(file);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in loading files. " + e);
        }
        return dataFiles;
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
    }
}
