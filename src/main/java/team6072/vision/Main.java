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

    for(int i = 0; i < cameraSinks.size(); i++){
      mLog.print("Cameras array number " + i + " : " + cameraSinks.get(i).toString());
    }

    if(cameraSinks.size() > 0){
      
      source = CameraServer.getInstance().putVideo("Testing image", 320, 240);
      Mat mat = new Mat();
      while(true){
        
        long num = cameraSinks.get(0).grabFrame(mat);
        mLog.alarm("YOOOOOOOO!!!!");
        mLog.debug("Timeout", num);
        source.putFrame(mat);
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
