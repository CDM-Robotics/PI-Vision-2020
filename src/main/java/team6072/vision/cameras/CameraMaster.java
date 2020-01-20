package team6072.vision.cameras;

import team6072.vision.cameras.CameraConfig;
import team6072.vision.logging.LogWrapper;
import team6072.vision.logging.LoggerConstants;
import edu.wpi.cscore.VideoSource;
import java.util.ArrayList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import team6072.vision.logging.LogWrapper.FileType;
import com.google.gson.JsonObject;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

/**
 * The main purpose of this class is to simply take the starting camera
 * configurations from the PiConfig class and change them into UsbCameras and
 * from Usbcameras into CvSinks. From there we can use CvSinks to get all the
 * frames that we need.
 */
public class CameraMaster {

  private static CameraMaster mSystem;
  private LogWrapper mLog;
  private ArrayList<CameraConfig> cameraConfigs;
  private ArrayList<VideoSource> videoSources;
  private ArrayList<CvSink> cameraSinks;

  private String configFile = "/boot/frc.json";

  public static CameraMaster getInstance() {
    if (mSystem == null) {
      mSystem = new CameraMaster();
    }
    return mSystem;
  }

  private CameraMaster() {
    mLog = new LogWrapper(FileType.CAMERA_SYSTEM, "Camera System", LoggerConstants.CAMERA_SYSTEM_PERMISSION);

    cameraConfigs = new ArrayList<CameraConfig>();
    videoSources = new ArrayList<VideoSource>();
    cameraSinks = new ArrayList<CvSink>();

    JsonArray cameraConfigsJson = getCameraConfigJson();
    for (JsonElement cameraConfig : cameraConfigsJson) {
      cameraConfigs.add(getCameraConfig(cameraConfig.getAsJsonObject()));
    }

    // creates all the video sources and shoves them into the array videoSources
    mLog.print("Starting Cameras");
    for (int i = 0; i < cameraConfigs.size(); i++) {
      mLog.print("setting up camera " + i);
      videoSources.add(getVideoSource(cameraConfigs.get(i)));
    }

    // creats all the CvSinks from Video Sources in the videoSources
    // array and shoves them innto the cameraSinks array
    for (int i = 0; i < videoSources.size(); i++) {
      mLog.print("setting up camera " + i);
      cameraSinks.add(getCameraSinks(videoSources.get(i)));
    }

    // startAutomaticCapture();
  }

  /**************************************************************************
   **************************************************************************
   ************************ Helper functions ******************************
   ************************************************************************** 
   *************************************************************************/

  /**
   * Reads the file on the raspberry pi of Camera Configs and returns the
   * information as a Json Array
   * 
   * 
   JSON format:
   {
       "team": <team number>,
       "ntmode": <"client" or "server", "client" if unspecified>
       "cameras": [
           {
               "name": <camera name>
               "path": <path, e.g. "/dev/video0">
               "pixel format": <"MJPEG", "YUYV", etc>   // optional
               "width": <video mode width>              // optional
               "height": <video mode height>            // optional
               "fps": <video mode fps>                  // optional
               "brightness": <percentage brightness>    // optional
               "white balance": <"auto", "hold", value> // optional
               "exposure": <"auto", "hold", value>      // optional
               "properties": [                          // optional
                   {
                       "name": <property name>
                       "value": <property value>
                   }
               ],
               "stream": {                              // optional
                   "properties": [
                       {
                           "name": <stream property name>
                           "value": <stream property value>
                       }
                   ]
               }
           }
       ]
       "switched cameras": [
           {
               "name": <virtual camera name>
               "key": <network table key used for selection>
               // if NT value is a string, it's treated as a name
               // if NT value is a double, it's treated as an integer index
           }
       ]
   }
 
   * 
   * @return
   */
  private JsonArray getCameraConfigJson() {
    mLog.print("Reading PI Configuration");
    JsonElement top;
    try {
      mLog.print("ConfigFileName " + configFile);
      top = new JsonParser().parse(Files.newBufferedReader(Paths.get(configFile)));

      // top level must be an object
      if (!top.isJsonObject()) {
        mLog.error("must be JSON object");
      }
      JsonObject obj = top.getAsJsonObject();

      // cameras
      JsonElement camerasElement = obj.get("cameras");
      if (camerasElement == null) {
        mLog.error("could not read cameras");
      }
      JsonArray mCamerasArray = camerasElement.getAsJsonArray();
      return mCamerasArray;

    } catch (IOException ex) {
      mLog.error("could not open '" + configFile + "': " + ex);
      return null;
    }
  }

  /**
   * Takes a JsonObject of a Camera Configuration and returns the information as a
   * CameraConfig Object. This object can be used to create USBCamera Objects
   * which are the key to capturing frames from our USB Cameras... Duh...
   */
  private CameraConfig getCameraConfig(JsonObject config) {
    CameraConfig cam = new CameraConfig();
    JsonElement nameElement = config.get("name"); // name
    JsonElement pathElement = config.get("path"); // path

    if (nameElement == null) {
      mLog.error("could not read camera name");
    }
    if (pathElement == null) {
      mLog.error("camera '" + cam.name + "': could not read path");
    }

    // Camera Config properties
    cam.name = nameElement.getAsString();
    cam.path = pathElement.getAsString();
    cam.streamConfig = config.get("stream");
    cam.config = config;
    return cam;
  }

  /**
   * Converts a CameraConfig object into a Video Source Object and returns the
   * video source.
   */
  private VideoSource getVideoSource(CameraConfig config) {
    mLog.print("Starting camera '" + config.name + "' on " + config.path);
    UsbCamera camera = new UsbCamera(config.name, config.path);
    Gson gson = new GsonBuilder().create();
    camera.setConfigJson(gson.toJson(config.config));
    camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);
    return camera;
  }

  /**
   * This is the MAGIc converting video sources (our usbcameras), and converting
   * it into CVSinks CVsinks are a class that we can use to get frames in MAT
   * form, so that we can manipulate it using OpeenCV. THIS STEP IS ESSENTIAL AND
   * IS THE WIZARDRY THAT CONNECTS OPENCV TO THE FRC CODE LIBRARY
   * 
   * After you have the CvSink you can use a mathod called sink.grabFrame(Mat m)
   * to grab the current frame. This method takes an empty MAT m and changes it
   * into the current frame. Then you can send it back to Network Tables using
   * CvSource and its function source.putFrame(Mat m). THis will send back the
   * fram to be viewed
   * 
   * @param camera
   * @return
   */
  private CvSink getCameraSinks(VideoSource camera) {
    CameraServer inst = CameraServer.getInstance();
    CvSink sink = inst.getVideo(camera);
    return sink;
  }

  /**************************************************************************
   **************************************************************************
   ************************ Public functions ******************************
   ************************************************************************** 
   *************************************************************************/

  public void switchCvSinks(){
    ArrayList<CvSink> tempCvSink = new ArrayList<CvSink>();
    for(int i = 0; i < cameraSinks.size(); i++){
      tempCvSink.add(cameraSinks.get((cameraSinks.size() - 1) - i));
    }
    cameraSinks = tempCvSink;
  }

  public void startAutomaticCapture(){
    // starts automatic capture of the frame.
    // This is the function that does all the capture and sourcing for you.
    for(VideoSource videoSource : videoSources){
      CameraServer inst = CameraServer.getInstance();
      inst.startAutomaticCapture(videoSource);
    }
  }

  public ArrayList<VideoSource> getVideoSources() {
    return videoSources;
  }

  public ArrayList<CvSink> getCameraSinks() {
    return cameraSinks;
  }
}
