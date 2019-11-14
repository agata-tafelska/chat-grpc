package atafelska.chat.client.utils;

public class InputUtils {

    public static boolean isValidHost(String host) {
        // TODO
        return host.equals("localhost");
    }

    public static boolean isValidUserName(String username) {
        // TODO
        return !username.isEmpty();
    }
}
