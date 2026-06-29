package net.rim.device.apps.api.ui;

final class VariableHeightCollectionListField$SetFocusedElementRunnable implements Runnable {
   boolean _isQueued;
   Object _element;
   private final VariableHeightCollectionListField this$0;

   VariableHeightCollectionListField$SetFocusedElementRunnable(VariableHeightCollectionListField _1) {
      this.this$0 = _1;
      this._isQueued = false;
   }

   @Override
   public final void run() {
      synchronized (this) {
         this._isQueued = false;
      }

      this.this$0.doSetElementWithFocus(this._element);
   }
}
