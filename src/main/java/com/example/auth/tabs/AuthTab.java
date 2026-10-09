package com.example.auth.tabs;

import com.example.auth.AuthState;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Pattern;

public class AuthTab {

    /** TODO: заменить на реальный эндпоинт авторизации. */
    private static final String AUTH_URL = "http://localhost:8080/api/login";

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\\"token\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

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

            // Блокируем кнопку на время запроса
            loginButton.setEnabled(false);
            statusLabel.setText("Авторизация...");
            sendAuthRequest(login, password, loginButton, statusLabel, passwordField);
        };

        loginButton.addActionListener(toListener(doLogin));

        var panel = new JBPanel<>(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = JBUI.insets(8);
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

    /**
     * POST {login, password} на {@link #AUTH_URL}.
     * Запрос асинхронный; UI обновляется через invokeLater (в EDT).
     * Успех = HTTP 200; из тела ответа при наличии извлекается "token".
     */
    private static void sendAuthRequest(String login,
                                        String password,
                                        JButton loginButton,
                                        JBLabel statusLabel,
                                        JBPasswordField passwordField) {
        String json = "{\"login\":\"" + escapeJson(login) + "\",\"password\":\""
                + escapeJson(password) + "\"}";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTH_URL))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((response, error) -> SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);

                    if (error != null) {
                        statusLabel.setText("Ошибка соединения: " + error.getMessage());
                        return;
                    }

                    if (response.statusCode() == 200) {
                        // Токен, если сервер его вернул, сохраняем для дальнейших запросов
                        var matcher = TOKEN_PATTERN.matcher(response.body());
                        if (matcher.find()) {
                            AuthState.getInstance().setToken(matcher.group(1));
                        }
                        AuthState.getInstance().login(login);
                        statusLabel.setText("Успешно: выполнен вход как \"" + login + "\"");
                        passwordField.setText("");
                    } else {
                        statusLabel.setText("Ошибка авторизации: HTTP " + response.statusCode());
                    }
                }));
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
