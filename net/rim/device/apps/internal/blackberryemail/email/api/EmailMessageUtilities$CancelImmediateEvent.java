package net.rim.device.apps.internal.blackberryemail.email.api;

import net.rim.device.api.notification.NotificationsManager;

class EmailMessageUtilities$CancelImmediateEvent implements Runnable {
   private long _type;
   private long _id;
   private Object _context;
   private boolean _finished = true;

   private synchronized void setCancelInfo(long type, long id, Object context) {
      this._type = type;
      this._id = id;
      this._context = context;
      this._finished = false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public synchronized void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         NotificationsManager.cancelImmediateEvent(this._type, this._id, null, this._context);
         var3 = false;
      } finally {
         if (var3) {
            this._finished = true;
         }
      }

      this._finished = true;
   }
}
