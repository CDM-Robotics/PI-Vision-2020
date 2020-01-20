package team6072.vision.visionProcessing;

import edu.wpi.cscore.CvSink;
import team6072.vision.visionProcessing.pipelines.Pipeline;
import org.opencv.core.Mat;
import team6072.vision.visionProcessing.updateListeners.UpdateListener;

public class VisionThread extends Thread{

    private CvSink mCvSink;
    private Pipeline mPipeline;
    private UpdateListener mUpdateListener;
    private boolean mRunnable = true;

    public VisionThread(CvSink cvSink, Pipeline pipeline, UpdateListener updateListener){
        mCvSink = cvSink;
        mPipeline = pipeline;
        mUpdateListener = updateListener;
    }

    public void run(){
        Mat m = new Mat();
        while(mRunnable){
            mCvSink.grabFrame(m);
            mPipeline.process(m);
            mUpdateListener.updateNetworkTables();
        }
    }

    public void end(){
        mRunnable = false;
    }
}