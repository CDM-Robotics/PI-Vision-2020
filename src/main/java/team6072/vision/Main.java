package team6072.vision;

import java.util.ArrayList;

import team6072.vision.logging.LogWrapper;
import team6072.vision.nt.NetworkTablesThread;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.networktables.NetworkTableInstance;

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
   *    These functions will run from the NetworkTablesSystem, but will require functions to be created in other 
   *      branches of the PI
   *          for example: switching the Cameras
   *              this will require getting the CvSinks, switching them in the array, 
   *                killing the current pipelines in the PipelineMasterSYstem, and then reinitializing the PipelineMaster
   * Fourth run the PipelineMaster so that the Vision threads can start running the 
   *    pipeline and updateListener functions on a loop
   *    cvsinks are given to the VisionThreads
   *    pipeline function starts processing the image
   *    then the UpdateListener function feeds
   *    information to networkTables using the NetworkTablesSystem.
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
    NetworkTablesThread.getInstance();
    

    ArrayList<CvSink> cameraSinks = CameraSystem.getInstance().getCameraSinks();

    for (int i = 0; i < cameraSinks.size(); i++) {
      mLog.print("Cameras array number " + i + " : " + cameraSinks.get(i).toString());
    }

    NetworkTablesThread.getInstance().start();
    for(int i = 0; true; i++){
      NetworkTablesThread.getInstance().entry1.setDouble(i);
      NetworkTablesThread.getInstance().entry2.setDouble(100000 - i);
    }


/*     //EXAMPLE OF THE WIZARDRY **********************

    // if (cameraSinks.size() > 0) {
    //   source = CameraServer.getInstance().putVideo("Testing image", 320, 240);
    //   Mat mat = new Mat();
    //   while(true){
    //     cameraSinks.get(0).grabFrame(m);
    //     source.putFrame(m);
    //   }
    // } */


    // loop forever
    // for (;;) {
    //   try {
    //     Thread.sleep(10000);
    //   } catch (InterruptedException ex) {
    //     return;
    //   }
    // }
  }
}


/**
 * Switch Cameras function 
 *    -This function will be in the Network Tables System and will be triggered upon the change of a
 *      Network tables value
 * 1) Switch CvSinks in teh CameraSystem, this function should be innside the CameraSYstem
 * 2) Kill the current Vision Threads, 
 *      this will be a function in the pipelineMasterSystem that simply runs through the array of VisionThreads
 *      and kills them... one by one
 * 3) restart the VisionThreads with the new CvSink Array. 
 */