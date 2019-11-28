package atafelska.chat.client.net;

import atafelska.chat.client.core.Logger;

import java.util.concurrent.TimeUnit;

/**
 * This class represents single task that is scheduled with delay.
 * Execution of the task may be delayed multiple times and as a result never done.
 * <p>
 * This class is not thread-safe.
 * There is risk of performing task multiple times if it is initialized multiple times without appropriate time period (min 100ms)
 */
public class DelayedTask {
    private static final long NOT_SCHEDULED = -1;
    private static final int CHECK_TIME_FRAME_MS = 100;

    private Runnable runnable;
    private long executionTimestamp = NOT_SCHEDULED;

    public DelayedTask(Runnable runnable) {
        this.runnable = runnable;
    }

    public void initialize() {
        if (executionTimestamp != NOT_SCHEDULED) {
            throw new IllegalStateException("Initializing task without canceling previous execution delay is not allowed. Make sure to call `cancel` first before next initialization");
        }
        new Thread(() -> {
            Logger.print("Initializing delayed task");
            while (true) {
                try {
                    if (executionTimestamp != NOT_SCHEDULED && System.currentTimeMillis() > executionTimestamp) {
                        executionTimestamp = NOT_SCHEDULED;
                        runnable.run();
                        return;
                    }
                    Thread.sleep(CHECK_TIME_FRAME_MS);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    public void delayExecution(long seconds) {
        executionTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
    }

    public void cancel() {
        executionTimestamp = NOT_SCHEDULED;
    }
}
