package net.rim.device.internal.ipc;

public class IPCBaseRunnable implements Runnable {
   protected Object _listener;

   public Object getListener() {
      return this._listener;
   }

   protected void doLogging(Throwable t) {
      t.printStackTrace();
   }

   public void setListener(Object listener) {
      this._listener = listener;
   }

   @Override
   public void run() {
      throw null;
   }
}
