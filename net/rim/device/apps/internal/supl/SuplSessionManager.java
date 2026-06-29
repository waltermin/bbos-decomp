package net.rim.device.apps.internal.supl;

import java.util.Vector;
import net.rim.device.api.gps.LCS;
import net.rim.device.api.util.CyclicQueue;
import net.rim.device.internal.system.RadioInternal;

public final class SuplSessionManager {
   private int _sessionType;
   private static final int SET = 0;
   private static final int NETWORK = 1;
   private static final int[] MAX_SUPL_CONN = new int[]{4, 1, -805044223, 35, -804913088, 3014701, 3211312, 3342386};
   private static CyclicQueue _queue = new CyclicQueue();
   private static Vector _active = new Vector();

   public SuplSessionManager(byte[] pdu) {
      try {
         if (RadioInternal.isSUPLEnabled()) {
            this._sessionType = 1;
            SuplSession session = new SuplSession(pdu);
            this.startSession(session);
            return;
         }
      } finally {
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public SuplSessionManager() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         if (!RadioInternal.isSUPLEnabled()) {
            LCS.assistDataRequestFailed();
            return;
         }

         this._sessionType = 0;
         SuplSession session = new SuplSession();
         this.startSession(session);
         var3 = false;
      } finally {
         if (var3) {
            LCS.assistDataRequestFailed();
            return;
         }
      }
   }

   private final synchronized void startSession(SuplSession session) {
      int cap = MAX_SUPL_CONN[this._sessionType];
      if (_active.size() < cap) {
         _active.addElement(session);
         SuplSessionMonitor mon = new SuplSessionMonitor(session, this);
         mon.start();
      } else {
         if (this._sessionType != 1) {
            _queue.enqueue(session);
         }
      }
   }

   protected final synchronized void sessionFinished(SuplSession session) {
      System.out.println("SuplSession finished");

      try {
         if (_active.indexOf(session) != -1) {
            _active.removeElement(session);
            if (_queue.size() > 0 && this._sessionType != 1) {
               SuplSessionMonitor mon = new SuplSessionMonitor((SuplSession)_queue.dequeue(), this);
               mon.start();
               return;
            }
         }
      } finally {
         return;
      }
   }
}
