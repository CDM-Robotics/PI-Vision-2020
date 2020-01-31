package team6072.vision.visionProcessing;

import edu.wpi.cscore.CvSink;
import team6072.vision.logging.LogWrapper;
import org.opencv.core.Mat;

public abstract class VisionThreadBase extends Thread{

    private LogWrapper mLog;
    private CvSink mCvSink;
    private boolean mRunnable = true;

    public VisionThreadBase(CvSink cvSink){
        mCvSink = cvSink;
    }

    public void run(){
        Mat m = new Mat();
        while(mRunnable){
            mCvSink.grabFrame(m);
            process(m);
            updateInformation();
        }
    }

    public void end(){
        mRunnable = false;
    }

    public abstract void process(Mat m);

    public abstract void updateInformation();

}