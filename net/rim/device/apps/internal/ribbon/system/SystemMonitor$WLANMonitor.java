package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioStatusListener;

final class SystemMonitor$WLANMonitor implements RadioStatusListener {
   private final SystemMonitor this$0;

   public SystemMonitor$WLANMonitor(SystemMonitor _1) {
      this.this$0 = _1;
      Application app = Application.getApplication();
      app.addRadioListener(4, this);
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.this$0.updateWiFiConnection(service);
   }

   @Override
   public final void radioTurnedOff() {
      this.this$0.setNetworkProp(-1839664706744174700L, null);
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.this$0.updateWiFiConnection(service);
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
}
