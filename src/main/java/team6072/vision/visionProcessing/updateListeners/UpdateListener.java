package team6072.vision.visionProcessing.updateListeners;

import team6072.vision.visionProcessing.pipelines.Pipeline;

public abstract class UpdateListener{

    /**
     * This function will take the pipeline, extract the necessary data from it, make
     * the calculations necessary, and then send it to the NetworktablesThread
     * 
     * to send the data to the NetworkTablesThread, it will just directly call the public Entries
     */
    public abstract void updateNetworkTables(Pipeline pipeline);

}