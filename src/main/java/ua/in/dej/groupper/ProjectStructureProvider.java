package ua.in.dej.groupper;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectStructureProvider implements com.intellij.ide.projectView.TreeStructureProvider {

    public static final char COMPOSE_BY_CHAR = '_';

    @Nullable
    @Override
    public Object getData(@NotNull Collection<AbstractTreeNode<?>> collection, @NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent, @NotNull Collection<AbstractTreeNode<?>> children, ViewSettings viewSettings) {
        List<AbstractTreeNode<?>> resultList = new ArrayList<>();
        final boolean groupingAlways = PropertiesComponent.getInstance().getBoolean(SettingConfigurable.GROUPING_ALWAYS, false);
        if (parent.getValue() instanceof PsiDirectory) {
            PsiDirectory directory = (PsiDirectory) parent.getValue();
            String path = directory.getVirtualFile().getPath();
            if (groupingAlways || SettingsManager.isComposed(path)) {
                resultList.addAll(createComposedFiles(children, viewSettings));
            } else {
                resultList.addAll(children);
            }
        } else {
            resultList.addAll(children);
        }

        return resultList;
    }

    @NotNull
    private List<AbstractTreeNode<?>> createComposedFiles(@NotNull Collection<AbstractTreeNode<?>> fileNodes, ViewSettings viewSettings) {
        List<AbstractTreeNode<?>> resultList = new ArrayList<>();
        Project project = Utils.getCurrentProject();
        if (project != null) {
            HashSet<String> composedDirNameSet = new HashSet<>();
            List<AbstractTreeNode<?>> notComposedFileNodes = new ArrayList<>();

            Pattern pattern = Pattern.compile(PropertiesComponent.getInstance().getValue(SettingConfigurable.PREFIX_PATTERN, SettingConfigurable.DEFAULT_PATTERN));

            for (AbstractTreeNode<?> fileNode : fileNodes) {
                Object record = fileNode.getValue();
                if (record instanceof PsiFile) {
                    PsiFile psiFile = (PsiFile) record;
                    String fileName = psiFile.getName();

                    Matcher m = pattern.matcher(fileName);
                    if (m.find()) {
                        String composedDirName = m.group(0);
                        composedDirNameSet.add(composedDirName);
                    } else {
                        notComposedFileNodes.add(fileNode);
                    }
                } else {
                    try {
                        if (record instanceof PsiClass) {
                            PsiClass pClass = (PsiClass) record;
                            if (pClass.getParent() instanceof PsiJavaFileImpl) {
                                PsiJavaFileImpl file = (PsiJavaFileImpl) pClass.getParent();
                                String fileName = file.getName();

                                Matcher m = pattern.matcher(fileName);
                                if (m.find()) {
                                    String composedDirName = m.group(0);
                                    composedDirNameSet.add(composedDirName);
                                } else {
                                    notComposedFileNodes.add(fileNode);
                                }
                            } else {
                                notComposedFileNodes.add(fileNode);
                            }
                        } else {
                            notComposedFileNodes.add(fileNode);
                        }
                    } catch (Throwable e) {
                        // Do nothing coz WedStorm don't know what is PsiClass :)
                        notComposedFileNodes.add(fileNode);
                    }
                }
            }

            for (String composedDirName : composedDirNameSet) {
                List<AbstractTreeNode<?>> composedFileNodes = filterByDirName(fileNodes, composedDirName);
                if (composedFileNodes.size() == 1) {
                    resultList.addAll(composedFileNodes);
                    continue;
                }
                Object firstRecord = composedFileNodes.get(0).getValue();
                if (firstRecord instanceof PsiFile) {
                    PsiFile psiFile = (PsiFile) firstRecord;
                    try {
                        psiFile = PsiFileFactory.getInstance(project).createFileFromText(psiFile.getLanguage(), "");
                    } catch (Throwable e) {
                        // Android studio fix
                    }
                    DirectoryNode composedDirNode = new DirectoryNode(project, viewSettings, psiFile, composedDirName);
                    composedDirNode.addAllChildren(composedFileNodes);
                    resultList.add(composedDirNode);
                }
                try {
                    if (firstRecord instanceof PsiClass) {
                        ClassTreeNode treeNode = (ClassTreeNode) composedFileNodes.get(0);
                        PsiClass pClass = treeNode.getPsiClass();
                        PsiFile psiFile = (PsiFile) pClass.getContainingFile();
                        DirectoryNode composedDirNode = new DirectoryNode(project, viewSettings, psiFile, composedDirName);
                        composedDirNode.addAllChildren(composedFileNodes);
                        resultList.add(composedDirNode);
                    }
                } catch (Throwable e) {
                    // Do nothing coz WedStorm don't know what is PsiClass :)
                }
            }
//            if (resultList.size() > 0) {
//                resultList.sort((a, b) -> {
//                    String aa = ((PsiFile) a.getValue()).getName();
//                    String bb = ((PsiFile) b.getValue()).getName();
//                    return aa.compareTo(bb);
//                });
//            }
            if (!notComposedFileNodes.isEmpty()) {
                resultList.addAll(notComposedFileNodes);
            }
        }
        return resultList;
    }

    @NotNull
    private List<AbstractTreeNode<?>> filterByDirName(Collection<AbstractTreeNode<?>> fileNodes, String token) {
        List<AbstractTreeNode<?>> resultList = new ArrayList<>();

        Pattern pattern = Pattern.compile(PropertiesComponent.getInstance().getValue(SettingConfigurable.PREFIX_PATTERN, SettingConfigurable.DEFAULT_PATTERN));

        for (AbstractTreeNode<?> fileNode : fileNodes) {
            Object record = fileNode.getValue();
            if (record instanceof PsiFile) {
                PsiFile psiFile = (PsiFile) record;
                String fileName = psiFile.getName();

                Matcher m = pattern.matcher(fileName);
                if (m.find()) {

                    String composedDirName = m.group(0);
                    if (composedDirName.equals(token)) {
                        resultList.add(fileNode);
                    }
                }
            } else {
                try {
                    if (record instanceof PsiClass) {
                        PsiClass pClass = (PsiClass) record;
                        if (pClass.getParent() instanceof PsiJavaFileImpl) {
                            PsiJavaFileImpl file = (PsiJavaFileImpl) pClass.getParent();
                            String fileName = file.getName();

                            Matcher m = pattern.matcher(fileName);
                            if (m.find()) {

                                String composedDirName = m.group(0);
                                if (composedDirName.equals(token)) {
                                    resultList.add(fileNode);
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    // Do nothing coz WedStorm don't know what is PsiClass :)
                }
            }
        }

        return resultList;
    }
}
