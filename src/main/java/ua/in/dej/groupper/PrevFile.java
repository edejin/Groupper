package ua.in.dej.groupper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

public class PrevFile extends AnAction {
    public void actionPerformed(AnActionEvent event) {
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        Project p = event.getData(PlatformDataKeys.PROJECT);

        VariantsResponce variant = VariantManager.getVariants(file);
        if (variant == null) {
            return;
        }

        int prevIndex = (variant.currentIndex - 1 + variant.variants.size()) % variant.variants.size();

        String aaa = variant.pathPart + variant.variants.get(prevIndex);

        FileEditorManager manager = FileEditorManager.getInstance(p);

        manager.closeFile(variant.file);

        File f = new File(aaa);
        VirtualFile newFile = LocalFileSystem.getInstance().findFileByIoFile(f);

        if (newFile != null) {
            manager.openFile(newFile, true);
        }
    }
}
