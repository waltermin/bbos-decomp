package net.rim.device.apps.api.ui;

public interface ImageOperation {
   boolean scroll(int var1, boolean var2);

   boolean canZoom(boolean var1);

   boolean rotateStep();

   void zoomToFit();

   void zoom1To1();

   boolean zoom(boolean var1);
}
