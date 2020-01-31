package team6072.vision.visionProcessing.threads;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import team6072.vision.visionProcessing.VisionThreadBase;
import team6072.vision.logging.LogWrapper;
import team6072.vision.logging.LoggerConstants;
import team6072.vision.logging.LogWrapper.FileType;
import team6072.vision.nt.NetworkTablesThread;

import org.opencv.core.Mat;

public class TurretThread extends VisionThreadBase{

    private LogWrapper mLog;
    private Mat mMat;
    private CvSource cvSource;

    public TurretThread(CvSink cvSink){
        super(cvSink);
        mLog = new LogWrapper(FileType.VISION_THREAD, "Turret Thread", LoggerConstants.VISION_THREAD_PERMISSION);

        mMat = new Mat();
        cvSource = NetworkTablesThread.getInstance().getNewCvSource("Turret Thread");
    }

    public void process(Mat m){
        mMat = m;
    }

    public void updateInformation(){
        mLog.print("Putting frame");
        cvSource.putFrame(mMat);
    }


}