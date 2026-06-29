package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.notification.NotificationsManager;

final class Notifications$CancelImmediateEvent implements Runnable {
   private long _type;
   private int _id;
   private Object _reference;
   private Object _context;
   private boolean _scheduled;

   private final synchronized void setCancelInfo(long type, int id, Object reference, Object context) {
      this._type = type;
      this._id = id;
      this._reference = reference;
      this._context = context;
      this._scheduled = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         NotificationsManager.cancelImmediateEvent(this._type, this._id, this._reference, this._context);
         var3 = false;
      } finally {
         if (var3) {
            this._scheduled = false;
         }
      }

      this._scheduled = false;
   }

   static final boolean access$300(Notifications$CancelImmediateEvent x0) {
      return x0._scheduled;
   }

   static final void access$400(Notifications$CancelImmediateEvent x0, long x1, int x2, Object x3, Object x4) {
      x0.setCancelInfo(x1, x2, x3, x4);
   }
}
