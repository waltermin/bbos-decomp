package net.rim.device.cldc.io.dns;

import java.util.Vector;
import net.rim.device.api.system.EventLogger;

class DNSResolverIPv4$DNSResolverIPv4Thread extends Thread {
   private DNSResolverIPv4 _resolver;
   private Vector _queue;

   DNSResolverIPv4$DNSResolverIPv4Thread(DNSResolverIPv4 resolver) {
      this._resolver = resolver;
      this._queue = new Vector();
   }

   public void addRequest(DNSRequest req) {
      synchronized (this._queue) {
         this._queue.addElement(req);
         this._queue.notify();
      }
   }

   @Override
   public void run() {
      EventLogger.logEvent(1197736374800106759L, 1231972724, 0);

      while (true) {
         DNSRequest req;
         synchronized (this._queue) {
            while (this._queue.size() == 0) {
               try {
                  this._queue.wait();
               } catch (InterruptedException var5) {
               }
            }

            req = (DNSRequest)this._queue.elementAt(0);
            this._queue.removeElementAt(0);
         }

         try {
            this._resolver.executeQuery(req);
         } catch (DNSException e) {
            int status = -1;
            if (e.getErrorCode() != 0) {
               status = e.getErrorCode();
            }

            if (req.getListener() != null) {
               req.getListener().DNSEvent(req.getPacketId(), status, null);
            }
         } catch (Throwable var8) {
         }

         DNSRequest var9 = null;
      }
   }
}
