package net.rim.device.apps.internal.messaging;

import net.rim.device.apps.api.messaging.MessageFilter;
import net.rim.device.apps.api.ui.DialogWithBackgroundThread;
import net.rim.device.apps.api.ui.DialogWithBackgroundThreadRunnable;

final class SetFilterRunnable implements DialogWithBackgroundThreadRunnable {
   private MessageFilter _filter;
   private DialogWithBackgroundThread _dialog;
   private boolean _isDone;

   public SetFilterRunnable(MessageFilter filter) {
      this._filter = filter;
   }

   public final boolean isDone() {
      return this._isDone;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      this._isDone = false;
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this._filter.size();
         var3 = false;
      } finally {
         if (var3) {
            this._dialog.dismiss();
            this._isDone = true;
         }
      }

      this._dialog.dismiss();
      this._isDone = true;
   }

   @Override
   public final void setDialogWithBackgroundThread(DialogWithBackgroundThread dialog) {
      this._dialog = dialog;
   }

   @Override
   public final void stop() {
   }
}
