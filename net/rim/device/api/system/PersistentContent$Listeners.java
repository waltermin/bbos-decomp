package net.rim.device.api.system;

import net.rim.device.api.listener.EventListenerManager;
import net.rim.device.internal.proxy.Proxy;

final class PersistentContent$Listeners {
   private EventListenerManager _listeners = new EventListenerManager();
   private PersistentContent$Listeners$Indicator _indicator;
   private int _state;
   private int _modeGeneration;
   private Application _proxy;

   PersistentContent$Listeners(int state, int modeGeneration) {
      this._state = state;
      this._modeGeneration = modeGeneration;
      this._proxy = Proxy.getInstance();
   }

   final void addIndicator(PersistentContentListener listener) {
      this._indicator = new PersistentContent$Listeners$Indicator(this, listener);
   }

   final void updateStateIndicator() {
      this.updateStateIndicator(this._state);
   }

   private final void updateStateIndicator(int state) {
      if (this._indicator != null) {
         this._indicator.update(state);
      }
   }

   final void add(PersistentContentListener listener, boolean weakListener) {
      this._listeners.add(listener, weakListener);
   }

   final void remove(PersistentContentListener listener) {
      this._listeners.remove(listener);
   }

   final boolean isListener(PersistentContentListener listener) {
      return this._listeners.isListener(listener);
   }

   final int getState() {
      return this._state;
   }

   final boolean isUpdateComplete() {
      return this._listeners.isUpdateComplete();
   }

   final void stateChanged(int newState, int lockGeneration) {
      this.updateStateIndicator(newState);
      if (this._state != newState) {
         this._state = newState;
         this._proxy
            .invokeLater(
               new PersistentContent$Listeners$EventLauncher(this._listeners, new PersistentContent$Listeners$StateChangeEvent(newState, lockGeneration), true)
            );
      }
   }

   final void lockChanged(int lockGeneration) {
      this._proxy
         .invokeLater(new PersistentContent$Listeners$EventLauncher(this._listeners, new PersistentContent$Listeners$LockChangeEvent(lockGeneration), false));
   }

   final void modeChanged(int modeGeneration) {
      if (this._modeGeneration != modeGeneration) {
         this._modeGeneration = modeGeneration;
         this._proxy
            .invokeLater(new PersistentContent$Listeners$EventLauncher(this._listeners, new PersistentContent$Listeners$ModeChangeEvent(modeGeneration), true));
      }
   }
}
