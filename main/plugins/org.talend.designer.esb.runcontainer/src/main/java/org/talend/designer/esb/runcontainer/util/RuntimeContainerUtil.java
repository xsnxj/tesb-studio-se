package org.talend.designer.esb.runcontainer.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class RuntimeContainerUtil {

    private static final List<String> CONTAINER_FILES;

    static {
        final List<String> containerFiles = new ArrayList<>();

        containerFiles.add("/bin/trun");
        containerFiles.add("/bin/setenv");
        containerFiles.add("/etc");
        containerFiles.add("/system/org/apache/karaf");
        containerFiles.add("/system/org/talend/esb");
        containerFiles.add("/lib/boot");
        // containerFiles.add("/version.txt");

        CONTAINER_FILES = Collections.unmodifiableList(containerFiles);
    }

    private RuntimeContainerUtil() {
    }

    public static boolean isValidLocation(String rtHome) {
        if (rtHome == null) {
            throw new IllegalArgumentException("rtHome cannot be null");
        }
        // validate, 1st version, 2nd etc
        rtHome = rtHome.trim();
        if (rtHome.isEmpty()) {
            return false;
        }

        File rtDir = new File(rtHome);
        // find version.txt, 1st in root, second check in container folder
        if (!rtDir.isDirectory()) {
            return false;
        }

        File version = new File(rtHome + "/version.txt");
        String ver = "";
        try {
            if (version.exists()) {
                ver = Files.readAllLines(version.toPath()).get(0);
            } else {
                version = new File(rtHome + "/container/version.txt");
                if (version.exists()) {
                    ver = Files.readAllLines(version.toPath()).get(0);
                    rtHome += "/container/";
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (ver.isEmpty()) {
            return false;
        }

        for (String f : CONTAINER_FILES) {
            File resFile = new File(rtHome + f);
            if (!resFile.exists()) {
                return false;
            }
        }

        return true;
    }

    public static void copyContainer(String from, String to) throws IOException {
        Path fromPath = Paths.get(from);
        Path toPath = Paths.get(to);
        Path bakPath = null;
        
        if (Files.exists(toPath)) {
            bakPath = toPath.getParent().resolve(toPath.getFileName().toString() + ".bak");
            deleteDir(bakPath);
            Files.move(toPath, bakPath, StandardCopyOption.REPLACE_EXISTING);
        }

        Files.createDirectories(toPath);

        try {
            copyDir(fromPath, toPath);
        } catch (IOException e) {
            deleteDir(toPath);
            if (bakPath != null) {
                Files.move(bakPath, toPath, StandardCopyOption.REPLACE_EXISTING);
            }
            throw e;
        } finally {
            if (bakPath != null) {
                deleteDir(bakPath);
            }
        }
    }

    private static void deleteDir(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw e;
                }
            }
        });
    }

    private static void copyDir(Path from, Path to) throws IOException {
        Files.walkFileTree(from, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetdir = to.resolve(from.relativize(dir));
                try {
                    Files.copy(dir, targetdir);
                } catch (FileAlreadyExistsException e) {
                    if (!Files.isDirectory(targetdir))
                        throw e;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, to.resolve(from.relativize(file)));
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
