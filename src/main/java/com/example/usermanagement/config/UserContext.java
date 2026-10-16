package com.example.usermanagement.config;

public final class UserContext {
    private static final ThreadLocal<UserSessionInfo> CTX = new ThreadLocal<>();

    public static void set(UserSessionInfo userSession) {CTX.set(userSession);}

    public static UserSessionInfo get() {return CTX.get();}

    public static void clear() {CTX.remove();}
}
