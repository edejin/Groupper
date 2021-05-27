package ua.in.dej.groupper;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariantManager {

    private static String getCurrentSubPath(VirtualFile currentFile) {
        String parentPath = getParentPath(currentFile) + File.separator;
        if (PropertiesComponent.getInstance().getBoolean(SettingConfigurable.PREFIX_HIDE, false)) {
            String currentFileName = currentFile.getName();
            parentPath = parentPath + getComposedFileName(currentFileName);
        }
        return parentPath;
    }

    private static String getParentPath(VirtualFile currentFile) {
        VirtualFile parent = currentFile.getParent();
        return parent.getPath();
    }

    private static File[] getParentFiles(VirtualFile currentFile) {
        String parentPath = getParentPath(currentFile);
        File folder = new File(parentPath);
        return folder.listFiles();
    }

    private static String getComposedFileName(String currentFileName) {
        Pattern pattern = Pattern.compile(PropertiesComponent.getInstance().getValue(SettingConfigurable.PREFIX_PATTERN, SettingConfigurable.DEFAULT_PATTERN));

        Matcher m = pattern.matcher(currentFileName);
        boolean findResult = m.find();
        return findResult ? m.group(0) : "";
    }

    public static VariantsResponce getVariants(VirtualFile currentFile) {
        String currentFileName = currentFile.getName();
        File[] listOfFiles = getParentFiles(currentFile);

        List<String> allFileNames = new ArrayList<>();
        for (File record : listOfFiles) {
            if (record.isFile()) {
                allFileNames.add(record.getName());
            }
        }

        final String finalComposedFileName = getComposedFileName(currentFileName);

        allFileNames.removeIf(s -> !getComposedFileName(s).equals(finalComposedFileName));
        int currentIndex = allFileNames.indexOf(currentFileName);

        if (PropertiesComponent.getInstance().getBoolean(SettingConfigurable.PREFIX_HIDE, false)) {
            List<String> newFileNames = new ArrayList<>();
            for (String fName : allFileNames) {
                newFileNames.add(fName.replaceFirst(finalComposedFileName, ""));
            }
            allFileNames = newFileNames;
        }

        if (allFileNames.size()<2) {
            return null;
        }

        return new VariantsResponce(getCurrentSubPath(currentFile), allFileNames, currentFile, currentIndex);
    }
}
