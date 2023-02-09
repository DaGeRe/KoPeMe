package de.dagere.kopeme;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;

import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;

/**
 * Configuration class for Android projects to read environment variables, properties, etc
 * from a config.json file in src/main/resources.
 */
public class AndroidConfiguration {
    
    //  Configuration file name for Android projects inside main/src/resources folder.
    static final String ANDROID_CONFIG = "kopeme_config.json";

    public static String read(String fieldName) {
        ClassLoader classLoader = AndroidConfiguration.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(ANDROID_CONFIG);
        String fieldValue;
        try {
            JsonNode rootNode, fieldNode;
            rootNode = KoPeMeConstants.OBJECTMAPPER.readTree(inputStream);
            fieldNode = rootNode.get(fieldName);
            fieldValue = fieldNode.asText();
        } catch (Exception e) {
            fieldValue = null;
        }
        return fieldValue;
    }
}