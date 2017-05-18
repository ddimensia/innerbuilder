package org.jetbrains.plugins.innerbuilder;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.NonFocusableCheckBox;
import org.jetbrains.annotations.Nullable;

public final class InnerBuilderOptionSelector {
    private static final List<Option> OPTIONS = createGeneratorOptions();

    private static List<Option> createGeneratorOptions() {
        final List<Option> options = new ArrayList<Option>(8);
        final Map<String, InnerBuilderOption> prefixMap = new LinkedHashMap<String, InnerBuilderOption>(3);
        prefixMap.put("None", null);
        prefixMap.put("'with'", InnerBuilderOption.WITH_NOTATION);
        prefixMap.put("'set'", InnerBuilderOption.SET_NOTATION);

        options.add(
                SelectorOption.newBuilder()
                        .withCaption("Generate builder methods for final fields")
                        .withMnemonic('f')
                        .withOption(InnerBuilderOption.FINAL_SETTERS)
                        .build());
        options.add(
                SelectorOption.newBuilder()
                        .withCaption("Make original fields final for builder methods")
                        .withMnemonic('m')
                        .withOption(InnerBuilderOption.MAKE_FIELDS_FINAL)
                        .build());
        options.add(
                SelectorOption.newBuilder()
                        .withOption(InnerBuilderOption.NEW_BUILDER_METHOD)
                        .withCaption("Generate static newBuilder() method")
                        .withMnemonic('n')
                        .build());
        options.add(
                SelectorOption.newBuilder()
                        .withCaption("Generate builder copy constructor")
                        .withMnemonic('o')
                        .withOption(InnerBuilderOption.COPY_CONSTRUCTOR)
                        .build());
        options.add(
                MultiValueOption.newBuilder()
                        .withCaption("Prefix for builder methods")
                        .withMnemonic('p')
                        .withToolTip(
                                "Generate builder methods that start with the select prefix or None, for example: "
                                        + "builder.withName(String name)")
                        .withOptionMap(prefixMap)
                        .build());
        options.add(
                SelectorOption.newBuilder()
                        .withCaption("Add JSR-305 @Nonnull annotation")
                        .withMnemonic('j')
                        .withToolTip(
                                "Add @Nonnull annotations to generated methods and parameters, for example: "
                                        + "@Nonnull public Builder withName(@Nonnull String name) { ... }")
                        .withOption(InnerBuilderOption.JSR305_ANNOTATIONS)
                        .build());
        options.add(
                SelectorOption.newBuilder()
                        .withCaption("Add Findbugs @NonNull annotation")
                        .withMnemonic('b')
                        .withToolTip(
                                "Add @NonNull annotations to generated methods and parameters, for example: "
                                        + "@NonNull public Builder withName(@NonNull String name) { ... }")
                        .withOption(InnerBuilderOption.FINDBUGS_ANNOTATION)
                        .build());
        options.add(
                SelectorOption.newBuilder()
                        .withCaption("Add Javadoc")
                        .withMnemonic('c')
                        .withToolTip("Add Javadoc to generated builder class and methods")
                        .withOption(InnerBuilderOption.WITH_JAVADOC)
                        .build());
        return options;
    }

    private InnerBuilderOptionSelector() {
    }

    @Nullable
    public static List<PsiFieldMember> selectFieldsAndOptions(final List<PsiFieldMember> members,
                                                              final Project project) {
        if (members == null || members.isEmpty()) {
            return null;
        }

        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return members;
        }

        final JComponent[] optionComponents = buildOptionComponents();

        final PsiFieldMember[] memberArray = members.toArray(new PsiFieldMember[members.size()]);

        final MemberChooser<PsiFieldMember> chooser = new MemberChooser<PsiFieldMember>(memberArray,
                false, // allowEmptySelection
                true,  // allowMultiSelection
                project, null, optionComponents);

        chooser.setTitle("Select Fields and Options for the Builder");
        chooser.selectElements(memberArray);
        if (chooser.showAndGet()) {
            return chooser.getSelectedElements();
        }

        return null;
    }

    private static JComponent[] buildOptionComponents() {
        final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        final int optionCount = OPTIONS.size();
        final JComponent[] componentArray = new JComponent[optionCount];
        for (int i = 0; i < optionCount; i++) {
            componentArray[i] = buildOptionComponent(propertiesComponent, OPTIONS.get(i));
        }

        return componentArray;
    }

    private static JComponent buildOptionComponent(final PropertiesComponent propertiesComponent,
                                                   final Option option) {
        if (option instanceof SelectorOption) {
            return buildOptionCheckBox(propertiesComponent, (SelectorOption) option);
        } else if (option instanceof MultiValueOption) {
            return buildOptionComboBox(propertiesComponent, (MultiValueOption) option);
        }

        return null;
    }

    private static JCheckBox buildOptionCheckBox(final PropertiesComponent propertiesComponent,
                                                 final SelectorOption selectorOption) {
        final InnerBuilderOption option = selectorOption.getOption();

        final JCheckBox optionCheckBox = new NonFocusableCheckBox(selectorOption.getCaption());
        optionCheckBox.setMnemonic(selectorOption.getMnemonic());
        optionCheckBox.setToolTipText(selectorOption.getToolTip());

        final String optionProperty = option.getProperty();
        optionCheckBox.setSelected(propertiesComponent.isTrueValue(optionProperty));
        optionCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent event) {
                propertiesComponent.setValue(optionProperty, Boolean.toString(optionCheckBox.isSelected()));
            }
        });
        return optionCheckBox;
    }

    private static JComponent buildOptionComboBox(final PropertiesComponent propertiesComponent,
                                                         final MultiValueOption multiValueOption) {
        final Map<String, InnerBuilderOption> optionMap = multiValueOption.getOptionMap();

        final JLabel jLabel = new JLabel(multiValueOption.getCaption());
        final JComboBox<String> optionComboBox = new JComboBox<String>(optionMap.keySet().toArray(new String[optionMap.size()]));
        optionComboBox.setEditable(false);
        optionComboBox.setToolTipText(multiValueOption.getToolTip());
        final JPanel jPanel = new JPanel(new FlowLayout());
        jPanel.add(optionComboBox);
        jPanel.add(jLabel);

        for (Map.Entry<String, InnerBuilderOption> entry : optionMap.entrySet()) {
            InnerBuilderOption option = entry.getValue();
            if (option != null) {
                if (propertiesComponent.isTrueValue(option.getProperty())) {
                    optionComboBox.setSelectedItem(entry.getKey());
                    break;
                }
            }
        }

        optionComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                for (Map.Entry<String, InnerBuilderOption> entry : optionMap.entrySet()) {
                    String key = entry.getKey();
                    InnerBuilderOption option = entry.getValue();
                    if (option != null) {
                        if (key.equals(optionComboBox.getSelectedItem())) {
                            propertiesComponent.setValue(option.getProperty(), Boolean.TRUE.toString());
                        } else {
                            propertiesComponent.setValue(option.getProperty(), Boolean.FALSE.toString());
                        }
                    }
                }
            }
        });

        return jPanel;
    }
}

