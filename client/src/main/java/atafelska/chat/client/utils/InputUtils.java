package atafelska.chat.client.utils;

import java.util.regex.Pattern;

public class InputUtils {

    public static boolean isValidHost(String host) {
        final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
        final String IP_REGEX = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
        final Pattern IP_PATTERN = Pattern.compile(IP_REGEX);
        return IP_PATTERN.matcher(host).matches() || host.equals("localhost");
    }

    public static boolean isValidUserName(String username) {
        final String USERNAME_REGEX = "[a-zA-Z0-9\\._\\-]{3,20}";
        final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);
        return USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidPassword(String password) {
        final String PASSWORD_REGEX = ".{8,30}";
        final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isMessageValid(String text) {
        return !text.isEmpty();
    }
}