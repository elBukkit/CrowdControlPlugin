package com.elbukkit.plugins.crowd.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * File utilities
 * 
 * @author WinSock
 * @version 1.0
 */
public class FileUtils {
    /**
     * Copies source file to destination file.
     * 
     * @param src Source file
     * @param dst Destination file
     * @throws IOException If it cannot make the file
     */
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
