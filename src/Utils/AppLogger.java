package Utils;

import java.io.IOException;
import java.util.logging.*;

public class AppLogger {
    private static FileHandler fileHandler;

    public static void setup() throws IOException {
        // 1. Get the ROOT logger (Empty string "" covers all loggers in the app)
        Logger rootLogger = Logger.getLogger("");

        // 2. Remove the default Console Handler (Stops printing to screen)
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler h : handlers) {
            if (h instanceof ConsoleHandler) {
                rootLogger.removeHandler(h);
            }
        }

        // 3. Add the File Handler (Creates application.log)
        if (fileHandler == null) {
            // "true" means append to the existing file
            fileHandler = new FileHandler("application.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);
        }
    }
}