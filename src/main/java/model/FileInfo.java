package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.File;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class FileInfo {
    @NonNull
    private String name;

    @NonNull
    private String parentPath;

    @NonNull
    private boolean isFolder;

    private String hash;

    public FileInfo(@NonNull String name, @NonNull String parentPath, @NonNull boolean isFolder, String hash) {
        this.name = name;
        this.parentPath = parentPath;
        this.isFolder = isFolder;
        this.hash = hash;
    }

    public String getAbsolutePath() {
        return this.parentPath + File.separator + this.name;
    }
}
