package net.rim.device.internal.vad;

public interface VADUserEventListener {
   int EVENT_ACTIVATE = 0;
   int EVENT_ACTIVATED = 1;
   int EVENT_DEACTIVATE = 2;
   int EVENT_DEACTIVATED = 3;
   int EVENT_ADAPT_DIGITS = 4;
   int EVENT_RESET_DIGITS = 5;
   int CONTEXT_LOCAL = 0;
   int CONTEXT_BLUETOOTH_SCO_PENDING = 1;
   int CONTEXT_BLUETOOTH_SCO_UP = 2;
   int CONTEXT_BLUETOOTH_SCO_DOWN = 3;

   void vadEvent(int var1, int var2);
}
