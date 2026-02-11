package afs.student.util;

public final class AppConfig {
    private AppConfig() {}

    // Important: read/write from project folder: data/...
    public static final String DATA_DIR = "data";

    // for split (regex)
    public static final String SPLIT_DELIM = "\\|";

    // for writing
    public static final String WRITE_DELIM = "|";
}
