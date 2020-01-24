package team6072.vision.nt;

import java.util.ArrayList;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import team6072.vision.logging.LogWrapper;
import team6072.vision.nt.onChangeListeners.NTOnChangeListener;
import team6072.vision.nt.onChangeListeners.NTSwitchCamerasListener;
import team6072.vision.logging.LoggerConstants;
import team6072.vision.logging.LogWrapper.FileType;

/**
 * Extends thread, use .start() to start the thread use .end() to end the thread
 */
public class NetworkTablesThread extends Thread {

  // standard Variables //
  private static NetworkTablesThread mNetworkTablesController;
  private LogWrapper mLog;
  public boolean mRunnable = true;

  // Network Tables Variables //
  private NetworkTableInstance ntinst;
  private NetworkTable mVisionTable;
  private ArrayList<NTOnChangeListener> mListeners;

  public static NetworkTablesThread getInstance() {
    if (mNetworkTablesController == null) {
      mNetworkTablesController = new NetworkTablesThread();
    }
    return mNetworkTablesController;
  }

  private NetworkTablesThread() {
    mLog = new LogWrapper(FileType.NETWORK_TABLES, "Network Tables Thread", LoggerConstants.NETWORK_TABLES_PERMISSION);

    // initializing Network Tables //
    ntinst = NetworkTableInstance.getDefault();

    mLog.print("Setting up NetworkTables client for team " + 6072);
    ntinst.startClientTeam(6072);
    
    mListeners = new ArrayList<NTOnChangeListener>();
    mVisionTable = ntinst.getTable("Vision Table");


    // initializing Entries //

    // initializing listeners tp the mListeners Array //

  }

  public void run() {
    while (mRunnable) {
      for (NTOnChangeListener ntOnChangeListener : mListeners) {
        ntOnChangeListener.checkState();
      }
    }
  }

  public void end() {
    mRunnable = false;
  }

}