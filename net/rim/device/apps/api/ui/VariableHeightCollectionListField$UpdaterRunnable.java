package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Application;

final class VariableHeightCollectionListField$UpdaterRunnable implements Runnable {
   boolean _isQueued;
   Application _application;
   private final VariableHeightCollectionListField this$0;

   final void requeue(Application application) {
      this._application = application;
   }

   @Override
   public final void run() {
      synchronized (this) {
         if (this._application != null) {
            this._application.invokeLater(this);
            this._application = null;
            return;
         }

         this._isQueued = false;
      }

      this.this$0.doUpdateList();
   }

   VariableHeightCollectionListField$UpdaterRunnable(VariableHeightCollectionListField _1) {
      this.this$0 = _1;
   }
}
