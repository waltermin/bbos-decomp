package net.rim.device.api.ui;

import net.rim.device.api.system.Application;

class Field$UpdateLayoutRunnable implements Runnable {
   boolean _layoutPending;
   private final Field this$0;

   Field$UpdateLayoutRunnable(Field _1) {
      this.this$0 = _1;
   }

   public synchronized void invokeLater() {
      if (!this._layoutPending) {
         this._layoutPending = true;
         Application.getApplication().invokeLater(this);
      }

      try {
         this.wait(50);
      } catch (InterruptedException var2) {
      }
   }

   @Override
   public void run() {
      this.this$0.updateLayoutHelper();
      synchronized (this) {
         this.notify();
      }
   }
}
