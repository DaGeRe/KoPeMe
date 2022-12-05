package de.dagere.kopeme;
import java.io.InputStream;
import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;

/**
 * Configuration class for Android projects to read environment variables, properties, etc
 * from a config.json file in src/main/resources.
 */
public class AndroidConfiguration {
    
    //  Configuration file name for Android projects inside main/src/resources folder.
    static final String ANDROID_CONFIG = "kopeme_config.json";

    public static String read(String fieldName) {
        InputStream inputStream = AndroidConfiguration.class.getClassLoader().getResourceAsStream(ANDROID_CONFIG);
        String fieldValue;
        try {
            fieldValue = KoPeMeConstants.OBJECTMAPPER.readTree(inputStream).get(fieldName).asText();
        } catch (Exception e) {
            System.err.println("Couldn't read Android configuration: "+e.getMessage());
            fieldValue = null;
        }
        return fieldValue;
    }
}