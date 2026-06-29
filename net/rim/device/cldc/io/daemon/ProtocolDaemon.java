package net.rim.device.cldc.io.daemon;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.internal.io.NativeSocketEventDispatcher;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.vm.Process;

public final class ProtocolDaemon extends UiApplication {
   private static final long ID;

   private ProtocolDaemon() {
   }

   @Override
   protected final boolean acceptsForeground() {
      return false;
   }

   public final void submitRunnable(Runnable runnable) {
      this.invokeLater(new ProtocolDaemon$UtilRunnable(runnable, false));
   }

   public final void startThread(Thread thread) {
      this.invokeLater(new ProtocolDaemon$UtilRunnable(thread, true));
   }

   public static final ProtocolDaemon getInstance() {
      return (ProtocolDaemon)ApplicationRegistry.getApplicationRegistry().waitFor(6860522476510630950L);
   }

   public static final void start() {
      Process p = Process.currentProcess();
      p.setThreadLimit(64);
      p.haltDeviceIfThisProcessDies(true);
      ProtocolDaemon daemon = new ProtocolDaemon();
      ApplicationRegistry.getApplicationRegistry().put(6860522476510630950L, daemon);
      ((ApplicationManagerInternal)ApplicationManager.getApplicationManager()).setNativeSocketProcess();
      NativeSocketEventDispatcher.register(daemon.getProcessId());
      TransportRegistry.init();

      while (true) {
         try {
            daemon.enterEventDispatcher();
         } catch (Throwable var3) {
         }
      }
   }
}
