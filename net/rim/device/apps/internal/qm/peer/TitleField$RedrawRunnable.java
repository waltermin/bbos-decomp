package net.rim.device.apps.internal.qm.peer;

final class TitleField$RedrawRunnable implements Runnable {
   private TitleField _field;
   private boolean _invokeLaterPending;

   TitleField$RedrawRunnable(TitleField field) {
      this._field = field;
   }

   @Override
   public final void run() {
      synchronized (this) {
         this._invokeLaterPending = false;
      }

      TitleField.access$100(this._field);
   }

   static final boolean access$000(TitleField$RedrawRunnable x0) {
      return x0._invokeLaterPending;
   }

   static final boolean access$002(TitleField$RedrawRunnable x0, boolean x1) {
      return x0._invokeLaterPending = x1;
   }
}
