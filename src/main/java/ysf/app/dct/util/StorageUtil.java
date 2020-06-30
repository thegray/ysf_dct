package ysf.app.dct.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class StorageUtil {

    private final String UPLOADS_FOLDER = "uploads";
    private final String RESULTS_FOLDER = "results";

    private final String UPLOADS_PATH;
    private final String RESULTS_PATH;

    @Autowired
    public StorageUtil() {
        String curPath = Paths.get("").toAbsolutePath().toString();
        this.UPLOADS_PATH = curPath + "/" + UPLOADS_FOLDER;
        this.RESULTS_PATH = curPath + "/" + RESULTS_FOLDER;
    }

    public String getUploadsPath() {
        return UPLOADS_PATH;
    }

    public String getResultsPath() {
        return RESULTS_PATH;
    }
}
