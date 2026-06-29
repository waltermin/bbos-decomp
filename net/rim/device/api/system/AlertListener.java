package net.rim.device.api.system;

public interface AlertListener {
   int REASON_COMPLETED;
   int REASON_KEY_PRESSED;
   int REASON_STOP_CALLED;

   void audioDone(int var1);

   void buzzerDone(int var1);

   void vibrateDone(int var1);
}
