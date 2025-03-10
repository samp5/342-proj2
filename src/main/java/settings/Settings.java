package settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Settings {
  private static class SettingsLoader {
    public char tempUnit;
    public double[] lastLoc;
    public int lastPage;

    private static SettingsLoader fromSettings() {
      SettingsLoader sl = new SettingsLoader();
      sl.tempUnit = Settings.tempUnit;
      sl.lastLoc = Settings.lastLoc;
      sl.lastPage = Settings.lastPage;
      
      return sl;
    }
  }

  private static char tempUnit;
  private static double[] lastLoc;
  private static int lastPage;

  public static char getTempUnit() {
    return tempUnit;
  }

  public static void setTempUnit(char tempUnit) {
    Settings.tempUnit = tempUnit;
  }

  public static double[] getLastLoc() {
	  return lastLoc;
  }

  public static void setLastLoc(double lat, double lon) {
    Settings.lastLoc = new double[] {lat, lon};
  }

  public static int getLastPage() {
	  return lastPage;
  }

  public static void setLastPage(int lastPage) {
    Settings.lastPage = lastPage;
  }

  public static class SettingsLoadException extends Exception {}

  public static void loadSettings() throws SettingsLoadException {
    String settingsStr;
    try {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      InputStream is = cl.getResourceAsStream("settings/settings.json");
      settingsStr = new String(is.readAllBytes());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
      throw new SettingsLoadException();
    }

    SettingsLoader s = getObject(settingsStr);
    Settings.tempUnit = s.tempUnit;
    Settings.lastLoc = s.lastLoc;
    Settings.lastPage = s.lastPage;
  }

  public static void saveSettings() {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json;
    try {
      json = ow.writeValueAsString(SettingsLoader.fromSettings());
    } catch (JsonProcessingException e) {
      System.err.println("Failed to process settings into json. Settings not saved.");
      return;
    } 

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try {
      PrintWriter writer = new PrintWriter(new File(cl.getResource("settings/settings.json").getPath()));
      writer.write(json);
      writer.close();
    } catch (FileNotFoundException e) {
      System.err.println("Failed to write settings to file. Settings not saved.");
      return;
    }
  }

  /**
   * parse a json string into a {@code Root} object
   *
   * @param json the json to parse in string form
   * @return the {@code Root} object parsed
   */
  private static SettingsLoader getObject(String json) {
    ObjectMapper om = new ObjectMapper();
    SettingsLoader toRet = null;
    try {
      toRet = om.readValue(json, SettingsLoader.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return toRet;
  }
}
