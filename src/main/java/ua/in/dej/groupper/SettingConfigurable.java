package ua.in.dej.groupper;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingConfigurable implements Configurable {
    public static final String PREFIX_PATTERN = "folding_plugin_prefix_pattern";
    public static final String GROUPING_ALWAYS = "folding_plugin_grouping_always";
    public static final String PREFIX_HIDE = "folding_plugin_prefix_hide";

    public static final String DEFAULT_PATTERN = "[^\\.]{1,}(?=\\.)";

    private JPanel mPanel;
    private JTextField customPattern;
    private JCheckBox hideFoldingPrefix;
    private JCheckBox groupingAlwaysCheckBox;
    private JButton defaultButton;
    private boolean isModified = false;

    @Nls
    @Override
    public String getDisplayName() {
        return "Groupper";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "null:";
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        defaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                customPattern.setText(DEFAULT_PATTERN);
            }
        });

        groupingAlwaysCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                isModified = true;
            }
        });

        customPattern.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isModified = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isModified = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                isModified = true;
            }
        });

        hideFoldingPrefix.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                isModified = true;
            }
        });

        reset();

        return mPanel;
    }

    @Override
    public boolean isModified() {

        return isModified;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue(GROUPING_ALWAYS, Boolean.valueOf(groupingAlwaysCheckBox.isSelected()).toString());
        PropertiesComponent.getInstance().setValue(PREFIX_PATTERN, customPattern.getText());
        PropertiesComponent.getInstance().setValue(PREFIX_HIDE, Boolean.valueOf(hideFoldingPrefix.isSelected()).toString());

        if (isModified) {
            Project currentProject = Utils.getCurrentProject();

            if (currentProject != null) {
                ProjectView.getInstance(currentProject).refresh();
            }
        }

        isModified = false;
    }

    @Override
    public void reset() {
        final boolean groupingAlways = PropertiesComponent.getInstance().getBoolean(GROUPING_ALWAYS, false);
        groupingAlwaysCheckBox.setSelected(groupingAlways);
        customPattern.setText(PropertiesComponent.getInstance().getValue(PREFIX_PATTERN, DEFAULT_PATTERN));
        hideFoldingPrefix.getModel().setSelected(PropertiesComponent.getInstance().getBoolean(PREFIX_HIDE, false));
    }

    @Override
    public void disposeUIResources() {
    }
}
