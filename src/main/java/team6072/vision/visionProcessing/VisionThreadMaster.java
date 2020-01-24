package team6072.vision.visionProcessing;

import java.util.ArrayList;

import edu.wpi.cscore.CvSink;
import team6072.vision.cameras.CameraMaster;
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
        mLog.alarm("Starting!");
        startThreads();
    }

    public void startThreads(){
        ArrayList<CvSink> sinks = CameraMaster.getInstance().getCameraSinks();
        // for(CvSink sink : sinks){
        //     mLog.print(sink.toString());
        // }
        if(sinks.size() >= 2){
            VisionThread thread1 = new VisionThread(sinks.get(0), new TurretPipeline(), new TurretUpdateListener());
            mThreads.add(thread1);
            // mThreads.add(new VisionThread(sinks.get(1), new IntakePipeline(), new IntakeUpdateListener()));
            if(mThreads.get(0) != null){
                mLog.alarm("THere is an existing thread!");
            }
            for(int i = 0; i < mThreads.size(); i++){
                mLog.alarm("Starting Thread " + i + "!");
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