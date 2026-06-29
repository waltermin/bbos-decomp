package net.rim.device.internal.camera;

public interface CameraVideoListener extends CameraListener {
   void recordComplete(int var1);

   void recordStatusUpdate(int var1);

   void recordError(int var1);
}
