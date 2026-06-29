package net.rim.device.api.browser.push;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;
import net.rim.device.api.io.http.HttpServerConnection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;

final class MDSPushSource$RunThread extends Thread {
   private boolean _processing;
   private StreamConnectionNotifier _notifier;
   private final MDSPushSource this$0;

   MDSPushSource$RunThread(MDSPushSource _1) {
      this.this$0 = _1;
      this._processing = true;
   }

   public final void shutdown() {
      this._processing = false;
      if (this._notifier != null) {
         this._notifier.close();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (this._processing) {
         boolean var46 = false /* VF: Semaphore variable */;

         label490: {
            label491: {
               try {
                  try {
                     var46 = true;
                     this._notifier = (StreamConnectionNotifier)Connector.open("http://:" + this.this$0._port + ";DeviceSide=false");
                     ApplicationRegistry ioe = ApplicationRegistry.getApplicationRegistry();
                     synchronized (ioe) {
                        ioe.replace(-713047639350268568L, this._notifier);
                     }

                     while (this._processing) {
                        HttpServerConnection connection = (HttpServerConnection)this._notifier.acceptAndOpen();
                        this.this$0.processPush(connection);

                        try {
                           connection.close();
                           HttpServerConnection var71 = null;
                        } catch (Throwable var68) {
                           EventLogger.logEvent(-1133226195824034738L, ("PPce\n" + e.toString()).getBytes(), 0);
                           continue;
                        }
                     }

                     var46 = false;
                     break label490;
                  } catch (Throwable var69) {
                     label498: {
                        if (!this._processing) {
                           var46 = false;
                           break label491;
                        }

                        EventLogger.logEvent(-1133226195824034738L, ("PPex\n" + ioe.toString()).getBytes(), 0);
                        var46 = false;
                        break label498;
                     }
                  }
               } finally {
                  if (var46) {
                     if (this._notifier != null) {
                        label446:
                        try {
                           this._notifier.close();
                        } finally {
                           break label446;
                        }

                        this._notifier = null;
                     }
                  }
               }

               if (this._notifier == null) {
                  continue;
               }

               label458:
               try {
                  this._notifier.close();
               } finally {
                  break label458;
               }

               this._notifier = null;
               continue;
            }

            if (this._notifier != null) {
               label453:
               try {
                  this._notifier.close();
               } finally {
                  break label453;
               }

               this._notifier = null;
            }

            return;
         }

         if (this._notifier != null) {
            label462:
            try {
               this._notifier.close();
            } finally {
               break label462;
            }

            this._notifier = null;
         }
      }
   }
}
