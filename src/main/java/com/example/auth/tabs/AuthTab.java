package com.example.auth.tabs;

import com.example.auth.AuthState;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AuthTab {

    public JComponent createAuthTab() {
        JBTextField loginField = new JBTextField();
        loginField.getEmptyText().setText("Введите логин");

        JBPasswordField passwordField = new JBPasswordField();
        passwordField.getEmptyText().setText("Введите пароль");

        JBLabel statusLabel = new JBLabel("Не авторизован");

        JButton loginButton = new JButton("Войти");

        Runnable doLogin = () -> {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (login.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Ошибка: заполните логин и пароль");
                return;
            }

            // TODO: здесь реальная проверка на сервере, пока — локальная имитация.
            AuthState.getInstance().login(login);
            statusLabel.setText("Успешно: выполнен вход как \"" + login + "\"");
            passwordField.setText("");
        };

        loginButton.addActionListener(toListener(doLogin));

        var panel = new JBPanel<>(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        panel.add(new JBLabel("Логин:"), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        panel.add(loginField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        panel.add(new JBLabel("Пароль:"), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        panel.add(passwordField, gc);

        gc.gridx = 1;
        gc.gridy = 2;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        panel.add(loginButton, gc);

        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.LINE_START;
        panel.add(statusLabel, gc);

        // Отступ сверху, чтобы форма не прилипала к краю
        var wrapper = new JBPanel<>(new BorderLayout());
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

    private static ActionListener toListener(Runnable runnable) {
        return e -> runnable.run();
    }
}
