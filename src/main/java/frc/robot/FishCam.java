package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

public class FishCam{

    UsbCamera fishCam;

    public FishCam(){
        fishCam = CameraServer.getInstance().startAutomaticCapture(0);
        fishCam.setResolution(1280, 720);
        fishCam.setFPS(60);
        fishCam.setBrightness(0);
    }

}