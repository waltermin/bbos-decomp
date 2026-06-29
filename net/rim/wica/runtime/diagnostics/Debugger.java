package net.rim.wica.runtime.diagnostics;

public interface Debugger {
   long UNKNOWN_GENERATOR_ID = -1L;
   long DEBUGGER_INSTANCE = -8317686990539473415L;

   boolean isAttached();

   void logConsole(long var1, String var3);
}
