package com.example.auth.tabs;

import com.example.auth.AuthState;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class SessionTab {

    public JComponent createSessionTab() {
        JBLabel sessionLabel = new JBLabel("Не авторизован");
        updateSessionLabel(sessionLabel);

        JButton logoutButton = new JButton("Выйти");
        logoutButton.addActionListener(e -> AuthState.getInstance().logout());

        AuthState.Listener listener = () -> updateSessionLabel(sessionLabel);

        var panel = new JBPanel<>(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx = 0;
        gc.gridy = 0;
        panel.add(sessionLabel, gc);

        gc.gridy = 1;
        panel.add(logoutButton, gc);

        var wrapper = new JBPanel<>(new BorderLayout());
        wrapper.add(panel, BorderLayout.NORTH);

        // Круговая диаграмма статистики
        var chart = new PieChartPanel(List.of(
                new PieChartPanel.Slice("Fails", 100, new Color(0xE53935)),   // красный
                new PieChartPanel.Slice("Seccess", 300, new Color(0x43A047)), // зелёный
                new PieChartPanel.Slice("Igore", 10, new Color(0x9E9E9E)),    // серый
                new PieChartPanel.Slice("Brocken", 10, new Color(0xFDD835))   // жёлтый
        ));
        wrapper.add(chart, BorderLayout.CENTER);

        // Слушатель живёт ровно пока вкладка видима: у JTabbedPane неактивная
        // вкладка скрыта, поэтому симметрично подписываемся при показе
        // и отписываемся при скрытии (иначе после первого переключения
        // вкладок обновление прекращалось бы)
        wrapper.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateSessionLabel(sessionLabel);
                AuthState.getInstance().addListener(listener);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                AuthState.getInstance().removeListener(listener);
            }
        });

        return wrapper;
    }

    private void updateSessionLabel(JBLabel label) {
        AuthState state = AuthState.getInstance();
        if (state.isAuthenticated()) {
            label.setText("Вы вошли как: " + state.getLogin());
        } else {
            label.setText("Не авторизован");
        }
    }
}
