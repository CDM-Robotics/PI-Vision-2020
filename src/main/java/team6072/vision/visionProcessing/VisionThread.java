package team6072.vision.visionProcessing;

import edu.wpi.cscore.CvSink;
import team6072.vision.logging.LogWrapper;
import team6072.vision.logging.LoggerConstants;
import team6072.vision.visionProcessing.pipelines.Pipeline;
import org.opencv.core.Mat;
import team6072.vision.visionProcessing.updateListeners.UpdateListener;
import team6072.vision.logging.LogWrapper.FileType;

public class VisionThread extends Thread{

    private LogWrapper mLog;
    private CvSink mCvSink;
    private Pipeline mPipeline;
    private UpdateListener mUpdateListener;
    private boolean mRunnable = true;

    public VisionThread(CvSink cvSink, Pipeline pipeline, UpdateListener updateListener){
        mCvSink = cvSink;
        mPipeline = pipeline;
        mUpdateListener = updateListener;
        mLog = new LogWrapper(FileType.VISION_THREAD, "Vision Thread", LoggerConstants.VISION_THREAD_PERMISSION);
    }

    public void run(){
        Mat m = new Mat();
        while(mRunnable){
            mCvSink.grabFrame(m);
            mLog.alarm("Grabbed frame!");
            mUpdateListener.updateNetworkTables(mPipeline.process(m));
        }
    }

    public void end(){
        mRunnable = false;
    }
}