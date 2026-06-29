package net.rim.device.api.browser.push;

import java.io.InputStream;
import javax.microedition.io.Connection;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.HttpServerConnection;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;

final class MDSPushSource extends PushSource implements PushEventLogger {
   private Pushlet _listener;
   private MDSPushSource$RunThread _runThread;
   private static final long PUSH_NOTIFIER_KEY;
   private static final int DEFAULT_PORT;

   private MDSPushSource(int port) {
      super(port, null, null, null);
   }

   @Override
   public final void startPPGConnection(Pushlet listener) {
   }

   @Override
   public final void dataNetworkChanged(boolean dataAvailable) {
   }

   @Override
   public final void startListening(Pushlet listener) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         Object notifier = ar.get(-713047639350268568L);
         if (notifier instanceof Object) {
            label50:
            try {
               ((Connection)notifier).close();
            } finally {
               break label50;
            }

            ar.replace(-713047639350268568L, new Object());
         }
      }

      this._listener = listener;
      MDSPushSource$RunThread rt;
      synchronized (this) {
         rt = new MDSPushSource$RunThread(this);
         this._runThread = rt;
      }

      rt.start();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void close() {
      try {
         MDSPushSource$RunThread thread;
         synchronized (this) {
            thread = this._runThread;
            this._runThread = null;
         }

         if (thread != null) {
            thread.shutdown();
            return;
         }
      } catch (Throwable var7) {
         EventLogger.logEvent(-1133226195824034738L, ((StringBuffer)(new Object("PPex\n"))).append(ioe.toString()).toString().getBytes(), 0);
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processPush(HttpServerConnection connection) {
      InputStream in = null;
      boolean var28 = false /* VF: Semaphore variable */;

      label213: {
         try {
            label201:
            try {
               var28 = true;
               in = connection.openInputStream();
               HttpHeaders e = new Object();
               String key = null;

               for (int i = 0; (key = connection.getHeaderFieldKey(i)) != null; i++) {
                  ((HttpHeaders)e).setProperty(key, connection.getHeaderField(i));
               }

               this._listener.messageReceived((HttpHeaders)e, (PushInputStream)(new Object(connection, in)));
               var28 = false;
               break label213;
            } catch (Throwable var37) {
               EventLogger.logEvent(-1133226195824034738L, ((StringBuffer)(new Object("PPpp\n"))).append(e.toString()).toString().getBytes(), 0);
               var28 = false;
               break label201;
            }
         } finally {
            if (var28) {
               if (in != null) {
                  label187:
                  try {
                     in.close();
                  } finally {
                     break label187;
                  }
               }
            }
         }

         if (in != null) {
            try {
               in.close();
               return;
            } finally {
               return;
            }
         }

         return;
      }

      if (in != null) {
         try {
            in.close();
         } finally {
            return;
         }
      }
   }

   @Override
   public final int getSourceType() {
      return 1;
   }

   public static final PushSource[] getAllServices() {
      return !PushOptions.getOptions().getMDSEnablePush() ? null : new MDSPushSource[]{new MDSPushSource(7874)};
   }
}
