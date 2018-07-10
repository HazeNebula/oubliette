package nl.hazenebula.oubliette;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectFileSearcher {
    public static final String FIELDOBJECT_DIR = "/field-objects/";
    public static final String WALLOBJECT_DIR = "/walls/";

    public static List<List<String>> getIndexedPaths(String dir) {
        List<Path> files;
        try {
            URI uri = ObjectFileSearcher.class.getResource(dir).toURI();
            Path path;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem;
                try {
                    fileSystem = FileSystems.newFileSystem(uri,
                            Collections.emptyMap());
                } catch (FileSystemAlreadyExistsException e) {
                    fileSystem = FileSystems.getFileSystem(uri);
                }

                path = fileSystem.getPath(dir);
            } else {
                path = Paths.get(uri);
            }

            Stream<Path> walk = Files.walk(path, 1);
            files = walk.collect(Collectors.toList());
        } catch (URISyntaxException | IOException e) {
            return null;
        }

        List<List<String>> pathsByType = new ArrayList<>();

        while (!files.isEmpty()) {
            Path newFile = files.remove(files.size() - 1);
            String filename = newFile.toString();
            if (filename.charAt(filename.length() - 1) != '/') {
                int lastHyphenIndex = filename.lastIndexOf('-');

                if (lastHyphenIndex > -1) {
                    List<String> images = new LinkedList<>();
                    images.add(filename);
                    String objectType = filename.substring(0, lastHyphenIndex);

                    for (int i = files.size() - 1; i >= 0; --i) {
                        if (files.get(i).toString().startsWith(objectType)) {
                            images.add(files.remove(i).toString());
                        }
                    }

                    pathsByType.add(images);
                }
            }
        }

        return pathsByType;
    }

    public static List<List<File>> getObjects(String dirPath) {
        List<File> files = new ArrayList<>();
        File dir = new File(dirPath);
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                files.add(file);
            }
        }

        List<List<File>> objectsByType = new ArrayList<>();

        while (!files.isEmpty()) {
            File newFile = files.remove(files.size() - 1);
            String newFileName = newFile.getName();
            int lastHyphenIndex = newFileName.lastIndexOf('-');

            if (lastHyphenIndex > -1) {
                List<File> images = new ArrayList<>();
                images.add(newFile);
                String objectType = newFileName.substring(0, lastHyphenIndex);

                for (int i = files.size() - 1; i >= 0; --i) {
                    if (files.get(i).getName().startsWith(objectType)) {
                        images.add(files.remove(i));
                    }
                }

                objectsByType.add(images);
            }
        }

        return objectsByType;
    }
}
