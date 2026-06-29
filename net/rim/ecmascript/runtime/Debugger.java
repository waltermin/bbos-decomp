package net.rim.ecmascript.runtime;

public interface Debugger {
   int STEP_NONE;
   int STEP_INTO;
   int STEP_OVER;
   int STEP_RETURN;

   void registerDebugCallback(DebugCallback var1);

   boolean setBreakpoint(DebugScript var1, int var2);

   boolean clearBreakpoint(DebugScript var1, int var2);

   void clearAllBreakpoints();

   void setStepMode(int var1);

   void catchExceptions(boolean var1);
}
