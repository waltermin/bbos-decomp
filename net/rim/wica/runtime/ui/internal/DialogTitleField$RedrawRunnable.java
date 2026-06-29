package net.rim.wica.runtime.ui.internal;

final class DialogTitleField$RedrawRunnable implements Runnable {
   private DialogTitleField _field;
   private boolean _invokeLaterPending;

   DialogTitleField$RedrawRunnable(DialogTitleField field) {
      this._field = field;
   }

   @Override
   public final void run() {
      synchronized (this) {
         this._invokeLaterPending = false;
      }

      DialogTitleField.access$000(this._field);
   }
}
