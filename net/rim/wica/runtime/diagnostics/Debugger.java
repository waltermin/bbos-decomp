package net.rim.wica.runtime.diagnostics;

public interface Debugger {
   long UNKNOWN_GENERATOR_ID;
   long DEBUGGER_INSTANCE;

   boolean isAttached();

   void logConsole(long var1, String var3);
}
