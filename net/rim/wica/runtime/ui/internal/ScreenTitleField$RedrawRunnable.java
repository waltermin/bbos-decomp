package net.rim.wica.runtime.ui.internal;

final class ScreenTitleField$RedrawRunnable implements Runnable {
   private ScreenTitleField _field;
   private boolean _invokeLaterPending;

   ScreenTitleField$RedrawRunnable(ScreenTitleField field) {
      this._field = field;
   }

   @Override
   public final void run() {
      synchronized (this) {
         this._invokeLaterPending = false;
      }

      ScreenTitleField.access$100(this._field);
   }
}
