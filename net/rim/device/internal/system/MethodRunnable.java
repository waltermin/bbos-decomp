package net.rim.device.internal.system;

import net.rim.device.api.system.Application;
import net.rim.device.internal.proxy.Proxy;

public class MethodRunnable implements Runnable {
   protected Throwable _throwable;
   protected int _intResult;
   protected String _stringResult;

   public String getStringResult() {
      return this._stringResult;
   }

   protected void call() {
      throw null;
   }

   public void checkException() {
      if (this._throwable != null) {
         if (!(this._throwable instanceof RuntimeException)) {
            throw new RuntimeException(this._throwable.toString());
         } else {
            throw (RuntimeException)this._throwable;
         }
      }
   }

   public void runOnProxy() {
      Application proxy = Proxy.getInstance();
      proxy.invokeAndWait(this);
   }

   public int getIntegerResult() {
      return this._intResult;
   }

   @Override
   public final void run() {
      try {
         this.call();
      } catch (Throwable t) {
         this._throwable = t;
      }
   }
}
