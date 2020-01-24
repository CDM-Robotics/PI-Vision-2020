package team6072.vision.visionProcessing.updateListeners;

import org.opencv.core.Mat;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.cameraserver.CameraServer;

public class TurretUpdateListener extends UpdateListener{

    private CvSource source;
    
    public TurretUpdateListener(){
        source = CameraServer.getInstance().putVideo("Turret", 160, 120);
    }

    public void updateNetworkTables(Mat mat){
        source.putFrame(mat);
    }

}