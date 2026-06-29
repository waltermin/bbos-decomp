package net.rim.device.api.listener;

import net.rim.device.api.system.Application;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.WeakReference;

class EventListenerManager$EventNotification implements Runnable {
   WeakReference _applicationWR;
   Object _listener;
   Event _event;
   Thread _thread;
   boolean _run;
   boolean _invoked;

   Thread getThread() {
      return this._thread;
   }

   Thread invoke() {
      throw null;
   }

   @Override
   public void run() {
      Application application = (Application)this._applicationWR.get();
      if (application == null || !application.isAlive()) {
         application = Proxy.getInstance();
         this._applicationWR.set(application);
      }

      if (application != Application.getApplication()) {
         synchronized (this) {
            try {
               if (application.isHandlingEvents()) {
                  application.invokeLater(this);
                  this.wait(10000);
               }
            } catch (Throwable var8) {
            }

            if (!this._run) {
               this._applicationWR.set(null);
               this.run();
            }

            while (!this._invoked) {
               try {
                  this.wait();
               } catch (InterruptedException var7) {
               }
            }
         }
      } else if (application.isEventThread()) {
         new Thread(this).start();
      } else {
         synchronized (this) {
            this._run = true;
            if (this._listener instanceof WeakReference) {
               this._listener = ((WeakReference)this._listener).get();
            }
         }

         try {
            this._thread = this.invoke();
         } catch (Throwable var10) {
         }

         synchronized (this) {
            this._invoked = true;
            this.notify();
         }
      }
   }

   EventListenerManager$EventNotification(WeakReference applicationWR, Object listener, Event event) {
      this._applicationWR = applicationWR;
      this._listener = listener;
      this._event = event;
   }
}
