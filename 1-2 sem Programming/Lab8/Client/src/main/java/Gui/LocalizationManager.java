package Gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Класс для управления локализацией приложения
 */
public class LocalizationManager {
    /**
     * Кастомный ResourceBundle.Control для загрузки файлов свойств в кодировке UTF-8
     */
    public static class UTF8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            try (InputStream stream = loader.getResourceAsStream(resourceName)) {
                if (stream != null) {
                    return new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                }
            }
            return null;
        }
    }

    /**
     * Получение ресурсного бандла для указанной локали
     */
    public static ResourceBundle getResourceBundle(Locale locale) {
        try {
            return ResourceBundle.getBundle("messages", locale, new UTF8Control());
        } catch (Exception e) {
            System.err.println("Error loading resource bundle: " + e.getMessage());
            return ResourceBundle.getBundle("messages", Locale.ENGLISH, new UTF8Control());
        }
    }
} 