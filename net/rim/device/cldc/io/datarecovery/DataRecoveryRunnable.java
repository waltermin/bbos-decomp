package net.rim.device.cldc.io.datarecovery;

final class DataRecoveryRunnable implements Runnable {
   private Object[] _listeners;
   private int _event;
   private int _linkType;

   protected DataRecoveryRunnable(Object[] listeners, int event, int linkType) {
      this._listeners = listeners;
      this._event = event;
      this._linkType = linkType;
   }

   @Override
   public final void run() {
      for (int i = this._listeners.length - 1; i >= 0; i--) {
         try {
            ((DataRecoveryListener)this._listeners[i]).dataRecoveryEventOccurred(this._event, this._linkType);
         } catch (Throwable var3) {
         }
      }
   }
}
