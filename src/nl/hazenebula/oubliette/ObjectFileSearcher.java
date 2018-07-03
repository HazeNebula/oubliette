package nl.hazenebula.oubliette;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ObjectFileSearcher {
    public static final String FIELDOBJECT_DIR = ".\\field-objects\\";
    public static final String WALLOBJECT_DIR = ".\\wall-objects\\";

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
