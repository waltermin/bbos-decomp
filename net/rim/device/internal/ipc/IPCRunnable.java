package net.rim.device.internal.ipc;

public class IPCRunnable extends IPCBaseRunnable {
   protected Object _listener;

   @Override
   public final void run() {
      try {
         this.doRun(this.getListener());
      } catch (Throwable t) {
         this.doLogging(t);
      }
   }

   protected void doRun(Object _1) {
      throw null;
   }
}
