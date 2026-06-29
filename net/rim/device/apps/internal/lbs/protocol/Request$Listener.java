package net.rim.device.apps.internal.lbs.protocol;

public interface Request$Listener {
   int STATE_RENDER;
   int STATE_REQUEST;
   int STATE_DOWNLOAD;
   int STATE_DONE;

   void requestComplete(Request var1);

   void progressTick(int var1, int var2);

   void setState(int var1);
}
