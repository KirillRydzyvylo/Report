package com.example.auth;

import com.example.auth.tabs.AuthTab;
import com.example.auth.tabs.ProjectsFtTab;
import com.example.auth.tabs.SessionTab;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Инструментное окно "Auth":
 * 1) "Авторизация" — поля логина и пароля + кнопка "Войти" (видна всегда);
 * 2) "Сессия" — текущий авторизованный пользователь + кнопка "Выйти"
 *    (только после успешной авторизации);
 * 3) "Проекты ФТ" — список ФТ1..ФТ10 со скроллом
 *    (только после успешной авторизации).
 */
public class AuthToolWindowFactory implements ToolWindowFactory {

    private static final String SESSION_TAB = "Сессия";
    private static final String PROJECTS_FT_TAB = "Проекты ФТ";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        JBTabbedPane tabs = new JBTabbedPane();
        tabs.addTab("Авторизация", new AuthTab().createAuthTab());
        tabs.addTab("Сессия", new SessionTab().createSessionTab());
        tabs.addTab("Проекты ФТ", new ProjectsFtTab(project).createProjectsTab());

        var content = ContentFactory.getInstance()
                .createContent(tabs, "", false);
        toolWindow.getContentManager().addContent(content);

        // Вкладка "Statistick" появляется только при активной сессии
        AuthState.Listener statisticListener = () -> SwingUtilities.invokeLater(() -> {
            int existingSessionTab = tabs.indexOfTab(SESSION_TAB);
            int existingProjectFtTab = tabs.indexOfTab(PROJECTS_FT_TAB);

            if (AuthState.getInstance().isAuthenticated()) {
                if (existingSessionTab == -1) {
                    tabs.addTab(SESSION_TAB, new SessionTab().createSessionTab());
                }
                if (existingProjectFtTab == -1) {
                    tabs.addTab(PROJECTS_FT_TAB, new ProjectsFtTab(project).createProjectsTab());
                }
            } else {
                if (tabs.indexOfTab(SESSION_TAB) != -1) {
                    tabs.remove(tabs.indexOfTab(SESSION_TAB));
                }
                if (tabs.indexOfTab(PROJECTS_FT_TAB) != -1) {
                    tabs.remove(tabs.indexOfTab(PROJECTS_FT_TAB));
                }
            }
        });
        AuthState.getInstance().addListener(statisticListener);
        statisticListener.onAuthChanged(); // синхронизация с текущим состоянием

        // Снимаем слушатель, когда окно закрывается вместе с проектом
        content.setDisposer(new Disposable() {
            @Override
            public void dispose() {
                AuthState.getInstance().removeListener(statisticListener);
            }
        });
    }
}
