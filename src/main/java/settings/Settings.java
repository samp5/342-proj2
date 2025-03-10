package settings;

import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Settings {
  private static class SettingsLoader {
    public char tempUnit;
    public double[] lastLoc;
    public int lastPage;
  }

  private static char tempUnit;
  private static double[] lastLoc;
  private static int lastPage;

  public static char getTempUnit() {
    return tempUnit;
  }

  public static double[] getLastLoc() {
	  return lastLoc;
  }

  public static int getLastPage() {
	  return lastPage;
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
