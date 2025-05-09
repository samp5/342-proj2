package settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import views.util.UnitHandler.TemperatureUnit;

/**
 * Settings handler and manager for the program.
 * Likely should not be instantiated.
 */
public class Settings {
  /**
   * Class used for loading and saving the settings to and from JSON.
   */
  private static class SettingsLoader {
    public char tempUnit;
    public double[] lastLoc;
    public int lastScene;
    public String themeFile;

    /**
     * create a new {@code SettingsLoader} from current global {@code Settings}
     */
    private static SettingsLoader fromSettings() {
      SettingsLoader sl = new SettingsLoader();
      if (Settings.tempUnit == TemperatureUnit.Celsius) {
        sl.tempUnit = 'C';
      } else {
        sl.tempUnit = 'F';
      }

      sl.lastLoc = Settings.lastLoc;
      sl.lastScene = Settings.lastScene;
      sl.themeFile = Settings.themeFile;

      return sl;
    }

    /**
     * write this object's values into static {@code Settings}
     */
    private void toSettings() {
      if (this.tempUnit == 'C') {
        Settings.tempUnit = TemperatureUnit.Celsius;
      } else {
        Settings.tempUnit = TemperatureUnit.Fahrenheit;
      }

      Settings.lastScene = this.lastScene;
      Settings.lastLoc = this.lastLoc;
      Settings.themeFile = this.themeFile;
      Settings.theme = Settings.themeFromFile(this.themeFile);
    }
  }

  public enum Theme {
    Dark,
    Light,
    Kanagawa,
  };

  private static Theme themeFromFile(String filename) {
    String fn = filename.substring(filename.lastIndexOf("/") + 1);
    switch (fn) {
      case "dark.css":
        return Theme.Dark;
      case "light.css":
        return Theme.Light;
      case "kanagawa.css":
        return Theme.Kanagawa;
      default:
        return Theme.Light;

    }

  }

  private static TemperatureUnit tempUnit;
  private static double[] lastLoc;
  private static int lastScene;
  private static String themeFile;
  private static Theme theme;

  /**
   * get the current temp unit saved in settings
   *
   * @return the current temp unit saved in settings
   */
  public static TemperatureUnit getTempUnit() {
    return tempUnit;
  }

  /**
   * set the temp unit in settings
   * 
   * @param tempUnit the new unit
   */
  public static void setTempUnit(TemperatureUnit tempUnit) {
    Settings.tempUnit = tempUnit;
  }

  /**
   * get the last location saved in settings
   *
   * @return the last location saved in settings
   */
  public static double[] getLastLoc() {
    return lastLoc;
  }

  /**
   * set the last location in settings
   * 
   * @param lat the latitude
   * @param lon the longitude
   */
  public static void setLastLoc(double lat, double lon) {
    Settings.lastLoc = new double[] { lat, lon };
  }

  /**
   * get the last scene saved in settings
   *
   * @return the last scene saved in settings
   */
  public static int getLastScene() {
    return lastScene;
  }

  /**
   * set the last scene in settings
   * 
   * @param lastScene the scene index as seen in the sidebar
   */
  public static void setLastScene(int lastScene) {
    Settings.lastScene = lastScene;
  }

  /**
   * get the current color theme's filename
   *
   * @return the filname of the current color theme
   */
  public static String getThemeFile() {
    return themeFile;
  }

  /**
   * get the current color theme's filename
   *
   * @return the filname of the current color theme
   */
  public static Theme getTheme() {
    return theme;
  }

  /**
   * set the color theme's filename
   *
   * @param filename the filname of the new color theme
   */
  public static void setThemeFile(String filename) {
    themeFile = filename;
    theme = themeFromFile(filename);
  }

  /**
   * An {@code Exception} caused by {@code Settings} being unable to load
   */
  public static class SettingsLoadException extends Exception {
  }

  /**
   * statically load {@code Settings} with settings from the saved file
   */
  public static void loadSettings() throws SettingsLoadException {
    String settingsStr;

    try {
      // get the json file
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      InputStream is = cl.getResourceAsStream("settings/settings.json");

      // read the file into a string
      settingsStr = new String(is.readAllBytes());

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
      throw new SettingsLoadException();
    }

    // turn the string into an object
    SettingsLoader s = getObject(settingsStr);

    // load the settings
    s.toSettings();
  }

  /**
   * statically save {@code Settings} into the save file
   */
  public static void saveSettings() {
    String json;

    try {
      // turn the settings into a json string
      ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
      json = ow.writeValueAsString(SettingsLoader.fromSettings());

    } catch (JsonProcessingException e) {
      System.err.println("Failed to process settings into json. Settings not saved.");
      return;
    }

    try {
      // open the settings file
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      PrintWriter writer = new PrintWriter(new File(cl.getResource("settings/settings.json").getPath()));

      // write the json then close the file
      writer.write(json);
      writer.close();

    } catch (FileNotFoundException e) {
      System.err.println("Failed to write settings to file. Settings not saved.");
      return;
    }
  }

  /**
   * parse a json string into a {@code SettingsLoader} object
   *
   * @param json the json to parse in string form
   * @return the {@code SettingsLoader} object parsed
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
