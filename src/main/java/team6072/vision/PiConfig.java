package team6072.vision;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import team6072.vision.logging.LogWrapper;
import team6072.vision.logging.LogWrapper.FileType;
import team6072.vision.logging.LogWrapper.Permission;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import team6072.vision.logging.LoggerConstants;

public class PiConfig {

  private LogWrapper mLog;
  private String configFile = "/boot/frc.json";

  private int mTeamNumber;
  private boolean mIsNTServer;
  private boolean mIsPiInitialized;
  private JsonArray mCamerasArray;

  private static PiConfig mPiConfig;

  public static PiConfig getInstance() {
    if (mPiConfig == null) {
      mPiConfig = new PiConfig();
    }
    return mPiConfig;
  }

  /**
   * Read configuration file.
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  private PiConfig() {
    mLog = new LogWrapper(FileType.CONFIG, "PiConfig", LoggerConstants.PI_CONFIG_PERMISSION);

    // parse file
    JsonElement top;
    try {
      mLog.print("ConfigFileName " + configFile);
      top = new JsonParser().parse(Files.newBufferedReader(Paths.get(configFile)));

      // top level must be an object
      if (!top.isJsonObject()) {
        mLog.error("must be JSON object");
        mIsPiInitialized = false;
      }
      JsonObject obj = top.getAsJsonObject();

      // team number
      JsonElement teamElement = obj.get("team");
      if (teamElement == null) {
        mLog.error("could not read team number");
        mIsPiInitialized = false;
      }
      mTeamNumber = teamElement.getAsInt();

      // ntmode (optional)
      if (obj.has("ntmode")) {
        String str = obj.get("ntmode").getAsString();
        if ("client".equalsIgnoreCase(str)) {
          mIsNTServer = false;
        } else if ("server".equalsIgnoreCase(str)) {
          mIsNTServer = true;
        } else {
          mLog.error("could not understand ntmode value '" + str + "'");
        }
      }

      // cameras
      JsonElement camerasElement = obj.get("cameras");
      if (camerasElement == null) {
        mLog.error("could not read cameras");
        mIsPiInitialized = false;
      }
      mCamerasArray = camerasElement.getAsJsonArray();
      // for (JsonElement camera : cameras) {
      // if (!readCameraConfig(camera.getAsJsonObject())) {
      // initialized = false;
      // }
      // }

      // if (obj.has("switched cameras")) {
      // JsonArray switchedCameras = obj.get("switched cameras").getAsJsonArray();
      // for (JsonElement camera : switchedCameras) {
      // if (!readSwitchedCameraConfig(camera.getAsJsonObject())) {
      // return false;
      // }
      // }
      // }

      mIsPiInitialized = true;
    } catch (IOException ex) {
      mLog.error("could not open '" + configFile + "': " + ex);
      mIsPiInitialized = false;
    }

  }

  public int getTeamNumber() {
    return mTeamNumber;
  }

  public boolean isNTServer() {
    return mIsNTServer;
  }

  public boolean isInitialized() {
    return mIsPiInitialized;
  }

  public JsonArray getCamerasArray() {
    return mCamerasArray;
  }
}