package com.incubyte.parabank.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {
    private FileUtil() {
    }

    public static Path createDirectory(Path directory) {
        try {
            return Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new FrameworkException("Unable to create directory: " + directory, exception);
        }
    }

    public static Path writeTextFile(Path path, String content) {
        try {
            createDirectory(path.getParent());
            return Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new FrameworkException("Unable to write file: " + path, exception);
        }
    }
}
