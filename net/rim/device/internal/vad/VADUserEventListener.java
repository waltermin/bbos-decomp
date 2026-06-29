package net.rim.device.internal.vad;

public interface VADUserEventListener {
   int EVENT_ACTIVATE;
   int EVENT_ACTIVATED;
   int EVENT_DEACTIVATE;
   int EVENT_DEACTIVATED;
   int EVENT_ADAPT_DIGITS;
   int EVENT_RESET_DIGITS;
   int CONTEXT_LOCAL;
   int CONTEXT_BLUETOOTH_SCO_PENDING;
   int CONTEXT_BLUETOOTH_SCO_UP;
   int CONTEXT_BLUETOOTH_SCO_DOWN;

   void vadEvent(int var1, int var2);
}
