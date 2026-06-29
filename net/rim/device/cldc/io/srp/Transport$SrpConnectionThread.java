package net.rim.device.cldc.io.srp;

import java.util.Vector;
import net.rim.device.api.io.DatagramStatusListener;

final class Transport$SrpConnectionThread extends Thread {
   private Vector _queue;
   private boolean _shutdown;
   private final Transport this$0;

   private Transport$SrpConnectionThread(Transport _1) {
      this.this$0 = _1;
      this._queue = (Vector)(new Object(3));
   }

   final void add(Transport$SrpConnectionEvent event) {
      synchronized (this) {
         if (!this._shutdown) {
            this._queue.addElement(event);
            this.notifyAll();
         }
      }
   }

   final void close() {
      synchronized (this) {
         this._shutdown = true;
         this.notifyAll();
      }
   }

   @Override
   public final void run() {
      do {
         SrpUtils$DatagramInfo info = null;
         int errorCode = 0;

         try {
            try {
               do {
                  synchronized (this) {
                     if (!this._queue.isEmpty()) {
                        Transport$SrpConnectionEvent event = (Transport$SrpConnectionEvent)this._queue.firstElement();
                        this._queue.removeElementAt(0);
                        if (event != null) {
                           info = (SrpUtils$DatagramInfo)event._object;
                           errorCode = event._errorCode;
                        }
                     } else {
                        try {
                           if (this._shutdown) {
                              return;
                           }

                           this.wait();
                        } finally {
                           continue;
                        }
                     }
                  }
               } while (info == null);

               if (info.superReference != 0) {
                  this.this$0.xmitDgslEvent((DatagramStatusListener)info.object, info.superReference, errorCode, null);
               }

               info.data = null;
               info.offset = info.length = info.flags = 0;
               info.object = null;
               info.superReference = info.reference = 0;
            } finally {
               continue;
            }
         } finally {
            SrpUtils$DatagramInfo var23 = null;
         }
      } while (!this._shutdown || !this._queue.isEmpty());
   }

   Transport$SrpConnectionThread(Transport x0, Transport$1 x1) {
      this(x0);
   }
}
