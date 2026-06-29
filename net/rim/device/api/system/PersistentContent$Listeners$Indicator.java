package net.rim.device.api.system;

class PersistentContent$Listeners$Indicator implements Runnable {
   private PersistentContentListener _listener;
   private int _state;
   private boolean _pending;
   private final PersistentContent$Listeners this$0;

   PersistentContent$Listeners$Indicator(PersistentContent$Listeners _1, PersistentContentListener listener) {
      this.this$0 = _1;
      this._listener = listener;
      this._pending = false;
   }

   synchronized void update(int state) {
      this._state = state;
      if (!this._pending) {
         this._pending = true;
         this.this$0._proxy.invokeLater(this);
      }
   }

   @Override
   public void run() {
      synchronized (this) {
         this._pending = false;
      }

      try {
         this._listener.persistentContentStateChanged(this._state);
      } catch (Throwable var3) {
      }
   }
}
