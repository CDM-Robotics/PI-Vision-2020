package team6072.vision.nt;

import team6072.vision.logging.LogWrapper;
import team6072.vision.logging.LoggerConstants;
import team6072.vision.logging.LogWrapper.FileType;

public class NetworkTablesSystem{
  
  private static NetworkTablesSystem mNetworkTablesController;
  private LogWrapper mLog;

    public static NetworkTablesSystem getInstance(){
      if(mNetworkTablesController == null){
        mNetworkTablesController = new NetworkTablesSystem();
      }
      return mNetworkTablesController;
    }

    private NetworkTablesSystem(){
      mLog = new LogWrapper(FileType.NETWORK_TABLES, "Network Tables Controller", LoggerConstants.NETWORK_TABLES_PERMISSION);
      
      mLog.print("Starting Network Tables");


      // NetworkTable mVisionTable = ntinst.getTable("Vision Table");
      // NetworkTableEntry mPI1 = mVisionTable.getEntry("mPI1");
      
      

    }

    

}