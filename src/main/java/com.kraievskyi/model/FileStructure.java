package com.kraievskyi.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class FileStructure {
    private String name;
    private boolean isDirectory;
    private long size;
    private FileStructure[] children;
}
