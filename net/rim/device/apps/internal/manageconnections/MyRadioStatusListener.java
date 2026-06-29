package net.rim.device.apps.internal.manageconnections;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;

final class MyRadioStatusListener implements RadioStatusListener {
   private int _lastLevel;
   ConnectionsPopupScreen _screen;

   MyRadioStatusListener(int wafs, ConnectionsPopupScreen screen) {
      this._lastLevel = RadioInfo.getSignalLevel(wafs);
      this._screen = screen;
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.statusUpdate();
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.statusUpdate();
   }

   @Override
   public final void radioTurnedOff() {
      this.statusUpdate();
   }

   @Override
   public final void signalLevel(int level) {
      if (level == -256 ^ this._lastLevel == -256) {
         this._lastLevel = level;
         this.statusUpdate();
      }
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   private final void statusUpdate() {
      this._screen.updateLater();
   }
}
