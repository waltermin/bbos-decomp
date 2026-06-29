package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.notification.NotificationsManager;

final class Notifications$ContactAvailableRunnable implements Runnable {
   private PeerContact _contact;
   private boolean _scheduled;

   private final synchronized void invokeLater(PeerContact contact) {
      this._contact = contact;
      if (!this._scheduled) {
         PeerApplication.getInstance().postInvokeLaterInternal(this);
         this._scheduled = true;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         NotificationsManager.triggerImmediateEvent(-3969704423467496048L, 0, this._contact, null);
         PeerApplication.getInstance().contactAvailableDialog(this._contact);
         var3 = false;
      } finally {
         if (var3) {
            this._scheduled = false;
         }
      }

      this._scheduled = false;
   }

   static final void access$000(Notifications$ContactAvailableRunnable x0, PeerContact x1) {
      x0.invokeLater(x1);
   }
}
