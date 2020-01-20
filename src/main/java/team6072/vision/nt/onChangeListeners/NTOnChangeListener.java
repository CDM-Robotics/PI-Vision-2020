package team6072.vision.nt.onChangeListeners;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableValue;
import team6072.vision.CameraMaster;
import team6072.vision.visionProcessing.VisionThreadMaster;

public abstract class NTOnChangeListener{

    private NetworkTableEntry mNetworkTableEntry;
    private NetworkTableValue mPriorValue;

    public NTOnChangeListener(NetworkTableEntry networkTableEntry){
        mNetworkTableEntry = networkTableEntry;
        mPriorValue = mNetworkTableEntry.getValue();
    }

    public void checkState(){
        NetworkTableValue currentValue = mNetworkTableEntry.getValue();
        // This needs to be tested
        if(!currentValue.equals(mPriorValue)){
            mPriorValue = currentValue;
            execute();
        }

    }

    public NetworkTableEntry getEntry(){
        return mNetworkTableEntry;
    }

    public abstract void execute();

}

