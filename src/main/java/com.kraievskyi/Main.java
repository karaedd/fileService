package com.kraievskyi;

import com.kraievskyi.model.FileStructure;
import com.kraievskyi.service.impl.FileServiceImpl;

public class Main {
    public static void main(String[] args) {
        FileServiceImpl fileService = new FileServiceImpl();
        FileStructure deserialize = fileService.deserialize(
                "src/main/resources/atola-file-folder-tree.txt");
        System.out.println(fileService.getDirectorySize("root/dir_kz", deserialize));
        System.out.println(fileService.getFileDuplicates(deserialize));
    }
}
