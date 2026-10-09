package com.example.auth;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Простое состояние авторизации, общее для обеих вкладок.
 */
public final class AuthState {

    private static final AuthState INSTANCE = new AuthState();

    public interface Listener {
        void onAuthChanged();
    }

    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    private String login;
    private String token;
    private boolean authenticated;

    private AuthState() {
    }

    public static AuthState getInstance() {
        return INSTANCE;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getLogin() {
        return login;
    }

    /** Токен из ответа сервера (если сервер его вернул). */
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void login(String login) {
        this.login = login;
        this.authenticated = true;
        fireChanged();
    }

    public void logout() {
        this.login = null;
        this.authenticated = false;
        fireChanged();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void fireChanged() {
        for (Listener listener : listeners) {
            listener.onAuthChanged();
        }
    }
}
