package team6072.vision.visionProcessing;

import java.util.ArrayList;

import edu.wpi.cscore.CvSink;
import team6072.vision.cameras.CameraMaster;
import team6072.vision.logging.LogWrapper;
import team6072.vision.logging.LoggerConstants;
import team6072.vision.logging.LogWrapper.FileType;
import team6072.vision.visionProcessing.threads.IntakeThread;
import team6072.vision.visionProcessing.threads.TurretThread;

public class VisionThreadMaster {

    private LogWrapper mLog;
    private static VisionThreadMaster mPipelineMaster;
    private ArrayList<VisionThreadBase> mThreads;

    public static VisionThreadMaster getInstance() {
        if (mPipelineMaster == null) {
            mPipelineMaster = new VisionThreadMaster();
        }
        return mPipelineMaster;
    }

    private VisionThreadMaster() {
        mLog = new LogWrapper(FileType.VISION_THREAD, "Vision Thread Master",
                LoggerConstants.VISION_THREAD_MASTER_PERMISSION);
        mThreads = new ArrayList<VisionThreadBase>();
        mLog.alarm("Starting!");
        startThreads();
    }

    public void startThreads() {
        ArrayList<CvSink> sinks = CameraMaster.getInstance().getCameraSinks();

        if (sinks.size() >= 2) {

            // create Threads
            TurretThread turretThread = new TurretThread(sinks.get(0));
            IntakeThread intakeThread = new IntakeThread(sinks.get(1));
            // add threads to array list
            mThreads.add(turretThread);
            mThreads.add(intakeThread);
            // start threads

            if (mThreads.size() == 0) {
                mLog.error("THere is NO existing thread!");
            } else {
                for (int i = 0; i < mThreads.size(); i++) {
                    mLog.alarm("Starting Thread " + i + "!");
                    mThreads.get(i).start();
                }
            }

        } else {
            mLog.error("NOT ENOUGH CAMERAS TO INITIATE VISION THREADS");
        }
    }

    public void killThreads() {
        for (int i = 0; i < mThreads.size(); i++) {
            mThreads.get(i).end();
        }
        mThreads = new ArrayList<VisionThreadBase>();
    }

}