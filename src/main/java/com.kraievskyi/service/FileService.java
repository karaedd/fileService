package com.kraievskyi.service;

import com.kraievskyi.model.FileStructure;
import java.util.List;

public interface FileService {

    FileStructure deserialize(String filename);

    long getDirectorySize(String path, FileStructure fileStructure);

    List<String> getFileDuplicates(FileStructure fileStructure);
}
