package de.dagere.kopeme.kieker.writer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import kieker.common.configuration.Configuration;
import kieker.common.util.filesystem.FSUtil;
import kieker.monitoring.core.configuration.ConfigurationConstants;

public class WriterUtil {
   public static Path buildKiekerLogFolder(final String customStoragePath, final Configuration configuration) {
      final DateFormat date = new SimpleDateFormat("yyyyMMdd'-'HHmmss", Locale.US);
      date.setTimeZone(TimeZone.getTimeZone("UTC"));
      final String currentDateStr = date.format(new java.util.Date())
            + "-" + System.nanoTime(); // 'SSS' in SimpleDateFormat is not accurate enough for fast unit tests

      final String hostName = configuration.getStringProperty(ConfigurationConstants.HOST_NAME);
      final String controllerName = configuration.getStringProperty(ConfigurationConstants.CONTROLLER_NAME);

      final String filename = String.format("%s-%s-UTC-%s-%s", FSUtil.FILE_PREFIX, currentDateStr, hostName, controllerName);

      return Paths.get(customStoragePath, filename);
   }
}
