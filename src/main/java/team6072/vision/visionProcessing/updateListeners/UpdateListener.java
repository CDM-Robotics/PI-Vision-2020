package team6072.vision.visionProcessing.updateListeners;

import org.opencv.core.Mat;

public abstract class UpdateListener{

    /**
     * This function will take the Mat, extract the necessary data from it, make
     * the calculations necessary, and then send it to the NetworktablesThread
     * 
     * to send the data to the NetworkTablesThread, it will just directly call the public Entries
     */
    public abstract void updateNetworkTables(Mat mat);

}