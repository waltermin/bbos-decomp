package net.rim.wica.runtime.diagnostics;

import net.rim.device.api.system.ApplicationRegistry;

public final class DebugServices {
   private DebugServices() {
   }

   public static final boolean isAttached() {
      Debugger debugger = getDebugger();
      return debugger != null && debugger.isAttached();
   }

   public static final void logConsole(String message) {
      logConsole(-1, message);
   }

   public static final void logConsole(long generator, String message) {
      Debugger debugger = getDebugger();
      if (debugger != null && debugger.isAttached()) {
         debugger.logConsole(generator, message);
      }
   }

   private static final Debugger getDebugger() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      return (Debugger)registry.get(-8317686990539473415L);
   }
}
