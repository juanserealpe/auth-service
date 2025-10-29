package co.edu.unicauca.utilities;

import java.time.LocalDateTime;

public class Logger {
    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_GREEN  = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    private Logger() {}

    private static String timestamp() {
        return LocalDateTime.now().toString();
    }


    public static void info(Class<?> source, String message) {
        System.out.println(ANSI_BLUE+"[INFO][" + timestamp() + "][" + source.getSimpleName() + "] "+ ANSI_RESET + message);
    }

    public static void success(Class<?> source, String message) {
        System.out.println(ANSI_GREEN + "[SUCCESS]["+ timestamp() + "][" + source.getSimpleName() + "] "+ ANSI_RESET + message);
    }

    public static void warn(Class<?> source, String message) {
        System.out.println(ANSI_YELLOW +"[WARN][" + timestamp() + "][" + source.getSimpleName() + "] " + message + ANSI_RESET);
    }

    public static void error(Class<?> source, String message) {
        System.out.println(ANSI_RED + "[ERROR][" + timestamp() + "][" + source.getSimpleName() + "] " + message + ANSI_RESET);
    }
}
