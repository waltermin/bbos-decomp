package net.rim.device.apps.internal.profiles;

public interface AlertEngineListener {
   int COMPLETE;
   int DEVICE_NON_IDLE;
   int CANCELED;
   int OVERRIDE;
   int STOP_REQUESTED;
   int TIMEDOUT;
   int ERRORED;

   void alertDone(long var1, int var3);
}
