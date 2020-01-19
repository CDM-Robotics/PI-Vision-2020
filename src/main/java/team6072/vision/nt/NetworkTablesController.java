package team6072.vision.nt;

import edu.wpi.first.networktables.NetworkTableInstance;
import team6072.vision.PiConfig;
import team6072.vision.logging.LogWrapper;
import team6072.vision.logging.LoggerConstants;
import team6072.vision.logging.LogWrapper.FileType;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

public class NetworkTablesController{
  
  private static NetworkTablesController mNetworkTablesController;
  private LogWrapper mLog;

    public static NetworkTablesController getInstance(){
      if(mNetworkTablesController == null){
        mNetworkTablesController = new NetworkTablesController();
      }
      return mNetworkTablesController;
    }

    private NetworkTablesController(){
      PiConfig mPiConfig = PiConfig.getInstance();
      mLog = new LogWrapper(FileType.NETWORK_TABLES, "Network Tables Controller", LoggerConstants.NETWORK_TABLES_PERMISSION);
      
      mLog.print("Starting Network Tables");


      // NetworkTable mVisionTable = ntinst.getTable("Vision Table");
      // NetworkTableEntry mPI1 = mVisionTable.getEntry("mPI1");
      
      

    }

    

}