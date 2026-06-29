package net.rim.device.cldc.io.sms;

import java.util.Vector;
import net.rim.device.api.io.DatagramStatusListener;

final class StatusThread extends Thread {
   private Transport _transport;
   private Vector _status = new Vector();

   public StatusThread(Transport transport) {
      this._transport = transport;
   }

   public final void addStatus(boolean dgramIdFlag, DatagramStatusListener listener, int id, int status, Object context) {
      synchronized (this._status) {
         this._status.addElement(new StatusThread$StatusObject(dgramIdFlag, listener, id, status, context));
         this._status.notify();
      }
   }

   @Override
   public final void run() {
      while (true) {
         try {
            StatusThread$StatusObject status;
            synchronized (this._status) {
               if (this._status.size() == 0) {
                  status = null;
                  this._status.wait();
               }

               status = (StatusThread$StatusObject)this._status.elementAt(0);
               this._status.removeElementAt(0);
            }

            Thread.yield();
            Thread.yield();
            if (!status._dgramIdFlag) {
               this._transport.passDgslEvent(status._id, status._status, status._context);
            } else {
               this._transport.xmitDgslEvent(status._listener, status._id, status._status, status._context);
            }
         } catch (Throwable var5) {
         }
      }
   }
}
