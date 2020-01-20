package team6072.vision.visionProcessing.pipelines;

import org.opencv.core.Mat;

public abstract class Pipeline{

    /**
     * This function takes a Mat and does all the OpenCv filters necessary and then finally 
     * saves the data as member variables in the class
     * @param m
     */
    public abstract void process(Mat m);

}