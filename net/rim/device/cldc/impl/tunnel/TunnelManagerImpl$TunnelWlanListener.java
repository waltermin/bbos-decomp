package net.rim.device.cldc.impl.tunnel;

import net.rim.device.api.system.RadioStatusListener;

final class TunnelManagerImpl$TunnelWlanListener implements RadioStatusListener {
   private TunnelManagerImpl _manager;

   TunnelManagerImpl$TunnelWlanListener(TunnelManagerImpl manager) {
      this._manager = manager;
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this._manager.networkServiceChange(false, networkId, service);
   }

   @Override
   public final void radioTurnedOff() {
      this._manager.networkServiceChange(false, 0, 0);
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this._manager.networkServiceChange(false, networkId, service);
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      this._manager.pdpStateChange(apn, state, cause);
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }
}
