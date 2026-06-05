package com.toystore.util;

import javax.servlet.ServletContext;
import java.io.File;

public final class DataPaths {
    private static ServletContext servletContext;

    private DataPaths() {
    }

    public static void init(ServletContext context) {
        servletContext = context;
    }

    public static String get(String filename) {
        if (servletContext == null) {
            throw new IllegalStateException("DataPaths not initialized. Deploy the application via a servlet container.");
        }

        String path = servletContext.getRealPath("/WEB-INF/" + filename);
        if (path == null) {
            path = System.getProperty("java.io.tmpdir") + File.separator + "toystore" + File.separator + filename;
            File parent = new File(path).getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
        }
        return path;
    }
}
