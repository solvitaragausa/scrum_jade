package lv.rtu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnifferReadySignal {

    private static final Logger logger = LoggerFactory.getLogger(SnifferReadySignal.class);
    private static volatile boolean ready = false;

    private SnifferReadySignal() {
    }

    public static void promptAndWaitForUser() {
        logger.info("=== Configure Sniffer ===");
        logger.info("Tools -> Start Sniffing");
        logger.info("Press Enter to continue...");

        new Thread(() -> {
            try {
                System.in.read();
                ready = true;
                logger.info("Workflow starting");
            } catch (Exception e) {
                logger.error("Input error: {}", e.getMessage());
            }
        }).start();
    }

    public static void waitForSnifferReady() {
        while (!ready) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

