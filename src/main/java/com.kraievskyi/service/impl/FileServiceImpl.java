package com.kraievskyi.service.impl;

import com.kraievskyi.model.FileStructure;
import com.kraievskyi.service.FileService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileServiceImpl implements FileService {

    @Override
    public FileStructure deserialize(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            String[] tokens = line.split("\\|");
            FileStructure root = new FileStructure(tokens[0], true,
                    0, new FileStructure[Integer.parseInt(tokens[1])]);
            deserializeHelper(root, reader);
            return root;
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize file: " + filename);
        }
    }

    private void deserializeHelper(FileStructure parent, BufferedReader reader) {
        long totalSize = 0;
        for (int i = 0; i < parent.getChildren().length; i++) {
            String line;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read line: " + e);
            }
            String[] tokens = line.split(":");
            if (tokens.length == 2) { // This is a file
                long fileSize = Long.parseLong(tokens[1]);
                parent.getChildren()[i] = new FileStructure(
                        tokens[0], false, fileSize, null);
                totalSize += fileSize;
            } else { // This is a directory
                tokens = line.split("\\|");
                int numChildren = Integer.parseInt(tokens[1]);
                FileStructure directory = new FileStructure(
                        tokens[0], true, 0, new FileStructure[numChildren]);
                parent.getChildren()[i] = directory;
                deserializeHelper(directory, reader);
                totalSize += directory.getSize();
            }
        }
        parent.setSize(totalSize);
    }

    @Override
    public long getDirectorySize(String path, FileStructure fileStructure) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (!path.contains("root")) {
            throw new IllegalArgumentException("Path must include root");
        }
        String[] pathComponents = path.split("/");
        FileStructure current = fileStructure;
        for (int i = 1; i < pathComponents.length; i++) {
            String component = pathComponents[i];
            FileStructure child = Arrays.stream(current.getChildren())
                    .filter(c -> c.getName().equals(component))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Path not found: " + path));
            if (i == pathComponents.length - 1) {
                if (!child.isDirectory()) {
                    throw new IllegalArgumentException("Path is not a directory: " + path);
                }
                return child.getSize();
            }
            if (!child.isDirectory()) {
                throw new IllegalArgumentException("Path not found: " + path);
            }
            current = child;
        }
        return current.getSize();
    }

    @Override
    public List<String> getFileDuplicates(FileStructure fileStructure) {
        Map<String, List<String>> filesMap = new HashMap<>();
        buildFilesMap(filesMap, "root", fileStructure);
        List<String> duplicates = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : filesMap.entrySet()) {
            List<String> paths = entry.getValue();
            if (paths.size() > 1) {
                duplicates.addAll(paths);
            }
        }
        return duplicates;
    }

    private void buildFilesMap(Map<String, List<String>> filesMap,
                               String path, FileStructure fileStructure) {
        for (FileStructure child : fileStructure.getChildren()) {
            StringBuilder childPath = new StringBuilder(path);
            childPath.append("/").append(child.getName());
            if (child.isDirectory()) {
                buildFilesMap(filesMap, String.valueOf(childPath), child);
            } else {
                StringBuilder key = new StringBuilder(child.getName());
                key.append(":").append(child.getSize());
                filesMap.putIfAbsent(String.valueOf(key), new ArrayList<>());
                filesMap.get(String.valueOf(key)).add(String.valueOf(childPath));
            }
        }
    }
}
