package com.zj.ccIm.core.sender.compress;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class Checker {
    private static final List<String> format = new ArrayList<>();
    private static final String JPG = "jpg";
    private static final String JPEG = "jpeg";
    private static final String PNG = "png";
    private static final String WEBP = "webp";
    private static final String GIF = "gif";
    private static final String BMP = "bmp";

    static {
        format.add(JPG);
        format.add(JPEG);
        format.add(PNG);
        format.add(WEBP);
        format.add(GIF);
        format.add(BMP);
    }

    static boolean isImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        String suffix = path.substring(path.lastIndexOf(".") + 1);
        return format.contains(suffix.toLowerCase());
    }

    static boolean isJPG(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        String suffix = path.substring(path.lastIndexOf(".")).toLowerCase();
        return suffix.contains(JPG) || suffix.contains(JPEG);
    }

    static String checkSuffix(String path) {
        if (TextUtils.isEmpty(path)) {
            return ".JPEG";
        }
        return path.substring(path.lastIndexOf("."));
    }

    static boolean isNeedCompress(int leastCompressSize, String path) {
        if (leastCompressSize > 0) {
            File source = new File(path);
            if (!source.exists()) {
                return false;
            }

            return source.length() > (leastCompressSize << 10);
        }
        return true;
    }
}
