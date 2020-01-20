package team6072.vision.visionProcessing;

import java.util.ArrayList;

import edu.wpi.cscore.CvSink;
import team6072.vision.CameraMaster;
import team6072.vision.logging.LogWrapper;
import team6072.vision.logging.LoggerConstants;
import team6072.vision.logging.LogWrapper.FileType;
import team6072.vision.visionProcessing.pipelines.IntakePipeline;
import team6072.vision.visionProcessing.pipelines.TurretPipeline;
import team6072.vision.visionProcessing.updateListeners.IntakeUpdateListener;
import team6072.vision.visionProcessing.updateListeners.TurretUpdateListener;

public class VisionThreadMaster{

    private LogWrapper mLog;
    private static VisionThreadMaster mPipelineMaster;
    private ArrayList<VisionThread> mThreads;

    public static VisionThreadMaster getInstance(){
        if(mPipelineMaster == null){
            mPipelineMaster = new VisionThreadMaster();
        }
        return mPipelineMaster;
    }

    private VisionThreadMaster(){
        mLog = new LogWrapper(FileType.PIPELINE, "PipeLine Master", LoggerConstants.PIPELINE_MASTER_PERMISSION);
        mThreads = new ArrayList<VisionThread>();
        startThreads();
    }

    public void startThreads(){
        ArrayList<CvSink> sinks = CameraMaster.getInstance().getCameraSinks();
        if(sinks.size() >= 2){
            mThreads.add(new VisionThread(sinks.get(0), new TurretPipeline(), new TurretUpdateListener()));
            mThreads.add(new VisionThread(sinks.get(1), new IntakePipeline(), new IntakeUpdateListener()));

            for(int i = 0; i < mThreads.size(); i++){
                mThreads.get(i).start();
            }
        } else {
            mLog.error("NOT ENOUGH CAMERAS TO INITIATE VISION THREADS");
        }
    }

    public void killThreads(){
        for(int i = 0; i < mThreads.size(); i++){
            mThreads.get(i).end();
        }
        mThreads = new ArrayList<VisionThread>();
    }

}