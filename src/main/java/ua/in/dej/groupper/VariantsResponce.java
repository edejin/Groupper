package ua.in.dej.groupper;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

public class VariantsResponce {
    String pathPart;
    List<String> variants;
    VirtualFile file;
    int currentIndex;

    public VariantsResponce(String path, List<String> variants, VirtualFile file, int currentIndex) {
        this.pathPart = path;
        this.variants = variants;
        this.file = file;
        this.currentIndex = currentIndex;
    }
}
