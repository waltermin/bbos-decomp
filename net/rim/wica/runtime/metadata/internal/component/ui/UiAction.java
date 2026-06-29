package net.rim.wica.runtime.metadata.internal.component.ui;

public final class UiAction implements Runnable {
   private UIControlImpl _control;
   private int _action;

   public UiAction(UIControlImpl control, int action) {
      this._control = control;
      this._action = action;
   }

   @Override
   public final void run() {
      this._control.onEvent(this._action);
   }
}
