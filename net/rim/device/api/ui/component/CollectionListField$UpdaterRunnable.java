package net.rim.device.api.ui.component;

import net.rim.device.api.system.Application;

final class CollectionListField$UpdaterRunnable implements Runnable {
   boolean _isQueued;
   Application _application;
   private final CollectionListField this$0;

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

   CollectionListField$UpdaterRunnable(CollectionListField _1) {
      this.this$0 = _1;
   }
}
