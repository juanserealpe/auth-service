package co.edu.unicauca.enums;

public enum AuthErrorCode {
    USER_NOT_FOUND("A-400", "Degree work not found"),
    USER_WITHOUT_ROLE_EXPECTED("A-401", "One or more student IDs are not valid STUDENT accounts");

    private final String code;
    private final String defaultMessage;

    AuthErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}