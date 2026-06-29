package net.rim.device.internal.proxy;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;

public final class Proxy extends Application {
   private RunnableThread _runnableThread;
   private static final long ID = 2026383997602970267L;

   public final void submitRunnable(Runnable runnable) {
      this.invokeLater(new Proxy$UtilRunnable(runnable, false));
   }

   public final void startThread(Thread thread) {
      this.invokeLater(new Proxy$UtilRunnable(thread, true));
   }

   public final void invokeRunnable(Runnable runnable) {
      this._runnableThread.add(runnable);
   }

   @Override
   public final void addListener(int device, Object listener) {
      switch (device) {
         case 5:
         case 14:
         case 16:
         case 34:
         case 46:
            throw new RuntimeException("Listener is not allowed on the Proxy.  Use ProtocolDaemon instead.");
         default:
            super.addListener(device, listener);
      }
   }

   public static final Proxy getInstance() {
      return (Proxy)ApplicationRegistry.getApplicationRegistry().waitFor(2026383997602970267L);
   }

   public static final Application ProxyMain() {
      Proxy proxy = new Proxy();
      ApplicationRegistry.getApplicationRegistry().put(2026383997602970267L, proxy);
      proxy._runnableThread = new RunnableThread();
      proxy._runnableThread.start();
      return proxy;
   }
}
