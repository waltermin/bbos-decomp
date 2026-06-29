package net.rim.ecmascript.runtime;

public interface DebugCallback {
   int EVENT_NONE;
   int EVENT_BREAKPOINT;
   int EVENT_STEP;
   int EVENT_THROW;

   void event(DebugContext var1, int var2);
}
