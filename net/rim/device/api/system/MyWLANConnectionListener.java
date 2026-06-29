package net.rim.device.api.system;

final class MyWLANConnectionListener implements WLANListenerInternal {
   private WLANConnectionListener _listener;

   MyWLANConnectionListener(WLANConnectionListener listener) {
      this._listener = listener;
   }

   @Override
   public final boolean equals(Object o) {
      if (!(o instanceof MyWLANConnectionListener)) {
         return super.equals(o);
      }

      MyWLANConnectionListener mwcListener = (MyWLANConnectionListener)o;
      return this._listener.equals(mwcListener._listener);
   }

   @Override
   public final void networkSuccess() {
      this._listener.networkConnected();
   }

   @Override
   public final void networkFail(int status, int error, int extendedInfo) {
      this._listener.networkDisconnected(status);
   }

   @Override
   public final void radioStatus(boolean started) {
   }

   @Override
   public final void networkFound(int handle) {
   }

   @Override
   public final void networkApChange() {
   }
}
