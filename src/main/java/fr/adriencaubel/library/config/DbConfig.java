package fr.adriencaubel.library.config;

public final class DbConfig {
    private DbConfig() {}

    public static String host() { return env("DB_HOST", "localhost"); }
    public static int port() { return Integer.parseInt(env("DB_PORT", "3345")); }
    public static String dbName() { return env("DB_NAME", "library"); }
    public static String user() { return env("DB_USER", "root"); }
    public static String password() { return env("DB_PASSWORD", "password"); }

    public static String jdbcUrl() {
        // serverTimezone: avoids common MySQL timezone warnings
        return "jdbc:mysql://" + host() + ":" + port() + "/" + dbName()
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe%2FParis";
    }

    private static String env(String key, String defaultValue) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? defaultValue : v;
    }
}
