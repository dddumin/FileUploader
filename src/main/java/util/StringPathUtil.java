package util;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class StringPathUtil {
    public static String getParentPathWithoutServerRoot(String absolutePath) {
        if (absolutePath.equals(Constants.SERVER_ROOT_DIR)) {
            return "";
        } else {
            return absolutePath.replace(Constants.SERVER_ROOT_DIR + File.separator, "");
        }
    }

    public static String getFileNameWithoutExtension(String fileName) {
        String[] split = fileName.split("\\.");
        return String.join(".", Arrays.copyOf(split, split.length - 1));
    }

    public static String getFileExtension(String fileName) {
        String[] nameSplit = fileName.split("\\.");
        return "." + nameSplit[nameSplit.length - 1];
    }
}
