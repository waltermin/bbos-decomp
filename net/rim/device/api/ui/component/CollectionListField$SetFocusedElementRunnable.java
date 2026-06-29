package net.rim.device.api.ui.component;

final class CollectionListField$SetFocusedElementRunnable implements Runnable {
   boolean _isQueued;
   Object _element;
   private final CollectionListField this$0;

   CollectionListField$SetFocusedElementRunnable(CollectionListField _1) {
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
