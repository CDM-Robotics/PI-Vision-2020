package team6072.vision.nt.onChangeListeners;

import edu.wpi.first.networktables.NetworkTableEntry;
import team6072.vision.visionProcessing.VisionThreadMaster;
import team6072.vision.cameras.CameraMaster;

public class NTSwitchCamerasListener extends NTOnChangeListener{
    public NTSwitchCamerasListener(NetworkTableEntry networkTableEntry){
        super(networkTableEntry);
    }

    public void execute(){
        VisionThreadMaster.getInstance().killThreads();
        CameraMaster.getInstance().switchCvSinks();
        VisionThreadMaster.getInstance().startThreads();
    }
}