package team6072.vision;

import team6072.vision.configs.CameraConfig;
import team6072.vision.logging.LogWrapper;
import team6072.vision.logging.LoggerConstants;
import team6072.vision.configs.SwitchedCameraConfig;
import edu.wpi.cscore.VideoSource;
import java.util.ArrayList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import team6072.vision.logging.LogWrapper.FileType;
import com.google.gson.JsonObject;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CameraSystem {

  private static CameraSystem mSystem;
  private LogWrapper mLog;
  public ArrayList<CameraConfig> cameraConfigs = new ArrayList<>();
  public ArrayList<SwitchedCameraConfig> switchedCameraConfigs = new ArrayList<>();
  public ArrayList<VideoSource> cameras = new ArrayList<>();
  public ArrayList<CvSink> cameraSinks = new ArrayList<>();

  public static CameraSystem getInstance() {
    if (mSystem == null) {
      mSystem = new CameraSystem();
    }
    return mSystem;
  }

  private CameraSystem() {

    JsonArray cameraConfigsJson = PiConfig.getInstance().getCamerasArray();
    mLog = new LogWrapper(FileType.CAMERA_SYSTEM, "Camera System", LoggerConstants.CAMERA_SYSTEM_PERMISSION);

    for (JsonElement cameraConfig : cameraConfigsJson) {
      if (!readCameraConfig(cameraConfig.getAsJsonObject())) {
        // we are seriously screwed
        mLog.error("Camera not initialized, we are seriously screwed.");
      }
    }

    // start cameras
    mLog.print("Starting Cameras");
    for (int i = 0; i < cameraConfigs.size(); i++) {
      mLog.print("setting up camera " + i);
      cameras.add(startCamera(cameraConfigs.get(i)));
    }

    for (int i = 0; i < cameras.size(); i++) {
      mLog.print("setting up camera " + i);
      cameraSinks.add(startCameraSinks(cameras.get(i)));
    }

    mLog.print("Starting Vision Pipelines");
    if (cameras.size() >= 1) {
      // VisionThread visionThread = new VisionThread(cameras.get(0),
      // new MyPipeline(), pipeline -> {
      // // do something with pipeline results
      // });
      /*
       * something like this for GRIP: VisionThread visionThread = new
       * VisionThread(cameras.get(0), new GripPipeline(), pipeline -> { ... });
       */
      // visionThread.start();
    }
  }

  /**
   * Read single camera configuration.
   */
  private boolean readCameraConfig(JsonObject config) {
    CameraConfig cam = new CameraConfig();

    // name
    JsonElement nameElement = config.get("name");
    if (nameElement == null) {
      mLog.error("could not read camera name");
      return false;
    }
    cam.name = nameElement.getAsString();

    // path
    JsonElement pathElement = config.get("path");
    if (pathElement == null) {
      mLog.error("camera '" + cam.name + "': could not read path");
      return false;
    }
    cam.path = pathElement.getAsString();

    // stream properties
    cam.streamConfig = config.get("stream");

    cam.config = config;

    cameraConfigs.add(cam);
    return true;
  }

  /**
   * This is the MAGIc converting video sources (our usbcameras), and converting it into CVSinks
   * CVsinks are a class that we can use to get frames in MAT form, so that we can manipulate it using 
   * OpeenCV.  THIS STEP IS ESSENTIAL AND IS THE WIZARDRY THAT CONNECTS OPENCV TO THE FRC CODE LIBRARY
   * 
   * After you have the CvSink you can use a mathod called sink.grabFrame(Mat m) to grab the current frame.
   * This method takes an empty MAT m and changes it into the current frame. Then you can send it back to 
   * Network Tables using CvSource and its function source.putFrame(Mat m).  THis will send back the fram to be viewed
   * @param camera
   * @return
   */
  public CvSink startCameraSinks(VideoSource camera) {

    CameraServer inst = CameraServer.getInstance();
    CvSink sink = inst.getVideo(camera);

    return sink;
  }

  /**
   * Start running the camera.
   */
  public VideoSource startCamera(CameraConfig config) {
    mLog.print("Starting camera '" + config.name + "' on " + config.path);
    UsbCamera camera = new UsbCamera(config.name, config.path);

    CameraServer inst = CameraServer.getInstance();
    Gson gson = new GsonBuilder().create();
    MjpegServer server = inst.startAutomaticCapture(camera);

    camera.setConfigJson(gson.toJson(config.config));
    camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

    return camera;
  }

  public ArrayList<VideoSource> getCameras() {
    return cameras;
  }

  public ArrayList<CvSink> getCameraSinks() {
    return cameraSinks;
  }
}
