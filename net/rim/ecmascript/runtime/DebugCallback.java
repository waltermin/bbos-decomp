package net.rim.ecmascript.runtime;

public interface DebugCallback {
   int EVENT_NONE = 0;
   int EVENT_BREAKPOINT = 1;
   int EVENT_STEP = 2;
   int EVENT_THROW = 3;

   void event(DebugContext var1, int var2);
}
