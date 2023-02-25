package com.kraievskyi.service.impl;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.kraievskyi.model.FileStructure;
import com.kraievskyi.service.FileService;

public class FileServiceImplTest {

    private FileService fileService;
    private FileStructure fileStructure;
    private static final String FILE_NAME = "src/main/resources/example.txt";

    @BeforeEach
    public void setUp() {
        fileService = new FileServiceImpl();
        fileStructure = fileService.deserialize(FILE_NAME);
    }

    @Test
    public void testDeserialize() {
        assertNotNull(fileStructure);
        assertEquals("root", fileStructure.getName());
        assertTrue(fileStructure.isDirectory());
        assertEquals(36923799, fileStructure.getSize());
        assertNotNull(fileStructure.getChildren());
        assertEquals(4, fileStructure.getChildren().length);
    }

    @Test
    public void testGetDirectorySize() {
        assertEquals(36923799, fileService.getDirectorySize("root", fileStructure));
        assertEquals(0, fileService.getDirectorySize("root/dir_mn/dir_bb/dir_bb", fileStructure));
        assertThrows(IllegalArgumentException.class, () -> fileService.getDirectorySize("file1.txt", fileStructure));
        assertThrows(IllegalArgumentException.class, () -> fileService.getDirectorySize("dir1/not-a-real-path", fileStructure));
    }

    @Test
    public void testGetFileDuplicates() {
        List<String> duplicates = fileService.getFileDuplicates(fileStructure);
        assertNotNull(duplicates);
        assertEquals(4, duplicates.size());
        assertEquals("root/dir_aa/file_tq", duplicates.get(0));
    }
}
