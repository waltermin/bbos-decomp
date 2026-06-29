package net.rim.device.apps.internal.lbs.protocol;

public interface Request$Listener {
   int STATE_RENDER = 1;
   int STATE_REQUEST = 2;
   int STATE_DOWNLOAD = 3;
   int STATE_DONE = 4;

   void requestComplete(Request var1);

   void progressTick(int var1, int var2);

   void setState(int var1);
}
