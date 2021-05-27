package ua.in.dej.groupper;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DirectoryNode extends ProjectViewNode<PsiFile> {

    private final String mName;
    private List<AbstractTreeNode<?>> mChildNodeList;

    protected DirectoryNode(Project project, ViewSettings viewSettings, PsiFile directory, String name) {
        super(project, directory, viewSettings);
        mName = name;
        mChildNodeList = new ArrayList<AbstractTreeNode<?>>();
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        for (final AbstractTreeNode<?> childNode : mChildNodeList) {
            ProjectViewNode<?> treeNode = (ProjectViewNode) childNode;
            if (treeNode.contains(file)) {
                return true;
            }
        }
        return false;
    }

    public void addChildren(AbstractTreeNode<?> treeNode) {
        mChildNodeList.add(treeNode);
    }

    public void addAllChildren(List<AbstractTreeNode<?>> treeNodeList) {
        mChildNodeList.addAll(treeNodeList);
    }

    @NotNull
    @Override
    public List<AbstractTreeNode<?>> getChildren() {
        if (PropertiesComponent.getInstance().getBoolean(SettingConfigurable.PREFIX_HIDE, false)) {
            final ArrayList<AbstractTreeNode<?>> abstractTreeNodes = new ArrayList<>();
            for (AbstractTreeNode<?> fileNode : mChildNodeList) {
                PsiFile psiFile = null;
                ViewSettings settings = null;
                if (fileNode.getValue() instanceof PsiFile) {
                    psiFile = (PsiFile) fileNode.getValue();
                    settings = ((PsiFileNode) fileNode).getSettings();
                }
                try {
                    if (fileNode.getValue() instanceof PsiClass) {
                        ClassTreeNode treeNode = (ClassTreeNode) fileNode;
                        PsiClass pClass = treeNode.getPsiClass();
                        psiFile = (PsiFile) pClass.getContainingFile();
                        settings = treeNode.getSettings();
                    }
                } catch (Throwable e) {
                    // Do nothing coz WedStorm don't know what is PsiClass :)
                }
                final ViewSettings finalSettings = settings;
                String shortName = psiFile.getName().substring(mName.length());
                final int beginIndex = shortName.indexOf(ProjectStructureProvider.COMPOSE_BY_CHAR);
                if (beginIndex != -1) {
                    shortName = shortName.substring(beginIndex + 1);
                }
                abstractTreeNodes.add(new FoldingNode(fileNode.getProject(), psiFile, finalSettings, shortName));
            }
            return abstractTreeNodes;
        } else {
            return mChildNodeList;
        }
    }


    @Override
    protected void update(PresentationData presentation) {
        presentation.setPresentableText(mName);
        presentation.setIcon(Icons.PACK);
    }
}
