package net.rim.device.api.system;

final class Monitor {
   private long _guid;
   private boolean _hasBeenNotified;
   private Thread _owner;

   Monitor(long guid) {
      this._guid = guid;
   }

   Monitor(long guid, Thread owner) {
      this._guid = guid;
      this._owner = owner;
   }

   final boolean hasBeenNotified() {
      return this._hasBeenNotified;
   }

   final Thread getOwner() {
      return this._owner;
   }

   final long getGUID() {
      return this._guid;
   }

   final void wakeyWakey() {
      this._hasBeenNotified = true;
      this.notifyAll();
   }
}
