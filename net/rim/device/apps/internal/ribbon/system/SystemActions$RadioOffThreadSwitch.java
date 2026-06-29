package net.rim.device.apps.internal.ribbon.system;

final class SystemActions$RadioOffThreadSwitch implements Runnable {
   private boolean _updatePending;
   private SystemActions _sysActs;

   SystemActions$RadioOffThreadSwitch(SystemActions sysActs) {
      this._sysActs = sysActs;
   }

   @Override
   public final void run() {
      this._updatePending = false;
      this._sysActs.radioOffMainThread();
   }
}
