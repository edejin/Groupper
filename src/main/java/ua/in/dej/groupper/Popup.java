package ua.in.dej.groupper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.IPopupChooserBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Popup extends AnAction {
    public void actionPerformed(AnActionEvent event) {
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        Project p = event.getData(PlatformDataKeys.PROJECT);

        VariantsResponce variant = VariantManager.getVariants(file);
        if (variant == null) {
            return;
        }
        List<String> variants = new ArrayList<String>(variant.variants);
        IPopupChooserBuilder<String> popupBuilder = JBPopupFactory.getInstance().createPopupChooserBuilder(variants);
        popupBuilder.setItemChosenCallback(t -> {
                    String aaa = variant.pathPart + t.toString();

                    FileEditorManager manager = FileEditorManager.getInstance(p);

                    manager.closeFile(variant.file);

                    File f = new File(aaa);
                    VirtualFile newFile = LocalFileSystem.getInstance().findFileByIoFile(f);

                    if (newFile != null) {
                        manager.openFile(newFile, true);
                    }
        });
        JBPopup popup = popupBuilder.createPopup();
        popup.showInBestPositionFor(event.getDataContext());
//        popup.addListener(new JBPopupListener() {
//            @Override
//            public void beforeShown(LightweightWindowEvent lightweightWindowEvent) {
//
//            }
//
//            @Override
//            public void onClosed(@NotNull LightweightWindowEvent lightweightWindowEvent) {
//                if (lightweightWindowEvent.isOk()) {
//                    String aaa = variant.pathPart + variants.getSelectedValue();
//
//                    FileEditorManager manager = FileEditorManager.getInstance(p);
//
//                    manager.closeFile(variant.file);
//
//                    File f = new File(aaa);
//                    VirtualFile newFile = LocalFileSystem.getInstance().findFileByIoFile(f);
//
//                    if (newFile != null) {
//                        manager.openFile(newFile, true);
//                    }
//                }
//            }
//        });
    }
}
