package net.rim.device.apps.internal.ribbon.system;

final class SystemActions$RadioOnThreadSwitch implements Runnable {
   private boolean _updatePending;
   private SystemActions _sysActs;

   SystemActions$RadioOnThreadSwitch(SystemActions sysActs) {
      this._sysActs = sysActs;
   }

   @Override
   public final void run() {
      this._updatePending = false;
      this._sysActs.radioOnMainThread();
   }
}
