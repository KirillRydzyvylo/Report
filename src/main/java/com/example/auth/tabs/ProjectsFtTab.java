package com.example.auth.tabs;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Вкладка "Проекты ФТ": скроллируемый список кликабельных элементов
 * ФТ1..ФТ10 высотой 30 пикселей каждый.
 */
public class ProjectsFtTab {

    private static final int ITEM_HEIGHT = 30;

    private final Project project;

    public ProjectsFtTab(Project project) {
        this.project = project;
    }

    public JComponent createProjectsTab() {
        DefaultListModel<String> model = new DefaultListModel<>();

        for(String elName : elements()){
            model.addElement(elName);
        }

        JBList<String> list = createList(model);

        JBScrollPane scrollPane = new JBScrollPane(list);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JBLabel hintLabel = new JBLabel("Кликните по элементу списка");

        var panel = new JBPanel<>(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(hintLabel, BorderLayout.SOUTH);
        return panel;
    }

    private @NonNull JBList<String> createList(DefaultListModel<String> model) {
        JBList<String> list = new JBList<>(model);
        list.setFixedCellHeight(ITEM_HEIGHT);
        // Видимых строк меньше, чем элементов, — чтобы скролл был виден сразу
        list.setVisibleRowCount(8);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index >= 0) {
                    String item = model.get(index);
                    NotificationGroupManager.getInstance()
                            .getNotificationGroup("AuthPlugin")
                            .createNotification("Проекты ФТ", "Выбран: " + item,
                                    NotificationType.INFORMATION)
                            .notify(project);
                }
            }
        });
        return list;
    }

    private ArrayList<String> elements(){
        ArrayList<String> model = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            model.add("ФТ" + i);
        }
        return model;
    }
}
