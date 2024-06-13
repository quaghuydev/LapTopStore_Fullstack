package vn.id.quanghuydevfs.drcomputer.util.upload.image;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloudinaryUtils {
    private static final Pattern PUBLIC_ID_PATTERN = Pattern.compile("/(.*?)(\\.\\w+)");

    public static String extractPublicIdFromUrl(String url) {
        Matcher matcher = PUBLIC_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
