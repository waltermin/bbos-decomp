package net.rim.device.apps.internal.profiles;

public interface AlertEngineListener {
   int COMPLETE = 1397051715;
   int DEVICE_NON_IDLE = 1313424460;
   int CANCELED = 1128353347;
   int OVERRIDE = 1331053906;
   int STOP_REQUESTED = 1398034256;
   int TIMEDOUT = 1414485332;
   int ERRORED = 1414546776;

   void alertDone(long var1, int var3);
}
