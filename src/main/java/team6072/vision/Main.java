package team6072.vision;

import java.util.ArrayList;

import team6072.vision.logging.LogWrapper;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoSink;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.networktables.NetworkTableInstance;
import team6072.vision.nt.NetworkTablesController;
import org.opencv.core.Mat;

/*
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
 */

public final class Main {

  private static LogWrapper mLog;
  private static PiConfig mPiConfig;
  public static int team;
  public static boolean server;
  public static CvSource source;

  private Main() {
  }

  /**
   * Main.
   * This function starts the Pi's other threads and leads it to GLORY inn the following way
   * 
   * first, the main function must take the information from PiConfig and parse out the existing CameraConfigs
   *    this is handled, right now, with the class PiConfig.  I may make it More organized later
   * two, it then must use the cameraConfigs from PiConfig to create the required USBCameras it needs,
   *    and then from there it must create CvSinks from the VideoSources from USBCameras.  
   *        This is handled right now with CameraSystem.java.  Will make it more organized later.
   * Third, it then must initialized Network Tables and initialize the values that will go on to control the PI
   *    After creating the values, The NetworkTables System must create listeners to certian functions that will
   *    manipulate the pi in ways that are necessary for its functionality.  Like
   *          switching the cameras, or restarting the PIor restarting the threads, etc.
   * Fourth run the pipelines so that the cvsinks are given to the pipelines and the pipelines
   *    are processing the image
   * Then create and run the UpdateListener threads that will run on a loop takign information from
   *    the pipelines adn feed it to networkTables using the NetworkTablesSystem.
   */
  public static void main(String... args) {

    mLog = new LogWrapper(LogWrapper.FileType.MAIN, "Main", LogWrapper.Permission.ALL);

    // read configuration
    mLog.print("Reading PI Configuration");
    mPiConfig = PiConfig.getInstance();
    if (!mPiConfig.isInitialized()) {
      return;
    }

    // start NetworkTables
    // NetworkTablesController.getInstance();
    NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    if (mPiConfig.isNTServer()) {
      mLog.print("Setting up NetworkTables server");
      ntinst.startServer();
    } else {
      mLog.print("Setting up NetworkTables client for team " + mPiConfig.getTeamNumber());
      ntinst.startClientTeam(mPiConfig.getTeamNumber());
    }

    ArrayList<CvSink> cameraSinks = CameraSystem.getInstance().getCameraSinks();

    for (int i = 0; i < cameraSinks.size(); i++) {
      mLog.print("Cameras array number " + i + " : " + cameraSinks.get(i).toString());
    }

    if (cameraSinks.size() > 0) {
      Mat mat = new Mat();
      for(int i = 0; i < cameraSinks.size(); i++){
        // how do i put unique camera pipelines on these

      }
    }

    // for(int i = 0; i < cameras.size(); i++){
    // CvSource source = CameraServer.getInstance().putVideo("PI" + i, 160, 120);
    // source.putFrame(cameras.get(i));

    // }

    // loop forever
    for (;;) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) {
        return;
      }
    }
  }
}
