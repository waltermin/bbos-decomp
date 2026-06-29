package net.rim.device.api.system;

public interface AlertListener {
   int REASON_COMPLETED = 2;
   int REASON_KEY_PRESSED = 1;
   int REASON_STOP_CALLED = 3;

   void audioDone(int var1);

   void buzzerDone(int var1);

   void vibrateDone(int var1);
}
