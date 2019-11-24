package atafelska.chat.server;

public class Logger {
    public static void print(String text) {
        System.out.println(getLogMessage(text));
    }

    private static String getLogMessage(String message) {
        // In this setup the call which is interesting from logging point of view
        // is 4th in stack trace counting from top
        int stackTraceIndex = 3;

        int lineNumber = Thread.currentThread().getStackTrace()[stackTraceIndex].getLineNumber();
        String filename = Thread.currentThread().getStackTrace()[stackTraceIndex].getFileName();
        String methodName = Thread.currentThread().getStackTrace()[stackTraceIndex].getMethodName();
        String currentThread = Thread.currentThread().getName();

        return "(" + filename + ": " + lineNumber + ") " + methodName + " (Thread: " + currentThread + "): " + message;
    }
}
