package net.rim.ecmascript.runtime;

public interface Debugger {
   int STEP_NONE = 0;
   int STEP_INTO = 1;
   int STEP_OVER = 2;
   int STEP_RETURN = 3;

   void registerDebugCallback(DebugCallback var1);

   boolean setBreakpoint(DebugScript var1, int var2);

   boolean clearBreakpoint(DebugScript var1, int var2);

   void clearAllBreakpoints();

   void setStepMode(int var1);

   void catchExceptions(boolean var1);
}
