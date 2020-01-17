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
import java.util.List;
import com.google.gson.JsonObject;
import edu.wpi.cscore.UsbCamera;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.first.cameraserver.CameraServer;

public class CameraSystem{

    private static CameraSystem mSystem;
    private LogWrapper mLog;
    public List<CameraConfig> cameraConfigs = new ArrayList<>();
    public List<SwitchedCameraConfig> switchedCameraConfigs = new ArrayList<>();
    public List<VideoSource> cameras = new ArrayList<>();

    public static CameraSystem getInstance(){
        if(mSystem == null){
            mSystem = new CameraSystem();
        }
        return mSystem;
    }
    private CameraSystem(){
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
        for (CameraConfig config : cameraConfigs) {
            cameras.add(startCamera(config));
        }

        mLog.print("Starting Vision Pipelines");
        if (cameras.size() >= 1) {
          // VisionThread visionThread = new VisionThread(cameras.get(0),
          //         new MyPipeline(), pipeline -> {
          //   // do something with pipeline results
          // });
          /* something like this for GRIP:
          VisionThread visionThread = new VisionThread(cameras.get(0),
                  new GripPipeline(), pipeline -> {
            ...
          });
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
   * Start running the camera.
   */
  public VideoSource startCamera(CameraConfig config) {
    mLog.print("Starting camera '" + config.name + "' on " + config.path);
    CameraServer inst = CameraServer.getInstance();
    UsbCamera camera = new UsbCamera(config.name, config.path);
    MjpegServer server = inst.startAutomaticCapture(camera);

    Gson gson = new GsonBuilder().create();

    camera.setConfigJson(gson.toJson(config.config));
    camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

    if (config.streamConfig != null) {
      server.setConfigJson(gson.toJson(config.streamConfig));
    }

    return camera;
  }

}
