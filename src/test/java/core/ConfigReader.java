package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load config.properties", e);
        }
    }

    public static String get(String key) {
        String sys = System.getProperty(key);
        return (sys != null && !sys.isBlank()) ? sys : props.getProperty(key);
    }

    public static int getInt(String key) {
      return Integer.parseInt(get(key));
    }

    public static boolean getBool(String key) {
      return Boolean.parseBoolean(get(key));
    }
}
