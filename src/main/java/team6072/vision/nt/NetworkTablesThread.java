package team6072.vision.nt;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import team6072.vision.PiConfig;
import team6072.vision.logging.LogWrapper;
import team6072.vision.logging.LoggerConstants;
import team6072.vision.logging.LogWrapper.FileType;

/**
 * Extends thread, use .start() to start the thread use .end() to end the thread
 */
public class NetworkTablesThread extends Thread {

  private static NetworkTablesThread mNetworkTablesController;
  private LogWrapper mLog;

  private NetworkTableInstance ntinst;
  private NetworkTable mVisionTable;
  public NetworkTableEntry entry1;
  public NetworkTableEntry entry2;
  public NetworkTableEntry entry3;
  public NetworkTableEntry entry4;

  public double priorEntry3;
  public double priorEntry4;

  public boolean mRunnable = true;

  public static NetworkTablesThread getInstance() {
    if (mNetworkTablesController == null) {
      mNetworkTablesController = new NetworkTablesThread();
    }
    return mNetworkTablesController;
  }

  private NetworkTablesThread() {
    mLog = new LogWrapper(FileType.NETWORK_TABLES, "Network Tables Thread", LoggerConstants.NETWORK_TABLES_PERMISSION);
    ntinst = NetworkTableInstance.getDefault();
    PiConfig mPiConfig = PiConfig.getInstance();
    mLog.print("Starting Network Tables");
    if (mPiConfig.isNTServer()) {
      mLog.print("Setting up NetworkTables server");
      ntinst.startServer();
    } else {
      mLog.print("Setting up NetworkTables client for team " + mPiConfig.getTeamNumber());
      ntinst.startClientTeam(mPiConfig.getTeamNumber());
    }

    mVisionTable = ntinst.getTable("Vision Table");
    entry1 = mVisionTable.getEntry("entry1");
    entry2 = mVisionTable.getEntry("entry2");
    entry3 = mVisionTable.getEntry("entry3");
    entry4 = mVisionTable.getEntry("entry4");

    entry3.setDouble(4);
    entry4.setDouble(85);

    // priorEntry3 = entry3.getDouble(0);
    // priorEntry4 = entry4.getDouble(0);
  }

  public void run() {
    while (mRunnable) {
      double curnEntry3 = entry3.getDouble(priorEntry3);
      double curnEntry4 = entry4.getDouble(priorEntry4);
      if (curnEntry3 != priorEntry3) {
        mLog.alarm("Entry 3 has Changed!");
        mLog.debug("Entry3", curnEntry3);
        priorEntry3 = curnEntry3;
      }
      if (curnEntry4 != priorEntry4) {
        mLog.alarm("Entry 4 has Changed!");
        mLog.debug("Entry4", curnEntry4);
        priorEntry4 = curnEntry4;
      }
    }
  }

  public void end(){
    mRunnable = false;
  }

}